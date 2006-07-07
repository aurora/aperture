/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.util.List;
import java.util.Vector;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.sesame.repository.RStatement;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.sesame.sail.SailInitializationException;
import org.openrdf.sesame.sailimpl.memory.MemoryStore;
import org.openrdf.sesame.server.config.RepositoryConfig;
import org.openrdf.util.iterator.CloseableIterator;


/**
 * 
 * 
 * @author grimnes
 * $Id$
 */
public class RepositoryUtil {
	/** 
	 * 
	 * @param node the starting node
	 * @param rep the repository
	 * @param backwards - if this is true we will also traverse nodes (null,null,RESOURCE)
	 * @return a list of statements
	 */
	public static List getCBD(Resource node, Repository rep, boolean backwards) {
		List res;
		res=new Vector();
		CloseableIterator i;
		for (i = rep.getStatements(node,null,null);i.hasNext();) {
			RStatement s = (RStatement) i.next();
			res.add(s);
			if (s.getObject() instanceof BNode) 
				res.addAll(getCBD( (Resource) s.getObject(),rep,backwards));
		}
		i.close();
		
		if (backwards) {
			for (i = rep.getStatements(null,null,node);i.hasNext();) {
				RStatement s = (RStatement) i.next();
				res.add(s);
				if (s.getSubject() instanceof BNode) 
					res.addAll(getCBD( (Resource) s.getObject(),rep,backwards));
			}
			i.close();
		}
			
		return res;
	}
	
	public static Repository createSimpleRepository() throws SailInitializationException { 
		Repository rep=new Repository(new MemoryStore());
		rep.initialize();
		return rep;
	}
}

