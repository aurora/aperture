@echo off
set LIB_DIR=..\lib
set LOCALCLASSPATH=
for %%i in ("%LIB_DIR%\*.jar") do call "lcp.bat" %%i
@echo on
java -classpath %LOCALCLASSPATH% org.semanticdesktop.aperture.examples.crawler.CrawlerFrame %1
