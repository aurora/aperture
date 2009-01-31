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
 * File:        Functor.java
 * Created by:  Dave Reynolds
 * Created on:  29-Mar-03
 * 
 * (c) Copyright 2003, 2004, 2005, 2006 Hewlett-Packard Development Company, LP
 * [See end of file]
 * $Id: Functor.java,v 1.22 2006/03/22 13:52:20 andy_seaborne Exp $
 *****************************************************************/
package org.semanticdesktop.nepomuk.nrl.inference.model;


import java.util.List;
import java.util.logging.Logger;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.semanticdesktop.nepomuk.nrl.inference.BindingEnvironment;
import org.semanticdesktop.nepomuk.nrl.inference.BuiltinRegistry;
import org.semanticdesktop.nepomuk.nrl.inference.RuleContext;

/**
 * A functor comprises a functor name and a list of 
 * arguments. The arguments are Nodes of any type except functor nodes
 * (there is no functor nesting).  Functors play three roles in rules -
 * in heads they represent actions (procedural attachement); in bodies they
 * represent builtin predicates; in TriplePatterns they represent embedded
 * structured literals that are used to cache matched subgraphs such as
 * restriction specifications.
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.22 $ on $Date: 2006/03/22 13:52:20 $
 */
public class Functor implements ClauseEntry {
    private static final URI FUNCTOR_DATATYPE = new URIImpl("urn:x-hp-jena:Functor");

	/** Functor's name */
    protected String name;
    
    /** Argument list - an array of Values */
    protected Value[] args;
    
    /** A built in that implements the functor */
    protected Builtin implementor;
    
    /** A static Filter instance that detects triples with Functor objects */
//    public static final Filter acceptFilter = new Filter() {
//                public boolean accept(Object t) {
//                    if (((Triple)t).getSubject().isLiteral()) return true;
//                    Value n = ((Triple)t).getObject();
//                    return n.isLiteral() && n.getLiteralDatatype() == FunctorDatatype.theFunctorDatatype;
//                }
//            };
    
    protected static Logger logger = Logger.getLogger(Functor.class.getName());
    
    /**
     * Constructor. 
     * @param name the name of the functor
     * @param args a list of Values defining the arguments
     */
    public Functor(String name, List args) {
        this.name = name;
        this.args = (Value[]) args.toArray(new Value[]{});
    }
    
    /**
     * Constructor. 
     * @param name the name of the functor
     * @param args an array of Values defining the arguments, this will not be copied so beware of
     * accidental structure sharing
     */
    public Functor(String name, Value[] args) {
        this.name = name;
        this.args = args;
    }
    
    /**
     * Constructor
     * @param name the name of the functor
     * @param args a list of Values defining the arguments
     * @param registry a table of builtins to consult to check for 
     * implementations of this functor when used as a rule clause
     */
    public Functor(String name, List args, BuiltinRegistry registry) {
        this.name = name;
        this.args = (Value[]) args.toArray(new Value[]{});
        this.implementor = registry.getImplementation(name);
    }
    
    /**
     * Return the functor name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Return the functor aguments as an array of Values
     */
    public Value[] getArgs() {
        return args;
    }
    
    /**
     * Return the length of the functor argument array.
     */
    public int getArgLength() {
        return args.length;
    }
    
    /**
     * Returns true if the functor is fully ground, no variables
     */
    public boolean isGround() {
        for (int i = 0; i < args.length; i++) {
            Value n = args[i];
            if (n instanceof Variable) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns true if the functor is fully ground in the given environment
     */
    public boolean isGround(BindingEnvironment env) {
        for (int i = 0; i < args.length; i++) {
            Value n = args[i];
            if (env.getBinding(args[i]) instanceof Variable) return false;
        }
        return true;
    }
    
    /**
     * Execute the given built in as a body clause.
     * @param context an execution context giving access to other relevant data
     * @return true if the functor has an implementation and that implementation returns true when evaluated
     */
    public boolean evalAsBodyClause(RuleContext context) {
        if (getImplementor() == null) {
            logger.warning("Invoking undefined functor " + getName() + " in " + context.getRule().toShortString());
            return false;
        }
        return implementor.bodyCall(getBoundArgs(context.getEnv()), args.length, context);
    }
    
    /**
     * Execute the given built in as a body clause, only if it is side-effect-free.
     * @param context an execution context giving access to other relevant data
     * @return true if the functor has an implementation and that implementation returns true when evaluated
     */
    public boolean safeEvalAsBodyClause(RuleContext context) {
        if (getImplementor() == null) {
            logger.warning("Invoking undefined functor " + getName() + " in " + context.getRule().toShortString());
            return false;
        }
        if (implementor.isSafe()) {
            return implementor.bodyCall(getBoundArgs(context.getEnv()), args.length, context);
        } else {
            return false;
        }
    }
    
    /**
     * Return a new Value array containing the bound versions of this Functor's arguments
     */
    public Value[] getBoundArgs(BindingEnvironment env) {
        Value[] boundargs = new Value[args.length];
        for (int i = 0; i < args.length; i++) {
            boundargs[i] = env.getBinding(args[i]);
        }
        return boundargs;
    }
    
    /**
     * Return the Builtin that implements this functor
     * @return the Builtin or null if there isn't one
     */
    public Builtin getImplementor() {
        if (implementor == null) {
            implementor = BuiltinRegistry.theRegistry.getImplementation(name);
        }
        return implementor;
    }
    
    /**
     * Set the Builtin that implements this functor.
     */
    public void setImplementor(Builtin implementor) {
        this.implementor = implementor;
    }
    
    /**
     * Printable string describing the functor
     */
    public String toString() {
        StringBuffer buff = new StringBuffer(name);
        buff.append("(");
        for (int i = 0; i < args.length; i++) {
            buff.append(args[i]);
            if (i < args.length - 1) {
                buff.append(" ");
            }
        }
        buff.append(")");
        return buff.toString();
    }

    /**
     * tests that a given Value represents a functor
     */
    public static boolean isFunctor(Value n) {
        if (n == null) return false;
        return n instanceof Literal && ((Literal)n).getDatatype() == FUNCTOR_DATATYPE;
    }
    
    /**
     * Equality is based on structural comparison
     */
    public boolean equals(Object obj) {
        if (obj instanceof Functor) {
            Functor f2 = (Functor)obj;
            if (name.equals(f2.name) && args.length == f2.args.length) {
                for (int i = 0; i < args.length; i++) {
                    if (!args[i].equals(f2.args[i])) return false;
                }
                return true;
            }
        }
        return false;
    }
    
    /** hash function override */
    public int hashCode() {
        return (name.hashCode()) ^ (args.length << 2);
    }
    
    /**
     * Compare Functors, taking into account variable indices.
     * The equality function ignores differences between variables.
     */
    public boolean sameAs(Object o) {
        if (o instanceof Functor) {
            Functor f2 = (Functor)o;
            if (name.equals(f2.name) && args.length == f2.args.length) {
                for (int i = 0; i < args.length; i++) {
                	// TODO: Figure out what this means
                    // if (! Value_RuleVariable.sameValueAs(args[i], f2.args[i])) return false;
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Create a functor and wrap it up as a Literal Value
     * @param name the name of the functor
     * @param args an array of Values defining the arguments, this will not be copied so beware of
     * accidental structure sharing
     */
    public static Value makeFunctorValue(String name, Value[] args) {
        return makeFunctorValue( new Functor( name, args ) );
    }
    
    /**
     * Wrap  a functor as a Literal Value
     * @param f the functor data structure to be wrapped in a Value.
     */
    public static Value makeFunctorValue(Functor f) {
        return new LiteralImpl(f.name, FUNCTOR_DATATYPE);
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
