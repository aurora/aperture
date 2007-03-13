/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.UpdateException;
import org.semanticdesktop.aperture.util.ModelUtil;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * ConfigurationUtil provides utility methods for setting and retrieving standard DataSource configuration
 * properties from an RDFContainer.
 */
public class ConfigurationUtil {

    private ConfigurationUtil() {
    // prevent instantiation
    }

    public static void setName(String name, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.name, name);
    }

    public static String getName(RDFContainer configuration) {
        return configuration.getString(DATASOURCE_GEN.name);
    }

    public static void setRootUrl(String url, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.rootUrl, url);
    }

    public static String getRootUrl(RDFContainer configuration) {
        return configuration.getString(DATASOURCE_GEN.rootUrl);
    }

    public static void setRootFolder(String folder, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.rootFolder, folder);
    }

    public static String getRootFolder(RDFContainer configuration) {
        return configuration.getString(DATASOURCE_GEN.rootFolder);
    }

    public static void setHostname(String hostname, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.hostname, hostname);
    }

    public static String getHostname(RDFContainer configuration) {
        return configuration.getString(DATASOURCE_GEN.hostname);
    }

    public static void setPort(int port, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.port, port);
    }

    public static Integer getPort(RDFContainer configuration) {
        return configuration.getInteger(DATASOURCE_GEN.port);
    }

    public static void setBasepath(String basepath, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.basepath, basepath);
    }

    public static String getBasepath(RDFContainer configuration) {
        return configuration.getString(DATASOURCE_GEN.basepath);
    }

    public static void setBasepaths(Collection basepaths, RDFContainer configuration) {
        // first remove all old base paths
        Collection oldPaths = getBasepaths(configuration);
        Iterator iterator = oldPaths.iterator();
        URI id = configuration.getDescribedUri();

        while (iterator.hasNext()) {
            String oldPath = (String) iterator.next();
            try {
                configuration.remove(configuration.getValueFactory().createStatement(id, DATASOURCE_GEN.basepath,
                    configuration.getValueFactory().createLiteral(oldPath)));
            }
            catch (ModelException e) {
                throw new RuntimeException(e);
            }
        }

        // now add the new paths
        iterator = basepaths.iterator();
        while (iterator.hasNext()) {
            String path = (String) iterator.next();
            configuration.add(DATASOURCE_GEN.basepath, path);
        }
    }

    public static Collection getBasepaths(RDFContainer configuration) {
        ArrayList result = new ArrayList();

        Collection values = configuration.getAll(DATASOURCE_GEN.basepath);
        Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            Node value = (Node) iterator.next();
            if (value instanceof Literal) {
                result.add(((Literal) value).getValue());
            }
        }

        return result;
    }

    public static void setUsername(String username, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.username, username);
    }

    public static String getUsername(RDFContainer configuration) {
        return configuration.getString(DATASOURCE_GEN.username);
    }

    public static void setPassword(String password, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.password, password);
    }

    public static String getPassword(RDFContainer configuration) {
        return configuration.getString(DATASOURCE_GEN.password);
    }

    public static void setMaximumDepth(int maximumDepth, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.maximumDepth, maximumDepth);
    }

    public static Integer getMaximumDepth(RDFContainer configuration) {
        return configuration.getInteger(DATASOURCE_GEN.maximumDepth);
    }

    public static void setMaximumByteSize(long maximumSize, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.maximumSize, maximumSize);
    }

    public static Long getMaximumByteSize(RDFContainer configuration) {
        return configuration.getLong(DATASOURCE_GEN.maximumSize);
    }

    public static void setIncludeHiddenResources(boolean value, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.includeHiddenResources, value);
    }

    public static Boolean getIncludeHiddenResourceS(RDFContainer configuration) {
        return configuration.getBoolean(DATASOURCE_GEN.includeHiddenResources);
    }

    public static void setIncludeEmbeddedResources(boolean value, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.includeEmbeddedResources, value);
    }

    public static Boolean getIncludeEmbeddedResourceS(RDFContainer configuration) {
        return configuration.getBoolean(DATASOURCE_GEN.includeEmbeddedResources);
    }

    public static void setConnectionSecurity(String securityType, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.connectionSecurity, securityType);
    }

    public static String getConnectionSecurity(RDFContainer configuration) {
        return configuration.getString(DATASOURCE_GEN.connectionSecurity);
    }

    public static void setSSLFileName(String sslfilename, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.sslFileName, sslfilename);
    }

    public static String getSSLFileName(RDFContainer configuration) {
        return configuration.getString(DATASOURCE_GEN.sslFileName);
    }

    public static void setSSLPassword(String sslpword, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.sslFilePassword, sslpword);
    }

    public static String getSSLPassword(RDFContainer configuration) {
        return configuration.getString(DATASOURCE_GEN.sslFilePassword);
    }

    public static Boolean getIncludeInbox(RDFContainer configuration) {
        return configuration.getBoolean(DATASOURCE_GEN.includeInbox);
    }

    public static void setIncludeInbox(boolean value, RDFContainer configuration) {
        configuration.put(DATASOURCE_GEN.includeInbox, value);
    }

    /**
     * Stores the specified DomainBoundaries in the specified configuration. This method will overwrite a
     * previously set DomainBoundaries but this is only guaranteed to work correctly when it has also been
     * stored through this same method.
     */
    public static void setDomainBoundaries(DomainBoundaries boundaries, RDFContainer configuration) {
        // fetch the Model and the DataSource ID
        URI id = configuration.getDescribedUri();
        Model model = configuration.getModel();

        try {
            deletePatternStatements(id, DATASOURCE.includePattern, model);
            deletePatternStatements(id, DATASOURCE.excludePattern, model);

            // add statements reflecting the specified DomainBoundaries
            if (boundaries != null) {
                addPatternStatements(id, boundaries.getIncludePatterns(), DATASOURCE_GEN.includePattern,
                    model);
                addPatternStatements(id, boundaries.getExcludePatterns(), DATASOURCE_GEN.excludePattern,
                    model);
            }
        }
        catch (ModelException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This is a translation of an SERQL query into triple-pattern operations. The original query looked as
     * follows:
     * 
     * <pre>
     * CONSTRUCT * 
     * FROM 
     * 		 {&lt;id&gt;} &lt;predicate&gt;    {p},
     * 	 {p}    rdf:type       {t},
     * 	 {p}    rdf:value      {v},
     * 	[{p}    data:condition {c}]     			
     * USING NAMESPACE 
     * 		rdf = &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt;, 
     * 	rdfs = &lt;http://www.w3.org/2000/01/rdf-schema#&gt;,
     * 	data = &lt;DATASOURCE.NS&gt;
     * </pre>
     */
    private static void deletePatternStatements(URI id, URI predicate, Model model) throws ModelException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            List<Statement> statementsToRemove = new LinkedList<Statement>();
            iterator = model.findStatements(id, predicate, Variable.ANY);

            while (iterator.hasNext()) {
                Statement mainStatement = iterator.next();
                Node object = mainStatement.getObject();
                if (object instanceof Resource) {
                    Resource objectResource = (Resource) object;
                    List<Statement> types = findStatements(objectResource, RDF.type, model);
                    List<Statement> values = findStatements(objectResource, RDF.value, model);
                    List<Statement> conditions = findStatements(objectResource, DATASOURCE.condition, model);
                    if (types.size() > 0 && values.size() > 0) {
                        statementsToRemove.add(mainStatement);
                        statementsToRemove.addAll(types);
                        statementsToRemove.addAll(values);
                        statementsToRemove.addAll(conditions);
                    }
                }
            }
            // LOGGER.info(query);
            model.removeAll(statementsToRemove.iterator());
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }
    }

    private static List<Statement> findStatements(Resource resource, URI predicate, Model model)
            throws ModelException {
        List<Statement> result = new LinkedList<Statement>();
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(resource, predicate, Variable.ANY);
            while (iterator.hasNext()) {
                result.add(iterator.next());
            }
        }
        finally {
            if (iterator != null) {
                iterator.close();
            }
        }
        return result;
    }

    private static void addPatternStatements(URI sourceID, List patterns, URI predicate, Model model)
            throws ModelException {

        // loop over all patterns
        Iterator iterator = patterns.iterator();
        while (iterator.hasNext()) {
            UrlPattern pattern = (UrlPattern) iterator.next();

            // create a BNode for this pattern
            BlankNode patternResource = model.createBlankNode();

            // connect it to the DataSource's Resource using the appropriate pattern predicate
            model.addStatement(ModelUtil.createStatement(model, sourceID, predicate, patternResource));

            // store the statements modeling the pattern contents
            Collection<Statement> collection = pattern.getStatements(model, patternResource);
            model.addAll(collection.iterator());

        }
    }

    /**
     * Create a DomainBoundaries instance populated with UrlPatterns that reflect the specified configuration.
     */
    public static DomainBoundaries getDomainBoundaries(RDFContainer configuration) {
        // fetch the Model and the DataSource ID
        URI id = configuration.getDescribedUri();
        Model model = (Model) configuration.getModel();

        // fetch all UrlPatterns
        List includePatterns = getPatterns(id, DATASOURCE_GEN.includePattern, model);
        List excludePatterns = getPatterns(id, DATASOURCE_GEN.excludePattern, model);

        // return the UrlPatterns as a DomainBoundaries instance
        return new DomainBoundaries(includePatterns, excludePatterns);
    }

    private static List<UrlPattern> getPatterns(URI id, URI predicate, Model model) {
        // query for all include or exclude pattern statements
        ClosableIterator<? extends Statement> statements = null;
        ArrayList result = new ArrayList();
        try {
            statements = model.findStatements(id, predicate, Variable.ANY);

            while (statements.hasNext()) {
                Statement statement = (Statement) statements.next();
                Node value = statement.getObject();

                // only proceed when the value is a Resource
                if (value instanceof Resource) {
                    Resource patternResource = (Resource) value;

                    // determine its type and value
                    Node typeValue = getSingleValue(patternResource, RDF.type, model);
                    Node patternValue = getSingleValue(patternResource, RDF.value, model);

                    // skip in case of inappropriate values
                    if (!(typeValue instanceof URI) || !(patternValue instanceof Literal)) {
                        continue;
                    }

                    // convert the pattern Value to a String
                    String patternString = ((Literal) patternValue).getValue();

                    // create the appropriate UrlPattern
                    if (DATASOURCE_GEN.RegExpPattern.equals(typeValue)) {
                        result.add(new RegExpPattern(patternString));
                    }
                    else if (DATASOURCE_GEN.SubstringPattern.equals(typeValue)) {
                        // also fetch the condition statement
                        Node conditionValue = getSingleValue(patternResource, DATASOURCE_GEN.condition, model);
                        SubstringCondition condition = resolveCondition(conditionValue);
                        if (condition != null) {
                            result.add(new SubstringPattern(patternString, condition));
                        }
                    }
                }
            }
        }
        catch (ModelException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (statements != null) {
                statements.close();
            }
        }

        return result;
    }

    private static Node getSingleValue(Resource resource, URI predicate, Model model) throws ModelException {
        ClosableIterator<? extends Statement> statements = null;
        Node result = null;
        try {
            statements = model.findStatements(resource, predicate,Variable.ANY);
            if (statements.hasNext()) {
                Statement statement = (Statement) statements.next();
                result = statement.getObject();
            }
        }
        finally {
            if (statements != null) {
                statements.close();
            }
        }
        return result;
    }

    public static SubstringCondition resolveCondition(Node value) {
        String comp = value.toString();
        if (DATASOURCE.STARTS_WITH.toString().equals(comp)) {
            return new SubstringCondition.StartsWith();
        }
        else if (DATASOURCE.ENDS_WITH.toString().equals(comp)) {
            return new SubstringCondition.EndsWith();
        }
        else if (DATASOURCE.CONTAINS.toString().equals(comp)) {
            return new SubstringCondition.Contains();
        }
        else if (DATASOURCE.DOES_NOT_CONTAIN.toString().equals(comp)) {
            return new SubstringCondition.DoesNotContain();
        }
        else {
            return null;
        }
    }
}