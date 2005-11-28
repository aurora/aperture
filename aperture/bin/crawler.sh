#!/bin/sh

cd "`dirname "${0}"`/.."
APERTURE_HOME=.

for i in ${APERTURE_HOME}/lib/*.jar
do
  # if the directory is empty, then it will return the input string
  # this is stupid, so case for it
  if [ -f "$i" ] ; then
    if [ -z "$LOCALCLASSPATH" ] ; then
      LOCALCLASSPATH="$i"
    else
      LOCALCLASSPATH="$i":"$LOCALCLASSPATH"
    fi
  fi
done

java -classpath ${LOCALCLASSPATH} org.semanticdesktop.aperture.examples.crawler.CrawlerFrame $*
