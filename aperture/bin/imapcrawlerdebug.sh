#!/bin/sh
echo "This script runs the IMAP crawler in debug mode"
echo "The standard output has been redirected to imap-debug-output.txt"
. `dirname ${0}`/lcp.sh
java -classpath $LOCALCLASSPATH -Djava.util.logging.config.file=logging.properties -Dmail.debug=true org.semanticdesktop.aperture.examples.ExampleImapCrawler $* > imap-debug-output.txt
