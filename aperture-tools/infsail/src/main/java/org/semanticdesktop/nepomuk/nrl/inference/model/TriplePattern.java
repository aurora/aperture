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
 * File:        TriplePattern.java
 * Created by:  Dave Reynolds
 * Created on:  18-Jan-03
 * 
 * (c) Copyright 2003, 2004, 2005, 2006 Hewlett-Packard Development Company, LP
 * [See end of file]
 * $Id: TriplePattern.java,v 1.26 2006/03/22 13:52:53 andy_seaborne Exp $
 *****************************************************************/
package org.semanticdesktop.nepomuk.nrl.inference.model;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;


/**
 * Datastructure which defines a triple pattern as used in simple
 * rules and in find interfaces. 
 * <p>
 * Wildcards are recorded by using Node_RuleVariable entries rather than
 * nulls because they can be named. If a null is specified that is
 * converted to a variable of name "". Note that whilst some engines might simply
 * require Node_Variables the forward engine requires variables represented using
 * the more specialized subclass - Node_RuleVariable.</p>
 * <p>
 * It would make more sense to have TriplePattern subclass Triple
 * but that is final for some strange reason.</p>
 * 
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
 * @version $Revision: 1.26 $ on $Date: 2006/03/22 13:52:53 $
 */
public class TriplePattern implements ClauseEntry {

    /** The subject element of the pattern */
    protected Value subject;
    
    /** The predicate element of the pattern */
    protected Value predicate;
    
    /** The object element of the pattern */
    protected Value object;
    
    /**
     * Constructor - builds a pattern from three nodes,
     * use Node_RuleVariables as variables, use a variable
     * with an empty name as a wildcard, can also use null
     * as a wildcard.
     */
    public TriplePattern(Value subject, Value predicate, Value object) {
        this.subject   = normalize(subject);
        this.predicate = normalize(predicate);
        this.object    = normalize(object);
    }
    
  
    
    /**
     * Constructor - builds a dgenerate pattern from a simple triple.
     * This would be much easier if we merged Triples and TriplePatterns!
     */
    public TriplePattern(Statement match) {
        this.subject   = normalize(match.getSubject());
        this.predicate = normalize(match.getPredicate());
        this.object    = normalize(match.getObject());
    }
    
    /**
     * Returns the object.
     * @return Value
     */
    public Value getObject() {
        return object;
    }

    /**
     * Returns the predicate.
     * @return Value
     */
    public Value getPredicate() {
        return predicate;
    }

    /**
     * Returns the subject.
     * @return Value
     */
    public Value getSubject() {
        return subject;
    }

  

    /**
     * Return the triple pattern as a triple 
     */
    public Value getSubjectMatch() {
    	return toMatch(subject);
    }
    
    public Value getPredicateMatch() {
    	return toMatch(predicate);
    }
    
    public Value getObjectMatch() {
    	return toMatch(object);
    }
    
    
    /**
     * Compare two patterns for compatibility - i.e. potentially unifiable.
     * Two patterns are "compatible" in the sense we mean here
     * if all their ground terms match. A variable in either pattern
     * can match a ground term or a variable in the other. We are not,
     * currently, checking for multiple occurances of the same variable.
     * Functor-valued object literals are treated as a special case which 
     * are only checked for name/arity matching.
     */
    public boolean compatibleWith(TriplePattern pattern) {
        boolean ok = subject instanceof Variable || pattern.subject instanceof Variable || subject.equals(pattern.subject);
        if (!ok) return false;
        ok =  predicate instanceof Variable || pattern.predicate instanceof Variable || predicate.equals(pattern.predicate);
        if (!ok) return false;
        if (object instanceof Variable || pattern.object instanceof Variable) return true;
      
        return object.equals(pattern.object);
    }
    
    /**
     * Test if a pattern is just a variant of this pattern. I.e. it is the same
     * up to variable renaming. This takes into account multiple occurances
     * of the same variable.
     */
    public boolean variantOf(TriplePattern pattern) {
        Map vmap = new HashMap();
        if ( ! variantOf(subject, pattern.subject, vmap) ) return false;
        if ( ! variantOf(predicate, pattern.predicate, vmap) ) return false;
      
        return variantOf(object, pattern.object, vmap);
    }
    
    /**
     * Test if one Value is a variant of another give a table of variable matches.
     */
    private boolean variantOf(Value n, Value p, Map vmap) {
        if (n instanceof Variable) {
            if (p instanceof Variable) {
                Object nMatch = vmap.get(n);
                if (nMatch == null) {
                    // First match of these pairs
                    vmap.put(n, p);
                    return true;
                } else {
                    return nMatch == p;
                }
            } else {
                return false;
            }
        } else {
            return n.equals(p);
        }
    }
    
    /**
     * Check a pattern to see if it is legal, used to exclude backchaining goals that
     * could never be satisfied. A legal pattern cannot have literals in the subject or
     * predicate positions and is not allowed nested functors in the object.
     */
    public boolean isLegal() {
        if (subject instanceof Literal || predicate instanceof Literal) return false;
        if (Functor.isFunctor(subject)) return false;
        if (Functor.isFunctor(object)) {
            Value[] args = ((Functor)object).getArgs();
            for (int i = 0; i < args.length; i++) {
                if (Functor.isFunctor(args[i])) return false;  
            }
        }
        return true;
    }
    
    /**
     * Compare two patterns and return true if arg is a more
     * specific (more grounded) version of this one.
     * Does not handle functors.
     */
    public boolean subsumes(TriplePattern arg) {
        return (subject instanceof Variable  || subject.equals(arg.subject))
            && (predicate instanceof Variable || predicate.equals(arg.predicate))
            && (object instanceof Variable || object.equals(arg.object));
    }
    
    /**
     * Test if the pattern is ground, contains no variables.
     */
    public boolean isGround() {
        if (subject instanceof Variable || predicate instanceof Variable || object instanceof Variable) return false;
        if (Functor.isFunctor(object)) {
            return ((Functor)object).isGround();
        }
        return true;
    }
    
    /**
     * Printable string
     */
    public String toString() {
        return simplePrintString(subject) + 
                " @" + simplePrintString(predicate) + 
                " " + simplePrintString(object);
    }
    
    /**
     * Simplified printable name for a triple
     */
    public static String simplePrintString(Statement t) {
        return simplePrintString(t.getSubject()) + 
                " @" + simplePrintString(t.getPredicate()) + 
                " " + simplePrintString(t.getObject());
    }

    /**
     * Simplified printable name for a Value
     */
    public static String simplePrintString(Value n) {
        if (n instanceof URI) {
            String uri = n.toString();
            int split = uri.lastIndexOf('#');
            if (split == -1) {
                split = uri.lastIndexOf('/');
                if (split == -1) split = -1;
            }
            String ns = uri.substring(0, split+1);
            String prefix = "";
            if (ns.equals(RDF.NAMESPACE)) {
                prefix = "rdf:";
            } else if (ns.equals(RDFS.NAMESPACE)) {
                prefix = "rdfs:";
            }
            return prefix + uri.substring(split+1);
        } else {
            return n.toString();
        }
    }
            
    /**
     * Convert any null wildcards to Value_RuleVariable wildcards.
     */
    private static Value normalize(Value Value) {
        if (Value == null) return Variable.WILD;
        return Value;
    }
            
    /**
     * Convert any Value_RuleVariable wildcards to null. This loses
     * the variable named but is used when converting a singleton
     * pattern to a TripleMtch
     */
    private static Value toMatch(Value Value) {
        return Value instanceof Variable ? null : Value;
    }
    
    /** 
     * Equality override - used so that TriplePattern variants (same to within variable renaming) test as equals
     */
    public boolean equals(Object o) {
//        return o instanceof TriplePattern && 
//                subject.equals(((TriplePattern)o).subject) &&
//                predicate.equals(((TriplePattern)o).predicate) &&
//                object.equals(((TriplePattern)o).object);
        return o instanceof TriplePattern &&
                ValueEqual(subject, ((TriplePattern)o).subject) &&
                ValueEqual(predicate, ((TriplePattern)o).predicate) &&
                ValueEqual(object, ((TriplePattern)o).object);
    }
    
    /** Helper - equality override on Values */
    private boolean ValueEqual(Value n1, Value n2) {
        if ((n1 instanceof Variable) && (n2 instanceof Variable)) {
            return true;
        } else {
            return n1.equals(n2);
        }
    }
        
    /** hash function override */
    public int hashCode() {
        int hash = 0;
        if (!(subject instanceof Variable)) hash ^= (subject.hashCode() >> 1);
        if (!(predicate instanceof Variable)) hash ^= predicate.hashCode();
        if (!(object instanceof Variable)) hash ^= (object.hashCode() << 1);
        return hash;
//        return (subject.hashCode() >> 1) ^ predicate.hashCode() ^ (object.hashCode() << 1);
    }
    
    /**
     * Compare triple patterns, taking into account variable indices.
     * The equality function ignores differences between variables.
     */
    public boolean sameAs(Object o) {
        if (! (o instanceof TriplePattern) ) return false;
        TriplePattern other = (TriplePattern) o;
        return Variable.sameNodeAs(subject, other.subject) && Variable.sameNodeAs(predicate, other.predicate) && Variable.sameNodeAs(object, other.object);
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

