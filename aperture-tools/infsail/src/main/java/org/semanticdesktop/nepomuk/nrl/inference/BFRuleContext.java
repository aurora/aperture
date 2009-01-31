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
 *    Adapted from the Jena, as detailed below.
 */
/******************************************************************
 * File:        RuleContext.java
 * Created by:  Dave Reynolds
 * Created on:  15-Apr-2003
 * 
 * (c) Copyright 2003, 2004, 2005, 2006 Hewlett-Packard Development Company, LP
 * [See end of file]
 * $Id: BFRuleContext.java,v 1.15 2006/03/22 13:52:24 andy_seaborne Exp $
 *****************************************************************/
package org.semanticdesktop.nepomuk.nrl.inference;


import info.aduna.iteration.CloseableIteration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.sail.SailException;
import org.semanticdesktop.nepomuk.openrdf.InfSailConnection;

/**
 * An implementation of the generic RuleContext interface used by
 * the basic forward (BF) rule engine. This provides additional
 * methods specific to the functioning of that engine.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.15 $ on $Date: 2006/03/22 13:52:24 $
 */
public class BFRuleContext implements RuleContext {
    /** The binding environment which represents the state of the current rule execution. */
    protected BindingStack env;
    
    /** The rule current being executed. */
    protected Rule rule;
    
    /** The enclosing inference graph. */
    protected URI updateGraph;
    
    /** A stack of triples which have been added to the graph but haven't yet been processed. */
    //protected List<Statement> stack;
    
    /** A temporary list of Triples which will be added to the stack and triples at the end of a rule scan */
    protected List<Statement> pending;

    /** A temporary list of Triples which will be removed from the graph at the end of a rule scan */
    protected List<Statement> deletesPending = new ArrayList<Statement>();

	private URI infGraph;

	private URI baseGraph;

	private InfSailConnection connection;



    
    protected static Logger logger = Logger.getLogger(BFRuleContext.class.getName());
    
    /**
     * Constructor.
     * @param base - the triples we already knew 
     * @param update - the new triples added
     * @param infgraph - the model to add inferred statements to 
     * @throws ModelException 
     */
    public BFRuleContext(InfSailConnection connection, URI base, URI update, URI infgraph) {
    	this.connection=connection;
    	this.baseGraph=base;
        this.updateGraph = update;
        this.infGraph=infgraph;
        
        //this.allData=new UnionModelImpl(unionms, SingleWriteModel.READ_ONLY_MODEL, base.getContextURI(),graph.getContextURI() );
        //this.allData=unionms.getUnionModel(null, base, update);
        
        env = new BindingStack();
        //stack = new ArrayList<Statement>();
        pending = new ArrayList<Statement>();
    }
    
    public void close() { 
    	//allData.close();
    }
    
    public URI getBaseGraph() { 
    	return baseGraph; 
    }
    
    public URI getInfGraph() {
    	return infGraph;
    }
    
    /**
     * Returns the current variable binding environment for the current rule.
     * @return BindingEnvironment
     */
    public BindingEnvironment getEnv() {
        return env;
    }

    /**
     * Returns the graph.
     * @return InfGraph
     */
    public URI getUpdateGraph() {
        return updateGraph;
    }

    /**
     * Returns the rule.
     * @return Rule
     */
    public Rule getRule() {
        return rule;
    }

    /**
     * Sets the rule.
     * @param rule The rule to set
     */
    public void setRule(Rule rule) {
        this.rule = rule;
    }

    
    
    /**
     * Add a triple to a temporary "pending" store, ready to be added to added to the
     * deductions graph and the processing stack later.
     * <p>This is needed to prevent concurrrent modification exceptions which searching
     * the deductions for matches to a given rule.
     */
    public void add(Statement t) {
        pending.add(t);
        //pendingCache.add(t);
    }
            
    /**
     * Take all the pending triples and add them to both the given graph and
     * to the processing stack.
     * @throws ModelException 
     */
    public int flushPending() throws SailException {
    	int added=0;
    	added+=pending.size();
    	
    	// HMM, the Sail only lets me add a single statement
    	//connection.addStatement(subj, pred, obj, contexts)
    	
    	connection.addBase(infGraph, pending);
    	connection.baseCommit();
    	pending.clear();
    	
    	// TODO: removes
        
        // Flush out pending removes as well
        /* for (Iterator<Statement> i = deletesPending.iterator(); i.hasNext(); ) {
        	Statement t = i.next();
            updategraph.removeStatement(t);
        }
        deletesPending.clear(); */
    	
        return added;
    }
    
    /**
     * Return true if the triple is already in either the graph or the stack.
     * I.e. it has already been deduced.
     * @throws SailException 
     * @throws ModelException 
     */
    public boolean contains(Statement t) throws SailException {
        // Can't use stackCache.contains because that does not do semantic equality
        return contains(t.getSubject(), t.getPredicate(), t.getObject());
    }
    
    /**
     * Return true if the triple pattern is already in either the graph or the stack.
     * I.e. it has already been deduced.
     * @throws SailException 
     * @throws ModelException 
     */
    public boolean contains(Value s, Value p, Value o) throws SailException {
        // Can't use stackCache.contains because that does not do semantic equality
		CloseableIteration<? extends Statement, SailException> it = connection.getStatements((Resource)s, (URI) p, o, false, baseGraph, updateGraph);
        try { 
        	return it.hasNext();
        } finally { 
        	it.close();
        }
    }
    


    
    
    
    /**
     * Reset the binding environemnt back to empty
     */
    public void resetEnv() {
        env.reset();
    }
    
    
    /**
     * Remove a triple from the deduction graph (and the original graph if relevant).
     */
    public void remove(Statement t) {
    	throw new RuntimeException("Remove not implemented!");
        //deletesPending.add(t);
//        graph.delete(t);
    }

	public CloseableIteration<? extends Statement, SailException> findAllStatements(Value subj, Value pred, Value obj) throws SailException {
		// base + update? 
		return connection.getStatements((Resource)subj, (URI)pred, obj, false, baseGraph, updateGraph);
	}
	
	public CloseableIteration<? extends Statement, SailException> findBaseStatements(Value subj, Value pred, Value obj) throws SailException {
		// base + update? 
		return connection.getStatements((Resource)subj, (URI)pred, obj, false, baseGraph);
	}
	
	public CloseableIteration<? extends Statement, SailException> findUpdateStatements(Value subj, Value pred, Value obj) throws SailException {
		// base + update? 
		return connection.getStatements((Resource)subj, (URI)pred, obj, false, updateGraph);
	}

	public boolean inUpdateGraph(Statement t) throws SailException {
		boolean res = false;
		CloseableIteration<? extends Statement, SailException> i = connection.getStatements(t.getSubject(), 
				t.getPredicate(), 
				t.getObject(), false, updateGraph);
		if (i.hasNext()) res=true; 
		i.close();
		return res; 
	}

}

/*
    (c) Copyright 2003, 2004, 2005, 2006 Hewlett-Packard Development Company, LP
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
