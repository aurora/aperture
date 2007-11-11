package org.semanticdesktop.aperture.tagcrawlers.flickr;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.tagcrawlers.AbstractTagCrawler;
import org.semanticdesktop.aperture.tagcrawlers.bibsonomy.BibsonomyDataSource;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.contacts.Contact;
import com.aetrion.flickr.contacts.ContactsInterface;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photosets.Photoset;
import com.aetrion.flickr.photosets.PhotosetsInterface;
import com.aetrion.flickr.tags.Tag;
import com.aetrion.flickr.tags.TagsInterface;

/**
 * @author Daniel Burkhart Aperture Crawler for a FlickrDatasource
 * 
 * crawl a specified Flickr Account for Tags, Images, Friends...
 * 
 */
public class FlickrCrawler extends AbstractTagCrawler {
	
	Flickr flickr;
	User user;
	String username;

	/**
	 * My Flickr API Key
	 */
	private final String API_ID = "f47691529440669449065e6962e1346a";

	private static final Logger LOGGER = Logger.getLogger(FlickrCrawler.class
			.getName());

	private final String FLICKR_URI = "http://www.flickr.com/photos/";

	/** PHOTONS constant */
	public static final String PHOTONS = "http://example.com/photos#";

	/**
	 * A default constructor
	 * @param ds the data source instances with configuration of this crawler
	 */
	public FlickrCrawler(DataSource ds) {
		super();
		setDataSource(ds);
	}

	

	/**
	 * pushes the User objects (of the users friends) into the params-hashmap
	 * 
	 * @param userID
	 *            Flickr ID for the account to crawl
	 * @param localFlickr
	 * @return true, if the crawling was finished successful
	 * @throws FlickrException
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unused", "unchecked" })
    private boolean crawlFriends(String userID, Flickr localFlickr)
			throws IOException, SAXException, FlickrException {
		ContactsInterface contactsInterface = localFlickr.getContactsInterface();
		Collection c = contactsInterface.getPublicList(userID);
		for (Iterator i = c.iterator(); i.hasNext();) {
			Contact contact = (Contact) i.next();
			LOGGER.info("frient found: " + contact.getId() + " ,"
					+ contact.getUsername());
		}
		return true;
	}

	/**
	 * gets a list of tags
	 * 
	 * @param localUsername the username
	 * @param password the password
	 * @return a list of tags
	 * @throws FlickrException
	 * @throws SAXException
	 * @throws IOException
	 * @throws SailUpdateException 
	 * @throws ParserConfigurationException 
	 */
	@SuppressWarnings("unchecked")
    protected List<String> crawlTags(String localUsername, String password)
			throws IOException, SAXException, FlickrException, ParserConfigurationException {
		this.username = localUsername;
				
		LOGGER.fine("Starting import images from Flickr...");

		TagsInterface tags = getFlickr().getTagsInterface();

		Collection<Tag> c = tags.getListUser(getUser().getId());
		LOGGER.fine("Found "+c.size()+" tags for flickr user "+localUsername);
		List<String> res=new Vector<String>();
		for (Tag t: c) {
			String uri = FLICKR_URI + localUsername + "/tags/" + t.getValue();
			res.add(uri);
		}
		return res;
	}
	


	/**
	 * pushes the image objects into the params-hashmap crawls for all images of
	 * the user
	 */
	@SuppressWarnings("unchecked")
    private boolean crawlImages()
			throws IOException, SAXException, FlickrException, ParserConfigurationException {

		// first list all photosets from the user
		Collection c = flickr.getPhotosetsInterface().getList(getUser().getId())
				.getPhotosets();

		// List of all Photosets for a user
		for (Iterator i = c.iterator(); i.hasNext();) {
			Photoset set = (Photoset) i.next();
			String setID = set.getId();
			getPhotosFromSet(setID, getFlickr());
		}

		// list all photos that are not in any photoset and add them
		addPhotosNotInSet();
		return true;
	}

	/**
	 * Add the flickr photos that are not specified in any set
	 * 
	 * @param userID
	 * @param flickr
	 * @throws FlickrException
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
    private void addPhotosNotInSet()
			throws IOException, SAXException, FlickrException {
		int perpage = 500;
		//int count = 0;
		int page = 0;
		//boolean next = true;

		Collection c = flickr.getPhotosInterface().getNotInSet(perpage, page);
		for (Iterator i = c.iterator(); i.hasNext();) {
			Photo photo = (Photo) i.next();
			LOGGER.info("photo found: " + photo.getId() + " ,"
					+ photo.getDescription());
            
            // TODO: report the found photos
		}

	}

	@SuppressWarnings("unchecked")
    private void getPhotosFromSet(String setID, Flickr localFlickr)
			throws IOException, SAXException, FlickrException {
		PhotosetsInterface photosetsInterface = localFlickr.getPhotosetsInterface();
        Collection c = photosetsInterface.getPhotos(setID, 0, Integer.MAX_VALUE);
		for (Iterator i = c.iterator(); i.hasNext();) {
			Photo photo = (Photo) i.next();
			LOGGER.info("photo found: " + photo.getId() + " ,"
					+ photo.getDescription());
			
			// extract data of one photo, handle it
            crawlPhoto(photo);
		}
	}



	@SuppressWarnings("unchecked")
    private void crawlPhoto(Photo photo) throws FlickrException, IOException, SAXException {
        LOGGER.finest("handling original url: " + photo.getOriginalUrl());
        URIImpl uri  = new URIImpl(photo.getOriginalUrl());
        // TODO: was the photo alreadz in the AccessData
        //if (alreadyinAccessData)
        //    return;
        
        RDFContainer cont =  handler.getRDFContainerFactory(this, photo.getUrl()).getRDFContainer(uri);
        cont.add(RDF.type, new URIImpl(PHOTONS+"Photo"));
        String title = photo.getTitle();
        if (title != null)
        {
            cont.put(NIE.title, title);
            cont.put(RDFS.label, title);
        }
        // get the meat, above was only minimal
        photo = flickr.getPhotosInterface().getPhoto(photo.getId(), photo.getSecret());
        // ADD ALL DATA OF PHOTO
        photo.getDateAdded();
        photo.getGeoData();
        // TODO: extract
        
        // EXIF
        // collection of what?
        // http://www.flickr.com/services/api/flickr.photos.getExif.html
        // so you make a big CASE 
        //Collection exif = flickr.getPhotosInterface().getExif(photo.getId(), photo.getSecret());
        
        
        DataObjectBase d = new DataObjectBase(uri, source, cont);
        handler.objectNew(this, d);        
    }



    @Override
	protected void crawlTheRest(String localUsername, String password) throws Exception {
        FlickrDataSource.CrawlType crawlType = ((FlickrDataSource)source).getCrawlType();
        if (crawlType != null && 
            (crawlType.equals(FlickrDataSource.CrawlType.ItemsAndTagsCrawlType) || 
             crawlType.equals(FlickrDataSource.CrawlType.ItemsAndTagsCrawlType))) {
            this.username = localUsername;
            crawlImages();
        }
	}


    /**
     * Returns the instance of the Flickr class used by this crawler. THis method creates a new
     * one if none is available at the moment
     * @return the instnace of the Flickr class used by this crawler
     * @throws ParserConfigurationException
     */
	public Flickr getFlickr() throws ParserConfigurationException {
		if (flickr == null)
		{
			flickr = new Flickr(API_ID, new REST("www.flickr.com", 80));
		}
		return flickr;
	}



	/**
	 * Returns the user whose credentials are being used by this crawler
	 * @return the user whose credentials are being used by this crawler
	 * @throws IOException
	 * @throws SAXException
	 * @throws FlickrException
	 */
	public User getUser() throws IOException, SAXException, FlickrException {
		if (user == null)
		{
			PeopleInterface peopleInterface = flickr.getPeopleInterface();
			// Get the Flickr NSID by the username
			user = peopleInterface.findByUsername(username);
		}
		return user;
	}
	
	




}
