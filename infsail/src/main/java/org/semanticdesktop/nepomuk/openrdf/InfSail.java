/**
 * Copyright (c) Gunnar Aastrand Grimnes, DFKI GmbH, 2008. 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the distribution.
 *  * Neither the name of the DFKI GmbH nor the names of its contributors may be used to endorse or promote products derived from 
 *    this software without specific prior written permission.
 *    
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 *    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 *    SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 *    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS  
 *    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE  
 *    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.semanticdesktop.nepomuk.openrdf;

import info.aduna.iteration.CloseableIteration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.helpers.SailWrapper;
import org.semanticdesktop.nepomuk.nrl.inference.NRL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author grimnes
 *
 */
public class InfSail extends SailWrapper {

	private InfSailConnection _connection;
	
	/**
	 * This stores mappings from View URI to the metaDataGraph describing that view
	 */
	private Map<Resource,Resource> metaDataGraphs;

	/**
	 * Stores views on graph, i.e. base=>view
	 */
	private Map<URI, Set<View>> views;
	
	private static final boolean DEBUG = false;

	private UnionSail unionsail;

	Logger log=LoggerFactory.getLogger(InfSail.class);


	public InfSail(Sail base) throws SailException {
		super(base);
		if (!(base instanceof UnionSail)) throw new SailException("InfSail must build on top of a unionsail!");
		this.unionsail=(UnionSail)base; 
	}

	@Override
	public SailConnection getConnection() throws SailException {
		return new InfSailConnection(this, unionsail.getConnection());
	}

	void addStrata(URI view, URI strata) throws SailException {
		boolean release = acquireConnection();
		try { 
			_connection.addStatement(view, NrlGraphs.HAS_STRATA, strata, getMetaGraph(view));
			_connection.commit();
			addUnion(view,strata);
		} finally { 
			if (release) releaseConnection();
		}
	}



	/**
	 *  regenerate the imports and inf hashtables
	 * @throws SailException 
	 */  
	private void reIndex() throws SailException {
		unionsail.reset();
		
		metaDataGraphs=new HashMap<Resource, Resource>();

//		// list all meta-data graphs
//		metaDataGraphs=new HashSet<URI>();
//
//		CloseableIteration<? extends Statement, SailException> si = _connection.getStatements(null, RDF.TYPE, NRL.GraphMetadata, false);
//		while (si.hasNext()) {
//			metaDataGraphs.add((URI) si.next().getSubject());
//		}
//		si.close();

		CloseableIteration<? extends Statement, SailException> si = _connection.getStatements(null, NRL.imports, null, false);
		while (si.hasNext()) {
			Statement s = si.next();
			URI target = (URI) s.getSubject(); // TODO: check for cast 
			URI source=(URI) s.getObject();


		}
		si.close();

		views=new HashMap<URI, Set<View>>();

		// TODO: replace with query, this assuming NRL Semantics
		si = _connection.getStatements(null, NRL.viewOn, null, false);
		while (si.hasNext()) {
			Statement s = si.next();
			URI target = (URI) s.getSubject(); // TODO: check for cast 
			URI source=(URI) s.getObject();

			// TODO: This should call createSemanticView instead - this one doesn't create meta-data triples
			if (views.containsKey(source)) {
				views.get(source).add(new View(target,source,SemanticViewSpecification.getNRL(),this));
			} else { 
				Set<View> set=new HashSet<View>(1);
				set.add(new View(target,source,SemanticViewSpecification.getNRL(),this));
				views.put(source, set);
			}

			unionsail.addUnion(target, source);

		}
		si.close();
	}

	public void removeGraph(Resource graph) throws SailException {
		removeGraph(graph, null);
	}

	/**
	 * this is a more intelligent version of connection.clear(graph)
	 * it will respect unions and views  
	 * @param graph
	 * @throws InfSailException when the graph cannot be deleted
	 */
	public void removeGraph(Resource graph, InfSailConnection connection) throws SailException {
		if (unionsail.isInUnion(graph)) 
			throw new InfSailException("You cannot remove a graph that is included in another view/union. At the moment you have to untangel this yourself. Sorry! :)");
		boolean release=false;
		if (connection==null) {
			release=acquireConnection();
			connection=_connection;
		}
		try { 
			for (Entry<URI,Set<View>> e: views.entrySet()) {
				for (View v: e.getValue()) {
					if (v.name==graph) {
						List<URI> s = v.getStratas(_connection);
						if (s.size()>0)
							connection.clear(s.toArray(new URI[s.size()]));
						
						// wipe the meta-data
						connection.clear(metaDataGraphs.get(e.getKey()));
						metaDataGraphs.remove(e.getKey());
						// remove the view entry
						views.remove(e.getKey());
					}
				}
			}
			connection.commit();

			if (unionsail.isUnion(graph)) {
				try {
					unionsail.removeUnion(graph);
				} catch (UnionSailException e) {
					throw new InfSailException(e); 
				}
			} else { 
				connection.clear(graph);
				connection.commit();
			}
		} finally { 
			if (release) releaseConnection();
		}
	}


	public void createSemanticView(URI baseGraph, URI newView, SemanticViewSpecification viewSemantics) throws SailException {
		createSemanticView(baseGraph, newView, viewSemantics, null);
	}

	public void createSemanticView(URI baseGraph, URI newView, SemanticViewSpecification viewSemantics, InfSailConnection connection) throws SailException {
		//if (viewSemantics!=SemanticView.NRL) throw new InfSailException("Only NRL semantics are supported at the moment. ");
		boolean release=false;
		if (connection==null) {
			release = acquireConnection();
			connection=_connection;
		}
		try { 
			URI newMeta = createMetaDataGraph(newView, NRL.KnowledgeBase, connection);
			connection.addStatement(newView, NRL.viewOn, baseGraph, newMeta);
			connection.addStatement(newView, NRL.superGraphOf, baseGraph, newMeta);
			connection.addStatement(newView, NRL.hasSemantics, viewSemantics.uri, newMeta);

			View view = new View(baseGraph,newView,viewSemantics,this);
			addView(view);
			addUnion(newView, baseGraph);

			connection.reinfer(baseGraph);
			connection.commit();
		} finally { 
			if (release) releaseConnection();
		}
	}

	private void addUnion(Resource target, Resource... source) {
		unionsail.addUnion(target, source);

	}

	private void addView(View view) {
		if (views.containsKey(view.base)) {
			views.get(view.base).add(view);
		} else { 
			Set<View> set=new HashSet<View>(1);
			set.add(view);
			views.put(view.base, set);
		}
	}

	public void importGraph(Resource target, Resource ... source) throws SailException {
		importGraph(null, target, source);
	}
	public void importGraph(InfSailConnection connection, Resource target, Resource ... source) throws SailException {

		boolean release = false;
		if (connection==null) {
			release=acquireConnection();
			connection=_connection;
		}

		try { 
			if (DEBUG) {
				if (connection.realSize(target)!=0) {
					throw new InfSailException("Target graph for import is not empty.");
				}
			}
			// TODO: Add meta-data
			addUnion(target, source);

		} finally { 		
			if (release) releaseConnection();
		}
	}

	private void releaseConnection() throws SailException {
		if (_connection!=null) 
			_connection.close();
		_connection=null;
	}

	private boolean acquireConnection() throws SailException {
		if (_connection==null) {
			_connection=(InfSailConnection) getConnection();
			return true;
		}
		return false;
	}

	private URI createMetaDataGraph(URI graph, URI type, InfSailConnection connection) throws SailException {
		URI res=getMetaGraph(graph);
		connection.addStatement(res, RDF.TYPE, NRL.GraphMetadata, res);
		connection.addStatement(res, NRL.metadataOn, graph, res);
		connection.addStatement(graph, RDF.TYPE, type, res);
		metaDataGraphs.put(graph,res);
		return res;
	}

	private URI getMetaGraph(URI graph) {
		return new URIImpl("nrlmeta:"+graph.toString());
	}

	/**
	 * Returns true if there are views of this resource
	 * @param context
	 * @return
	 */
	public boolean hasView(Resource context) {
		return views.containsKey(context);
	}

	/** 
	 * Get all views that are super-graphs of this context
	 * @param context
	 * @return
	 */
	public Set<View> getViews(Resource context) {
		return views.get(context);
	}

//	public InfGraph getInfGraph(View view) throws SailException {
//	Vector<URI> stratas = new Vector<URI>();
//	return new InfGraph(view.source, view.target, view.viewSpec.getRules());
//	}


	/**
	 * Dumps some debug info to stdout
	 *
	 */
	public void debug() {
		System.err.println("InfGraphs:");
		Set<View> allviews=new HashSet<View>();
		for (Set<View> v: views.values()) {
			for (View view: v)
				allviews.add(view);
		}

		for (View view: allviews) {
			System.err.println(view);
		}

		unionsail.debug();


	}

	@Override
	public void initialize() throws SailException {
		super.initialize();
		acquireConnection();
		try {
			reIndex();
		} finally { 
			releaseConnection();
		}
	}


}
