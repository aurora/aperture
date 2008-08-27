/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a superclass of all addressbook crawlers. Modified dates are not used, but a checksum is computed
 * to determine if entries are changed. Return addressbook enties are expected to use VCard vocabulary.
 */
public abstract class AddressbookCrawler extends CrawlerBase {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /** Key used to store the addressbook entry checksum in the AccessData - used for incremental crawling */
    protected static final String ADDRESSBOOK_CHECKSUM_KEY = "ADDRESSBOOK_CHECKSUM";

    /**
     * Crawls the addressbook and returns a list of DataObjects. Each DataObject corresponds to a single
     * entry in the addresbook.
     * @return a list of DataObjects representing addressbook entries
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public abstract List crawlAddressbook() throws Exception;

    /**
     * Returns the URI of the address book itself
     * @return the URI of the address book itself
     */
    public abstract URI getContactListUri();
    
    @SuppressWarnings("unchecked")
    protected ExitCode crawlObjects() {
        boolean crawlCompleted = false;

        try {
            List people = crawlAddressbook();
            URI contactListUri = getContactListUri();
            
            if (!isStopRequested() && (accessData == null || !accessData.isKnownId(contactListUri.toString()))) {
                reportContactListDataObject(contactListUri);
            }
            
            Iterator it = people.iterator();
            
            while (!isStopRequested() && it.hasNext()) {
                DataObject o = (DataObject) it.next();
                String sum = computeChecksum(o);
                if (accessData != null && accessData.isKnownId(o.getID().toString())) {
                    if (accessData.get(o.getID().toString(), ADDRESSBOOK_CHECKSUM_KEY).equals(sum)) {
                        reportUnmodifiedDataObject(o.getID().toString());
                    }
                    else {
                        reportModifiedDataObject(o);
                    }
                }
                else {
                    if (accessData != null) {
                        accessData.put(o.getID().toString(), ADDRESSBOOK_CHECKSUM_KEY, sum);
                    }
                    reportNewDataObject(o);
                }
            }
            // now we need to close all the objects that may not have reached the crawler
            // do to a stop request.
            if (it.hasNext()) {
                crawlCompleted = false;
                while (it.hasNext()) {
                    DataObject o = (DataObject) it.next();
                    o.dispose();
                }
            } else {
                crawlCompleted = true;
            }
            // Blah - crawl objects, friends etc.
        }
        catch (Exception e) {
            logger.error("Could not crawl addressbook data source", e);
            return ExitCode.FATAL_ERROR;
        }

        // determine the exit code
        return crawlCompleted ? ExitCode.COMPLETED : ExitCode.STOP_REQUESTED;
    }

    private void reportContactListDataObject(URI contactListUri) {
        RDFContainerFactory rdff = getRDFContainerFactory(contactListUri.toString());
        RDFContainer rdf = rdff.getRDFContainer(contactListUri);
        rdf.add(RDF.type,NCO.ContactList);
        rdf.add(NIE.rootElementOf,getDataSource().getID());
        DataObjectBase object = new DataObjectBase(contactListUri, source, rdf);
        reportNewDataObject(object);
    }

    /**
     * Compute a MD5 checksum of the values of this addressbook entry. This is based on immediate RDF
     * properties, and might cause two problems: 1. BlankNodes will always have different IDs and will mess up
     * the id. 2. if this has sub elements (i.e. addresses), this might return the same although the address
     * might have changed.
     * 
     * TODO: FIX THIS! (then again, traversing whole tree might be slow)
     * 
     * @param o - the dataobject to checksum
     * @return a md5 hex-digest as a string.
     */
    @SuppressWarnings("unchecked")
    private String computeChecksum(DataObject o) {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("md5");
        }
        catch (NoSuchAlgorithmException e) {
            logger.warn("MD5 not available - using no checksum.");

            return "nochecksum";
        }
        // hack hack
        RDFContainer rdf = o.getMetadata();
        Model model = rdf.getModel();

        // List all properties
        List predValues = new Vector();
        ClosableIterator<? extends Statement> i = null;

        try {
            i = model.findStatements(rdf.getDescribedUri(),Variable.ANY, Variable.ANY);
            while (i.hasNext()) {
                Statement s = i.next();
                if (s.getObject() instanceof BlankNode) {
                    logger.warn("BlankNodes messes up checksum generation!");
                }
                predValues.add(s.getPredicate().toString() + s.getObject().toString());
            }
        }
        catch (ModelRuntimeException me) {
            logger.error("Could not find statements", me);
        }
        finally {
            if (i != null) {
                i.close();
            }
        }

        // sort them...
        Collections.sort(predValues);

        // DAMN java 1.4
        for (Iterator it = predValues.iterator(); it.hasNext();)
            md.update(((String) it.next()).getBytes());

        StringBuilder digest = new StringBuilder("");
        byte[] dig = md.digest();
        for (int j = 0; j < dig.length; j++) {
            digest.append(Integer.toHexString(dig[j]));

        }

        return digest.toString();
    }
}
