/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.rdf.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.logging.Logger;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sail.SailInitializationException;
import org.openrdf.sesame.sail.SailUpdateException;
import org.openrdf.sesame.sailimpl.memory.MemoryStore;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.RDFUtil;

public class RDFContainerSesame implements RDFContainer {
    
    private static Logger log = Logger.getLogger(RDFContainerSesame.class.getName());

    /** 
     * internal RDF graph
     */
    Repository repository;
    
    ValueFactory valfac;
    
    /**
     * internal dataobject
     */
    Resource dataobject;
    
    URI dataobjectUri;
    
    public RDFContainerSesame(URI dataobjectUri) {
        this(dataobjectUri.toString());
        this.dataobjectUri  = dataobjectUri;
    }

    
    public RDFContainerSesame(String dataobjectUri) {
        super();
        MemoryStore memstore =  new MemoryStore();
        repository = new Repository(memstore);
        try
        {
            repository.initialize();
            valfac = memstore.getValueFactory();
            dataobject = valfac.createURI(dataobjectUri);
        } catch (SailInitializationException e)
        {
            log.severe("cannot initialize standard in-memory SAIL: "+e);
            throw new RuntimeException(e);
        }
    }

    public URI getDataObjectUri()
    {
        if (dataobjectUri == null)
            try
            {
                dataobjectUri = new URI(dataobject.toString());
            } catch (URISyntaxException e)
            {
                log.info("cannot create uri of "+dataobject.toString()+": "+e);
                throw new RuntimeException(e);
            }
        return dataobjectUri;
    }

    public void put(URI property, String value)
    {
        add(dataobject, valfac.createURI(property.toString()), valfac.createLiteral(value));        
    }

    public void put(URI property, Date value)
    {
        
        String date = RDFUtil.dateTime2String(value);        
        add(dataobject, valfac.createURI(property.toString()), valfac.createLiteral(date, XMLSchema.DATETIME));        
    }

    public void put(URI property, boolean value)
    {
        // TODO Auto-generated method stub
        
    }

    public void put(URI property, int value)
    {
        // TODO Auto-generated method stub
        
    }

    public void put(URI property, URI value)
    {
        // TODO Auto-generated method stub
        
    }

    public void add(URI subject, URI property, String value)
    {
        // TODO Auto-generated method stub
        
    }

    public void add(URI subject, URI property, Date value)
    {
        // TODO Auto-generated method stub
        
    }

    public void add(URI subject, URI property, boolean value)
    {
        // TODO Auto-generated method stub
        
    }

    public void add(URI subject, URI property, int value)
    {
        // TODO Auto-generated method stub
        
    }

    public void add(URI subject, URI property, URI value)
    {
        // TODO Auto-generated method stub
        
    }

    public void put(org.openrdf.model.URI property, String value)
    {
        // TODO Auto-generated method stub
        
    }

    public void put(org.openrdf.model.URI property, Date value)
    {
        // TODO Auto-generated method stub
        
    }

    public void put(org.openrdf.model.URI property, boolean value)
    {
        // TODO Auto-generated method stub
        
    }

    public void put(org.openrdf.model.URI property, int value)
    {
        // TODO Auto-generated method stub
        
    }

    public void put(org.openrdf.model.URI property, org.openrdf.model.URI value)
    {
        // TODO Auto-generated method stub
        
    }
    
    public void add(Resource subject, org.openrdf.model.URI property, Value object) {
        try
        {
            repository.add(subject, property, object);
        } catch (SailUpdateException e)
        {
            log.info("cannot add statement: "+e);
            throw new RuntimeException(e);
        }
    }

    public void add(Statement statement)
    {
        // TODO Auto-generated method stub
        
    }

    public Object getRawRDF()
    {
        return repository;
    }

    public Object getRawResource()
    {
        return dataobject;
    }

}


/*
 * $Log$
 * Revision 1.1  2005/10/26 14:08:59  leo_sauermann
 * added the sesame-model and began with RDFContainer
 *
 */