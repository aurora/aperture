#!/bin/sh
. `dirname ${0}`/lcp.sh
java -classpath $LOCALCLASSPATH -Djava.util.logging.config.file=logging.properties junit.swingui.TestRunner org.semanticdesktop.aperture.TestAll
