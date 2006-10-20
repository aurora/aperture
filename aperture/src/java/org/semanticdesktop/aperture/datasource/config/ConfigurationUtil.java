/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.sesame.query.MalformedQueryException;
import org.openrdf.sesame.query.QueryLanguage;
import org.openrdf.sesame.repository.RStatement;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sail.SailUpdateException;
import org.openrdf.util.iterator.CloseableIterator;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

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
	
	private static final Logger LOGGER = Logger.getLogger(ConfigurationUtil.class.getName());

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
    		configuration.remove(new StatementImpl(id, DATASOURCE_GEN.basepath, new LiteralImpl(oldPath)));
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
    		Value value = (Value) iterator.next();
    		if (value instanceof Literal) {
    			result.add(((Literal) value).getLabel());
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

        try {
        	   deletePatternStatements(id,DATASOURCE.includePattern, repository);
        	   deletePatternStatements(id,DATASOURCE.excludePattern, repository);

            // add statements reflecting the specified DomainBoundaries
            if (boundaries != null) {
                addPatternStatements(id, boundaries.getIncludePatterns(), DATASOURCE_GEN.includePattern, repository);
                addPatternStatements(id, boundaries.getExcludePatterns(), DATASOURCE_GEN.excludePattern, repository);
            }
        }
        catch (SailUpdateException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void deletePatternStatements(URI id, URI predicate, Repository repository) throws SailUpdateException {
    	
    		try {
    			String query="construct * from {<"+id.toString()+">} <"+predicate.toString()+"> {p},"+
    			"{p} rdf:type {t},"+
    			"{p} rdf:value {v},"+
    			"[{p} data:condition {c}] "+    			
    			"USING NAMESPACE rdf = <http://www.w3.org/1999/02/22-rdf-syntax-ns#>,"+ 
    			"rdfs = <http://www.w3.org/2000/01/rdf-schema#>,"+
    			"data = <"+DATASOURCE.NS+">";
    			//LOGGER.info(query);
    			repository.remove(QueryLanguage.SERQL,query);
    		}
    		catch (MalformedQueryException e) {
    			LOGGER.log(Level.WARNING,"Query failed. Very odd. ",e);
    		}
    	
    }

	private static void addPatternStatements(URI sourceID, List patterns, URI predicate, Repository repository) throws SailUpdateException {
        // a ValueFactory will be used to create BNodes
        ValueFactory factory = repository.getSail().getValueFactory();

        // loop over all patterns
        Iterator iterator = patterns.iterator();
        while (iterator.hasNext()) {
            UrlPattern pattern = (UrlPattern) iterator.next();

            // create a BNode for this pattern
            BNode patternResource = factory.createBNode();

            // store the statements modeling the pattern contents
            repository.add(pattern.getStatements(patternResource));

            // connect it to the DataSource's Resource using the appropriate pattern predicate
            repository.add(sourceID, predicate, patternResource);
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
        List includePatterns = getPatterns(id, DATASOURCE_GEN.includePattern, repository);
        List excludePatterns = getPatterns(id, DATASOURCE_GEN.excludePattern, repository);

        // return the UrlPatterns as a DomainBoundaries instance
        return new DomainBoundaries(includePatterns, excludePatterns);
    }

    private static List getPatterns(URI id, URI predicate, Repository repository) {
        // query for all include or exclude pattern statements
        CloseableIterator statements = repository.getStatements(id, predicate, null);
        try {
            ArrayList result = new ArrayList();

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
	                	LOGGER.config("type of boundary pattern not valid: "+typeValue);
	                    continue;
	                }
	
	                // convert the pattern Value to a String
	                String patternString = ((Literal) patternValue).getLabel();
	
	                // create the appropriate UrlPattern
	                if (DATASOURCE_GEN.RegExpPattern.equals(typeValue)) {
	                    result.add(new RegExpPattern(patternString));
	                }
	                else if (DATASOURCE_GEN.SubstringPattern.equals(typeValue)) {
	                    // also fetch the condition statement
	                    Value conditionValue = getSingleValue(patternResource, DATASOURCE_GEN.condition, repository);
	                    SubstringCondition condition = resolveCondition(conditionValue);
	                    if (condition != null) {
	                        result.add(new SubstringPattern(patternString, condition));
	                    }
	                    else
	                    	LOGGER.config("cannot detect subtring pattern condition: "+conditionValue);
	                }
	                else {
	                    // unknown type, ignore
	                	LOGGER.config("type of boundary pattern not known: "+typeValue);
	                }
	            }
	        }
	        
	        return result;
        }
        finally {
        	statements.close();
        }
    }

    private static Value getSingleValue(Resource resource, URI predicate, Repository repository) {
        CloseableIterator statements = repository.getStatements(resource, predicate, null);
        try {
            Value result = null;
            
	        if (statements.hasNext()) {
	            RStatement statement = (RStatement) statements.next();
	            result = statement.getObject();
	        }
	        
	        return result;
        }
        finally {
        	statements.close();
        }
    }

    public static SubstringCondition resolveCondition(Value value) {
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