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
            this.dataobjectUri = new URI(dataobjectUri.toString());
        } catch (SailInitializationException e)
        {
            log.severe("cannot initialize standard in-memory SAIL: "+e);
            throw new RuntimeException(e);
        } catch (URISyntaxException e)
        {
            log.severe("URISyntax: "+e);
            throw new RuntimeException(e);
        }
    }

    public URI getDataObjectUri()
    {
        return dataobjectUri;
    }

    public void put(URI property, String value)
    {
        add(dataobjectUri, property, value); 
    }

    public void put(URI property, Date value)
    {
        add(dataobjectUri, property, value); 
    }

    public void put(URI property, boolean value)
    {
        add(dataobjectUri, property, value);                
    }

    public void put(URI property, int value)
    {
        add(dataobjectUri, property, value); 
    }

    public void put(URI property, URI value)
    {
        add(dataobjectUri, property, value); 
    }

    public void add(URI subject, URI property, String value)
    {
        add(valfac.createURI(subject.toString()), valfac.createURI(property.toString()), valfac.createLiteral(value)); 
    }

    public void add(URI subject, URI property, Date value)
    {
        String date = RDFUtil.dateTime2String(value);        
        add(valfac.createURI(subject.toString()), valfac.createURI(property.toString()), valfac.createLiteral(date, XMLSchema.DATETIME)); 
    }

    public void add(URI subject, URI property, boolean value)
    {
        String val = value?"true":"false";     
        add(valfac.createURI(subject.toString()), valfac.createURI(property.toString()), valfac.createLiteral(val, XMLSchema.BOOLEAN)); 
    }

    public void add(URI subject, URI property, int value)
    {
        String val = Integer.toString(value);     
        add(valfac.createURI(subject.toString()), valfac.createURI(property.toString()), valfac.createLiteral(val, XMLSchema.INT)); 
    }

    public void add(URI subject, URI property, URI value)
    {
        add(valfac.createURI(subject.toString()), valfac.createURI(property.toString()), valfac.createURI(value.toString())); 
    }

    public void put(org.openrdf.model.URI property, String value)
    {
        add(dataobject, property, valfac.createLiteral(value));
    }

    public void put(org.openrdf.model.URI property, Date value)
    {
        String date = RDFUtil.dateTime2String(value);
        add(dataobject, property, valfac.createLiteral(date, XMLSchema.DATETIME));
    }

    public void put(org.openrdf.model.URI property, boolean value)
    {
        String val = value?"true":"false";  
        add(dataobject, property, valfac.createLiteral(val, XMLSchema.BOOLEAN));
    }

    public void put(org.openrdf.model.URI property, int value)
    {
        String val = Integer.toString(value);     
        add(dataobject, property, valfac.createLiteral(val, XMLSchema.INT)); 
    }

    public void put(org.openrdf.model.URI property, org.openrdf.model.URI value)
    {
        add(dataobject, property, value); 
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
        try
        {
            repository.add(statement);
        } catch (SailUpdateException e)
        {
            log.info("cannot add statement: "+e);
            throw new RuntimeException(e);
        }
    }

    public Object getRawRDF()
    {
        return repository;
    }

    public Object getRawResource()
    {
        return dataobject;
    }
    
    public Repository getRepository()
    {
        return repository;
    }

    public Resource getResource()
    {
        return dataobject;
    }

}


/*
 * $Log$
 * Revision 1.2  2005/10/26 14:57:02  leo_sauermann
 * added testcase for the RDFContainerSesame
 *
 * Revision 1.1  2005/10/26 14:08:59  leo_sauermann
 * added the sesame-model and began with RDFContainer
 *
 */