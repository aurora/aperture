The OSGI-enabled version of the sesame onejar have been created as follows:

1. Downloaded the Sesame 2.1.2 onejar release from sourceforge:
http://sourceforge.net/project/showfiles.php?group_id=46509&package_id=168413&release_id=599896
The openrdf-sesame-2.1.2-onejar.jar got copied into this folder

2. Downloaded the bnd tool v. 0.249 from aQute:
http://www.aqute.biz/repo/biz/aQute/bnd/0.0.249/bnd-0.0.249.jar
the bnd got copied into this folder

3. Wrote the bnd file (included in this folder)

4.  With three files in this folder (sesame,bnd jar and the bundle definition) executed
java -jar bnd-0.0.249.jar wrap 
     -output openrdf-sesame-2.1.2-onejar-osgi.jar 
     -properties openrdf-sesame-2.1.2-onejar.bnd 
     openrdf-sesame-2.1.2-onejar.jar
     
(Obviously the entire command needs to go in the same line, the linebreaks have been
included for readability).

5. Copied the resulted openrdf-sesame-2.1.2-onejar-osgi.jar into the lib folder.