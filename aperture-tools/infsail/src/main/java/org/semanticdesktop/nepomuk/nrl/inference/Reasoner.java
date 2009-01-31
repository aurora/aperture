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
 *    
 */
package org.semanticdesktop.nepomuk.nrl.inference;

import info.aduna.iteration.CloseableIteration;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.sail.SailException;
import org.semanticdesktop.nepomuk.nrl.inference.exceptions.ReasonerException;
import org.semanticdesktop.nepomuk.nrl.inference.model.ClauseEntry;
import org.semanticdesktop.nepomuk.nrl.inference.model.Functor;
import org.semanticdesktop.nepomuk.nrl.inference.model.TriplePattern;
import org.semanticdesktop.nepomuk.nrl.inference.model.Variable;
import org.semanticdesktop.nepomuk.openrdf.InfSailConnection;
import org.semanticdesktop.nepomuk.openrdf.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author grimnes
 *
 */
public class Reasoner {
	
	private static final boolean TRACE = false;
	Logger log=LoggerFactory.getLogger(Reasoner.class);
	
	
	public static boolean sameValueAs(Value a, Value b) {
		return a.equals(b);
	}

	public static Value createBlankNode() {
		return new BNodeImpl(UUID.randomUUID().toString());
	}

//	public static Model createMemModel() {
//		try {
//			return RDF2Go.getModelFactory().createModel();
//		} catch (Exception e) {
//			throw new ModelRuntimeException(e);
//		}
//	}

	public List<URI> applyRules(InfSailConnection connection, URI baseGraph, URI dataGraph, View inf) throws SailException {
		return applyRules(connection, baseGraph, dataGraph, inf, null);
	}
	
	/** 
	 * Apply the rules to the data,
	 * Add any new to newStatement
	 *  
	 * @param newStatements 
	 * 
	 * @param data
	 * @param rules
	 * @param target
	 * @return the number of triples added 
	 * @throws ModelException
	 */
	public List<URI> applyRules(InfSailConnection connection, URI baseGraph, URI dataGraph, View inf, Set<Statement> newStatements) throws SailException {
		
		int res=0;
		int this_strata=0;
		int i=0;
		
		List<URI> resStratas = new Vector<URI>();
		
		URI base = inf.name; // this is the current existing triples
		do { 
			//List<Statement> data = listStatements(connection, dataGraph);
			this_strata=0;
			URI strata=inf.getStrata(connection);
			
			BFRuleContext context = new BFRuleContext(connection, base, dataGraph, strata);
			List<Rule> rules = new Vector<Rule>();
			ruleFilter:
			for(Rule r: inf.rules) {
				if (r.isAxiom()) continue;
				for (ClauseEntry clause: r.getBody()) { 
					if (sloppyMatch(connection, clause, dataGraph)) {
						rules.add(r);
						continue ruleFilter;
					}
				}
			}
			//System.err.println("Filtered from "+inf.rules.size()+" to "+rules.size());
			for(Rule r: rules) {
				
				context.setRule(r);
				
				if (matchClauseList(r.getBody(),context, newStatements)) {
					int added = context.flushPending();
					res+=added;
					this_strata+=added;
				}
			}
			// context.close(); ? 
			if (this_strata>0) {
				// This should update the base to include the just inferred data. 
				dataGraph=strata;  
				inf.addStrata(strata);
				resStratas.add(strata);
			}
			log.debug("Strata "+i++);
			if (TRACE)
				System.err.println("Strata "+i);
		} while (this_strata>0); 
		 
		return resStratas;
	}
	
	private boolean sloppyMatch(InfSailConnection connection, ClauseEntry clause, URI dataGraph) throws SailException {
		if (clause instanceof TriplePattern) {
			TriplePattern triplepattern = (TriplePattern)clause;
			Value s = triplepattern.getSubjectMatch();
			Value p = triplepattern.getPredicateMatch();
			Value o = triplepattern.getObjectMatch();
			if (s==null && p== null && o == null) return true;
			CloseableIteration<? extends Statement, SailException> r = null;
			try {
				r = connection.getStatements((Resource)s,(URI)p,o, false, dataGraph);
				return r.hasNext();
			} finally { 
				if (r!=null) r.close();
			}
			
		} else { 
			throw new ReasonerException("Only triple-patterns are supported! Was: "+clause.getClass());
		}
	}

	private boolean matchClauseList(ClauseEntry[] entries, BFRuleContext context, Set<Statement> newStatements) throws SailException {
		return matchClauseList(entries, context, false, entries.length-1, newStatements);
	}
	/**
	 * Great... 
	 * @param entries
	 * @param context
	 * @param foundInNewData
	 * @param newStatements 
	 * @return
	 * @throws ModelException
	 */
	private boolean matchClauseList(ClauseEntry[] entries, BFRuleContext context, boolean foundInNewData, int index, Set<Statement> newStatements) throws SailException {
	        Rule rule = context.getRule();
	        BindingStack env = (BindingStack) context.getEnv();

	        if (index == -1) {
	        	if (!foundInNewData && rule.getBody().length>0) return false;
	            // Check any non-pattern clauses 
	            /**for (int i = 0; i < rule.bodyLength(); i++) {
	                Object clause = rule.getBodyElement(i);
	                if (clause instanceof Functor) {
	                	throw new ReasonerException("Only triple-patterns are supported!"); 
	                    // Fire a built in
	                    //if (!((Functor)clause).evalAsBodyClause(context)) {
	                    //    return false;       // guard failed
	                    //}
	                }
	            }**/
	            // Now fire the rule
	            log.debug("Fired rule: " + rule.toShortString() + " = " + rule.instantiate(env));
	            if (TRACE) {
	            	System.err.println("Fired rule: " + rule.toShortString() + " = " + rule.instantiate(env));
	            }
	            
	           
	            for (int i = 0; i < rule.headLength(); i++) {
	                ClauseEntry hClause = rule.getHeadElement(i);
	                if (hClause instanceof TriplePattern) {
	                    if (!(env.getBinding(((TriplePattern) hClause).getSubject()) instanceof Literal)) {
	                        // Only add the result if it is legal at the RDF level.
	                        // E.g. RDFS rules can create assertions about literals
	                        // that we can't record in RDF
	                    	Statement t = env.instantiate((TriplePattern) hClause);
	                        if ( ! context.contains(t)  ) {
	                        	if (newStatements!=null) newStatements.add(t);
	                            context.add(t);
	                        }
	                    }
	                } else //if (hClause instanceof Functor) {
	                	throw new ReasonerException("Only triple-patterns are supported! Was: "+hClause.getClass());
//	                } else if (hClause instanceof Rule) {
//	                	throw new ReasonerException("Only triple-patterns are supported!");
//	                }
	            }
	            return true;
	        }

	        TriplePattern clause=(TriplePattern) entries[index];
	        Value objPattern = env.getBinding(clause.getObject());
	        if (Functor.isFunctor(objPattern)) {
	            // Can't search on functor patterns so leave that as a wildcard
	            objPattern = null;
	        }
	        Value subjPattern = env.getBinding(clause.getSubject());
	        Value predPattern = env.getBinding(clause.getPredicate());
	        
	        CloseableIteration<? extends Statement, SailException> i = context.findBaseStatements(subjPattern, predPattern, objPattern);
	        boolean foundMatch = false;
	        
	        while (i.hasNext()) {
	            Statement t = i.next();
	            
	            // Add the bindings to the environment
	            env.push();
	            if (matchNode(clause.getPredicate(), t.getPredicate(), env)
	                    && matchNode(clause.getObject(), t.getObject(), env)
	                    && matchNode(clause.getSubject(), t.getSubject(), env)) {
	                foundMatch |= matchClauseList(entries, context, foundInNewData , index-1, newStatements);
	            }
	            env.unwind();
	        }
	        i.close();
	        
	        i = context.findUpdateStatements(subjPattern, predPattern, objPattern);
	        
	        while (i.hasNext()) {
	            Statement t = i.next();
	            
	            // Add the bindings to the environment
	            env.push();
	            if (matchNode(clause.getPredicate(), t.getPredicate(), env)
	                    && matchNode(clause.getObject(), t.getObject(), env)
	                    && matchNode(clause.getSubject(), t.getSubject(), env)) {
	                foundMatch |= matchClauseList(entries, context, true, index-1, newStatements);
	            }
	            env.unwind();
	        }
	        i.close();
	        
	        return foundMatch ;
	    }
	
	private boolean matchNode(Value pattern, Value node, BindingStack env)  {
		  if (pattern instanceof Variable) {
	            int index = ((Variable)pattern).getIndex();
	            return env.bind(index, node);
	        } else if (pattern instanceof Variable) {
	            return true;
	        } else if (pattern instanceof Resource || pattern instanceof Literal){
	            return pattern.equals(node);
	        } else { 
	        	throw new ReasonerException("Unknown node type "+node.getClass());
	        }
	}
	
	public void applyAxioms(InfSailConnection connection, URI base, View inf) throws SailException {
		URI strata=null;
		for (Rule r: inf.rules) {
			if (r.isAxiom()) {
				
				for (ClauseEntry c: r.getHead()) { 
					if (c instanceof TriplePattern) {
						TriplePattern t = (TriplePattern)c;
						if (!(t.getSubject() instanceof Resource)) {
							throw new ReasonerException("Axiom with non-resource subject in head: "+c+" in "+r.toShortString());
						}
						if (!(t.getPredicate() instanceof URI)) {
							throw new ReasonerException("Axiom with non-uri predicate in head: "+c+" in "+r.toShortString());
						}
						if (!(t.getObject() instanceof Literal) && 
							!(t.getObject() instanceof Resource)) {
							throw new ReasonerException("Axiom with invalid RDF object in head: "+c+" in "+r.toShortString());
						}
	                    Statement s = new StatementImpl((Resource)t.getSubject(),(URI) t.getPredicate(),t.getObject());
	                    if (!contains(connection, s, base)) {
	                    	log.debug("Adding axiom "+r.toShortString());
	                    	if (strata==null) {
	        					strata = inf.getStrata(connection);
	        					inf.addStrata(strata);
	                    	}
	                    	connection.addBase(strata, s);
	                    	
	                    }
	                } else if (c instanceof Functor) {
	                	throw new ReasonerException("Only triple-patterns are supported!");
	                } else if (c instanceof Rule) {
	                	throw new ReasonerException("Only triple-patterns are supported!");
	                }
				}
			}
		}
		connection.commit();
	}

	private boolean contains(InfSailConnection connection, Statement s, URI context) throws SailException {
		CloseableIteration<? extends Statement, SailException> r = connection.getStatements(s.getSubject(), s.getPredicate(), s.getObject(), false, context);
		try {
			return r.hasNext();
		} finally { 
			r.close();
		}
	}

	
}
