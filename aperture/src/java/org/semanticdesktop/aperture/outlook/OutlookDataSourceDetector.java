/*
 * Copyright (c) 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.outlook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.detector.DataSourceDescription;
import org.semanticdesktop.aperture.detector.DataSourceDetector;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.OSUtils;
import org.semanticdesktop.aperture.util.UriUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Detect Microsoft Outlook. This instantiates the Microsoft Outlook ActiveX control and tests if
 * this fails or not.
 * 
 * @author sauermann
 */
public class OutlookDataSourceDetector implements DataSourceDetector {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /*
     * (non-Javadoc)
     * 
     * @see org.semanticdesktop.aperture.detector.DataSourceDetector#detect()
     */
    public List<DataSourceDescription> detect() throws Exception {
        if (!OSUtils.isWindows())
            return Collections.emptyList();
        
        // new outlook datasource
        OutlookDataSource ds = new OutlookDataSource();
        Model m=RDF2Go.getModelFactory().createModel();
        m.open();
        URI id = UriUtil.generateRandomURI(m);
        ds.setConfiguration(new RDFContainerImpl(m, id));
        ds.setName("Microsoft Outlook");
        ds.setComment("Your emails, appointments, contacts and other elements from Outlook.");
        ds.setRootUrl(OutlookCrawler.OUTLOOKURIPREFIX);
        
        // try it out...
        boolean outlookIsThere = false;
        OutlookCrawler crawler = new OutlookCrawler();
        try {
            // fake datasource
            try {
                crawler.setDataSource(ds);
                crawler.beginCall();
                outlookIsThere = true;
            } catch (Throwable t) {
                // this is ok
                logger.debug("Outlook is not there, this is ok: "+t, t);
            } finally {
                crawler.endCall();
            }
        } catch (Throwable t) {
            logger.debug("cannot detect Outlook: "+t, t);
        } finally {
                crawler.release();
                // TODO: this does not work :-/ there will be a daemon thread hanging around. 
                // crawler.killKillKill(); calling KillKillKill does indeed fuckup the whole windows
                // messaging hard. it mustnt be called.
        }
        if (!outlookIsThere)
            return Collections.emptyList();
        
        
        ArrayList<DataSourceDescription> result = new ArrayList<DataSourceDescription>(1);
        result.add(new DataSourceDescription(ds));
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.semanticdesktop.aperture.detector.DataSourceDetector#getSupportedType()
     */
    public URI getSupportedType() {
        return OUTLOOKDS.OutlookDataSource;
    }

}
