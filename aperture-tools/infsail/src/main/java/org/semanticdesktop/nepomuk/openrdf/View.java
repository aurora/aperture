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

import info.aduna.iteration.CloseableIteration;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.sail.SailException;
import org.semanticdesktop.nepomuk.nrl.inference.Rule;

public class View { 
	
	private static final boolean DEBUG=false;
	
	URI base; 
	public URI name; 

	public List<Rule> rules;
	private Vector<URI> stratas;

	private SemanticViewSpecification viewSpec;

	private InfSail sail;

	public View(URI base, URI name, SemanticViewSpecification viewSpec, InfSail sail) {
		this.base=base;
		this.name=name;
		this.viewSpec=viewSpec;
		this.rules=viewSpec.getRules();
		this.sail=sail;
	}
	
	/* public View(URI base, URI name, List<Rule> rules, Vector<URI> stratas) {
		this.base = base;
		this.name = name;
		this.rules = rules;
		this.stratas=stratas;
	}
	
	public View(URI base, URI name, List<Rule> rules) {
		this(base,name,rules,new Vector<URI>());
	} */

	public List<URI> getStratas(InfSailConnection connection) throws SailException {
		if (stratas==null) {
			stratas = new Vector<URI>();
			CloseableIteration<? extends Statement, SailException> r = connection.getStatements(name, NrlGraphs.HAS_STRATA, null, false);
			while (r.hasNext()) {
				stratas.add((URI) r.next().getObject());
			}
			r.close();
		}
		return (List<URI>) stratas.clone();
	}

	public URI getStrata(InfSailConnection connection) throws SailException {
		if (stratas==null) getStratas(connection);
		URI strata_name=new URIImpl(name.toString()+stratas.size());
		if (DEBUG)
			if (connection.size(strata_name)!=0) throw new SailException("Newly created strata-model is not empty: "+strata_name); 
		return strata_name;
	} 

	public String toString() { 
		return String.format("InfGraph(%s based on %s) [%s]",name,base,stratas);
	}

	/**
	 * Returns a list of all named graphs that make up this Model
	 * I.e. all stratas+base
	 * @return
	 * @throws SailException 
	 */
	public Collection<URI> getAllGraphs(InfSailConnection connection) throws SailException {
		List<URI> allStratas = getStratas(connection);
		allStratas.add(base);
		return allStratas;
	}

	public void addStrata(URI strata) throws SailException {
		stratas.add(strata);
		sail.addStrata(name, strata);
	}

	

}