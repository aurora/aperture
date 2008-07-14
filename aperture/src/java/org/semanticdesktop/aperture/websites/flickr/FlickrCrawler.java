package org.semanticdesktop.aperture.websites.flickr;

import java.io.IOException;
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
import org.semanticdesktop.aperture.vocabulary.NAO;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
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

    @Override
    @SuppressWarnings("unchecked")
    protected ExitCode crawlObjects() {
        final DataSource localSource = getDataSource();
        final RDFContainer configuration = localSource.getConfiguration();
        final String username = ConfigurationUtil.getUsername(configuration);
        final String password = ConfigurationUtil.getPassword(configuration);

        try {
            FlickrCredentials credentials = new FlickrCredentials(API_KEY, password);
            Flickr flickr = credentials.getFlickrInterface();

            PeopleInterface peopleIf = flickr.getPeopleInterface();
            User meUser = peopleIf.findByEmail(username);
            String meId = meUser.getId();

            // FIXME should we uriencode the meId? (probably not necessary)
            // FIXME store flickr data from different accounts in different contexts?
            String contextUriString = "http://www.flickr.com/photos/" + meId +"/";

            String photoUriPrefix = contextUriString;

            PhotosInterface photosIf = flickr.getPhotosInterface();
            // PhotosetsInterface photosetsIf = flickr.getPhotosetsInterface();

            // FIXME Currently, we only download the first 10 images.
            PhotoList pl = peopleIf.getPublicPhotos(meId, 10, 0);
            // PhotoList pl = photosetsIf.getPhotos(meId, 10, 0);
            
            for (Iterator<Photo> it = pl.iterator(); it.hasNext();) {
                Photo photo = it.next();
                // NOTE to get all information, we need to use photosIf
                photo = photosIf.getPhoto(photo.getId(), credentials.secret);

                String id = photo.getId();
                
                // FIXME we could add this as well
                // String license = photo.getLicense();

                final String photoUriString = photoUriPrefix + id+ "/";

                ObjectType objectType;
                
                String timeMillis;
                if (accessData!=null) {
                    accessData.touch(photoUriString);
                    timeMillis = accessData.get(photoUriString, AccessData.DATE_KEY);
                } else { 
                    timeMillis=null; 
                }
                
                if (timeMillis == null) {
                    // FIXME check whether AccessData really works
                    objectType = ObjectType.NEW;
                }
                else {
                    long t = Long.parseLong(timeMillis);
                    Date lastUpdate = photo.getLastUpdate();
                    if (lastUpdate == null || lastUpdate.getTime() > t) {
                        objectType = ObjectType.CHANGED;
                    }
                    else {
                        //FIXME check whether tags implicate a change in lastUpdate
                        objectType = ObjectType.UNMODIFIED;
                    }
                }

                if (objectType == ObjectType.UNMODIFIED) {
                    reportUnmodifiedDataObject(photoUriString);
                    continue;
                }
                
                if (accessData!=null) accessData.put(photoUriString, AccessData.DATE_KEY, Long.toString(System.currentTimeMillis()));

                List<DataObject> dataObjects = new ArrayList<DataObject>();

                DataObject objPhotoIE = newDataObject(dataObjects, photoUriString);
                DataObject objPhotoDOWebsite = newDataObject(dataObjects, photo.getUrl());
                {
                    RDFContainer rdf = objPhotoDOWebsite.getMetadata();
                    rdf.add(RDF.type, NFO.Website);
                    rdf.add(NFO.fileUrl, photo.getUrl());
                    rdf.add(NIE.interpretedAs, objPhotoIE.getID());
                }
                // only attempt downloading original image, if password/secret is set
                if (credentials.secret!=null) {
                    DataObject objPhotoDOOriginalImage = newDataObject(dataObjects, photo.getUrl());
                    RDFContainer rdf = objPhotoDOOriginalImage.getMetadata();
                    rdf.add(RDF.type, NFO.Image);
                     photo.setOriginalSecret(credentials.secret);
                    rdf.add(NFO.fileUrl, photo.getOriginalUrl());
                    rdf.add(NIE.interpretedAs, objPhotoIE.getID());
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
                    rdf.add(NIE.mimeType, "image/jpeg");
                    // FIXME add Owner

                    Collection<Tag> tags = photo.getTags();
                    for (final Tag t : tags) {

//                        String tagsPrefix = "http://www.flickr.com/people/"+t.getAuthor()+"/tags/";
                        String tagsPrefix = photoUriString;
                        final String tagValue = t.getValue();
                        if (tagValue != null) {
                            String tag = tagsPrefix + tagValue;

                            DataObject objTag = newDataObject(dataObjects, tag);
                            rdf.add(NAO.hasTag, objTag.getID());
                            {
                                RDFContainer rdfTag = objTag.getMetadata();
                                rdfTag.add(RDF.type, NAO.Tag);
                                addIfNotNull(rdfTag, NAO.prefLabel, tagValue);
                                // FIXME add Creator
                            }
                        }
                    }
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

                // String format = photo.getOriginalFormat();
                // InputStream in = photosIf.getImageAsStream(photo, Size.ORIGINAL);
                // System.out.println(in+"/"+in.read());
                // in.close();
            }
            return ExitCode.COMPLETED;
        }
        catch (IOException e) {
            LOG.info("Could not crawl Flickr datasource", e);
//            e.printStackTrace();
            return ExitCode.FATAL_ERROR;
        }
        catch (FlickrException e) {
            LOG.info("Could not crawl Flickr datasource", e);
//            e.printStackTrace();
            return ExitCode.FATAL_ERROR;
        }
        catch (SAXException e) {
            LOG.info("Could not crawl Flickr datasource", e);
//            e.printStackTrace();
            return ExitCode.FATAL_ERROR;
        }
        catch (RuntimeException e) {
            LOG.info("Could not crawl Flickr datasource", e);
//            e.printStackTrace();
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
            this(apiKey,null);
        }
        
        public FlickrCredentials(final String apiKey, final String secret) throws IOException,
                FlickrException, SAXException {
            this.secret = secret;
            flickr = new Flickr(apiKey);
            if (secret!=null) RequestContext.getRequestContext().setSharedSecret(secret);

//            AuthInterface authIf = flickr.getAuthInterface();
//            frob = authIf.getFrob();
        }

        public Flickr getFlickrInterface() {
            return flickr;
        }

        public String getFrob() {
            return frob;
        }
    }
}
