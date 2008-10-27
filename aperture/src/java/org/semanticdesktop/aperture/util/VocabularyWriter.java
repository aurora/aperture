/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

/**
 * reads an RDF/S file and creates an Aperture Vocabulary file from it.
 * 
 * usage:
 * <pre>
VocabularyWriter -i &lt;inputrdfsfile&gt; -o &lt;outputdir&gt; 
  --package &lt;packagename&gt; -a &lt;namespace&gt; -n &lt;nameofjavafile&gt;
  -namespacestrict &lt;false|true&gt;

 -namespacestrict If true, only elements from within the namespace (-a)
     are generated. Default false.

Example values:
--package  org.semanticdesktop.aperture.outlook.vocabulary 
-o src/outlook/org/semanticdesktop/aperture/outlook/vocabulary/
-i doc/ontology/data.rdfs
-a http://aperture.semanticdesktop.org/ontology/data#
-n DATA


 * @author sauermann
 * $Id$
 */
public class VocabularyWriter {
	
    public static final String [] JAVA_RESERVED_WORDS = {
        "abstract",    "continue",    "for", "new", "switch",
        "assert",   "default", "goto",   "package", "synchronized",
        "boolean", "do",  "if",  "private", "this",
        "break",   "double",  "implements",  "protected",   "throw",
        "byte",    "else",    "import",  "public",  "throws",
        "case",    "enum",    "instanceof",  "return",  "transient", 
        "catch",   "extends", "int", "short",   "try",
        "char",    "final",  "interface",   "static",  "void",
        "class",   "finally", "long",    "strictfp",  "volatile",
        "const",  "float",   "native",  "super",   "while"
    };
    
    public static final Set<String> JAVA_RESERVED_WORDS_SET = prepareJavaKeywordSet(); 
        
    
	String inputRdf = null;
	String outputDir = null;
	String outputFileN = null;
	String ns = null;
	String packagen = null;
	Model myModel = null;
	// output stream
	PrintStream outP;
	
	// transform variables
	File inputRdfF;
	File outputDirF;
	File outputF;
    File outputOntologyFile;
    Boolean namespacestrict = false;
    Syntax syntax;
    
    // avoid duplicates
    HashMap<String, String> uriToLocalName = new HashMap<String, String>();

    // flag that forces the source generation even if the generated class file 
    // is newer than the ontology file
    private boolean forceGeneration;

	public VocabularyWriter() {
		super();
		forceGeneration = false;
	}
	
	private static Set<String> prepareJavaKeywordSet() {
        Set<String> result = new HashSet<String>(100);
        for (String keyword : JAVA_RESERVED_WORDS) {
            result.add(keyword);
        }
        return (Set<String>)Collections.unmodifiableSet(result);
    }

    public void go(String[] args) throws Exception 
	{
		getOpt(args);
		
		if (ontologyUpToDate()) {
		    return;
		}
		loadOnt();
		writeVocab();
	}
	
	private boolean ontologyUpToDate() {
        if (outputF.canRead() && !forceGeneration) {
            long input = inputRdfF.lastModified();
            long output = outputF.lastModified();
            return output >= input;
        } else {
            return false;
        }
    }

    private void loadOnt()  throws Exception  {
		// read
        myModel = RDF2Go.getModelFactory().createModel();
        myModel.open();
        syntax = getSyntax(inputRdf);
		//System.out.println("reading from "+inputRdfF.getAbsolutePath()+" in format " + syntax);
		Reader reader = new BufferedReader(new FileReader(inputRdfF));
		myModel.readFrom(reader, syntax);
		reader.close();
        copyFile(inputRdfF,outputOntologyFile);
	}

	private Syntax getSyntax(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return Syntax.RdfXml;
        } else {
            String extension = fileName.substring(lastDot + 1);
            if ("rdf".equals(extension) || "rdfs".equals(extension) || "owl".equals(extension) || "xml".equals(extension)) {
                return Syntax.RdfXml;
            } else if ("ttl".equals(extension) || "nt".equals(extension) || "n3".equals(extension)) {
                return Syntax.Turtle;
            } else if ("trix".equals(extension)) {
                return Syntax.Trix;
            } else if ("trig".equals(extension)) {
                return Syntax.Trig;
            } 
        }
        return null;
    }

    private void copyFile(File in, File out) {
        byte [] buffer = new byte[4096];
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            if (in.getCanonicalPath().equals(out.getCanonicalPath())) {
                return;
            }
            inputStream = new FileInputStream(in);
            outputStream = new FileOutputStream(out);
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            closeStream(inputStream);
            closeStream(outputStream);
        }
    }

    private void closeStream(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void writeVocab() throws Exception {
		
		// prepare output
		outP = new PrintStream(outputF);
		try 
		{
			// preamble
		    outP.println("/*");
		    outP.println(" * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.");
		    outP.println(" * All rights reserved.");
	        outP.println(" * ");
            outP.println(" * Licensed under the Academic Free License version 3.0.");
            outP.println(" */");
			outP.println("package "+packagen+";");
            outP.println("import java.io.InputStream;");
            outP.println("import java.io.FileNotFoundException;");
            outP.println("import org.ontoware.rdf2go.model.Model;");
            outP.println("import org.ontoware.rdf2go.model.Syntax;");
			outP.println("import org.ontoware.rdf2go.model.node.URI;");
			outP.println("import org.ontoware.rdf2go.model.node.impl.URIImpl;");
            outP.println("import org.semanticdesktop.aperture.util.ResourceUtil;");
			outP.println("/**");
			outP.println(" * Vocabulary File. Created by " + VocabularyWriter.class.getName()+" on "+new Date());
			outP.println(" * input file: "+inputRdf);
			outP.println(" * namespace: "+ns);
			outP.println(" */");
			outP.println("public class "+outputFileN+" {");
            outP.println();
            outP.println("    /** Path to the ontology resource */");
            outP.println("    public static final String " + outputFileN + "_RESOURCE_PATH = ");
            outP.print("      \"" + packagen.replace('.', '/'));
            outP.println("/" + inputRdfF.getName() + "\";");
            outP.println();
            writeGetOntologyMethod();
            outP.println();
            outP.println("    /** The namespace for " + outputFileN + " */");
			outP.println("    public static final URI NS_" + outputFileN + " = new URIImpl(\""+ns+"\");");
		
			// iterate through classes
			generateElement(RDFS.Class, false);
			generateElement(OWL.Class, false);
			
			// iterate through properties
			generateElement(RDF.Property, true);
			generateElement(OWL.DatatypeProperty, true);
			generateElement(OWL.ObjectProperty, true);
			
			// end
			outP.println("}");
		} finally {
			outP.close();
		}
		System.out.println("successfully wrote file to "+outputF);
	}

    private void writeGetOntologyMethod() {
        outP.println("    /**");
        outP.println("     * Puts the " + outputFileN + " ontology into the given model.");
        outP.println("     * @param model The model for the source ontology to be put into.");
        outP.println("     * @throws Exception if something goes wrong.");
        outP.println("     */");
        outP.println("    public static void get" + outputFileN + "Ontology(Model model) {");
        outP.println("        try {");
        outP.println("            InputStream stream = ResourceUtil.getInputStream(" + outputFileN + "_RESOURCE_PATH, " + outputFileN + ".class);");
        outP.println("            if (stream == null) {");
        outP.println("                throw new FileNotFoundException(\"couldn't find resource \" + " + outputFileN + "_RESOURCE_PATH);");
        outP.println("             }");
        outP.println("            model.readFrom(stream, Syntax." + getSyntaxName() + ");");
        outP.println("        } catch(Exception e) {");
        outP.println("             throw new RuntimeException(e);");
        outP.println("        }");
        outP.println("    }");
    }
	
	private String getSyntaxName() {
        if (syntax.equals(Syntax.RdfXml)) {
            return "RdfXml";
        } else if (syntax.equals(Syntax.Ntriples)) {
            return "Ntriples";
        } else if (syntax.equals(Syntax.Trig)) {
            return "Trig";
        } else if (syntax.equals(Syntax.Trix)) {
            return "Trix";
        } else if (syntax.equals(Syntax.Turtle)) {
            return "Turtle";
        } else {
            return null;
        }
    }

    public void generateElement(URI type, boolean isProperty) throws Exception
	{
        ClosableIterator<? extends Statement> queryC = myModel.findStatements(Variable.ANY,RDF.type,type);
        List<URI> classesList = new LinkedList<URI>();
		try {
		    while (queryC.hasNext()) {
		        Statement answer = queryC.next();
		        Resource rx = answer.getSubject();
		        // we do not create constants for blank nodes
		        if (!(rx instanceof URI))
		        	continue;
		        URI vx = (URI) rx;
		        String uri = vx.toString();
		        String localName = getLocalName(vx);
		        //String javalocalName = asLegalJavaID(localName, !isProperty); don't capitalize, what for?
		        String javalocalName = asLegalJavaID(localName, false);
                if (uriToLocalName.containsKey(uri))
                    continue;
                uriToLocalName.put(uri, javalocalName);
                // check namespace strict?
		        if (namespacestrict && !uri.startsWith(ns))
		            continue;
		        outP.println("    /**");
		        printCommentAndLabel(vx);
		        outP.println("     */");
		        outP.println("    public static final URI " + javalocalName +" = new URIImpl(\"" + uri + "\");");
		        classesList.add(vx);
		    }
		}
		finally {
			queryC.close();
		}
		for (URI uri : classesList) {
		    generateElement(uri, false);
		}
	}
	
	/**
	 * The RDF2Go interface doesn't support getting a local name from the URI. I 'borrowed' this snippet
	 * from the Sesame LiteralImpl.
	 */
	private String getLocalName(URI vx) {
		String fullUri = vx.toString();
		int splitIdx = fullUri.indexOf('#');

		if (splitIdx < 0) {
			splitIdx = fullUri.lastIndexOf('/');
		}

		if (splitIdx < 0) {
			splitIdx = fullUri.lastIndexOf(':');
		}

		if (splitIdx < 0) {
			throw new RuntimeException("Not a legal (absolute) URI: " + fullUri);
		}
		return fullUri.substring(splitIdx + 1);
	}

	/**
	 * print comment and label of the uri to the passed stream
	 * @param uri
	 */
	public void printCommentAndLabel(URI uri) throws Exception {

        ClosableIterator<? extends Statement> queryC = myModel.findStatements(uri,RDF.type,Variable.ANY);
		try {
			String l = "";
		    while (queryC.hasNext()) {
		    	Statement answer = queryC.next();;
		        Node vl = answer.getObject();
                if (vl instanceof URI) {
                    URI vlUri = (URI)vl;
                    if (vl.equals(RDFS.Class)) {
                        l = "Class";
                    } else if (vl.equals(RDF.Property)) {
                        l = "Property";
                    } else {
                        l = "Instance of " + vl.toString();
                    }
                } else {
                    l += vl.toString()+" ";
                }
		    }
		    if (l.length() > 0)
		    	outP.println("     * Type: "+l + " <br/>");
		}
		finally {
			queryC.close();
		}
        
        addJavadocLine(uri, RDFS.label, "Label");
        addJavadocLine(uri,RDFS.comment,"Comment");        
        addJavadocLine(uri,RDFS.domain,"Domain");
        addJavadocLine(uri,RDFS.range,"Range");
	}
    
    private void addJavadocLine(URI uri,URI property, String title) {
        ClosableIterator<? extends Statement> queryC = myModel.findStatements(uri,property,Variable.ANY);
        try {
            String l = "";
            while (queryC.hasNext()) {
                Statement answer = queryC.next();;
                Node vl = answer.getObject();
                l += vl.toString()+" "; 
            }
            if (l.length() > 0)
                outP.println("     * " + title + ": "+l + " <br/>");
        }
        finally {
            queryC.close();
        }
    }

	public void getOpt(String[] args) throws Exception 
	{
		int i = 0;
		if (args.length==0) {
			help();
			throw new Exception("no arguments given");
		}
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
            } else if (args[i].equals("-f")) {
                forceGeneration = true;
		    } else if (args[i].equals("-namespacestrict"))         
            {
                i++;
                String s = args[i];
                if ("false".equals(s))
                    namespacestrict = false;
                else if ("true".equals(s))
                    namespacestrict = true;
                else throw new Exception("namespacestrict only allows 'true' or 'false', not '"+s+"'");
                    
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
		System.out.println("input file: " + inputRdf);
        if (!inputRdfF.canRead()) {
            usage("cannot read the input file");
        }
        
		outputDirF = new File(outputDir);
        if (!outputDirF.canWrite()) {
            usage("cannot write to the output directory");
        }
        
		outputF = new File(outputDir, outputFileN+".java");
        outputOntologyFile = new File(outputDir, inputRdfF.getName());        
	}

	private void help() {
		System.err.println("Syntax: java VocabularyWriter -i inputfile -o outputdir -a namespace -n classname --package package ");
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
        StringBuilder buf = new StringBuilder();
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
        if (JAVA_RESERVED_WORDS_SET.contains(result)) {
        	result = result + "_";
        }
        	
        return result;
    }

	private static void usage(String string) throws Exception {
		throw new Exception(string);
	}

}
