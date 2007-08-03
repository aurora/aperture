Aperture Webserver
==================
Date: 3.8.2007
Authors: Benjamin Horak, Leo Sauermann
Copyright: DFKI GmbH

Features:
- extract plaintext and RDF from files

Installation:
- put the WAR into a Tomcat >= 5.5
- restart Tomcat
- go to the website http://yourtomcathost/aperture-webserver/

Planned Features
================
- Datasource configuration
- Crawler
- web search engine? sparql endpoint?
(but this leads to a full Aduna Metadata Server, 
which we don't need to replicate)

Development
===========
- checkout aperture-webserver using Eclipse >= 3.2, you need the J2EE Web Developer Tools (WST)
- get the latest aperture release
- copy all aperture JARs into the WebContent/WEB-INF/lib folder

Building Releases
=================
In Eclipse, click "Export > War File".