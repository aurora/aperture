#!/bin/bash

JAVA_CLASSES=`find ./java -name "*.java"`
IMPORT_LIST=`cat $JAVA_CLASSES | grep '^import' | grep -v 'import java\.' | sed 's/import //' | sed 's/\.[^\.]*$//' | sort | uniq`
IMPORT_LIST_WITHOUT_KNOWN_LIBS=`echo $IMPORT_LIST | sed 's/ /\n/g' | grep -v semanticdesktop | grep -v 'org\.pdfbox' | grep -v 'org\.openrdf' | grep -v 'org\.apache\.commons' | grep -v 'javax\.mail' | grep -v 'com\.jacob' | grep -v 'net\.fortuna\.ical4j' | grep -v 'org\.apache\.poi' | grep -v 'org\.htmlparser' | grep -v 'org\.ontoware\.rdf2go' | grep -v 'org\.ontoware\.aifbcommons' | grep -v 'com\.sun\.mail\.imap' | grep -v static`

echo -n Import-Package:; echo $IMPORT_LIST_WITHOUT_KNOWN_LIBS | sed 's/ /\n/g' |  sed 's/$/, /' | sed 's/^/ /'