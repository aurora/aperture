/**
 * 
 */
package org.semanticdesktop.nepomuk.nrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.TestCase;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.trix.TriXWriter;
import org.openrdf.sail.memory.MemoryStore;
import org.semanticdesktop.nepomuk.nrl.inference.Rule;
import org.semanticdesktop.nepomuk.openrdf.InfSail;
import org.semanticdesktop.nepomuk.openrdf.SemanticViewSpecification;
import org.semanticdesktop.nepomuk.openrdf.UnionMemoryStore;

/**
 * @author grimnes
 *
 */
public class BasicInferenceTest extends TestCase {

	URI claudia=new URIImpl("urn:claudia");
	URI dirk=new URIImpl("urn:dirk");
	URI property=new URIImpl("http://example.org/inv");
	
	URI a=new URIImpl("http://example.org/a");
	URI b=new URIImpl("http://example.org/b");
	URI c=new URIImpl("http://example.org/c");

	URI p=new URIImpl("http://example.org/p");
	URI p2=new URIImpl("http://example.org/p2");
	
	URI context=new URIImpl("http://example.org/context");
	URI basegraph=new URIImpl("http://example.org/base");
	URI infgraph=new URIImpl("http://example.org/inf");
	URI ambrosia=new URIImpl("urn:ambrosia");
	URI keith=new URIImpl("urn:keith");
	
	URI graph1=new URIImpl("urn:graph1");
	URI graph2=new URIImpl("urn:graph2");
	URI graph3=new URIImpl("urn:graph3");

	
	private InfSail infsail;
	private MemoryStore base;
	private SailRepository repo;
	private SailRepositoryConnection con;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		base = new MemoryStore();
		File f=File.createTempFile("nrl", "test");
		f.delete();
		f.deleteOnExit();
		//infsail = InfSail.createInfSail(f, UnionStyle.NATIVE);
		infsail=new InfSail(new UnionMemoryStore());
		repo=new SailRepository(infsail);
		repo.initialize();
		con = repo.getConnection();
		con.setAutoCommit(false);
	}
	
	private void setup(String rulesFile) throws Exception {
		InputStream is=getClass().getResourceAsStream("/org/semanticdesktop/nepomuk/nrl/"+rulesFile);
		List<Rule> rules = Rule.parseRules(Rule.rulesParserFromReader(new BufferedReader(new InputStreamReader(is))));
		
		infsail.createSemanticView(basegraph, infgraph, new SemanticViewSpecification("testrules", rules, new URIImpl("urn:test:rules")));
	}

	public void testBasic() throws Exception {
		setup("simplerule.rule");
		con.add(claudia, property, dirk,basegraph);
		con.commit();
		
		infsail.debug();
		
		dump();
		
		System.err.println(con.size());
		assertTrue("Graph must contain original statement ", con.hasStatement(claudia, property, dirk,false, infgraph));
		assertTrue("Graph must contain inverse statement ", con.hasStatement(dirk, property,claudia,false,infgraph));
		assertFalse("Graph must not contain wrong statements ", con.hasStatement(claudia, property, claudia, false, infgraph));
		assertFalse("Graph must not contain wrong statements ", con.hasStatement(dirk, property, dirk, false,infgraph));
		assertEquals("Graph must be the right size: ", 2, con.size(infgraph));
		
	}
	
	public void testDelayed() throws Exception {
		con.add(claudia, property, dirk,basegraph);
		con.commit();
		
		setup("simplerule.rule");

		infsail.debug();
		
		dump();
		
		System.err.println(con.size());
		assertTrue("Graph must contain original statement ", con.hasStatement(claudia, property, dirk,false, infgraph));
		assertTrue("Graph must contain inverse statement ", con.hasStatement(dirk, property,claudia,false,infgraph));
		assertFalse("Graph must not contain wrong statements ", con.hasStatement(claudia, property, claudia, false, infgraph));
		assertFalse("Graph must not contain wrong statements ", con.hasStatement(dirk, property, dirk, false,infgraph));
		assertEquals("Graph must be the right size: ", 2, con.size(infgraph));
		
	}
	
	/** 
	 * This tests two things: 
	 * * Incremental adds of triples.
	 * * rules with two precendent-clauses 
	 *  
	 * @throws Exception
	 */
	public void test2Body() throws Exception { 
		setup("2body.rule");
		con.add(claudia, RDFS.SUBCLASSOF, dirk,basegraph);
		con.commit();
		con.add(dirk, RDFS.SUBCLASSOF, ambrosia,basegraph);
		con.commit();
		infsail.debug();
		
		assertTrue("Graph must contain transitive statement ", con.hasStatement(claudia, RDFS.SUBCLASSOF,ambrosia, false, infgraph));
		assertTrue("Graph must contain original statement ", con.hasStatement(claudia, RDFS.SUBCLASSOF, dirk, false, infgraph));
		assertFalse("Graph must not contain wrong statements ", con.hasStatement(claudia, RDFS.SUBCLASSOF, claudia, false, infgraph));
		assertFalse("Graph must not contain wrong statements ", con.hasStatement(ambrosia, RDFS.SUBCLASSOF, dirk, false, infgraph));
		assertEquals("Graph must be the right size: ", 3,con.size(infgraph));
		
	}
	
	/** 
	 * This tests the case where a previously inferred statement is needed to infer a new one 
	 *  
	 * @throws Exception
	 */
	public void testIncrementeal() throws Exception { 
		setup("2body.rule");
		con.add(claudia, RDFS.SUBCLASSOF, dirk, basegraph);
		con.add(dirk, RDFS.SUBCLASSOF, ambrosia, basegraph);
		con.add(keith, RDFS.SUBCLASSOF, claudia, basegraph);
		con.commit();
		
		infsail.debug();

		assertTrue("Graph must contain inferred statement ", con.hasStatement(keith, RDFS.SUBCLASSOF,ambrosia, false, infgraph));
		assertTrue("Graph must contain original statement ", con.hasStatement(claudia, RDFS.SUBCLASSOF, dirk, false, infgraph));
		assertFalse("Graph must not contain wrong statements ", con.hasStatement(claudia, RDFS.SUBCLASSOF, claudia, false, infgraph));
		assertFalse("Graph must not contain wrong statements ", con.hasStatement(ambrosia, RDFS.SUBCLASSOF, dirk, false, infgraph));
		assertEquals("Graph must be the right size: ", 6, con.size(infgraph));
	}
	
	/** 
	 * Test two rules interacting. 
	 * @throws Exception
	 */
	public void testTwoRules() throws Exception { 
		setup("2rules.rule");
		con.add(claudia, a, dirk,basegraph);
		con.commit();
		
		assertTrue("Graph must contain original statement", con.hasStatement(claudia,a,dirk,false, infgraph));
		assertTrue("Graph must contain first inferred statement ", con.hasStatement(claudia,b,dirk,false, infgraph));
		assertTrue("Graph must contain second inferred statement ", con.hasStatement(claudia,c,dirk,false, infgraph));
		
		assertEquals("Graph must be the right size: ", 3,con.size(infgraph));
	}
	
	/**
	 * test rules with no premise
	 */
	public void testAxiom() throws Exception { 
		setup("axiom.rule");
		
		assertTrue("Graph must contain axiom ", con.hasStatement(a,p,b,false, infgraph));
		
		assertEquals("Graph must be the right size: ", 1,con.size(infgraph));
		;
		
	}
	
	public void test2Head() throws Exception {
		setup("2head.rule");
		con.add(claudia, a, dirk,basegraph);
		con.commit();
		assertTrue("Graph must contain original statement", con.hasStatement(claudia,a,dirk,false, infgraph));
		assertTrue("Graph must contain first inferred statement ", con.hasStatement(claudia,b,dirk,false, infgraph));
		assertTrue("Graph must contain second inferred statement ", con.hasStatement(claudia,c,dirk,false, infgraph));
		
		assertEquals("Graph must be the right size: ", 3,con.size(infgraph));
		;
	}
	
	public void testDoubleCoRecursion() throws Exception {
		setup("corecursion.rule");

		con.add(p, RDFS.SUBPROPERTYOF, RDFS.SUBCLASSOF,basegraph);
		con.add(a, p, b,basegraph);
		con.add(b, p, c,basegraph);
		con.commit();
		
		assertTrue("Graph must contain transitive of superProperty of p statement ", con.hasStatement(a,RDFS.SUBCLASSOF,c,false, infgraph));		
		assertTrue("Graph must contain superProperty of p statement ", con.hasStatement(a,RDFS.SUBCLASSOF,b,false, infgraph));
		
		assertEquals("Graph must be the right size: ", 6,con.size(infgraph));
		;
	}
	
	public void testAxiomPlusRule() throws Exception { 
		setup("axiomPlusRule.rule");
		
		con.add(a,RDFS.ISDEFINEDBY, b,basegraph);
		con.commit();
		assertTrue("Graph must contain superProperty of RDFS.isDefinedBy ", con.hasStatement(a,RDFS.SEEALSO,b,false, infgraph));		
		assertEquals("Graph must be the right size: ", 3,con.size(infgraph));
		;
	}
	
	/** 
	 * Test stacking of views 
	 * @throws Exception
	 */
	public void testStackedInf() throws Exception { 
		setup("2rules1.rule");
		
		InputStream is=getClass().getResourceAsStream("/org/semanticdesktop/nepomuk/nrl/2rules2.rule");
		List<Rule> rules = Rule.parseRules(Rule.rulesParserFromReader(new BufferedReader(new InputStreamReader(is))));
		
		URI infgraph2=new URIImpl("http://example.org/inf2");
		infsail.createSemanticView(infgraph, infgraph2, new SemanticViewSpecification("testrules", rules, new URIImpl("urn:test:rules")));
		
		con.add(claudia, a, dirk,basegraph);
		con.commit();
		
		infsail.debug();
		
		assertTrue("Graph must contain original statement", con.hasStatement(claudia,a,dirk,false, infgraph));
		assertTrue("Graph must contain first inferred statement ", con.hasStatement(claudia,b,dirk,false, infgraph));
		assertTrue("Graph must contain second inferred statement ", con.hasStatement(claudia,c,dirk,false, infgraph2));
		
		assertEquals("Graph must be the right size: ", 3,con.size(infgraph2));
		
		
	}
	
	/** 
	 * This tests inference on top of a union 
	 *  
	 * @throws Exception
	 */
	public void testUnion() throws Exception { 
		
		con.add(claudia, RDFS.SUBCLASSOF, dirk,graph1);
		con.commit();
		con.add(dirk, RDFS.SUBCLASSOF, ambrosia,graph2);
		con.commit();
		
		infsail.importGraph(basegraph, graph1, graph2);
		
		setup("2body.rule");
		
		infsail.debug();
		
		assertTrue("Graph must contain transitive statement ", con.hasStatement(claudia, RDFS.SUBCLASSOF,ambrosia, false, infgraph));
		assertTrue("Graph must contain original statement ", con.hasStatement(claudia, RDFS.SUBCLASSOF, dirk, false, infgraph));
		assertFalse("Graph must not contain wrong statements ", con.hasStatement(claudia, RDFS.SUBCLASSOF, claudia, false, infgraph));
		assertFalse("Graph must not contain wrong statements ", con.hasStatement(ambrosia, RDFS.SUBCLASSOF, dirk, false, infgraph));
		assertEquals("Graph must be the right size: ", 3,con.size(infgraph));
		
	}
	
	public void testRemoveView() throws Exception {
		setup("simplerule.rule");
		con.add(claudia, property, dirk,basegraph);
		con.commit();
		
		infsail.debug();
		
		dump();
		
		infsail.removeGraph(infgraph);
		
		infsail.debug();
		
		dump();
		
		assertEquals("InfGraph must be empty:",con.size(infgraph),0);
		
//		System.err.println(con.size());
//		assertTrue("Graph must contain original statement ", con.hasStatement(claudia, property, dirk,false, infgraph));
//		assertTrue("Graph must contain inverse statement ", con.hasStatement(dirk, property,claudia,false,infgraph));
//		assertFalse("Graph must not contain wrong statements ", con.hasStatement(claudia, property, claudia, false, infgraph));
//		assertFalse("Graph must not contain wrong statements ", con.hasStatement(dirk, property, dirk, false,infgraph));
//		assertEquals("Graph must be the right size: ", 2, con.size(infgraph));
		
	}
	
	public void testRemoveData() throws Exception {
		setup("simplerule.rule");
		con.add(claudia, property, dirk,basegraph);
		con.commit();
		
		infsail.debug();
		
		dump();
		try { 
			infsail.removeGraph(basegraph);
			fail("We should not be able to remove the basegraph that has a view!");
		} catch(Exception e) { 
			// this is expected behaviour
		}
		
		infsail.removeGraph(infgraph);
		infsail.removeGraph(basegraph);
		
		infsail.debug();
		
		dump();
		
		assertEquals("InfGraph must be empty:",con.size(infgraph),0);
		assertEquals("BaseGraph must be empty:",con.size(basegraph),0);
//		System.err.println(con.size());
//		assertTrue("Graph must contain original statement ", con.hasStatement(claudia, property, dirk,false, infgraph));
//		assertTrue("Graph must contain inverse statement ", con.hasStatement(dirk, property,claudia,false,infgraph));
//		assertFalse("Graph must not contain wrong statements ", con.hasStatement(claudia, property, claudia, false, infgraph));
//		assertFalse("Graph must not contain wrong statements ", con.hasStatement(dirk, property, dirk, false,infgraph));
//		assertEquals("Graph must be the right size: ", 2, con.size(infgraph));
		
	}
	
	private void dump() throws RepositoryException, RDFHandlerException {
		con.export(new TriXWriter(System.out));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		con.close(); 
		repo.shutDown();
	}

	
}
