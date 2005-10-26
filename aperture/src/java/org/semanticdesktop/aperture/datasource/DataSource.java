/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.datasource;

import java.util.Properties;

/**
 * A DataSource defines the characteristics of a source from which DataObjects
 * can be extracted. A Datasource contains all information necessary to realize
 * these objects, such as paths, usernames, passwords, etc. The configuration of
 * the datasource is passed to it using the <code>initConfiguration()</code>
 * method. Then, the configuration can be checked using the
 * <code>checkConfiguration()</code>, where the datasource should try to
 * contact the datasource and see if the configuration is working. This is used
 * in the configuration editor user-interface to see if the configuration works
 * and give feedback to the user if passwords, filenames, urls, etc are right.
 */
public interface DataSource {

    /**
     * get the configuration of this datasource.
     * 
     * @return the configuration
     */
    public Properties getConfiguration();

    /**
     * Initialize the datasource using the passed configuration map. each
     * parameter relevant to the datasource (data path, passwords, usernames,
     * timeouts, rules, etc) is passed this method must only be called once.
     * TODO: should it check the configuration (passwords allright, etc) now or
     * when the crawler starts?
     * 
     * @throws InitializationException
     *             when the configuration contains syntactically wrong
     *             parameters, parameters that cannot be parsed. Semantical
     *             errors (not-working urls, etc) are found on first access or
     *             on checkConfiguration().
     */
    public void initConfiguration(Properties configuration)
            throws InitializationException;

    /**
     * check the configuration that was passed in initConfiguration(). The
     * InitializationException should contain a user-friendly message that
     * explains why this datasource does not work. If a password is wrong, the
     * exception should say so. This method is usually called once before the
     * datasource is used or after the user edited the configuration and it
     * should check if passwords and other configs are working.
     * 
     * @throws InitializationException
     *             when the configuration cannot be used to open a datasource
     */
    public void checkConfiguration() throws InitializationException;

    /**
     * Gets the id of this data source. The Aduna Interface identifies
     * DataSources by ID and Name. Gnowsis uses Uris. This ID SHOULD conform to
     * the URI scheme norm! TODO: should it be a URI or not? for future's sake:
     * yes!
     * 
     * @return A URI identifier for the data source.
     */
    public String getID();

    /**
     * Set the ID of this data source.
     * 
     * @param id
     *            the new ID
     */
    public void setID(String id);

    /**
     * Gets the name of this data source.
     * 
     * @return A descriptive name for the data source.
     */
    public String getName();

    /**
     * Sets the name of this data source.
     * 
     * @param name
     *            A descriptive name for the data source.
     */
    public void setName(String name);
}

/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */