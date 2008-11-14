/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.lucenehandler;

import java.io.IOException;
import java.text.ParseException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.util.RDFTool;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerHandlerBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An example handler for Lucene. This class cares for updating a lucene index
 * using a crawler. It uses a passed crawler to see the current state in a data
 * source and then compares the returned set of resources with the stored
 * resources. resources that are in the index but not in the datasource have to
 * be deleted, resources whose dcterms:modified date changed have to be deleted
 * and inserted again and completely new resources have to be inserted.
 * 
 * The updater enhances the documents returned by the crawler by adding
 * information about the crawler to the documents.
 * 
 * @author Sauermann
 */

public class LuceneHandler extends CrawlerHandlerBase implements CrawlerHandler {

	/**
	 * java logger for simplicity
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());

	IndexWriter writer;
	
	String indexDir;
	Analyzer analyzer;

	
	public void init(String indexDir, Analyzer analyzer)
	{
		this.indexDir = indexDir;	
		this.analyzer = analyzer;
	}
	
	public void init(String indexDir)
	{
		init(indexDir, new StandardAnalyzer());
	}


	@Override
	public void clearFinished(Crawler crawler, ExitCode exitCode) {
		super.clearFinished(crawler, exitCode);
	}


	@Override
	public void clearStarted(Crawler crawler) {
		super.clearStarted(crawler);

	}


	@Override
	public void crawlStarted(Crawler crawler) {
		super.crawlStarted(crawler);
//		 check Lucene
		try {
			writer = new IndexWriter(indexDir, analyzer, false);
		} catch (Exception y) {
			logger.info("creating new index");
			try {
				writer = new IndexWriter(indexDir, analyzer, true);
			} catch (Exception e) {
				logger.error("cannot create store, this is a problem: "+e, e);
				throw new RuntimeException(e);
			}
		} 
		// higher merge factor, menasit buffers 50 docs in ram
		writer.setMergeFactor(3);
		// take 1000 resource in mind before writing
		//writer.setMinMergeDocs(1000);

	}


	@Override
	public void objectRemoved(Crawler dataCrawler, String url) {
		try {
			removeUri(new URIImpl(url));
		} catch (IOException e) {
			logger.warn("Error removing object: "+e, e);
		}
	}


	public RDFContainer getRDFContainer(URI uri) {
		Model m = RDF2Go.getModelFactory().createModel();
		m.open();
		return new RDFContainerImpl(m, uri);
	}


	public void crawlStopped(Crawler crawler, ExitCode exitCode) {

		// close writer
		try {
		writer.optimize();
		writer.close();
		} catch (Exception x)
		{
			logger.warn("cannot store data: "+x,x);
		}
		
	}

	public void objectNew(Crawler crawler, DataObject object) {
		// extract full text and more
		try {
			processBinary(object);
		} catch (Exception e) {
			logger.warn("Cannot process "+object+": "+e,e);
		} 
		
		Document doc = objectToDocument(object);
		if (doc != null)
			try {
				logger.debug("adding document to index: " + object.getID());
				writer.addDocument(doc);
			} catch (IOException e1) {
				logger.warn("error storing doc: "+e1, e1);
			}
		object.dispose();
	}

	public void objectChanged(Crawler crawler, DataObject object) {
		// extract full text and more
		try {
			processBinary(object);
		} catch (Exception e) {
			logger.warn("Cannot process "+object+": "+e,e);
		} 
		
		Document doc = objectToDocument(object);
		
		// remove old
		try {
			removeUri(object.getID());
		} catch (IOException e) {
			logger.warn("cannot remove old version of "+object.getID()+": "+e, e);
		}
		if (doc != null)
			try {
				logger.debug("adding document to index: " + object.getID());
				writer.addDocument(doc);
			} catch (IOException e1) {
				logger.warn("Error storing doc: "+e1, e1);
			}
		object.dispose();
	}
	
	private void removeUri(URI id) throws IOException {
		IndexReader indexreader = IndexReader.open(indexDir);
		indexreader.deleteDocuments(new Term("uri", id.toString()));
		indexreader.close();
	}

	/**
	 * minimalistic approach to transforming RDF to plain lucene documents,
	 * with big information loss.
	 * @param object object to store
	 * @return a lucene Document
	 */
	private Document objectToDocument(DataObject object) {
		Document doc = new Document();
		RDFContainer meta = object.getMetadata();
		doc.add(new Field("uri", meta.getDescribedUri().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
		// iterate through all fields
		
		for (ClosableIterator<? extends Statement> i = meta.getModel().findStatements(meta.getDescribedUri(), Variable.ANY, Variable.ANY); i.hasNext(); )
		{
			Statement s = i.next();
			if (s.getObject() instanceof DatatypeLiteral) {
				DatatypeLiteral l = (DatatypeLiteral) s.getObject();
				if (l.getDatatype().equals(XSD._dateTime)){
					String date;
					try {
						date = DateTools.dateToString(RDFTool.string2Date(l.getValue()), Resolution.SECOND);
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
					doc.add(new Field(s.getPredicate().toString(),date,Field.Store.YES, Field.Index.TOKENIZED));
				}
				else {
					doc.add(new Field(s.getPredicate().toString(), l.getValue(), Field.Store.YES, Field.Index.TOKENIZED));				
			}

			}
			else {
				doc.add(new Field(s.getPredicate().toString(), s.getObject().toString(), Field.Store.YES, Field.Index.TOKENIZED));
			}
			
		}
		return doc;
	}



}