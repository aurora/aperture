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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDFS;
import org.semanticdesktop.nepomuk.nrl.inference.NRL;
import org.semanticdesktop.nepomuk.nrl.inference.Rule;

public class SemanticViewSpecification { 
	
	private static final String NRL_RULES =  "/org/semanticdesktop/nepomuk/nrl/inference/rules/nrl.rules";
	private static final String RDFS_RULES = "/org/semanticdesktop/nepomuk/nrl/inference/rules/rdfs.rules";

	
	private static List<Rule> nrl_rules;
	private static SemanticViewSpecification nrl;
	private static SemanticViewSpecification rdfs;
	private static List<Rule> rdfs_rules;
	
	private String name;
	private List<Rule> rules;
	public URI uri;
	
	public SemanticViewSpecification(String name, List<Rule> rules, URI uri) {
		this.name=name;
		this.rules=rules;
		this.uri=uri;
	}

	public static SemanticViewSpecification getNRL() {
		if (nrl==null) {
			InputStream is=SemanticViewSpecification.class.getResourceAsStream(NRL_RULES);
			nrl_rules = Rule.parseRules(Rule.rulesParserFromReader(new BufferedReader(new InputStreamReader(is))));
			nrl=new SemanticViewSpecification("NRL", nrl_rules, NRL.NS_NRL_GEN);
		}
		return nrl;
	}
	
	/** 
	 * 
	 * @return
	 */
	public static SemanticViewSpecification getRDFS() {
		if (rdfs==null) {
			InputStream is=SemanticViewSpecification.class.getResourceAsStream(RDFS_RULES);
			rdfs_rules = Rule.parseRules(Rule.rulesParserFromReader(new BufferedReader(new InputStreamReader(is))));
			rdfs=new SemanticViewSpecification("RDFS", rdfs_rules, new URIImpl(RDFS.NAMESPACE));
		}
		return nrl;
	}

	public List<Rule> getRules() {
		return rules;
	}
}
