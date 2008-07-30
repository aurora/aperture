/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.mime;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.mail.AbstractJavaMailCrawler;
import org.semanticdesktop.aperture.crawler.mail.DataObjectFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An SubCrawer implementation for message/rfc822-style messages.
 */
public class MimeSubCrawler implements SubCrawler, DataObjectFactory.PartStreamFactory {

    private boolean stopRequested;

    @SuppressWarnings("unchecked")
    public void subCrawl(URI id, InputStream stream, SubCrawlerHandler handler, DataSource dataSource,
            AccessData accessData, Charset charset, String mimeType, RDFContainer parentMetadata)
            throws SubCrawlerException {
        DataObjectFactory fac = null;
        try {
            MimeMessage msg = new MimeMessage(null, stream);
            URI messageUri = parentMetadata.getDescribedUri();
            RDFContainerFactory myFac = handler.getRDFContainerFactory(parentMetadata.getDescribedUri().toString());
            fac = new DataObjectFactory(msg,myFac,this,dataSource,messageUri,null);
            DataObject object = null;
            while ((object = fac.getObject()) != null && !stopRequested) {
                if (accessData != null && accessData.get(object.getID().toString(), AbstractJavaMailCrawler.ACCESSED_KEY) != null) {
                    // here we assume the opposite, if the subcrawler has been invoked, this means that the
                    // parent data object has been found to be new or modified by other means, that's why we
                    // may safely assume that this object has been modified
                    handler.objectChanged(object);
                } else {
                    if (accessData != null) {
                        accessData.put(object.getID().toString(), AbstractJavaMailCrawler.ACCESSED_KEY, "");
                    }
                    handler.objectNew(object);
                }
            }
        }
        catch (MessagingException e) {
            throw new SubCrawlerException(e);
        }
        catch (IOException e) {
            throw new SubCrawlerException(e);
        }
        finally {
            if (fac != null) {
                fac.disposeRemainingObjects();
            }
        }
    }
    
    public void stopSubCrawler() {
        stopRequested = true;
    }

    public InputStream getPartStream(Part part) throws MessagingException, IOException {
        return part.getInputStream();
    }
}
