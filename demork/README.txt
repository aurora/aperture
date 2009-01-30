README for Java Demork.
-----------------------


This is a java parser for the super retarded Mork format, as used by Mozilla Browsers for the history file and by the Email client for address book entries.

All the tricky bits I have stolen from somewhere else, in fact this is the 3rd iteration of the code. 

I believe the first version was in perl by Jamie Zawinski <jwz@jwz.org> (http://www.jwz.org/hacks/mork.pl) 

And i found the python script here: https://bugzilla.mozilla.org/show_bug.cgi?id=241438

As the python script says: 

# Original "Mindy.py" copyright: Kumaran Santhanam 
#				 <kumaran@alumni.stanford.org>
#
# Subsequent butchery, demork.py: Mike Hoye 
#				  <mhoye@off.net>
#

I've now done the third version, I converted the python script line by line. Interestingly, in the conversion I've fixed some bug, cause my java version works on the abook_small.mab example in the examples dir, but the python version does not. Oh well. 

Hopefully, someone will soon do the right thing and rip Mork out of mozilla and stamp it dead forever, and replace it by xml, rdf, csv or ANYTHING BUT MORK. 

Usage:
======
Look at the main method in Demork...

Add the jar to your classpath, and do:

Demork demork = new Demork();
Database db = demork.inputMork(data);

XMLOut.outPut(db));
This will give you crappy xml output, but given the DB object it's easy enough to convert it to whatever you want. 
(I will do RDF for another project, drop me a line if you want it) 


-- Gunnar Aastrand Grimnes, gunnar.grimnes@dfki.de


