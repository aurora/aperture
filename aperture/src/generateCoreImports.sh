CORECLASSES=`jar tf ../build/dist/bundles/aperture-core-2006.1-alpha-3.jar | sed 's/^/\.\/java\//' | grep '\.java$' | grep -v Activator`

echo -n "Import-Package: "
grep -e '^import' $CORECLASSES | grep -v 'org\.ontoware\.rdf2go' | grep -v 'org\.ontoware\.aifbcommons' | grep -v 'org\.semanticdesktop\.aperture' | grep -v 'import java\.' | sed 's/.*:import //' | sed 's/\.[^.]*$/,/' | sed 's/^/ /' | sort | uniq | grep -v 'org\.openrdf'
