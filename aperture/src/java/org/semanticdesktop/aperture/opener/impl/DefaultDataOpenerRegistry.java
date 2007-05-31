/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticdesktop.aperture.opener.DataOpenerFactory;
import org.semanticdesktop.aperture.opener.DataOpenerRegistry;
import org.semanticdesktop.aperture.opener.file.FileOpenerFactory;
import org.semanticdesktop.aperture.opener.http.HttpOpenerFactory;


public class DefaultDataOpenerRegistry implements DataOpenerRegistry {

	Set openers;
	
	public DefaultDataOpenerRegistry() { 
		openers=new HashSet();
		openers.add(new FileOpenerFactory());
		openers.add(new HttpOpenerFactory());
	}
	
	public void add(DataOpenerFactory factory) {
		openers.add(factory);
	}

	public void remove(DataOpenerFactory factory) {
		openers.remove(factory);
	}

	public Set getAll() {
		return openers;
	}

	public Set get(String scheme) {
		Set res=new HashSet();
		for (Iterator i=openers.iterator();i.hasNext();) {
			DataOpenerFactory dof=(DataOpenerFactory) i.next();
			if (dof.getSupportedSchemes().contains(scheme))
				res.add(dof);
		}
		return res;
	}
	
}

