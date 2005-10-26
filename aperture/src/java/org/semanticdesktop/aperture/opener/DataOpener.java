/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.opener;

import java.net.URI;

import org.semanticdesktop.aperture.access.DataObjectNotFoundException;

/**
 * A DataOpener is a handler, tightly coupled to a DataSource, that
 * knows how to open DataObjects (identified by URI) to be seen by the user.
 * The DataOpener will open a file using the operating system or open an email
 * using an email application or open a KOrganizer address book entry using
 * KOrganizer.
 */

public interface DataOpener {
    
  /**
   * opens the passed DataObject in the application that the user
   * typically would use to access the object.
   * @param uri uri of the resource that should be opened
   * @throws an Exception when something goes wrong.
   * @throws ResourceNotFoundException when the uri does not point 
   * to a known resource, perhaps the resource was deleted?
   */
  public void openDataObject(URI uri) throws
      DataObjectNotFoundException, OpeningException;

}


/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */