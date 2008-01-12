#!/bin/sh
. `dirname ${0}`/lcp.sh
java -classpath $LOCALCLASSPATH junit.swingui.TestRunner org.semanticdesktop.aperture.TestAll
