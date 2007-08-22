/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;
import org.semanticdesktop.aperture.vocabulary.FRESNEL;
import org.semanticdesktop.aperture.vocabulary.SOURCEFORMAT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A simple generator for the DataSource convenience classes */
public class DataSourceClassGenerator {

    private static Logger log = LoggerFactory.getLogger(DataSourceClassGenerator.class);

    /** Array of java reserved words */
    public static final String[] JAVA_RESERVED_WORDS = { "abstract", "continue", "for", "new", "switch",
            "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this",
            "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public",
            "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int",
            "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long",
            "strictfp", "volatile", "const", "float", "native", "super", "while" };

    /** Set of java reserved words */
    public static final Set<String> JAVA_RESERVED_WORDS_SET = prepareJavaKeywordSet();

    String inputRdfFilePath = null;

    String outputDirectoryPath = null;

    String outputFileName = null;

    String classUri = null;

    String packageName = null;

    String vocabularyClassName = null;

    Model myModel = null;

    // output stream
    PrintStream outputStream;

    // transform variables
    File inputRdfFile;

    File outputDirFile;

    File outputFile;

    Boolean namespacestrict = false;

    Syntax inputFileSyntax;

    boolean domainBoundariesGenerated = false;

    // avoid duplicates
    HashMap<String, String> uriToLocalName = new HashMap<String, String>();

    // flag that forces the source generation even if the generated class file
    // is newer than the ontology file
    @SuppressWarnings("unused")
    private boolean forceGeneration;

    /** constructor */
    public DataSourceClassGenerator() {
        super();
        forceGeneration = false;
    }

    private static Set<String> prepareJavaKeywordSet() {
        Set<String> result = new HashSet<String>(100);
        for (String keyword : JAVA_RESERVED_WORDS) {
            result.add(keyword);
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * Does the job.
     * 
     * @param args command line arguments
     * @throws Exception if something goes wrong
     */
    public void go(String[] args) throws Exception {
        getOpt(args);

        if (ontologyUpToDate()) {
            return;
        }
        loadOnt();
        writeDataSourceClass();
    }

    private boolean ontologyUpToDate() {
        if (outputFile.canRead() && !forceGeneration) {
            long input = inputRdfFile.lastModified();
            long output = outputFile.lastModified();
            return output >= input;
        }
        else {
            return false;
        }
    }

    private void loadOnt() throws Exception {
        // read
        myModel = RDF2Go.getModelFactory().createModel();
        myModel.open();
        inputFileSyntax = getSyntax(inputRdfFilePath);
        // System.out
        // .println("reading from " + inputRdfFile.getAbsolutePath() + " in format " + inputFileSyntax);
        Reader reader = new BufferedReader(new FileReader(inputRdfFile));
        myModel.readFrom(reader, inputFileSyntax);
        DATASOURCE.getDATASOURCEOntology(myModel);
        SOURCEFORMAT.getSOURCEFORMATOntology(myModel);
        reader.close();
    }

    private Syntax getSyntax(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return Syntax.RdfXml;
        }
        else {
            String extension = fileName.substring(lastDot + 1);
            if ("rdf".equals(extension) || "rdfs".equals(extension)) {
                return Syntax.RdfXml;
            }
            else if ("ttl".equals(extension) || "nt".equals(extension) || "n3".equals(extension)) {
                return Syntax.Turtle;
            }
            else if ("trix".equals(extension)) {
                return Syntax.Trix;
            }
            else if ("trig".equals(extension)) {
                return Syntax.Trig;
            }
        }
        return null;
    }

    private void writeDataSourceClass() throws Exception {

        // prepare output
        outputStream = new PrintStream(outputFile);
        try {
            // preamble
            outputStream.println("package " + packageName + ";");
            outputStream.println("import " + URI.class.getName() + ";");
            outputStream.println("import " + Node.class.getName() + ";");
            outputStream.println("import " + ModelUtil.class.getName() + ";");
            outputStream.println("import org.semanticdesktop.aperture.datasource.DataSource;");
            outputStream.println("import org.semanticdesktop.aperture.datasource.base.DataSourceBase;");
            outputStream.println("import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;");
            outputStream.println("import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;");
            outputStream.println("import java.util.Collection;");
            outputStream.println("import java.util.List;");
            outputStream.println("import java.util.LinkedList;");
            outputStream.println();
            outputStream.println("/**");
            outputStream.println(" * Data source class file. Created by "
                    + DataSourceClassGenerator.class.getName() + " on " + new Date());
            outputStream.println(" * input file: " + inputRdfFilePath);
            outputStream.println(" * class uri: " + classUri);
            outputStream.println(" */");
            outputStream.println("public class " + outputFileName + " extends DataSourceBase {");
            outputStream.println();
            generateGetTypeMethod();

            URI fresnelLensURI = getFresnelLensURI(myModel, classUri);
            List<URI> optionsList = getConfigurationOptionsList(fresnelLensURI);

            for (URI uri : optionsList) {
                generateElement(uri);
            }

            // end
            outputStream.println("}");
        }
        finally {
            outputStream.close();
        }
        // System.out.println("successfully wrote file to " + outputFile);
    }

    private void generateGetTypeMethod() {
        outputStream.println("    /**");
        outputStream.println("     * @see DataSource#getType()");
        outputStream.println("     */");
        outputStream.println("    public URI getType() {");
        outputStream.println("        return " + vocabularyClassName + "." + outputFileName + ";");
        outputStream.println("    }");
    }

    private void generateElement(URI uri) throws Exception {

        Node commentNode = ModelUtil.getPropertyValue(myModel, uri, RDFS.comment);
        String comment = ((commentNode != null) ? commentNode.toString() : "");
        String localName = asLegalJavaID(getLocalName(uri.toString()), false);
        String capitalizedLocalName = asLegalJavaID(getLocalName(uri.toString()), true);
        Node rangeNode = ModelUtil.getPropertyValue(myModel, uri, RDFS.range);
        URI range = ((rangeNode != null) ? rangeNode.asURI() : null);
        String javaRangeType = getJavaRangeType(range);
        String currentVocabularyClassName = getVocabularyClassName(uri);

        URI widgetType = getWidgetType(uri);

        if (javaRangeType.equals("DomainBoundaries")) {
            addDomainBoundaries();
            return;
        }

        if (widgetType.equals(SOURCEFORMAT.MultipleTextFieldWidget)) {
            generateMultiValuedProperty(localName, capitalizedLocalName, comment, javaRangeType,
                currentVocabularyClassName);
        }
        else if (widgetType.equals(SOURCEFORMAT.ComboBoxWidget)) {
            generateEnumProperty(localName, capitalizedLocalName, comment, currentVocabularyClassName, uri);
        }
        else {
            generateSingleValuedProperty(localName, capitalizedLocalName, comment, javaRangeType,
                currentVocabularyClassName);
        }

        uriToLocalName.put(uri.toString(), localName);

    }

    private void generateSingleValuedProperty(String localName, String capitalizedLocalName, String comment,
            String javaRangeType, String currentVocabularyClassName) {
        outputStream.println();
        outputStream.println("    /**");
        outputStream.println("     * Returns the " + comment);
        outputStream.println("     * ");
        outputStream.println("     * @return the " + comment + " or null if no value has been set");
        outputStream.println("     * @throws NullPointerException if no configuration has been set, use");
        outputStream
                .println("     *             {@link #setConfiguration(RDFContainer)} before calling this method");
        outputStream.println("     */");
        outputStream.println("     public " + javaRangeType + " get" + capitalizedLocalName + "() {");
        outputStream.println("          return getConfiguration().get" + javaRangeType + "("
                + currentVocabularyClassName + "." + localName + ");");
        outputStream.println("     }");

        outputStream.println();
        outputStream.println("    /**");
        outputStream.println("     * Sets the " + comment);
        outputStream.println("     * ");
        outputStream.println("     * @param " + localName + " " + comment
                + ", can be null in which case any previous setting will be removed");
        outputStream.println("     * @throws NullPointerException if no configuration has been set, use");
        outputStream
                .println("     *             {@link #setConfiguration(RDFContainer)} before calling this method");
        outputStream.println("     */");
        outputStream.println("     public void set" + capitalizedLocalName + "(" + javaRangeType + " "
                + localName + ") {");
        outputStream.println("         if ( " + localName + " == null) {");
        outputStream.println("             getConfiguration().remove(" + currentVocabularyClassName + "."
                + localName + ");");
        outputStream.println("         } else {");
        outputStream.println("             getConfiguration().put(" + currentVocabularyClassName + "."
                + localName + "," + localName + ");");
        outputStream.println("         }");
        outputStream.println("     }");

    }

    private void generateMultiValuedProperty(String localName, String capitalizedLocalName, String comment,
            String javaRangeType, String currentVocabularyClassName) {
        outputStream.println();
        outputStream.println("    /**");
        outputStream.println("     * Returns a collection of all values of " + comment);
        outputStream.println("     * ");
        outputStream.println("     * @return a collection of all values of " + comment
                + " the collection may be empty if no values have been set");
        outputStream.println("     * @throws NullPointerException if no configuration has been set, use");
        outputStream
                .println("     *             {@link #setConfiguration(RDFContainer)} before calling this method");
        outputStream.println("     */");
        outputStream.println("     public Collection<" + javaRangeType + "> getAll" + capitalizedLocalName
                + "s() {");
        outputStream
                .println("          Collection<Node> collection = (Collection<Node>)getConfiguration().getAll("
                        + currentVocabularyClassName + "." + localName + ");");
        outputStream.println("          List<" + javaRangeType + "> result = new LinkedList<" + javaRangeType
                + ">();");
        outputStream.println("          for (Node node : collection) {");
        outputStream.println("              " + javaRangeType + " object = (" + javaRangeType
                + ")ModelUtil.convertNode(node," + javaRangeType + ".class);");
        outputStream.println("              if (object != null) {");
        outputStream.println("                   result.add(object);");
        outputStream.println("              }");
        outputStream.println("          }");
        outputStream.println("          return result;");
        outputStream.println("     }");

        outputStream.println();
        outputStream.println("    /**");
        outputStream.println("     * Sets the " + comment);
        outputStream.println("     * ");
        outputStream.println("     * @param " + localName + " " + comment
                + ", can be null in which case any previous setting will be removed");
        outputStream.println("     * @throws NullPointerException if no configuration has been set, use");
        outputStream
                .println("     *             {@link #setConfiguration(RDFContainer)} before calling this method");
        outputStream.println("     */");
        outputStream.println("     public void set" + capitalizedLocalName + "(" + javaRangeType + " "
                + localName + ") {");
        outputStream.println("         if ( " + localName + " == null) {");
        outputStream.println("             getConfiguration().remove(" + currentVocabularyClassName + "."
                + localName + ");");
        outputStream.println("         } else {");
        outputStream.println("             getConfiguration().put(" + currentVocabularyClassName + "."
                + localName + "," + localName + ");");
        outputStream.println("         }");
        outputStream.println("     }");
    }

    private void generateEnumProperty(String propertyLocalName, String capitalizedLocalName, String comment,
            String currentVocabularyClassName, URI propertyUri) {

        Node rangeNode = ModelUtil.getPropertyValue(myModel, propertyUri, RDFS.range);
        URI range = ((rangeNode != null) ? rangeNode.asURI() : null);
        
        String rangeLocalName = getLocalName(range.toString());

        Map<String, String> valuesMap = getValuesMap(propertyUri, myModel);
        outputStream.println();
        outputStream.println("    /**");
        outputStream.println("     * Enum of possible values of the " + propertyLocalName + " property");
        outputStream.println("     */");
        outputStream.println("     public static enum " + rangeLocalName + " {");

        Set<Entry<String, String>> entrySet = valuesMap.entrySet();

        Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String uriLocalName = getLocalName(entry.getKey());
            outputStream.println("         /** Constant representing " + entry.getKey() + "*/");
            outputStream.println("         " + uriLocalName + ((iterator.hasNext()) ? "," : ";"));
        }
        outputStream.println();

        outputStream.println("         public static " + rangeLocalName + " fromUri(URI uri) {");

        iterator = entrySet.iterator();

        if (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String comboBoxEntryLocalName = getLocalName(entry.getKey());
            System.out.println("A ku ku ");
            outputStream.println("             if (uri == null) {");
            outputStream.println("                 return null;");
            outputStream.println("             }");
            outputStream.println("             else if (uri.equals(" + currentVocabularyClassName + "."
                    + comboBoxEntryLocalName + ")) {");
            outputStream.println("                 return " + comboBoxEntryLocalName + ";");
            outputStream.println("             }");
            while (iterator.hasNext()) {
                entry = iterator.next();
                comboBoxEntryLocalName = getLocalName(entry.getKey());
                outputStream.println("             else if (uri.equals(" + currentVocabularyClassName + "."
                        + comboBoxEntryLocalName + ")) {");
                outputStream.println("                 return " + comboBoxEntryLocalName + ";");
                outputStream.println("             }");
            }
            outputStream.println("             else {");
            outputStream.println("                 return null;");
            outputStream.println("             }");
        }

        outputStream.println("         }");

        outputStream.println("         public URI toUri() {");

        iterator = entrySet.iterator();

        if (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String comboBoxEntryLocalName = getLocalName(entry.getKey());
            outputStream.println("             if (this.equals(" + comboBoxEntryLocalName + ")) {");
            outputStream.println("                 return " + currentVocabularyClassName + "." + comboBoxEntryLocalName + ";");
            outputStream.println("             }");
            while (iterator.hasNext()) {
                entry = iterator.next();
                comboBoxEntryLocalName = getLocalName(entry.getKey());
                outputStream.println("             else if (this.equals(" + comboBoxEntryLocalName + ")) {");
                outputStream.println("                 return " + currentVocabularyClassName + "." + comboBoxEntryLocalName + ";");
                outputStream.println("             }");
            }
            outputStream.println("             else {");
            outputStream.println("                 return null;");
            outputStream.println("             }");
        }

        outputStream.println("         }");

        outputStream.println("     }");

        outputStream.println();
        outputStream.println("    /**");
        outputStream.println("     * Returns the " + comment);
        outputStream.println("     * ");
        outputStream.println("     * @return the " + comment + " or null if no value has been set");
        outputStream.println("     * @throws NullPointerException if no configuration has been set, use");
        outputStream
                .println("     *             {@link #setConfiguration(RDFContainer)} before calling this method");
        outputStream.println("     */");
        outputStream.println("     public " + rangeLocalName + " get" + capitalizedLocalName + "() {");
        outputStream.println("          return " + rangeLocalName + ".fromUri(getConfiguration().getURI(" + currentVocabularyClassName + "." + propertyLocalName + "));");
        outputStream.println("     }");
        outputStream.println();
        outputStream.println();
        outputStream.println("    /**");
        outputStream.println("     * Sets the " + comment);
        outputStream.println("     * ");
        outputStream.println("     * @param " + propertyLocalName + " " + comment + ", can be null in which case any previous setting will be removed");
        outputStream.println("     * @throws NullPointerException if no configuration has been set, use");
        outputStream.println("     *             {@link #setConfiguration(RDFContainer)} before calling this method");
        outputStream.println("     */");
        outputStream.println("     public void set" + capitalizedLocalName + "(" + rangeLocalName + " "+ propertyLocalName + ") {");
        outputStream.println("         if ( " + propertyLocalName + " == null) {");
        outputStream.println("             getConfiguration().remove(" + currentVocabularyClassName + "." + propertyLocalName + ");");
        outputStream.println("         } else {");
        outputStream.println("             getConfiguration().put(" + currentVocabularyClassName + "." + propertyLocalName + "," + propertyLocalName + ".toUri());");
        outputStream.println("         }");
        outputStream.println("     }");

    }

    private URI getWidgetType(URI uri) {
        String query = "" + 
                "PREFIX fresnel: " + FRESNEL.NS_FRESNEL.toSPARQL() + "\n " + 
                "PREFIX sourceformat: " + SOURCEFORMAT.NS_SOURCEFORMAT.toSPARQL() + "\n " + 
                "PREFIX rdf: <" + RDF.RDF_NS + ">\n " + 
                "SELECT ?x " + 
                "WHERE { ?y fresnel:propertyFormatDomain " + uri.toSPARQL() + " . " + 
                "        ?y sourceformat:valueWidget ?z . " + 
                "        ?z rdf:type ?x . }";

        ClosableIterator<QueryRow> iterator = null;
        try {
            QueryResultTable table = myModel.sparqlSelect(query);
            iterator = table.iterator();
            if (iterator.hasNext()) {
                QueryRow row = iterator.next();
                URI result = (URI) row.getValue("x");
                if (result != null) {
                    return result;
                }
                else {
                    log.error("The widget for " + uri + " has a type that is not an uri");
                }
            }
            else {
                log.warn("No widget found for " + uri);
            }
        }
        finally {
            iterator.close();
        }
        return null;
    }

    private String getVocabularyClassName(URI range) {
        if (range.toString().startsWith(DATASOURCE.NS_DATASOURCE.toString())) {
            return DATASOURCE.class.getName();
        }
        else {
            return vocabularyClassName;
        }
    }

    private void addDomainBoundaries() {
        if (!domainBoundariesGenerated) {
            outputStream.println();
            outputStream.println("    /**");
            outputStream.println("     * Returns the domain boundaries for this data source");
            outputStream.println("     * ");
            outputStream.println("     * @return the domain boundaries for this data source");
            outputStream.println("     * @throws NullPointerException if no configuration has been set, use");
            outputStream
                    .println("     *             {@link #setConfiguration(RDFContainer)} before calling this method");
            outputStream.println("     */");
            outputStream.println("     public DomainBoundaries getDomainBoundaries() {");
            outputStream
                    .println("          return ConfigurationUtil.getDomainBoundaries(getConfiguration());");
            outputStream.println("     }");

            outputStream.println();
            outputStream.println("    /**");
            outputStream.println("     * Sets the domain boundaries for this data source");
            outputStream.println("     * ");
            outputStream
                    .println("     * @param domainBoundaries the domain boundaries, can be null in which case any previous setting will be removed");
            outputStream.println("     * @throws NullPointerException if no configuration has been set, use");
            outputStream
                    .println("     *             {@link #setConfiguration(RDFContainer)} before calling this method");
            outputStream.println("     */");
            outputStream.println("     public void setDomainBoundaries(DomainBoundaries domainBoundaries) {");
            outputStream.println("          if (domainBoundaries == null) {");
            outputStream.println("              DomainBoundaries emptyBoundaries = new DomainBoundaries();");
            outputStream
                    .println("              ConfigurationUtil.setDomainBoundaries(emptyBoundaries,getConfiguration());");
            outputStream.println("          } else {");
            outputStream
                    .println("              ConfigurationUtil.setDomainBoundaries(domainBoundaries,getConfiguration());");
            outputStream.println("          }");
            outputStream.println("     }");
            domainBoundariesGenerated = true;
        }
    }

    private String getJavaRangeType(URI range) {
        if (range == null || range.equals(RDFS.Literal) || range.equals(XSD._string)) {
            return "String";
        }
        else if (range.equals(XSD._int) || range.equals(XSD._integer)) {
            return "Integer";
        }
        else if (range.equals(XSD._long)) {
            return "Long";
        }
        else if (range.equals(XSD._boolean)) {
            return "Boolean";
        }
        else if (range.equals(DATASOURCE.Pattern)) {
            return "DomainBoundaries";
        }
        else {
            return "URI";
        }
    }

    /**
     * The RDF2Go interface doesn't support getting a local name from the URI. I 'borrowed' this snippet from
     * the Sesame LiteralImpl.
     */
    private String getLocalName(String vx) {
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

    private void getOpt(String[] args) throws Exception {
        int i = 0;
        if (args.length == 0) {
            help();
            throw new Exception("no arguments given");
        }
        // args
        while ((i < args.length) && args[i].startsWith("-")) {
            if (args[i].equals("-i")) {
                i++;
                inputRdfFilePath = args[i];
            }
            else if (args[i].equals("-o")) {
                i++;
                outputDirectoryPath = args[i];
            }
            else if (args[i].equals("-c")) {
                i++;
                classUri = args[i];
            }
            else if (args[i].equals("-n")) {
                i++;
                vocabularyClassName = args[i];
            }
            else if (args[i].equals("--package")) {
                i++;
                packageName = args[i];
            }
            else if (args[i].equals("-f")) {
                forceGeneration = true;
            }
            else if (args[i].equals("-namespacestrict")) {
                i++;
                String s = args[i];
                if ("false".equals(s))
                    namespacestrict = false;
                else if ("true".equals(s))
                    namespacestrict = true;
                else
                    throw new Exception("namespacestrict only allows 'true' or 'false', not '" + s + "'");

            }
            else
                throw new Exception("unknow argument " + args[i]);
            i++;
        }

        outputFileName = getLocalName(classUri);

        if (inputRdfFilePath == null)
            usage("no input file given");
        if (outputDirectoryPath == null)
            usage("no output dir given");
        if (classUri == null)
            usage("no class uri given");
        if (packageName == null)
            usage("no package name given");

        // transform variables
        inputRdfFile = new File(inputRdfFilePath);
        // System.out.println("input file: " + inputRdfFilePath);
        if (!inputRdfFile.canRead()) {
            usage("cannot read the input file");
        }

        outputDirFile = new File(outputDirectoryPath);
        if (!outputDirFile.canWrite()) {
            usage("cannot write to the output directory");
        }

        outputFile = new File(outputDirectoryPath, outputFileName + ".java");
    }

    private void help() {
        System.err
                .println("Syntax: java VocabularyWriter -i inputfile -o outputdir -c classuri --package package ");
    }

    /**
     * documentation see class, above.
     * 
     * @param args command line arguments
     * @throws Exception if something goes wrong
     */
    public static void main(String[] args) throws Exception {
        new DataSourceClassGenerator().go(args);
    }

    /**
     * Convert s to a legal Java identifier; capitalise first char if cap is true this method is copied from
     * jena code.
     */
    protected String asLegalJavaID(String s, boolean cap) {
        StringBuilder buf = new StringBuilder();
        int i = 0;

        // treat the first character specially - must be able to start a Java ID, may have to upcase
        try {
            for (; !Character.isJavaIdentifierStart(s.charAt(i)); i++) {
                // do nothing
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            System.err.println("Could not identify legal Java identifier start character in '" + s
                    + "', replacing with __");
            return "__";
        }
        buf.append(cap ? Character.toUpperCase(s.charAt(i)) : s.charAt(i));

        // copy the remaining characters - replace non-legal chars with '_'
        for (++i; i < s.length(); i++) {
            char c = s.charAt(i);
            buf.append(Character.isJavaIdentifierPart(c) ? c : '_');
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

    private URI getFresnelLensURI(Model model, String typeString) {
        URI typeUri = new URIImpl(typeString);
        try {
            Resource resource = ModelUtil.getSingleSubjectWithProperty(model, FRESNEL.classLensDomain,
                typeUri);
            if (resource != null) {
                return resource.asURI();
            }
            else {
                return null;
            }
        }
        catch (ClassCastException me) {
            log.warn("Couldn't fetch the fresnel lens uri", me);
        }
        return null;
    }

    private List<URI> getConfigurationOptionsList(URI fresnelLensURI) {
        List<URI> resultList = new LinkedList<URI>();
        try {
            Resource listNode = ModelUtil.getPropertyValue(myModel, fresnelLensURI, FRESNEL.showProperties)
                    .asResource();
            while (true) {
                if (listNode.equals(RDF.nil)) {
                    break;
                }
                URI firstUri = ModelUtil.getPropertyValue(myModel, listNode, RDF.first).asURI();
                resultList.add(firstUri);
                listNode = ModelUtil.getPropertyValue(myModel, listNode, RDF.rest).asResource();
            }
            return resultList;
        }
        catch (ClassCastException me) {
            log.warn("Couldn't fetch the list of the configuration " + "options", me);
        }
        return null;
    }

    private Map<String, String> getValuesMap(URI propertyUri, Model model) {
        Map<String, String> resultMap = new TreeMap<String, String>();
        URI propertyFormatUri = ModelUtil.getSingleSubjectWithProperty(myModel, FRESNEL.propertyFormatDomain,
            propertyUri).asURI();
        try {
            Resource widgetBlankNode = ModelUtil.getPropertyValue(model, propertyFormatUri,
                SOURCEFORMAT.valueWidget).asResource();
            List<Node> entriesList = ModelUtil.getAllPropertyValues(model, widgetBlankNode,
                SOURCEFORMAT.hasEntry);
            for (Node node : entriesList) {
                Resource resource = node.asResource();
                Node comboBoxLabelNode = ModelUtil.getPropertyValue(model, resource, SOURCEFORMAT.label);
                Node comboBoxValueNode = ModelUtil.getPropertyValue(model, resource, SOURCEFORMAT.value);
                resultMap.put(comboBoxValueNode.toString(), comboBoxLabelNode.toString());
            }
        }
        catch (ClassCastException me) {
            log.warn("Couldn't get the values map", me);
        }
        return resultMap;
    }

}
