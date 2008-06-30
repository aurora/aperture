#!/bin/sh
# UNTESTED, may have bugs or wreck your system. After somebody has tested this,
# please remove comment and read-command. Leo Sauermann, 30.6.2008
read -p "This script is untested and may not work or cause trouble. Kill it or press any key to continue..."
. `dirname ${0}`/lcp.sh
java -classpath $LOCALCLASSPATH -Djava.util.logging.config.file=logging.properties org.semanticdesktop.aperture.examples.ExampleAppleAddressbookCrawler -v -o addressbook.rdf