/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sesame.query.QueryLanguage;
import org.openrdf.sesame.repository.RBNode;
import org.openrdf.sesame.repository.RURI;
import org.openrdf.sesame.repository.RValue;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sailimpl.memory.MemoryStore;
import org.openrdf.util.iterator.CloseableIterator;

/**
 * reads an RDF/S file and creates an Aperture Vocabulary file from it.
 * 
 * usage:
 * <pre>
VocabularyWriter -i &lt;inputrdfsfile&gt; -o &lt;outputdir&lt; --package packagename -a namespace -n nameofjavafile
--package  org.semanticdesktop.aperture.outlook.vocabulary 
-o src/outlook/org/semanticdesktop/aperture/outlook/vocabulary/
-i doc/ontology/data.rdfs
-a http://aperture.semanticdesktop.org/ontology/data#
-n DATA


 * @author sauermann
 * $Id$
 */
public class VocabularyWriter {
	
	String inputRdf = null;
	String outputDir = null;
	String outputFileN = null;
	String ns = null;
	String packagen = null;
	Repository myRepository = null;
	// output stream
	PrintStream outP;
	
	// transform variables
	File inputRdfF;
	File outputDirF;
	File outputF;


	public VocabularyWriter() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void go(String[] args) throws Exception 
	{
		getOpt(args);
		loadOnt();
		writeVocab();
	}
	
	private void loadOnt()  throws Exception  {
		// read
		myRepository = new Repository(new MemoryStore());
		myRepository.initialize();
		RDFFormat informat = RDFFormat.forFileName(inputRdfF.getName(),RDFFormat.RDFXML);
		System.out.println("reading from "+inputRdfF.getAbsolutePath()+" in format "+informat);
		myRepository.add(inputRdfF, ns, informat);

		
	}

	private void writeVocab() throws Exception {
		
		// prepare output
		outP = new PrintStream(outputF);
		try 
		{
			// preamble
			outP.println("package "+packagen+";\n");
			outP.println("import org.openrdf.model.URI;");
			outP.println("import org.openrdf.model.impl.URIImpl;\n");
			outP.println("/**");
			outP.println(" * Vocabulary File. Created by " + VocabularyWriter.class.getName()+" on "+new Date());
			outP.println(" * input file: "+inputRdf);
			outP.println(" * namespace: "+ns);
			outP.println(" */");
			outP.println("public class "+outputFileN+" {");
			outP.println("	public static final String NS = \""+ns+"\";\n");
		
			// iterate through classes
			generateElement(RDFS.CLASS.toString(), false);
			generateElement(OWL.CLASS.toString(), false);
			
			// iterate through properties
			generateElement(RDF.PROPERTY.toString(), true);
			generateElement(OWL.DATATYPEPROPERTY.toString(), true);
			generateElement(OWL.OBJECTPROPERTY.toString(), true);
			
			// end
			outP.println("}");
		} finally {
			outP.close();
		}
		System.out.println("successfully wrote file to "+outputF);
	}
	
	public void generateElement(String type, boolean isProperty) throws Exception
	{
		String queryS = "SELECT x FROM {x} rdf:type {<"+type+">}";
		CloseableIterator queryC = myRepository.evaluateTupleQuery(QueryLanguage.SERQL, queryS);
		try {
		    while (queryC.hasNext()) {
		        List answer = (List)queryC.next();
		        RValue rx = (RValue) answer.get(0);
		        // we do not create constants for blank nodes
		        if (rx instanceof RBNode)
		        	continue;
		        RURI vx = (RURI) rx;
		        String uri = vx.toString();
		        String localName = vx.getLocalName();
		        String javalocalName = asLegalJavaID(localName, !isProperty);
		        outP.println("    /**");
		        printCommentAndLabel(uri);
		        outP.println("     */");
		        outP.println("    public static final URI " + javalocalName +
		        	" = new URIImpl(\"" + uri + "\");\n");
		    }
		}
		finally {
			queryC.close();
		}
	}
	
	/**
	 * print comment and label of the uri to the passed stream
	 * @param uri
	 */
	public void printCommentAndLabel(String uri) throws Exception {
		String queryS = "SELECT l FROM {<"+uri+">} rdfs:label {l}";
		CloseableIterator queryC = myRepository.evaluateTupleQuery(QueryLanguage.SERQL, queryS);
		try {
			String l = "";
		    while (queryC.hasNext()) {
		        List answer = (List)queryC.next();
		        RValue vl = (RValue) answer.get(0);
		        l += vl.toString()+" "; 
		    }
		    if (l.length() > 0)
		    	outP.println("     * Label: "+l);
		}
		finally {
			queryC.close();
		}
		queryS = "SELECT l FROM {<"+uri+">} rdfs:comment {l}";
		queryC = myRepository.evaluateTupleQuery(QueryLanguage.SERQL, queryS);
		try {
			String l = "";
		    while (queryC.hasNext()) {
		        List answer = (List)queryC.next();
		        RValue vl = (RValue) answer.get(0);
		        l += vl.toString()+" "; 
		    }
		    if (l.length() > 0)
		    	outP.println("     * Comment: "+l);
		}
		finally {
			queryC.close();
		}
		queryS = "SELECT d FROM {<"+uri+">} rdfs:domain {d}";
		queryC = myRepository.evaluateTupleQuery(QueryLanguage.SERQL, queryS);
		try {
			String l = "";
		    while (queryC.hasNext()) {
		        List answer = (List)queryC.next();
		        RValue vl = (RValue) answer.get(0);
		        if (vl instanceof RBNode)
		        	continue;
		        RURI vx = (RURI) vl;
		        l += vx.getLocalName()+" "; 
		    }
		    if (l.length() > 0)
		    	outP.println("     * Domain: "+l);
		}
		finally {
			queryC.close();
		}
		
		queryS = "SELECT d FROM {<"+uri+">} rdfs:range {d}";
		queryC = myRepository.evaluateTupleQuery(QueryLanguage.SERQL, queryS);
		try {
			String l = "";
		    while (queryC.hasNext()) {
		        List answer = (List)queryC.next();
		        RValue vl = (RValue) answer.get(0);
		        if (vl instanceof RBNode)
		        	continue;
		        RURI vx = (RURI) vl;
		        l += vx.getLocalName()+" "; 
		    }
		    if (l.length() > 0)
		    	outP.println("     * Range: "+l);
		}
		finally {
			queryC.close();
		}

		
	}

	public void getOpt(String[] args) throws Exception 
	{
		int i = 0;
		if (args.length==0)
			throw new Exception("no arguments given");
		// args
		while ((i<args.length) && args[i].startsWith("-"))
		{
			if (args[i].equals("-i"))
			{
				i++;
				inputRdf = args[i];
			} else if (args[i].equals("-o"))
			{
				i++;
				outputDir = args[i];
			} else if (args[i].equals("-a"))			
			{
				i++;
				ns = args[i];
			} else if (args[i].equals("-n"))			
			{
				i++;
				outputFileN = args[i];
			} else if (args[i].equals("--package"))			
			{
				i++;
				packagen = args[i];
			} else throw new Exception("unknow argument "+args[i]);
			i++;
		}
		
		if (inputRdf == null)
			usage("no input file given");
		if (outputDir == null)
			usage("no output dir given");
		if (ns == null)
			usage("no namespace given");
		if (outputFileN == null)
			usage("no output classname given");
		if (packagen == null)
			usage("no package name given");
		
		// transform variables
		inputRdfF = new File(inputRdf);
		outputDirF = new File(outputDir);
		outputF = new File(outputDir, outputFileN+".java");
	}

	/**
	 * documentation see class, above.
	 */
	public static void main(String[] args) throws Exception {
		new VocabularyWriter().go(args);
		
		
	}
	
    /** 
     * Convert s to a legal Java identifier; capitalise first char if cap is true 
     * this method is copied from jena code.
     * */
    protected String asLegalJavaID( String s, boolean cap ) {
        StringBuffer buf = new StringBuffer();
        int i = 0;

        // treat the first character specially - must be able to start a Java ID, may have to upcase
        try {
            for (; !Character.isJavaIdentifierStart( s.charAt( i )); i++) {}
        }
        catch (StringIndexOutOfBoundsException e) {
            System.err.println( "Could not identify legal Java identifier start character in '" + s + "', replacing with __" );
            return "__";
        }
        buf.append( cap ? Character.toUpperCase( s.charAt( i ) ) : s.charAt( i ) );

        // copy the remaining characters - replace non-legal chars with '_'
        for (++i; i < s.length(); i++) {
            char c = s.charAt( i );
            buf.append( Character.isJavaIdentifierPart( c ) ? c : '_' );
        }
        
        // check standard name
        String result = buf.toString();
        if (result.equals("class"))
        	result = "class_";
        return result;
    }

	private static void usage(String string) throws Exception {
		throw new Exception(string);
		
	}

}

