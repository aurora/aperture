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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;
import org.semanticdesktop.nepomuk.nrl.inference.Rule;
import org.semanticdesktop.nepomuk.openrdf.InfSail;
import org.semanticdesktop.nepomuk.openrdf.SemanticViewSpecification;
import org.semanticdesktop.nepomuk.openrdf.InfSail.UnionStyle;

/**
 * 
 */

/**
 * 
 * This class shows some simple usage-patterns of the NRLInfSail
 * 
 * @author grimnes
 *
 */
public class NrlExample {


	// An example ontology with 1 extra class
	private static final String MYONT = "@prefix foaf: <http://xmlns.com/foaf/0.1/>. \n"+
		"@prefix : <http://example.org/ont#>.\n"+
		"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n"+
		"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n"+
		":Nepomuker a rdfs:Class ; rdfs:label \"a nepomuker\" ; rdfs:subClassOf foaf:Person. ";

	// Two examples instances
	private static final String MYINST = "@prefix foaf: <http://xmlns.com/foaf/0.1/>. \n"+
		"@prefix : <http://example.org/inst#>.\n"+
		"@prefix ont: <http://example.org/ont#>.\n"+
		"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n"+
		"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n"+
		":Claudia a ont:Nepomuker ; rdfs:label \"Claudia\". \n" +
		":Dirk a ont:Nepomuker ; rdfs:label \"Dirk\" ; foaf:knows :Claudia . ";

	// Example rule
	private static final String MY_RULES = "@prefix foaf: <http://xmlns.com/foaf/0.1/> .\n\n"+
		"(?a foaf:knows ?b) -> (?b foaf:knows ?a) . ";

	private Sail base;
	private InfSail infsail;
	private Repository repo;
	private RepositoryConnection con;

	private Resource claudia=new URIImpl("http://example.org/inst#Claudia");
	private Resource dirk=new URIImpl("http://example.org/inst#Dirk");

	public void setupStore() throws SailException, RepositoryException {
		// The base sail can be whatever you want. Here I use an in-memory sail
		base = new MemoryStore();
		// Stack the sails
		infsail=InfSail.createInfSail(base, UnionStyle.REWRITE);
		// and create the repository
		repo=new SailRepository(infsail);
		repo.initialize();
		
		// In Sesame all operations are done through a connection, like in JDBC		
		con = repo.getConnection();
		con.setAutoCommit(true);
	}
	
	public void doStuff() throws RDFParseException, RepositoryException, MalformedURLException, IOException, SailException {
		// create model
		setupStore();
		
		// Read a schema - into a named graph
		URI foaf_named_graph = new URIImpl("http://example.org/Ontology1");
		
		con.add(new URL("http://xmlns.com/foaf/spec/index.rdf").openStream(), 
				"http://xmlns.com/foaf/0.1/",
				RDFFormat.RDFXML, 
				foaf_named_graph);
	
		URI ontology_named_graph = new URIImpl("http://example.org/Ontology2"); 
		
		// read some more ontology stuff 
		con.add(new StringReader(MYONT), 
				"http://example.org", 
				RDFFormat.N3, 
				ontology_named_graph);
		
		URI instances_named_graph = new URIImpl("http://example.org/Instances"); 
		
		// read some more ontology stuff 
		con.add(new StringReader(MYINST), 
				"http://example.org", 
				RDFFormat.N3, 
				instances_named_graph);
		
		
		// Create the knowledge-base
		
		URI kb_named_graph=new URIImpl("http://example.org/KB");
		
		// importGraph uses varargs, i.e. i can give as many arguments as I want
		infsail.importGraph(kb_named_graph, ontology_named_graph, foaf_named_graph, instances_named_graph);
		
		URI inf_named_graph=new URIImpl("http://example.org/infGraph");
		infsail.createSemanticView(kb_named_graph, inf_named_graph, SemanticViewSpecification.getNRL());

		// Now the other inf_named_graph contains everything from FOAF + RDFS inference
		// SemanticView also contains getNRL() which does NRL inference. 
		
		System.out.println("Is Claudia a person in the KB? :"+con.hasStatement(claudia, RDF.TYPE, FOAF.Person, false, kb_named_graph));
		System.out.println("Is Claudia a person in the InfGraph? :"+con.hasStatement(claudia, RDF.TYPE, FOAF.Person, false, inf_named_graph));
		
		
		// Create a custom view
		// rules are same format as Jena
		// This one says that foaf:knows is reflexive
		List<Rule> rules = Rule.parseRules(Rule.rulesParserFromReader(new BufferedReader(new StringReader(MY_RULES))));
		
		URI view_named_graph = new URIImpl("http://example.org/MyView");
		infsail.createSemanticView(kb_named_graph, view_named_graph,
				new SemanticViewSpecification("MyView", rules, new URIImpl("http://example.org/something")));
		
		// Dirk knows Claudia explicitly, but not vice versa 
		System.out.println("Does Claudia know Dirk in the KB? :"+con.hasStatement(claudia, FOAF.knows, dirk, false, kb_named_graph));
		System.out.println("Does Claudia know Dirk in the InfGraph? :"+con.hasStatement(claudia, FOAF.knows, dirk, false, inf_named_graph));
		System.out.println("Does Claudia know Dirk in the View? :"+con.hasStatement(claudia, FOAF.knows, dirk, false, view_named_graph));
		
		// Dump some info about union graphs and views
		infsail.debug();
		
		
		// ALWAYS close your connections. 
		con.close();
	
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws SailException 
	 * @throws MalformedURLException 
	 * @throws RepositoryException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
//		System.err.println(MYONT);
//		System.err.println("............");
//		System.err.println(MYINST);
//		System.err.println("............");
//		System.err.println(MY_RULES);
		
		
		new NrlExample().doStuff();
	}

}
