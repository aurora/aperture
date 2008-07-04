/*
 * Copyright (c) 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.detector.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.datasource.config.RegExpPattern;
import org.semanticdesktop.aperture.datasource.config.UrlPattern;
import org.semanticdesktop.aperture.datasource.filesystem.FILESYSTEMDS;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.detector.DataSourceDescription;
import org.semanticdesktop.aperture.detector.DataSourceDetector;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.OSUtils;
import org.semanticdesktop.aperture.util.RegistryReader;
import org.semanticdesktop.aperture.util.UriUtil;

/**
 * Detect the "my documents" folder of the user.
 * On the mac, this is "~/Documents", on windows the "my documents" folder,
 * etc.
 * @author sauermann, 2.7.2008 
 * @author grimnes
 */
public class HomeFolderDetector implements DataSourceDetector {

	private List<UrlPattern> getExcludes() {
		List<UrlPattern> res=new Vector<UrlPattern>();
		res.add(new RegExpPattern("^\\.svn"));
		// anything else? 
		return res;
	}

	private String getWinDir() {
		return RegistryReader.getCurrentUserPersonalFolderPath();
	}

	private String getMacDir() {
		return System.getProperty("user.home")+File.separator+"Documents";
	}

	private String getLinuxDir() {
	    String home = System.getProperty("user.home");
	    File f = new File(home+File.separator+"Documents");
	    if (f.exists())
	        // on most *nixes the ~/Documents folder is used for user's documents 
	        return f.getAbsolutePath();
	    else
	        return home;
	}

    public List<DataSourceDescription> detect() throws Exception {
        /**
         * Herko ter Horst suggested to use this:
         * javax.swing.filechooser.FileSytemView.getDefaultDirectory().
         * 
         * if the current code is not working, we may try this.
         */
        
        String path;
        if (OSUtils.isLinux()) {
            path=getLinuxDir();
        } else if (OSUtils.isMac()) {
            path=getMacDir();
        } else if (OSUtils.isWindows()) {
            path=getWinDir();
        }
        else {
            throw new Exception("Cannot detect your home-folder on your operating system: "+ System.getProperty("os.name")+
                ". Linux, windows, mac are supported, please add your document folder manually.");
        }
        
        FileSystemDataSource ds = new FileSystemDataSource();
        Model m=RDF2Go.getModelFactory().createModel();
        m.open();
        URI id = UriUtil.generateRandomURI(m);
        ds.setConfiguration(new RDFContainerImpl(m, id));
        ds.setName("My Documents");
        ds.setComment("This datasource will crawl your files from the folder "+path);
        ds.setRootFolder(path);
        
        ds.setDomainBoundaries(new DomainBoundaries(Collections.EMPTY_LIST, getExcludes()));
        ArrayList<DataSourceDescription> result = new ArrayList<DataSourceDescription>(1);
        result.add(new DataSourceDescription(ds));
        return result;
    }

    public org.ontoware.rdf2go.model.node.URI getSupportedType() {
        return FILESYSTEMDS.FileSystemDataSource;
    }

}
