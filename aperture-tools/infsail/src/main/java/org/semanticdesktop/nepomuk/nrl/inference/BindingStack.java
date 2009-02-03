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
 * File:        BindingStack.java
 * Created by:  Dave Reynolds
 * Created on:  28-Apr-03
 * 
 * (c) Copyright 2003, 2004, 2005, 2006 Hewlett-Packard Development Company, LP
 * [See end of file]
 * $Id: BindingStack.java,v 1.9 2006/03/22 13:52:24 andy_seaborne Exp $
 *****************************************************************/
package org.semanticdesktop.nepomuk.nrl.inference;


import java.util.ArrayList;
import java.util.Arrays;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.semanticdesktop.nepomuk.nrl.inference.model.Functor;
import org.semanticdesktop.nepomuk.nrl.inference.model.TriplePattern;
import org.semanticdesktop.nepomuk.nrl.inference.model.Variable;

/**
 * Provides a trail of possible variable bindings for a forward rule.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.9 $ on $Date: 2006/03/22 13:52:24 $
 */
public class BindingStack implements BindingEnvironment {
    
    // Current slightly quirky implementation tries to avoid allocating
    // store in which will be an inner loop. Does one array copy on
    // push but handles both versions of pop with point manipulation.
    
    /** The current binding set */
    protected Value[] environment;
    
    /** A stack of prior binding sets */
    protected ArrayList<Value[]> trail = new ArrayList<Value[]>();
    
    /** Index of the current binding set */
    protected int index = 0;
    
    /** Index of maximum allocated slot in the trail */
    protected int highWater = 0;
    
    /** Maximum number of distinct variables allowed in rules */
    protected static final int MAX_VAR = 10;
    
    /**
     * Constructor
     */
    public BindingStack() {
        trail.add(new Value[MAX_VAR]);
        environment = (Value[])trail.get(0);
        index = highWater = 0;
    }
    
    /**
     * Save the current environment on an internal stack 
     */
    public void push() {
        if (index == highWater) {
            trail.add(new Value[MAX_VAR]);
            highWater++;
        }
        Value[] newenv = (Value[]) trail.get(++index);
        System.arraycopy(environment, 0, newenv, 0, MAX_VAR);
        environment = newenv;
    }
    
    /**
     * Forget the current environment and return the previously
     * pushed state.
     * @throws IndexOutOfBoundsException if there was not previous push
     */
    public void unwind() throws IndexOutOfBoundsException {
        if (index > 0) {
            // just point to previous stack entry
            environment = (Value[]) trail.get(--index);
        } else {
            throw new IndexOutOfBoundsException("Underflow of BindingEnvironment");
        }
    }
    
    /**
     * Forget the previously pushed state but keep the current environment.
     * @throws IndexOutOfBoundsException if there was not previous push
     */
    public void commit() throws IndexOutOfBoundsException {
        if (index > 0) {
            // Swap top and previous stack entries and point to previous
            Value[] newenv = (Value[]) trail.get(index-1);
            trail.set(index-1, environment);
            trail.set(index, newenv);
            --index;
        } else {
            throw new IndexOutOfBoundsException("Underflow of BindingEnvironment");
        }
    }
   
    /**
     * Reset the binding environment to empty.
     */
    public void reset() {
        index = 0;
        environment = (Value[]) trail.get(0);
        Arrays.fill(environment, null);
    }
    
    /**
     * Return the current array of bindings
     */
    public Value[] getEnvironment() {
        return environment;
    }
    
    /**
     * If the Value is a variable then return the current binding 
     * rdf2go Variable.ANY if not bound 
     * otherwise return the Value itself.
     */
    public Value getBinding(Value node) {
        if (node instanceof Variable) {
        	Value r = environment[((Variable)node).getIndex()];
        	if (r==null) return null; 
        	return r; 
        } else if (Functor.isFunctor(node)) {
            Functor functor = (Functor)node;
            if (functor.isGround()) return node;
            Value[] args = functor.getArgs();
            ArrayList<Value> boundargs = new ArrayList<Value>(args.length);
            for (int i = 0; i < args.length; i++) {
                Value binding = getBinding(args[i]);
                if (binding == null) {
                    // Not sufficent bound to instantiate functor yet
                    return null;
                }
                boundargs.add(binding);
            }
            Functor newf = new Functor(functor.getName(), boundargs);
            return Functor.makeFunctorValue( newf );
        } else {
            return node;
        }
    }
    
    /**
     * Bind the ith variable in the current envionment to the given value.
     * Checks that the new binding is compatible with any current binding.
     * @return false if the binding fails
     */
    public boolean bind(int i, Value value) {
        Value node = environment[i];
        if (node == null) {
            environment[i] = value;
            return true;
        } else {
            return Reasoner.sameValueAs(node, value);
        }
    }
    
    /**
     * Bind a variable in the current envionment to the given value.
     * Checks that the new binding is compatible with any current binding.
     * @param var a Node_RuleVariable defining the variable to bind
     * @param value the value to bind
     * @return false if the binding fails
     */
    public boolean bind(Value var, Value value) {
        if (var instanceof Variable) {
            return bind(((Variable)var).getIndex(), value);
        } else {
            return Reasoner.sameValueAs(var,value);
        }
    }
    
    /**
     * Bind a variable in the current envionment to the given value.
     * Overrides and ignores any current binding.
     * @param var a Node_RuleVariable defining the variable to bind
     * @param value the value to bind
     */
    public void bindNoCheck(Variable var, Value value) {
        environment[var.getIndex()] = value;
    }
    
    /**
     * Instantiate a triple pattern against the current environment.
     * This version handles unbound varibles by turning them into bNodes.
     * @param clause the triple pattern to match
     * @param env the current binding environment
     * @return a new, instantiated triple
     */
    public Statement instantiate(TriplePattern pattern) {
        Value s = getBinding(pattern.getSubject());
        if (s instanceof Variable) s = Reasoner.createBlankNode();
        Value p = getBinding(pattern.getPredicate());
        if (p instanceof Variable) p = Reasoner.createBlankNode();
        Value o = getBinding(pattern.getObject());
        if (o instanceof Variable) o = Reasoner.createBlankNode();
        return new StatementImpl((Resource)s, (URI) p, o);
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

