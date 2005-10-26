
package org.semanticdesktop.aperture.rdf;

import java.net.URI;
import java.util.Date;

import org.openrdf.model.Statement;

/**
 * 
 * 
 * 
 * @author Sauermann
 * $Id$
 */
public interface RDFContainer {
    
    /**
     * Get the identifier of the dataobject that is primarily described by this  
     * @return
     */
    public URI getDataObjectUri();
    
    public void put(URI property, String value);
    public void put(URI property, Date value);
    public void put(URI property, boolean value);
    public void put(URI property, int value);
    public void put(URI property, URI value);
    
    public void add(URI subject, URI property, String value);
    public void add(URI subject, URI property, Date value);
    public void add(URI subject, URI property, boolean value);
    public void add(URI subject, URI property, int value);
    public void add(URI subject, URI property, URI value);
    
    public void put(org.openrdf.model.URI property, String value);
    public void put(org.openrdf.model.URI property, Date value);
    public void put(org.openrdf.model.URI property, boolean value);
    public void put(org.openrdf.model.URI property, int value);
    public void put(org.openrdf.model.URI property, org.openrdf.model.URI value);
    
    public void add(Statement statement);
    
    /**
     * return the raw RDF object, this is a RDF graph object from another
     * api like sesame or jena.
     * @return the graph api.
     */
    public Object getRawRDF();
    
    /**
     * return the raw RDF resource that represents the DataObject, 
     * this is a RDF Resource object from another
     * api like sesame or jena.
     * @return the graph api.
     */
    public Object getRawResource();
    

}


