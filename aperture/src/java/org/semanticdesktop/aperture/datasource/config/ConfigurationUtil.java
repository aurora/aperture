/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.sesame.repository.RStatement;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sail.SailUpdateException;
import org.openrdf.util.iterator.CloseableIterator;
import org.semanticdesktop.aperture.datasource.Vocabulary;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * ConfigurationUtil provides utility methods for setting and retrieving standard DataSource
 * configuration properties from an RDFContainer.
 * 
 * <p>
 * Some methods may rely on the use of a RDFcontainer implementation operating on top of a Sesame
 * Repository. These methods may also make use of the context support in the Repository class.
 * Consequently, it is important that the RDF model is also stored using a context-preserving storage
 * mechanism (e.g. TriX files).
 */
public class ConfigurationUtil {

    private static final String BOUNDARY_CONTEXT_POSTFIX = "-DomainBoundariesContext";

    private ConfigurationUtil() {
        // prevent instantiation
    }

    public static void setRootUrl(String url, RDFContainer configuration) {
        configuration.put(Vocabulary.ROOT_URL, url);
    }

    public static String getRootUrl(RDFContainer configuration) {
        return configuration.getString(Vocabulary.ROOT_URL);
    }

    public static void setPassword(String password, RDFContainer configuration) {
        configuration.put(Vocabulary.PASSWORD, password);
    }

    public static String getPassword(RDFContainer configuration) {
        return configuration.getString(Vocabulary.PASSWORD);
    }

    public static void setMaximumDepth(int maximumDepth, RDFContainer configuration) {
        configuration.put(Vocabulary.MAXIMUM_DEPTH, maximumDepth);
    }

    public static Integer getMaximumDepth(RDFContainer configuration) {
        return configuration.getInteger(Vocabulary.MAXIMUM_DEPTH);
    }

    public static void setMaximumByteSize(int maximumSize, RDFContainer configuration) {
        configuration.put(Vocabulary.MAXIMUM_BYTE_SIZE, maximumSize);
    }

    public static Integer getMaximumByteSize(RDFContainer configuration) {
        return configuration.getInteger(Vocabulary.MAXIMUM_BYTE_SIZE);
    }

    public static void setIncludeHiddenResources(boolean value, RDFContainer configuration) {
        configuration.put(Vocabulary.INCLUDE_HIDDEN_RESOURCES, value);
    }

    public static Boolean getIncludeHiddenResourceS(RDFContainer configuration) {
        return configuration.getBoolean(Vocabulary.INCLUDE_HIDDEN_RESOURCES);
    }

    public static void setIncludeEmbeddedResources(boolean value, RDFContainer configuration) {
        configuration.put(Vocabulary.INCLUDE_EMBEDDED_RESOURCES, value);
    }

    public static Boolean getIncludeEmbeddedResourceS(RDFContainer configuration) {
        return configuration.getBoolean(Vocabulary.INCLUDE_EMBEDDED_RESOURCES);
    }

    public static void setConnectionSecurity(String securityType, RDFContainer configuration) {
        configuration.put(Vocabulary.CONNECTION_SECURITY, securityType);
    }

    public static String getConnectionSecurity(RDFContainer configuration) {
        return configuration.getString(Vocabulary.CONNECTION_SECURITY);
    }

    /**
     * Stores the specified DomainBoundaries in the specified configuration. This method will overwrite a
     * previously set DomainBoundaries but this is only guaranteed to work correctly when it has also
     * been stored through this same method.
     * 
     * <p>
     * Implementation note: this method assumes that the specified RDFContainer uses a Sesame Repository
     * as its model and will make use of its context support to mark all statements relating to this
     * DomainBoundaries.
     */
    public static void setDomainBoundaries(DomainBoundaries boundaries, RDFContainer configuration) {
        // fetch the Repository and the DataSource ID
        URI id = configuration.getDescribedUri();
        Repository repository = (Repository) configuration.getModel();

        // determine the URI to use as the context of the domain boundary statements
        URI context = new URIImpl(id.toString() + BOUNDARY_CONTEXT_POSTFIX);

        try {
            // remove all existing domain boundary statements
            repository.clearContext(context);

            // add statements reflecting the specified DomainBoundaries
            if (boundaries != null) {
                addPatternStatements(id, boundaries.getIncludePatterns(), Vocabulary.INCLUDE_PATTERN,
                        context, repository);
                addPatternStatements(id, boundaries.getExcludePatterns(), Vocabulary.EXCLUDE_PATTERN,
                        context, repository);
            }
        }
        catch (SailUpdateException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addPatternStatements(URI sourceID, List patterns, URI predicate, URI context,
            Repository repository) throws SailUpdateException {
        // a ValueFactory will be used to create BNodes
        ValueFactory factory = repository.getSail().getValueFactory();

        // loop over all patterns
        Iterator iterator = patterns.iterator();
        while (iterator.hasNext()) {
            UrlPattern pattern = (UrlPattern) iterator.next();

            // create a BNode for this pattern
            BNode patternResource = factory.createBNode();

            // store the statements modeling the pattern contents
            repository.add(pattern.getStatements(patternResource), context);

            // connect it to the DataSource's Resource using the appropriate pattern predicate
            repository.add(sourceID, predicate, patternResource, context);
        }
    }

    /**
     * Create a DomainBoundaries instance populated with UrlPatterns that reflect the specified
     * configuration.
     * 
     * <p>
     * Implementation note: this method assumes that the specified RDFContainer uses a Sesame Repository
     * as its model.
     */
    public static DomainBoundaries getDomainBoundaries(RDFContainer configuration) {
        // fetch the Repository and the DataSource ID
        URI id = configuration.getDescribedUri();
        Repository repository = (Repository) configuration.getModel();

        // fetch all UrlPatterns
        List includePatterns = getPatterns(id, Vocabulary.INCLUDE_PATTERN, repository);
        List excludePatterns = getPatterns(id, Vocabulary.EXCLUDE_PATTERN, repository);

        // return the UrlPatterns as a DomainBoundaries instance
        return new DomainBoundaries(includePatterns, excludePatterns);
    }

    private static List getPatterns(URI id, URI predicate, Repository repository) {
        ArrayList result = new ArrayList();

        // query for all include or exclude pattern statements
        CloseableIterator statements = repository.getStatements(id, predicate, null);
        while (statements.hasNext()) {
            RStatement statement = (RStatement) statements.next();
            Value value = statement.getObject();

            // only proceed when the value is a Resource
            if (value instanceof Resource) {
                Resource patternResource = (Resource) value;

                // determine its type and value
                Value typeValue = getSingleValue(patternResource, RDF.TYPE, repository);
                Value patternValue = getSingleValue(patternResource, RDF.VALUE, repository);

                // skip in case of inappropriate values
                if (!(typeValue instanceof URI) || !(patternValue instanceof Literal)) {
                    continue;
                }

                // convert the pattern Value to a String
                String patternString = ((Literal) patternValue).getLabel();

                // create the appropriate UrlPattern
                if (Vocabulary.REGEXP_PATTERN.equals(typeValue)) {
                    result.add(new RegExpPattern(patternString));
                }
                else if (Vocabulary.SUBSTRING_PATTERN.equals(typeValue)) {
                    // also fetch the condition statement
                    Value conditionValue = getSingleValue(patternResource, Vocabulary.CONDITION, repository);
                    SubstringCondition condition = resolveCondition(conditionValue);
                    if (condition != null) {
                        result.add(new SubstringPattern(patternString, condition));
                    }
                }
                else {
                    // unknown type, silently ignore
                }
            }
        }

        statements.close();
        
        return result;
    }

    private static Value getSingleValue(Resource resource, URI predicate, Repository repository) {
        Value result = null;
        
        CloseableIterator statements = repository.getStatements(resource, predicate, null);
        if (statements.hasNext()) {
            RStatement statement = (RStatement) statements.next();
            result = statement.getObject();
        }

        statements.close();
        
        return result;
    }

    public static SubstringCondition resolveCondition(Value value) {
        if (Vocabulary.STARTS_WITH.equals(value)) {
            return new SubstringCondition.StartsWith();
        }
        else if (Vocabulary.ENDS_WITH.equals(value)) {
            return new SubstringCondition.EndsWith();
        }
        else if (Vocabulary.CONTAINS.equals(value)) {
            return new SubstringCondition.Contains();
        }
        else if (Vocabulary.DOES_NOT_CONTAIN.equals(value)) {
            return new SubstringCondition.DoesNotContain();
        }
        else {
            return null;
        }
    }
}