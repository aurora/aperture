/**
 * Copyright (c) Gunnar Aastrand Grimnes, DFKI GmbH, 2008. 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the distribution.
 *  * Neither the name of the DFKI GmbH nor the names of its contributors may be used to endorse or promote products derived from 
 *    this software without specific prior written permission.
 *    
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 *    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 *    SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 *    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS  
 *    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE  
 *    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.semanticdesktop.nepomuk.openrdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.aduna.concurrent.locks.Lock;
import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.LockingIteration;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.QueryRoot;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.evaluation.TripleSource;
import org.openrdf.query.algebra.evaluation.impl.BindingAssigner;
import org.openrdf.query.algebra.evaluation.impl.ConjunctiveConstraintSplitter;
import org.openrdf.query.algebra.evaluation.impl.ConstantOptimizer;
import org.openrdf.query.algebra.evaluation.impl.EvaluationStatistics;
import org.openrdf.query.algebra.evaluation.impl.EvaluationStrategyImpl;
import org.openrdf.query.algebra.evaluation.impl.FilterOptimizer;
import org.openrdf.query.algebra.evaluation.impl.QueryJoinOptimizer;
import org.openrdf.query.algebra.evaluation.util.QueryOptimizerList;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.memory.MemoryStoreConnection;
import org.openrdf.sail.memory.model.MemResource;
import org.openrdf.sail.memory.model.MemStatement;
import org.openrdf.sail.memory.model.MemURI;
import org.openrdf.sail.memory.model.MemValue;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.openrdf.sail.memory.model.ReadMode;

/**
 * @author grimnes
 *
 */
public class UnionMemoryStoreConnection extends MemoryStoreConnection implements
		SailConnection, UnionSailConnection {



	private UnionMemoryStore unionstore;

	public UnionMemoryStoreConnection(MemoryStore store) throws SailException {
		super(store);
		try {
			this.unionstore=(UnionMemoryStore) store;
		} catch (ClassCastException e) {
			throw new SailException("UnionMemoryStoreConnection needs a UnionMemoryStore!",e);
		}
	}

	public long realSize(Resource context) throws SailException {
		return super.size(context);
	}

	@Override
	protected CloseableIteration<? extends BindingSet, QueryEvaluationException> evaluateInternal(
			TupleExpr tupleExpr, Dataset dataset, BindingSet bindings, boolean includeInferred)
		throws SailException
	{
		// Clone the tuple expression to allow for more aggresive optimizations
		tupleExpr = tupleExpr.clone();

		if (!(tupleExpr instanceof QueryRoot)) {
			// Add a dummy root node to the tuple expressions to allow the
			// optimizers to modify the actual root node
			tupleExpr = new QueryRoot(tupleExpr);
		}

		Lock stLock = unionstore.getStatementsReadLock();

		try {
			int snapshot = unionstore.getCurrentSnapshot();
			ReadMode readMode = ReadMode.COMMITTED;

			if (transactionActive()) {
				snapshot++;
				readMode = ReadMode.TRANSACTION;
			}

			TripleSource tripleSource = new UnionMemTripleSource(includeInferred, snapshot, readMode);
			EvaluationStrategyImpl strategy = new EvaluationStrategyImpl(tripleSource, dataset);

			QueryOptimizerList optimizerList = new QueryOptimizerList();
			optimizerList.add(new BindingAssigner());
			optimizerList.add(new ConstantOptimizer(strategy));
			// TODO: replace compares with sameterms
			optimizerList.add(new QueryJoinOptimizer(new MemEvaluationStatistics()));
			optimizerList.add(new ConjunctiveConstraintSplitter());
			optimizerList.add(new FilterOptimizer());

			optimizerList.optimize(tupleExpr, dataset, bindings);

			CloseableIteration<BindingSet, QueryEvaluationException> iter;
			iter = strategy.evaluate(tupleExpr, bindings);
			return new LockingIteration<BindingSet, QueryEvaluationException>(stLock, iter);
		}
		catch (QueryEvaluationException e) {
			stLock.release();
			throw new SailException(e);
		}
		catch (RuntimeException e) {
			stLock.release();
			throw e;
		}
//		Lock queryLock = unionstore.getQueryReadLock();
//
//		try {
//			TripleSource tripleSource = new UnionMemTripleSource(includeInferred);
//			EvaluationStrategyImpl strategy = new EvaluationStrategyImpl(tripleSource);
//
//			CostComparator comparator = new MyMemCostComparator();
//			QueryJoinOptimizer joinOptimizer = new QueryJoinOptimizer(comparator);
//			joinOptimizer.optimize(tupleExpr, EmptyBindingSet.getInstance());
//
//			// Note: the query model should not be changed based on the supplied
//			// bindings, the same query model can later be used with different
//			// variable bindings
//			BooleanExprOptimizer booleanExprOptimizer = new BooleanExprOptimizer(strategy);
//			booleanExprOptimizer.optimize(tupleExpr, EmptyBindingSet.getInstance());
//
//			CloseableIteration<BindingSet, QueryEvaluationException> iter = null;
//			try {
//				iter = strategy.evaluate(tupleExpr, bindings);
//			}
//			catch (QueryEvaluationException e) {
//				throw new SailException(e);
//			}
//			return new LockingIteration<BindingSet, QueryEvaluationException>(queryLock, iter);
//		}
//		catch (RuntimeException e) {
//			queryLock.release();
//			throw e;
//		}
	}
	
	@Override
	protected CloseableIteration<? extends Statement, SailException> getStatementsInternal(Resource subj,
			URI pred, Value obj, boolean includeInferred, Resource... contexts)
		throws SailException
	{
		contexts=expandContext(contexts);
		return super.getStatementsInternal(subj, pred, obj, includeInferred, contexts);
	}
	
	class UnionMemTripleSource implements TripleSource {

		private boolean includeInferred;
		private int snapshot;
		private ReadMode readMode;

		public UnionMemTripleSource(boolean includeInferred, int snapshot, ReadMode readMode) {
			this.includeInferred = includeInferred;
			this.snapshot = snapshot;
			this.readMode = readMode;
		}

		public CloseableIteration<MemStatement, QueryEvaluationException> getStatements(Resource subj,
				URI pred, Value obj, Resource... contexts)
		{
			contexts=expandContext(contexts);
			return unionstore.createStatementIterator(QueryEvaluationException.class, subj, pred, obj,
					!includeInferred, snapshot, readMode, contexts);
		}

		public MemValueFactory getValueFactory() {
			return unionstore.getValueFactory();
		}
	} // end inner class MemTripleSource
	
	private Resource[] expandContext(Resource... contexts) {
		Set<Resource> res=new HashSet<Resource>();
		for (Resource r: contexts) {
			if (unionstore.unions.containsKey(r)) {
				res.addAll(expandContext(unionstore.unions.get(r)));
			}
			res.add(r);
		}
		return res.toArray(new Resource[res.size()]);
	}
	
	// double to skip on conversion list->array
	private Set<Resource> expandContext(Collection<? extends Resource> contexts) {
		Set<Resource> res=new HashSet<Resource>();
		for (Resource r: contexts) {
			if (unionstore.unions.containsKey(r)) {
				res.addAll(expandContext(unionstore.unions.get(r)));
			} else res.add(r);
		}
		return res;
	}
	/**
	 * Uses the MemoryStore's statement sizes to give cost estimates based on the
	 * size of the expected results. This process could be improved with
	 * repository statistics about size and distribution of statements.
	 * 
	 * @author Arjohn Kampman
	 * @author James Leigh
	 */
	protected class MemEvaluationStatistics extends EvaluationStatistics {

		@Override
		protected CardinalityCalculator createCardinalityCalculator() {
			return new MemCardinalityCalculator();
		}

		protected class MemCardinalityCalculator extends CardinalityCalculator {

			@Override
			public double getCardinality(StatementPattern sp) {
				Resource subj = (Resource)getConstantValue(sp.getSubjectVar());
				URI pred = (URI)getConstantValue(sp.getPredicateVar());
				Value obj = getConstantValue(sp.getObjectVar());
				Resource context = (Resource)getConstantValue(sp.getContextVar());

				MemValueFactory valueFactory = store.getValueFactory();

				// Perform look-ups for value-equivalents of the specified values
				MemResource memSubj = valueFactory.getMemResource(subj);
				MemURI memPred = valueFactory.getMemURI(pred);
				MemValue memObj = valueFactory.getMemValue(obj);
				MemResource memContext = valueFactory.getMemResource(context);

				if (subj != null && memSubj == null || pred != null && memPred == null || obj != null
						&& memObj == null || context != null && memContext == null)
				{
					// non-existent subject, predicate, object or context
					return 0.0;
				}

				// Search for the smallest list that can be used by the iterator
				List<Integer> listSizes = new ArrayList<Integer>(4);
				if (memSubj != null) {
					listSizes.add(memSubj.getSubjectStatementCount());
				}
				if (memPred != null) {
					listSizes.add(memPred.getPredicateStatementCount());
				}
				if (memObj != null) {
					listSizes.add(memObj.getObjectStatementCount());
				}
				if (memContext != null) {
					listSizes.add(memContext.getContextStatementCount());
				}

				double cardinality;

				if (listSizes.isEmpty()) {
					// all wildcards
					cardinality = unionstore.size();
				}
				else {
					cardinality = Collections.min(listSizes);

					// List<Var> vars = getVariables(sp);
					// int constantVarCount = countConstantVars(vars);
					//
					// // Subtract 1 from var count as this was used for the list
					// size
					// double unboundVarFactor = (double)(vars.size() -
					// constantVarCount) / (vars.size() - 1);
					//
					// cardinality = Math.pow(cardinality, unboundVarFactor);
				}

				return cardinality;
			}

			protected Value getConstantValue(Var var) {
				if (var != null) {
					return var.getValue();
				}

				return null;
			}
		}
	} // end inner class MemCardinalityCalculator
//
//	/**
//	 * Uses the MemoryStore's statement sizes to give cost estimates based on the
//	 * size of the expected results. This process could be improved with
//	 * repository statistics about size and distribution of statements.
//	 * 
//	 * @author Arjohn Kampman
//	 * @author James Leigh
//	 */
//	protected class MemEvaluationStatistics extends EvaluationStatistics {
//
//		@Override
//		protected CardinalityCalculator createCardinalityCalculator() {
//			return new MemCardinalityCalculator();
//		}
//
//		protected class MemCardinalityCalculator extends CardinalityCalculator {
//
//			@Override
//			public void meet(StatementPattern sp) {
//				Resource subj = (Resource)getConstantValue(sp.getSubjectVar());
//				URI pred = (URI)getConstantValue(sp.getPredicateVar());
//				Value obj = getConstantValue(sp.getObjectVar());
//				Resource context = (Resource)getConstantValue(sp.getContextVar());
//
//				MemValueFactory valueFactory = store.getValueFactory();
//
//				// Perform look-ups for value-equivalents of the specified values
//				MemResource memSubj = valueFactory.getMemResource(subj);
//				MemURI memPred = valueFactory.getMemURI(pred);
//				MemValue memObj = valueFactory.getMemValue(obj);
//				MemResource memContext = valueFactory.getMemResource(context);
//
//				if (subj != null && memSubj == null || pred != null && memPred == null || obj != null
//						&& memObj == null || context != null && memContext == null)
//				{
//					// non-existent subject, predicate, object or context
//					cardinality = 0;
//					return;
//				}
//
//				// Search for the smallest list that can be used by the iterator
//				List<Integer> listSizes = new ArrayList<Integer>(4);
//				if (memSubj != null) {
//					listSizes.add(memSubj.getSubjectStatementCount());
//				}
//				if (memPred != null) {
//					listSizes.add(memPred.getPredicateStatementCount());
//				}
//				if (memObj != null) {
//					listSizes.add(memObj.getObjectStatementCount());
//				}
//				if (memContext != null) {
//					listSizes.add(memContext.getContextStatementCount());
//				}
//
//				if (listSizes.isEmpty()) {
//					cardinality = unionstore.size();
//
//					int sqrtFactor = 2 * countBoundVars(sp);
//
//					if (sqrtFactor > 1) {
//						cardinality = Math.pow(cardinality, 1.0 / sqrtFactor);
//					}
//				}
//				else {
//					cardinality = Collections.min(listSizes);
//
//					int constantVarCount = countConstantVars(sp);
//					int boundVarCount = countBoundVars(sp);
//
//					// Subtract 1 from constantVarCount as this was used for the list
//					// size
//					int sqrtFactor = 2 * boundVarCount + Math.max(0, constantVarCount - 1);
//
//					if (sqrtFactor > 1) {
//						cardinality = Math.pow(cardinality, 1.0 / sqrtFactor);
//					}
//				}
//			}
//
//			protected Value getConstantValue(Var var) {
//				if (var != null) {
//					return var.getValue();
//				}
//
//				return null;
//			}
//		}
//	} // end inner class MemCardinalityCalculator

	@Override
	protected boolean removeStatementsInternal(Resource subj, URI pred,
			Value obj, boolean explicit, Resource... contexts)
			throws SailException {
		
		for (Resource u: contexts)
			if (unionstore.isUnion(u)) throw new SailException("Cannot delete from Union Context: "+u);
		
		return super.removeStatementsInternal(subj, pred, obj, explicit, contexts);
	}

	
}
