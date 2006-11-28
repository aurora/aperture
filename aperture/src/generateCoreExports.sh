#!/bin/bash

CORE_EXPORTS=`jar tf ../build/dist/bundles/aperture-core-2006.1-alpha-3.jar | sed 's/\.java$//' | grep -v Activator | sed 's/\/[^/]*$//' | sed 's/\//./g' | sort | uniq | grep -v 'impl$' | grep semanticdesktop`

echo -n Export-Package: 
echo $CORE_EXPORTS | sed 's/ /\n/g' | sed 's/^/ /' | sed 's/$/,/'


