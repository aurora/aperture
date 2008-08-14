/*
 * Copyright (c) 2008 Forschungszentrum L3S
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.websites.flickr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.NAO;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.websites.flickr.FlickrDataSource.CrawlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Exif;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.Size;
import com.aetrion.flickr.tags.Tag;

/**
 * Crawls metadata from Flickr accounts.
 * 
 * @author <a href="mailto:kohlschuetter@L3S.de">Christian Kohlschuetter</a>
 */
public class FlickrCrawler extends CrawlerBase {

    // FIXME make this configurable
    private static final String API_KEY = "f47691529440669449065e6962e1346a";

    private static final Logger LOG = LoggerFactory.getLogger(FlickrCrawler.class);

    public FlickrCrawler(DataSource ds) {
        super();
        setDataSource(ds);
    }

    private enum ObjectType {
        NEW, UNMODIFIED, CHANGED
    }

    // FIXME make this configurable via the GUI
    private final File localPhotoBasedir = new File(new File(System.getProperty("user.home")), "flickrPhotos");

    private static final int ENTRIES_PER_PAGE = 10;

    @Override
    @SuppressWarnings("unchecked")
    protected ExitCode crawlObjects() {
        final FlickrDataSource localSource = (FlickrDataSource) getDataSource();
        final RDFContainer configuration = localSource.getConfiguration();
        final String username = ConfigurationUtil.getUsername(configuration);
        final String password = ConfigurationUtil.getPassword(configuration);

        try {
            // TODO: actually, the shared secret of the flickr API key has to be passed here, not a password
            FlickrCredentials credentials = new FlickrCredentials(API_KEY, password);
            Flickr flickr = credentials.getFlickrInterface();

            PeopleInterface peopleIf = flickr.getPeopleInterface();
            User meUser;

            if (username.indexOf('@') != -1 && username.indexOf('.') != -1) {
                meUser = peopleIf.findByEmail(username);
            }
            else {
                // FIXME maybe remove support for Flickr-internal user-IDs
                meUser = peopleIf.findByUsername(username);
            }
            String meId = meUser.getId();

            // FIXME store flickr data from different accounts in different contexts?
            // this requires changes to Aperture's RDF API (context support)
            String contextUriString = "http://www.flickr.com/photos/" + meId + "/";

            String photoUriPrefix = contextUriString;

            PhotosInterface photosIf = flickr.getPhotosInterface();
            // PhotosetsInterface photosetsIf = flickr.getPhotosetsInterface();

            // FIXME check why CrawlType is NULL
            boolean downloadImages = !CrawlType.MetadataOnlyCrawlType.equals(localSource.getCrawlType());
            if (downloadImages) {
                localPhotoBasedir.mkdirs();
            }

            boolean notAProUser = false;

            int page = 0;
            int numEntries;
            do {
                numEntries = 0;

                PhotoList pl = peopleIf.getPublicPhotos(meId, ENTRIES_PER_PAGE, page);
                // PhotoList pl = photosetsIf.getPhotos(meId, ENTRIES_PER_PAGE, page);

                for (Iterator<Photo> it = pl.iterator(); it.hasNext();) {
                    numEntries++;
                    Photo photo = it.next();
                    // NOTE to get all information, we need to use photosIf
                    photo = photosIf.getPhoto(photo.getId(), credentials.secret);

                    String id = photo.getId();
                    final String photoUriString = photoUriPrefix + id + "/";

                    // TODO: photo.getLastUpdate() is broken in flickrj 1.0 but fixed in flickrj 1.1
                    // determine the date when the photo last changed
                    Date photoChangeDate = (photo.getLastUpdate() != null) ? photo.getLastUpdate() : photo
                            .getDatePosted();
                    if (photoChangeDate == null) {
                        LOG.warn("missing change-date for photo " + photoUriString
                                + ", using current system date");
                        photoChangeDate = new Date();
                    }

                    String timeMillis;
                    if (accessData != null) {
                        accessData.touch(photoUriString);
                        timeMillis = accessData.get(photoUriString, AccessData.DATE_KEY);
                    }
                    else {
                        timeMillis = null;
                    }

                    ObjectType objectType;
                    if (timeMillis == null) {
                        objectType = ObjectType.NEW;
                    }
                    else {
                        long t = Long.parseLong(timeMillis);
                        if (photoChangeDate.getTime() > t) {
                            objectType = ObjectType.CHANGED;
                        }
                        else {
                            // FIXME check whether tags implicate a change in lastUpdate
                            objectType = ObjectType.UNMODIFIED;
                        }
                    }

                    if (objectType == ObjectType.UNMODIFIED) {
                        reportUnmodifiedDataObject(photoUriString);
                        continue;
                    }

                    if (accessData != null) {

                        accessData.put(photoUriString, AccessData.DATE_KEY, Long.toString(photoChangeDate
                                .getTime()));
                    }

                    List<DataObject> dataObjects = new ArrayList<DataObject>();

                    DataObject objPhotoIE = newDataObject(dataObjects, photoUriString);
                    DataObject objPhotoDOWebsite = newDataObject(dataObjects, photo.getUrl());
                    {
                        RDFContainer rdf = objPhotoDOWebsite.getMetadata();
                        rdf.add(RDF.type, NFO.Website);
                        rdf.add(NFO.fileUrl, photo.getUrl());
                        rdf.add(NIE.interpretedAs, objPhotoIE.getID());
                    }

                    File localCopy = null;
                    String format;

                    String mimeType = null;
                    String suffix = "";

                    DataObject objPhotoDO = newDataObject(dataObjects, photo.getUrl());
                    {
                        RDFContainer rdf = objPhotoDO.getMetadata();
                        rdf.add(RDF.type, NFO.Image);

                        String photoUrl = null;
                        if (photo.getOriginalSecret().length() == 0) {
                            // You are not a PRO user
                            // see http://flickrj.sourceforge.net/faq.php?faq_id=1
                            if (!notAProUser) {
                                LOG
                                        .warn("You are not a Flickr-PRO user. Cannot download original image. Attempting largest size possible");
                                notAProUser = true;
                            }
                            Collection<Size> sizes = (Collection<Size>) photosIf.getSizes(photo.getId());

                            int largestWidth = -1;
                            for (Size size : sizes) {
                                if (size.getWidth() > largestWidth) {
                                    photoUrl = size.getSource();
                                    largestWidth = size.getWidth();
                                    suffix = "_" + size.getWidth() + "x" + size.getHeight();
                                }
                            }
                            format = "jpg";
                        }
                        else {
                            photoUrl = photo.getOriginalUrl();
                            format = photo.getOriginalFormat();
                            suffix = "_original";
                        }
                        if (photoUrl != null) {
                            rdf.add(NFO.fileUrl, photoUrl);
                        }
                        rdf.add(NIE.interpretedAs, objPhotoIE.getID());

                        if (downloadImages && photoUrl != null) {
                            // download the image

                            localCopy = new File(localPhotoBasedir, photo.getId() + suffix + "." + format);
                            if (localCopy.exists()) {
                                LOG.info("Skipping photo " + photoUrl + ". File already exists at "
                                        + localCopy);
                            }
                            else {
                                LOG.info("Copying photo " + photoUrl + " to " + localCopy + " (format "
                                        + format + ")");

                                URLConnection conn = new URL(photoUrl).openConnection();
                                InputStream in = conn.getInputStream();
                                IOUtil.writeStream(in, localCopy);
                                mimeType = conn.getContentType();
                                in.close();
                            }
                        }
                    }

                    {
                        RDFContainer rdf = objPhotoIE.getMetadata();
                        rdf.add(RDF.type, NIE.InformationElement);
                        addIfNotNull(rdf, RDFS.label, photo.getTitle());
                        addIfNotNull(rdf, NIE.title, photo.getTitle());
                        addIfNotNull(rdf, NIE.contentLastModified, photo.getDateAdded());
                        addIfNotNull(rdf, NIE.created, photo.getDatePosted());
                        addIfNotNull(rdf, NIE.contentCreated, photo.getDateTaken());
                        addIfNotNull(rdf, NIE.description, photo.getDescription());
                        rdf.add(NIE.isStoredAs, objPhotoDOWebsite.getID());
                        if (localCopy != null) {
                            rdf.add(NIE.isStoredAs, localCopy.toURI().toString());
                        }
                        if (mimeType == null) {
                            mimeType = "image/jpeg";
                        }
                        rdf.add(NIE.mimeType, mimeType);

                        Collection<Tag> tags = photo.getTags();
                        for (final Tag t : tags) {
//                            String tagsPrefix = "http://www.flickr.com/people/" + t.getAuthor() + "/tags/";
                            String tagsPrefix = "http://www.flickr.com/tags/";
                            // String tagsPrefix = photoUriString;
                            final String tagValue = t.getValue();
                            if (tagValue != null) {
                                String tag = tagsPrefix + tagValue;

                                DataObject objTag = newDataObject(dataObjects, tag);
                                rdf.add(NAO.hasTag, objTag.getID());
                                {
                                    RDFContainer rdfTag = objTag.getMetadata();
                                    rdfTag.add(RDF.type, NAO.Tag);
                                    addIfNotNull(rdfTag, NAO.prefLabel, tagValue);
                                }
                            }
                        }

                        // Collection<Exif> exifs = (Collection<Exif>) photosIf.getExif(photo.getId(), photo
                        // .getSecret());
                        // for (Exif exif : exifs) {
                        // // tagSpace + "-" + tag = id (e.g., EXIF-34850)
                        // String tagSpace = exif.getTagspace();
                        // String tag = exif.getTag();
                        //                            
                        // // human-readable label for tag
                        // String label = exif.getLabel();
                        // // human-readable value (sometimes null)
                        // String cleanValue = exif.getClean();
                        //                            
                        // // raw-value for this tag
                        // String rawValue = exif.getRaw();
                        // }
                    }

                    switch (objectType) {
                    case NEW:
                        for (DataObject dobj : dataObjects) {
                            reportNewDataObject(dobj);
                        }
                        break;
                    case CHANGED:
                        for (DataObject dobj : dataObjects) {
                            reportModifiedDataObject(dobj);
                        }
                        break;
                    default:
                        // unsupported operation, assume new
                        LOG.info("Unsupported ObjectType, assuming NEW: " + objectType);
                        for (DataObject dobj : dataObjects) {
                            reportNewDataObject(dobj);
                        }
                    }
                }
                page++;
            }
            while (numEntries == ENTRIES_PER_PAGE);

            return ExitCode.COMPLETED;
        }
        catch (IOException e) {
            LOG.info("Could not crawl Flickr datasource", e);
            // e.printStackTrace();
            return ExitCode.FATAL_ERROR;
        }
        catch (FlickrException e) {
            LOG.info("Could not crawl Flickr datasource", e);
            // e.printStackTrace();
            return ExitCode.FATAL_ERROR;
        }
        catch (SAXException e) {
            LOG.info("Could not crawl Flickr datasource", e);
            // e.printStackTrace();
            return ExitCode.FATAL_ERROR;
        }
        catch (RuntimeException e) {
            LOG.info("Could not crawl Flickr datasource", e);
            // e.printStackTrace();
            return ExitCode.FATAL_ERROR;
        }
    }

    private DataObject newDataObject(final Collection<DataObject> dataObjects, final String uriString) {
        URIImpl turi = new URIImpl(uriString);
        RDFContainer rdf = getRDFContainerFactory(uriString).getRDFContainer(turi);
        DataObject dObj = new DataObjectBase(turi, source, rdf);
        dataObjects.add(dObj);
        return dObj;
    }

    private void addIfNotNull(final RDFContainer rdf, final URI property, final String value) {
        if (value != null) {
            rdf.add(property, value);
        }
    }

    private void addIfNotNull(final RDFContainer rdf, final URI property, final Date value) {
        if (value != null) {
            rdf.add(property, value);
        }
    }

    private static class FlickrCredentials {

        private String secret;

        private String frob;

        private Flickr flickr;

        public FlickrCredentials(final String apiKey) throws IOException, FlickrException, SAXException {
            this(apiKey, null);
        }

        public FlickrCredentials(final String apiKey, final String secret) throws IOException,
                FlickrException, SAXException {
            this.secret = secret;
            flickr = new Flickr(apiKey);
            if (secret != null && secret.length() != 0) {
                // flickr.setSharedSecret(secret); // this is the code used in 1.1
                RequestContext.getRequestContext().setSharedSecret(secret); // this is the code used in 1.0
            }

            // AuthInterface authIf = flickr.getAuthInterface();
            // frob = authIf.getFrob();
        }

        public Flickr getFlickrInterface() {
            return flickr;
        }

        public String getFrob() {
            return frob;
        }
    }
}
