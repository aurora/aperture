package org.semanticdesktop.aperture.websites.bibsonomy;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.client.Bibsonomy;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.get.GetPostsQuery;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NAO;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.vocabulary.SWRC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Crawls bibtex entries of a bibsonomy user.
 */
public class BibsonomyCrawler extends CrawlerBase {

	private static final String API_URL = "http://www.bibsonomy.org/api";
	private static final String USERS_API_URL = API_URL + "/users/";
	private static final String TAGS_API_URL = API_URL + "/tags/";
	
	private static final Map<String,URI> bibtexTypesToSwrcTypes = new HashMap<String, URI>();
	static {
		// list taken from http://en.wikipedia.org/wiki/BibTeX
		bibtexTypesToSwrcTypes.put("article", SWRC.Article);
		bibtexTypesToSwrcTypes.put("book", SWRC.Book);
		bibtexTypesToSwrcTypes.put("booklet", SWRC.Booklet);
		//bibtexTypesToSwrcTypes.put("conference", SWRC.Conference); leave it out, 
		//conference is a valid entry type in bibtex, but in SWRC it is NOT a subclass of publication
		bibtexTypesToSwrcTypes.put("inbook", SWRC.InBook);
		bibtexTypesToSwrcTypes.put("incollection", SWRC.InCollection);
		bibtexTypesToSwrcTypes.put("inproceedings", SWRC.InProceedings);
		bibtexTypesToSwrcTypes.put("manual", SWRC.Manual);
		bibtexTypesToSwrcTypes.put("mastersthesis", SWRC.MasterThesis);
		bibtexTypesToSwrcTypes.put("misc", SWRC.Misc);
		bibtexTypesToSwrcTypes.put("phdthesis", SWRC.PhDThesis);
		bibtexTypesToSwrcTypes.put("proceedings", SWRC.Proceedings);
		bibtexTypesToSwrcTypes.put("techreport", SWRC.TechnicalReport);
		bibtexTypesToSwrcTypes.put("unpublished", SWRC.Unpublished);
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected ExitCode crawlObjects() {
		BibsonomyDataSource ds = (BibsonomyDataSource) source;
		String username = ds.getApiusername();
		String apikey = ds.getApikey();
		String crawledUser = ds.getCrawledusername();

		if (username == null || apikey == null) {
			throw new NullPointerException(
					"apiusername, apikey and crawledusername must not be null");
		}

		Bibsonomy bib = new Bibsonomy(username, apikey);
		bib.setApiURL(API_URL);

		int start = 0;
		int found = 0;
		do {
			GetPostsQuery q = new GetPostsQuery(start, start + 50);
			start += 50;
			q.setGrouping(GroupingEntity.USER, crawledUser);
			q.setResourceType(BibTex.class);
			try {
				bib.executeQuery(q);
				if (q.getHttpStatusCode() == 200) {
					List<Post<? extends Resource>> posts = q.getResult();
					found = posts.size();
					for (Post<? extends Resource> post : posts) {
						Resource res = post.getResource();
						if (res instanceof BibTex) {
							processSingleBibtexEntry(post, (BibTex) res);
						}
					}
				} else {
					logger.warn("Couldn't get data from Bibsonomy. Status code: "
							+ q.getHttpStatusCode()
							+ " error: "
							+ q.getError());
					return ExitCode.FATAL_ERROR;
				}
			} catch (IllegalStateException e) {
				logger.warn("Couldn't get data from Bibsonomy", e);
			} catch (ErrorPerformingRequestException e) {
				logger.warn("Couldn't get data from Bibsonomy", e);
			}
		} while (found == 50);

		return ExitCode.COMPLETED;
	}

	/**
	 * Processes a single bibtex entry and passes it to the crawler handler.
	 * 
	 * @param post the post containing the entry
	 * @param res the bibtex entry
	 */
	private void processSingleBibtexEntry(Post<? extends Resource> post, BibTex res) {
		
		URI bibtexUri = getBibtexUri(post,res);
		
		if (accessData != null && accessData.isKnownId(bibtexUri.toString())) {
			reportUnmodifiedDataObject(bibtexUri.toString());
			return;
		}
		
		RDFContainer cont = getRDFContainerFactory(bibtexUri.toString()).getRDFContainer(bibtexUri);
		DataObject object = convertBibtexEntryToDataObject(cont, post, res);
		reportNewDataObject(object);
	}
	
	/**
	 * Converts the bibtex entry to a data object. The metadata rdf container of the returned data object
	 * contains information extracted from the bibtex entry expressed with the SWRC and NAO vocabulary.
	 * @param cont the RDFContainer where the generated metadata should be stored
	 * @param post the post
	 * @param res the bibtex entry contained within that post
	 * @return a data object corresponding to the given bibtex entry
	 */
	public static DataObject convertBibtexEntryToDataObject(RDFContainer cont,
			Post<? extends Resource> post, BibTex res) {
		addBibtexType(res, cont);
		addDateProperty(cont, NIE.contentCreated,post.getDate());
		addStringProperty(cont, NIE.description, post.getDescription());
		addTagListProperty(cont, NAO.hasTag, post);
		addPersonNameListProperty(cont, SWRC.author, res.getAuthorList());
		addStringProperty(cont, SWRC.title, res.getTitle());
		addStringProperty(cont, SWRC.address, res.getAddress());
		addStringProperty(cont, SWRC.abstract_, res.getBibtexAbstract());
		addStringProperty(cont, SWRC.booktitle, res.getBooktitle());
		addStringProperty(cont, SWRC.chapter, res.getChapter());
		addBibtexDateProperty(cont, SWRC.date, res);
		addStringProperty(cont, SWRC.edition, res.getEdition());
		addPersonNameListProperty(cont, SWRC.editor, res.getEditorList());
		addStringProperty(cont, SWRC.howpublished, res.getHowpublished());
		addStringProperty(cont, SWRC.institution, res.getInstitution());
		addStringProperty(cont, SWRC.journal, res.getJournal());
		addStringProperty(cont, SWRC.note, res.getNote());
		addStringProperty(cont, SWRC.number, res.getNumber());
		addStringProperty(cont, SWRC.organization, res.getOrganization());
		addStringProperty(cont, SWRC.pages, res.getPages());
		addStringProperty(cont, SWRC.publisher, res.getPublisher());
		addStringProperty(cont, SWRC.school, res.getSchool());
		addStringProperty(cont, SWRC.series, res.getSeries());
		addStringProperty(cont, SWRC.volume, res.getVolume());
        return new DataObjectBase(cont.getDescribedUri(),null,cont);

		// data not included in the resulting rdf
		//post.getContentId();
		//post.getGroups();
		//post.getUser().getName();
		//res.getBibtexKey()
		//res.getIntraHash();
		//res.getBKey();
		//res.getCount();
		//res.getCrossref();
		//res.getDocuments();
		//res.getInterHash();
	    //res.getMiscFields();
	    //res.getOpenURL();
	    //res.getPosts();
	    //res.getPrivnote();
	    //res.getScraperId();
	    //res.getSimHash0();
	    //res.getSimHash1();
	    //res.getSimHash2();
	    //res.getSimHash3();
	    //res.getUrl();
	}

	private static void addBibtexType(BibTex res, RDFContainer cont) {
		String type = res.getType();
		URI swrcType = null;
		if (type != null) {
			swrcType = bibtexTypesToSwrcTypes.get(res.getType());
		}
		if (swrcType != null) {
			cont.add(RDF.type, swrcType);
		} else {
			cont.add(RDF.type, SWRC.Publication);
		}
	}

	private static void addStringProperty(RDFContainer cont, URI property, String string) {
		if (string != null && string.trim().length() > 0) {
			cont.add(property,stripBraces(string));
		}
	}
	
	private static void addDateProperty(RDFContainer cont, URI property, Date date) {
		if (date != null) {
			cont.add(property,date);
		}
	}
	
	private static void addPersonNameListProperty(RDFContainer cont, URI property, List<PersonName> list) {
	    URI previousLink = null;
	    Model model = cont.getModel();
        for (PersonName pName : list) {
            URI authorUri = UriUtil.generateRandomURI(model);
            model.addStatement(authorUri,RDF.type,NCO.Contact);
            model.addStatement(authorUri,NCO.fullname,pName.getName());
            URI uri = UriUtil.generateRandomURI(model);
            model.addStatement(uri,RDF.type,RDF.List);
            model.addStatement(uri,RDF.first,authorUri);
            if (previousLink == null) {
                model.addStatement(cont.getDescribedUri(),property,uri);
            } else {
                model.addStatement(previousLink,RDF.rest,uri);
            }
            previousLink = uri;
        }
        if (previousLink != null) {
            model.addStatement(previousLink,RDF.rest,RDF.nil);
        }
	}
	
	private static void addTagListProperty(RDFContainer cont, URI property, Post<? extends Resource> post) {
	    Model model = cont.getModel();
	    for (Tag tag : post.getTags()) {
            URI tagUri = model.createURI(TAGS_API_URL + tag.getName());
            model.addStatement(tagUri, RDF.type, NAO.Tag);
            model.addStatement(cont.getDescribedUri(), property, tagUri);
        }
	}
	
	private static void addBibtexDateProperty(RDFContainer cont, URI property, BibTex res) {
	    Calendar cal = new GregorianCalendar(1,1,1);
        if (res.getYear() != null) {
            try {
                cal.set(Calendar.YEAR, Integer.parseInt(res.getYear()));
            } catch (NumberFormatException nfe) {
                // do nothing
            }
        }
        if (res.getMonth() != null) {
            try {
                cal.set(Calendar.MONTH, Integer.parseInt(res.getMonth()));
            } catch (NumberFormatException nfe) {
                // do nothing
            }
        }
        if (res.getDay() != null) {
            try {
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(res.getDay()));
            } catch (NumberFormatException nfe) {
                // do nothing
            }
        }
        if (cal.get(Calendar.YEAR) > 1) {
            cont.add(property,new Date(cal.getTimeInMillis()));
        }
	}

	/**
	 * Returns a uri for the given bibtex entry.
	 * @param post the post containing the entry
	 * @param res the bibtex entry
	 * @return a uri for the given bibtex entry
	 */
	public static URI getBibtexUri(Post<? extends Resource> post, BibTex res) {
		return new URIImpl(USERS_API_URL + post.getUser().getName() + "/posts/" + res.getIntraHash());
	}
	
	private static String stripBraces(String in) {
		if (in == null || in.length() < 2) {
			return in;
		} else if (in.charAt(0) == '{' && in.charAt(in.length() - 1) == '}') {
			return in.substring(1,in.length() - 2);
		} else {
			return in;
		}
	}
}
