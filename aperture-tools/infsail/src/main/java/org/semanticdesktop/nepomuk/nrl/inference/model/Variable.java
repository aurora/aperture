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
package org.semanticdesktop.nepomuk.nrl.inference.model;

import org.openrdf.model.Resource;
import org.openrdf.model.Value;



/**
 * @author grimnes
 *
 * WHY oh WHY is variable in rdf2go final? 
 * and it's not even properly final, it's just got a single private empty constructor. 
 *  
 */
public class Variable implements Resource {
	
	public static final Variable WILD = new Variable("*", -1);
	
	private String name;
	public int index;

	public Variable(String name) {
		super();
		this.name=name;
	}
	
	public int getIndex() { 
		return index;
	}

	public Variable(String name, int index) {
		this.name=name;
		this.index=index;
	}

	
	public int hashCode() {
		return 0;
	}

	public boolean equals(Object other) {
		if (other instanceof Variable)
			// TODO: Do something
			return true;
		else
			return false;
	}

	public int compareTo(Value other) {
		if (other instanceof Variable) {
			return this.hashCode() - ((Variable) other).hashCode();
		} else {
			// sort by type
			return NodeUtils.compareByType(this, other);
		}
	}

	  /**
     * Compare two nodes, taking into account variable indices.
     */
    public static boolean sameNodeAs(Value n, Value m) {
        if (n instanceof Variable) {
            if (m instanceof Variable) {
                return ((Variable)n).index == ((Variable)m).index;
            } else {
                return false;
            }
        } else {
            return n.equals(m);
        }
    }

	public Variable cloneValue() {
		return new Variable(name,index);
	}
	
	public String toString() { 
		return "Var:"+name+"<"+index+">";
	}

	public String stringValue() {
		return name;
	}

}
