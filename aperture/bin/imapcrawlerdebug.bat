@echo off
echo This script runs the IMAP crawler in debug mode
echo The standard output has been redirected to imap-debug-output.txt
set LIB_DIR=..\lib
set LOCALCLASSPATH=
for %%i in ("%LIB_DIR%\*.jar") do call "lcp.bat" %%i
java -classpath %LOCALCLASSPATH% -Djava.util.logging.config.file=logging.properties -Dmail.debug=true org.semanticdesktop.aperture.examples.ExampleImapCrawler %* > imap-debug-output.txt
