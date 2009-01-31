/**
 * 
 */
package org.semanticdesktop.nepomuk.openrdf;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.SailException;

/**
 * @author grimnes
 *
 */
public class SpeedTest {
	
	
	public static void testSpeed(RepositoryConnection c, UnionSail unionsail) throws Exception {
		String data="@prefix foaf: <http://xmlns.com/foaf/0.1/>. \n"+ 
		"<urn:dirk> foaf:name \"dirk\" . ";

	    String[][] queries = {
    		{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x where { ?x foaf:name \"dirk\" . } ",
    			"whole graph", "2000"},
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x where { graph <urn:union> { ?x foaf:name \"dirk\" } . } ",
				"simple unionall", "2000"},
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x where { graph <urn:unionX> { ?x foaf:name \"dirk\" } . } ",
				"simple union none", "0"},
			
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x ?g where { graph ?g { ?x foaf:name \"dirk\" } . } ",
				"graph var", "2000"},
			
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x ?g where { graph ?g { ?x foaf:name \"dirk\" } . FILTER (?g = <urn:union> ) } ",
				"graph filter all", "2000"},
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x ?g where { graph ?g { ?x foaf:name \"dirk\" } . FILTER (?g = <urn:union1> ) } ",
				"graph filter half", "1000"},
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x ?g where { graph ?g { ?x foaf:name \"dirk\" } . FILTER (?g = <urn:union3> ) } ",
				"graph filter nested", "2000"},
				
				
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x ?g where { graph ?g { ?x foaf:name \"dirk\" } . FILTER (?g = <urn:union> || ?g = <urn:unionX> ) } ",
					"graph filter or none", "2000"},
			{"prefix foaf: <http://xmlns.com/foaf/0.1/> \nselect ?x ?g where { graph ?g { ?x foaf:name \"dirk\" } . FILTER (?g = <urn:union1> || ?g = <urn:union2> ) } ",
					"graph filter both halves", "2000"}
	    
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
		
		for(int i = 1; i <= 1000; i++) {
			if((i % 100) == 0)
				System.err.print("Setting up... " + i + "   \r");

			StringBuilder sb = new StringBuilder(data);  
			for(int j = 1; j < i; j += 10) {
				sb.append("\n<urn:dirk" + j + "> foaf:name \"dirk" + j + "\" .");
			}
			URIImpl con = new URIImpl("urn:context" + i);
			c.add(new StringReader(sb.toString()), "http://example.org/", RDFFormat.N3, con);
			all.add(con);
			if (i<=1000) half1.add(con); else half2.add(con);
		}
		System.err.println();
		
		c.commit();

		TupleQuery tq2 = c.prepareTupleQuery(QueryLanguage.SPARQL, queries[0][0]);
		System.err.println("Query " + (0+1) + ": " + queries[0][1]);
		long ctm2 = System.currentTimeMillis();
		TupleQueryResult qr2 = tq2.evaluate();
		int res2 = 0;
		while(qr2.hasNext()) {
			String s = qr2.next().toString();
			res2++;
			//System.err.println(s);
		}
		System.err.println("Elapsed: " + (System.currentTimeMillis() - ctm2) + "ms");
		System.err.println(res2 + " results, " + queries[0][2] + " expected");
		qr2.close();
		
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
			int res = 0;
			while(qr.hasNext()) {
				String s = qr.next().toString();
				res++;
				//System.err.println(s);
			}
			System.err.println("Elapsed: " + (System.currentTimeMillis() - ctm) + "ms");
			System.err.println(res + " results, " + queries[iQuery][2] + " expected");
			qr.close();
		}

	}
	
	public static void main(String args[]) throws Exception {
		RepositoryConnection c; 
		File f;
		try {
			f = File.createTempFile("test", "rdf");
		} catch (IOException e) {
			throw new SailException(e);
		}
		f.delete();
		System.err.println(f);
		UnionNativeStore sail = new UnionNativeStore(f,"spoc, spco, scpo, scop, sopc, socp, "+
													   "ospc, oscp, ocps, ocsp, opsc, opcs, "+
													   "posc, pocs, pcos, pcso, psoc, psco, "+
													   "cosp, cops, cspo, csop, cpso, cpos");
		//NativeStore base = new NativeStore(f);
		//UnionSail sail = new UnionMaterialisingSail(base);
		
		SailRepository r = new SailRepository(sail);
		r.initialize();
		
		
		c=r.getConnection();
		c.setAutoCommit(false);
		testSpeed(c, sail);
		
		c.close();
		r.shutDown();
		
	}
	
}
