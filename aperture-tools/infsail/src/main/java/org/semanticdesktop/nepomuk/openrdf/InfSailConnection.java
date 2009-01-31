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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.helpers.SailConnectionWrapper;
import org.semanticdesktop.nepomuk.nrl.inference.Reasoner;

/**
 * @author grimnes
 *
 */
public class InfSailConnection extends SailConnectionWrapper {


	private HashMap<Resource, Set<Statement>> added;
	private HashMap<Resource, Set<Statement>> removed;
	private InfSail infsail;
	private UnionSailConnection unionconnection;

	public InfSailConnection(InfSail sail, SailConnection connection) throws SailException {
		super(connection);
		if (!(connection instanceof UnionSailConnection)) throw new SailException("InfSailConnection must wrap a UnionSailConnection");
		this.unionconnection=(UnionSailConnection)connection;
		this.infsail=sail;
		resetTxn();
	}

	@Override
	public void addStatement(Resource subj, URI pred, Value obj, Resource... contexts) throws SailException {
		super.addStatement(subj, pred, obj, contexts);
		for (Resource c: contexts)
			_add(new StatementImpl(subj,pred,obj),c);
	}

	private void _add(Statement s, Resource c) {
		if (added.containsKey(c)) {
			added.get(c).add(s);
		} else { 
			Set<Statement> set=new HashSet<Statement>(1);
			set.add(s);
			added.put(c,set);
		}
	}

	@Override
	public void removeStatements(Resource subj, URI pred, Value obj, Resource... contexts) throws SailException {
		super.removeStatements(subj, pred, obj, contexts);
		for (Resource c: contexts)
			_remove(new StatementImpl(subj,pred,obj),c);
	}

	private void _remove(Statement s, Resource c) {
		if (removed.containsKey(c)) {
			removed.get(c).add(s);
		} else { 
			Set<Statement> set=new HashSet<Statement>(1);
			set.add(s);
			removed.put(c,set);
		}
	}
	
	@Override
	public void clear(Resource... contexts) throws SailException {
		// TODO Auto-generated method stub
		super.clear(contexts);
	}

	@Override
	public void commit() throws SailException {
		super.commit();
		HashMap<URI, List<URI>> nested=new HashMap<URI, List<URI>>();
		Queue<Resource> todo= new ConcurrentLinkedQueue<Resource>();
		todo.addAll(added.keySet());
		while(!todo.isEmpty()) {
			Resource context=todo.poll();
			
			if (infsail.hasView(context)) {
				
				URI addedGraph=new URIImpl("temp://"+UUID.randomUUID());
				// addedGraph needs to be in same store for union to work correctly.
			    addBase(addedGraph,added.get(context));
			    super.commit();

				for (View view: infsail.getViews(context)) { 
					//InfGraph inf=infsail.getInfGraph(view);
					
					Set<Statement> newStatements=new HashSet<Statement>(); 
					new Reasoner().applyRules(this, view.base, addedGraph, view, newStatements);
					
					// If another view is stacked on top of this we have to "trickle" the updates
					if (infsail.hasView(view.name)) {
						added.put(view.name, newStatements);
						todo.add(view.name);
					}
				}				
				// wipe temporary graph
				super.clear(addedGraph);
				super.commit();
			}
		}
		for (Resource context: removed.keySet()) { 
			if (infsail.hasView(context)) { 
				throw new SailException("Removing from inf-graphs is not supported :)");
			}
		}
		resetTxn();
	}

	@Override
	public void rollback() throws SailException {
		super.rollback();
		resetTxn();
	}
	

	private void resetTxn() {
		added=new HashMap<Resource, Set<Statement>>();
		removed=new HashMap<Resource, Set<Statement>>();
	}

	/** 
	 * Add all statements to the base connection. 
	 * Ignoring inference bits. 
	 * @param infGraph
	 * @param statements
	 * @throws SailException 
	 */
	public void addBase(URI context, Collection<Statement> statements) throws SailException {
		for (Statement s: statements ) { 
			super.addStatement(s.getSubject(), s.getPredicate(), s.getObject(), context);
		}
	}

	/** 
	 * Pretend the triples in baseGraph were just re-added
	 * @param baseGraph
	 * @throws SailException 
	 */
	public void reinfer(URI baseGraph) throws SailException {
		if (infsail.hasView(baseGraph)) { 
			for (View view : infsail.getViews(baseGraph)) { 
			
				Reasoner r = new Reasoner();
				r.applyAxioms(this, view.base, view);
				r.applyRules(this, view.base, baseGraph, view);
			}
		}
	}

	/** 
	 * Call commit on the base-sail without worrying about inference
	 * @throws SailException 
	 *
	 */
	public void baseCommit() throws SailException {
		super.commit();
	}

	public void addBase(URI context, Statement s) throws SailException {
		super.addStatement(s.getSubject(), s.getPredicate(), s.getObject(), context);
	}

	public long realSize(Resource target) throws SailException {
		return unionconnection.realSize(target);
	}
	
}
