#!/bin/sh
. `dirname ${0}`/lcp.sh
java -classpath ${LOCALCLASSPATH} org.semanticdesktop.aperture.examples.filecrawler.CrawlerFrame $*
