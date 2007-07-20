/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.semanticdesktop.aperture.opener.DataOpener;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;

/**
 * An outlook opener factory. A crappy hack, there is no way to close the
 * dummyModel. It will do for the time being until the outlook problem is
 * solved properly.
 */
public class OutlookOpenerFactory implements DataOpenerFactory {

    private static final Set SUPPORTED_SCHEMES = Collections.singleton("outlook");
    
    private OutlookCrawler crawler;
    
    public DataOpener get() {
        if (crawler == null) {
            crawler = prepareCrawler();
        }
        return crawler;
    }
    
    private OutlookCrawler prepareCrawler() {
        OutlookCrawler resultCrawler = new OutlookCrawler();
        OutlookDataSource source = new OutlookDataSource();
        Model dummyModel = RDF2Go.getModelFactory().createModel();
        dummyModel.open();
        RDFContainer configuration = new RDFContainerImpl(dummyModel,"urn:" + UUID.randomUUID().toString());
        source.setConfiguration(configuration);
        resultCrawler.setDataSource(source);
        return crawler;
    }

    public Set getSupportedSchemes() {
        return SUPPORTED_SCHEMES;
    }
}

