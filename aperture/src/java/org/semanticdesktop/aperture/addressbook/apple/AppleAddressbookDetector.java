/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.apple;

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

/**
 * Detects the apple addressbook datasource.
 * The algorithm goes like this: if on mac, return true.
 * @author grimnes
 * @author sauermann
 */
public class AppleAddressbookDetector implements DataSourceDetector {

    public List<DataSourceDescription> detect() throws Exception {
        if (!OSUtils.isMac()) return Collections.emptyList();
        
        // Does everyone on Macs have Address Book?
        // Our answer is: yes, sure.
        AppleAddressbookDataSource ds = new AppleAddressbookDataSource();
        Model m=RDF2Go.getModelFactory().createModel();
        m.open();
        ds.setConfiguration(new RDFContainerImpl(m, UriUtil.generateRandomURI(m)));
        ds.setName("Apple Addressbook");
        ds.setComment("Your contacts from the Apple Addressbook");
        ArrayList<DataSourceDescription> result = new ArrayList<DataSourceDescription>(1);
        result.add(new DataSourceDescription(ds));
        return result;
    }

    public URI getSupportedType() {
        return APPLEADDRESSBOOKDS.AppleAddressbookDataSource;
    }

}
