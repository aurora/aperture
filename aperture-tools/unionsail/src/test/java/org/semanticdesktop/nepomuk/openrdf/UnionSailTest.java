package org.semanticdesktop.nepomuk.openrdf;

import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;

public abstract class UnionSailTest extends TestCase {
	
	private URI A=new URIImpl("urn:a");
	private URI B=new URIImpl("urn:b");
	private URI C=new URIImpl("urn:c");
	
	private URI P=new URIImpl("urn:p");
	private URI O=new URIImpl("urn:o");	
	private URI Q=new URIImpl("urn:q");
	
	private URI S=new URIImpl("urn:s");
	private URI T=new URIImpl("urn:t");
	private URI U=new URIImpl("urn:u");
	private Literal alabel=new LiteralImpl("a");
	
	private SailRepository repo;
	private MemoryStore base;
	private UnionSail unionsail;
	private SailRepositoryConnection c;

	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		base = new MemoryStore();
		unionsail=createUnionSail(base);
		repo=new SailRepository(unionsail);
		repo.initialize();
		c = repo.getConnection();
	}

	protected abstract UnionSail createUnionSail(MemoryStore base) throws SailException; 

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		c.close();
		repo.shutDown();
	}

	public void testBasic() throws Exception { 
		c.add(A,P,B,S);
		assertTrue("Repo must contain statement", c.hasStatement(A,P,B,false,S));
		c.close();
	}
	
	public void testSingleUnion() throws Exception { 
		c.add(A,P,B,S);
		unionsail.debug();
		RepositoryResult<Resource> cid = c.getContextIDs();
		while(cid.hasNext()) {
			System.err.println(cid.next());
		}
		unionsail.createUnion(T,S);
		assertTrue("Repo must contain statement in orig context. ", c.hasStatement(A,P,B,false,S));
		assertTrue("Repo must contain statement in union", c.hasStatement(A,P,B,false,T));
		c.close();
	}
	
	public void testSingleUnion2() throws Exception { 
		c.add(A,P,B,S);
		c.add(A,P,C,U);
		unionsail.createUnion(T,S);
		assertTrue("Repo must contain statement in orig context. ", c.hasStatement(A,P,B,false,S));
		assertTrue("Repo must contain statement in union", c.hasStatement(A,P,B,false,T));
		
		assertTrue("Repo must contain statement in orig context. ", c.hasStatement(A,P,C,false,U));
		assertFalse("Repo must not contain statement in plain context.", c.hasStatement(A,P,C,false,S));
		assertFalse("Repo must not contain statement in union.", c.hasStatement(A,P,C,false,T));
		c.close();
	}
	
	public void testChainUnion() throws Exception { 
		c.add(A,P,B,S);
		unionsail.createUnion(T,S);
		unionsail.createUnion(U,T);
		assertTrue("Repo must contain statement in orig context. ", c.hasStatement(A,P,B,false,S));
		assertTrue("Repo must contain statement in union", c.hasStatement(A,P,B,false,T));
		assertTrue("Repo must contain statement in chained union", c.hasStatement(A,P,B,false,U));
		c.close();
	}
	
	public void testDelete() throws Exception { 
		c.add(A,P,B,S);
		unionsail.createUnion(T,S);
		assertTrue("Repo must contain statement in orig context. ", c.hasStatement(A,P,B,false,S));
		assertTrue("Repo must contain statement in union", c.hasStatement(A,P,B,false,T));
		
		c.remove(A,P,B,S);

		assertFalse("Repo must not contain statement in orig context. ", c.hasStatement(A,P,B,false,S));
		assertFalse("Repo must not contain statement in union", c.hasStatement(A,P,B,false,T));
		
		c.close();
	}
	
	public void testDeleteUnion() throws Exception { 
		c.add(A,P,B,S);
		unionsail.createUnion(T,S);
		assertTrue("Repo must contain statement in orig context. ", c.hasStatement(A,P,B,false,S));
		assertTrue("Repo must contain statement in union", c.hasStatement(A,P,B,false,T));

		try { 
			c.remove(A,P,B,T);
			fail("Should not be able to delete from union");
		} catch(Exception ex) {
			// ok 
		}
		
		c.close();
	}
	
	public void testSize() throws Exception { 
		for (int i=0; i<1000; i++)
			c.add(A,P,new URIImpl("urn:test:"+i),S);
		unionsail.createUnion(T,S);
		assertEquals("Original context must have right size", 1000, c.size(S));		
		assertEquals("Union must have right size", 1000, c.size(T));
		c.close();
	}
	
	public void testTwoUnion() throws Exception { 
		c.add(A,P,B,S);
		c.add(A,P,C,U);
		unionsail.createUnion(T,S,U);
		assertTrue("Repo must contain statement in orig context. ", c.hasStatement(A,P,B,false,S));
		assertTrue("Repo must contain statement in orig context. ", c.hasStatement(A,P,C,false,U));
		assertTrue("Repo must contain statement in union", c.hasStatement(A,P,B,false,T));
		assertTrue("Repo must contain statement in union", c.hasStatement(A,P,C,false,T));
		assertEquals("Union must have right size", 2, c.size(T));
		c.close();
	}
	
	public void testTwoSize() throws Exception { 
		for (int i=0; i<1000; i++)
			c.add(A,P,new URIImpl("urn:test1:"+i),S);
		for (int i=0; i<1000; i++)
			c.add(A,P,new URIImpl("urn:test2:"+i),U);
		
		unionsail.createUnion(T,S,U);
		assertEquals("Original context must have right size", 1000, c.size(S));
		assertEquals("Original context must have right size", 1000, c.size(U));
		assertEquals("Union must have right size", 2000, c.size(T));
		c.close();
	}
	
	public void testQuery() throws Exception { 
		c.add(A,P,B,S);
		unionsail.createUnion(T,S);
		//assertTrue("Repo must contain statement", c.hasStatement(A,P,B,false,S));
		//QueryParserUtil.getQueryParserRegistry().add(new SeRQLParserFactory());
		//String queryString="SELECT ?x WHERE { GRAPH ?G { ?x ?y ?z } FILTER ( ?G =  <"+S+"> || ?G=<"+U+"> )}";
		String queryString="SELECT ?x WHERE { GRAPH <"+S+"> { ?x ?y ?z } }";
		System.err.println(queryString);
		TupleQuery q = c.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult r = q.evaluate();
		assertTrue(r.hasNext());
		BindingSet b = r.next();
		assertEquals(A,b.getBinding("x").getValue());
		assertFalse(r.hasNext());
		r.close();
		
		queryString="SELECT ?x WHERE { GRAPH <"+T+"> { ?x ?y ?z } }";
		System.err.println(queryString);
		q = c.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		r = q.evaluate();
		assertTrue(r.hasNext());
		b = r.next();
		assertEquals(A,b.getBinding("x").getValue());
		assertFalse(r.hasNext());
		r.close();
		
		c.close();
	}
	
	public void testQuery2() throws Exception { 
		
		c.add(A,P,B,U);
		
		unionsail.createUnion(T,S);
		//assertTrue("Repo must contain statement", c.hasStatement(A,P,B,false,S));
		
		//String queryString="SELECT ?x FROM NAMED <"+S+"> WHERE { GRAPH <"+S+"> { ?x ?y ?z } }" ;
		String queryString="SELECT ?x FROM NAMED <"+S+"> WHERE { GRAPH <"+S+"> { ?x ?y ?z } }";
				
		System.err.println(queryString);
		TupleQuery q = c.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult r = q.evaluate();
		assertFalse(r.hasNext());
		r.close();
		
		queryString="SELECT ?x FROM <"+T+"> WHERE { GRAPH <"+T+"> { ?x ?y ?z } }";
		System.err.println(queryString);
		q = c.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		r = q.evaluate();
		assertFalse(r.hasNext());
		r.close();
		
		c.close();
	}
	
	public void testQuery3() throws Exception { 
		c.add(A,P,B,S);
		c.add(B,Q,C,T);
		unionsail.createUnion(U,S,T);

		String queryString="SELECT ?x WHERE { GRAPH <"+S+"> { ?a ?p ?b . ?b ?q ?x. } }";
		
		System.err.println(queryString);
		TupleQuery q = c.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult r = q.evaluate();
		if (r.hasNext()) {
			System.err.println(r.getBindingNames());
			while (r.hasNext()) {
				System.err.println(r.next());
			}
			fail("Query shouldn't have any matches.");
		}
		
		r.close();
		
		queryString="SELECT ?x WHERE { GRAPH <"+T+"> { ?a ?p ?b . ?b ?q ?x. } }";
		
		System.err.println(queryString);
		q = c.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		r = q.evaluate();
		assertFalse(r.hasNext());
		r.close();
		
		queryString="SELECT ?x WHERE { GRAPH <"+U+"> { ?a ?p ?b . ?b ?q ?x. } }";
		System.err.println(queryString);
		q = c.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		r = q.evaluate();
		assertTrue(r.hasNext());
		BindingSet b = r.next();
		assertEquals(C,b.getBinding("x").getValue());
		assertFalse(r.hasNext());
		r.close();
		
		
		c.close();
	}
	
	public void dontTestSpeed() throws Exception {
		String data="@prefix foaf: <http://xmlns.com/foaf/0.1/>. \n"+ 
		"<urn:dirk> foaf:name \"dirk\" . ";

	    String[][] queries = {
    		{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x where { ?x foaf:name \"dirk\" . } ",
    			"whole graph", "2000"},
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x where { graph <urn:union> { ?x foaf:name \"dirk\" } . } ",
				"simple unionall", "1"},
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x where { graph <urn:unionX> { ?x foaf:name \"dirk\" } . } ",
				"simple unionnone", "1"},
			
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x ?g where { graph ?g { ?x foaf:name \"dirk\" } . } ",
				"graph var", "2000"},
			
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x ?g where { graph ?g { ?x foaf:name \"dirk\" } . FILTER (?g = <urn:union> ) } ",
				"graph filter all", "1"},
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x ?g where { graph ?g { ?x foaf:name \"dirk\" } . FILTER (?g = <urn:union1> ) } ",
				"graph filter half", "1"},
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x ?g where { graph ?g { ?x foaf:name \"dirk\" } . FILTER (?g = <urn:union3> ) } ",
				"graph filter nested", "1"},
				
				
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x ?g where { graph ?g { ?x foaf:name \"dirk\" } . FILTER (?g = <urn:union> || ?g = <urn:unionX> ) } ",
					"graph filter or none", "1"},
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x ?g where { graph ?g { ?x foaf:name \"dirk\" } . FILTER (?g = <urn:union1> || ?g = <urn:union2> ) } ",
					"graph filter both halves", "1"}
	    
	    };
	
//		File f = File.createTempFile("test", "rdf");
//		f.delete();
//		
//		Sail nativeSail=new NativeStore(f);
//		Repository r=new SailRepository(nativeSail);
//		r.initialize();
//		
//		RepositoryConnection c = r.getConnection();
//		c.setAutoCommit(false);
		List<URI> all=new Vector<URI>(); 
		List<URI> half1=new Vector<URI>(); 
		List<URI> half2=new Vector<URI>(); 
		
		for(int i = 1; i <= 2000; i++) {
			if((i % 100) == 0)
				System.err.print("Setting up... " + i + "   \r");

			StringBuilder sb = new StringBuilder(data);  
			for(int j = 1; j < i; j += 10) {
				sb.append("\n<urn:dirk" + j + "> foaf:name \"dirk" + j + "\" .");
			}
			URIImpl con = new URIImpl("urn:context" + i);
			c.add(new StringReader(sb.toString()), "http://example.org/", RDFFormat.N3, con);
			all.add(con);
			if (i<1000) half1.add(con); else half2.add(con);
		}
		System.err.println();
		
		c.commit();

		URI[] contexts=all.toArray(new URI[all.size()]);
		unionsail.createUnion(new URIImpl("urn:union"), contexts);
		unionsail.createUnion(new URIImpl("urn:union1"), half1.toArray(new URI[half1.size()]));
		unionsail.createUnion(new URIImpl("urn:union2"), half2.toArray(new URI[half2.size()]));
		
		// This should be equal to union
		unionsail.createUnion(new URIImpl("urn:union3"),new URIImpl("urn:union1"),new URIImpl("urn:union2"));
		
		
		for(int iQuery = 0; iQuery < queries.length; iQuery++) {
			TupleQuery tq = c.prepareTupleQuery(QueryLanguage.SPARQL, queries[iQuery][0]);
			System.err.println("Query " + (iQuery+1) + ": " + queries[iQuery][1]);
			long ctm = System.currentTimeMillis();
			TupleQueryResult qr = tq.evaluate();
			System.err.println("Elapsed: " + (System.currentTimeMillis() - ctm) + "ms");
			int res = 0;
			while(qr.hasNext()) {
				String s = qr.next().toString();
				res++;
				//System.err.println(s);
			}
			System.err.println(res + " results, " + queries[iQuery][2] + " expected");
			qr.close();
		}

	}
}
