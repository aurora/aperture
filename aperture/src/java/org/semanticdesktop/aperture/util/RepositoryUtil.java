/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum f�r K�nstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.util.List;
import java.util.Vector;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.Connection;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryImpl;
import org.openrdf.sail.SailInitializationException;
import org.openrdf.sail.memory.MemoryStore;
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
		CloseableIterator<? extends Statement> i = null;
		Connection connection = null;
		try {
			List res;
			res=new Vector();
			
			for (i = connection.getStatements(node,null,null,false);i.hasNext();) {
				Statement s = (Statement) i.next();
				res.add(s);
				if (s.getObject() instanceof BNode) 
					res.addAll(getCBD( (Resource) s.getObject(),rep,backwards));
			}
			i.close();
			
			if (backwards) {
				for (i = connection.getStatements(null,null,node,false);i.hasNext();) {
					Statement s = (Statement) i.next();
					res.add(s);
					if (s.getSubject() instanceof BNode) 
						res.addAll(getCBD( (Resource) s.getObject(),rep,backwards));
				}
				i.close();
			}
				
			return res;
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			closeConnection(connection);
		}
	}
	
	private static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static Repository createSimpleRepository() throws SailInitializationException { 
		Repository rep=new RepositoryImpl(new MemoryStore());
		rep.initialize();
		return rep;
	}
}

