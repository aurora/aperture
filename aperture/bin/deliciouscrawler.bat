@echo off
set LIB_DIR=..\lib
set LOCALCLASSPATH=
for %%i in ("%LIB_DIR%\*.jar") do call "lcp.bat" %%i
java -classpath %LOCALCLASSPATH% -Djava.util.logging.config.file=logging.properties org.semanticdesktop.aperture.examples.ExampleDeliciousCrawler %*
