This jar contains unmodified classes from the ant.jar archive shipped with the
1.7.1 ant distribution. To recreate it:

Download the Ant 1.7.1 distribution from here (or any other apache mirror):
http://www.apache.net.pl/ant/binaries/apache-ant-1.7.1-bin.zip   

Unzip it somewhere

Within the folder you unzipped the archive to, go to the lib/ subfolder and open
the ant.jar with some archiving utility (I used WinRAR, but WinZIP, compressed
folders view, konqueror etc. - all should be ok). 

Delete all packages within the archive with the exception of:
org.apache.tools.bzip2
org.apache.tools.tar