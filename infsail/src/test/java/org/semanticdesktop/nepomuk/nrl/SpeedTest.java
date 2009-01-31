package org.semanticdesktop.nepomuk.nrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;
import org.semanticdesktop.nepomuk.nrl.inference.Rule;
import org.semanticdesktop.nepomuk.openrdf.InfSail;
import org.semanticdesktop.nepomuk.openrdf.SemanticViewSpecification;
import org.semanticdesktop.nepomuk.openrdf.UnionMemoryStore;

public class SpeedTest {
	
	URI basegraph=new URIImpl("http://example.org/base");
	URI infgraph=new URIImpl("http://example.org/inf");


	private List<Rule> rules;
	private MemoryStore base;
	private InfSail infsail;
	private SailRepository repo;
	private SailRepositoryConnection con;
	
	private void setup(String rulesFile) throws Exception {
		InputStream is=getClass().getResourceAsStream("/org/semanticdesktop/nepomuk/nrl/"+rulesFile);
		List<Rule> rules = Rule.parseRules(Rule.rulesParserFromReader(new BufferedReader(new InputStreamReader(is))));
		
		infsail.createSemanticView(basegraph, infgraph, new SemanticViewSpecification("testrules", rules, new URIImpl("urn:test:rules")));
	}
	
	public long test() throws Exception {
		

		base = new UnionMemoryStore();
		infsail=new InfSail(base);
		repo=new SailRepository(infsail);
		repo.initialize();
		con = repo.getConnection();
		con.setAutoCommit(false);
		
		con.add(new FileInputStream(new File("data/foaf.rdfs")),"",RDFFormat.RDFXML, basegraph);
		con.commit();
		//memmodel.readFrom(new FileInputStream(new File("data/foaf.rdfs"))););

		long start = System.currentTimeMillis();
		setup("rdfs.rules");
		
		
		System.err.println(con.size(basegraph));
		System.err.println(con.size(infgraph));
		long t = System.currentTimeMillis()-start;
		System.err.println(String.format("Took %.3f seconds.",(t)/1000.0));
		infsail.debug();

		con.close(); 
		repo.shutDown();
		return t;
	}
	
	public static void main(String [] args) throws Exception  {
		long res=0; 
		float IT=10;
		for (int i=0; i<IT; i++)
			res+=new SpeedTest().test();
		System.err.println(String.format("Average: %.3f",(res/IT)/1000.0));
	}
}
