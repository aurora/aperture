echo off
set APERTURE_JAR_DIR=..\build\dist
set LIB_DIR=..\lib
set LOCALCLASSPATH=
for %%i in ("%APERTURE_JAR_DIR%\*.jar") do call "lcp.bat" %%i
for %%i in ("%LIB_DIR%\*.jar") do call "lcp.bat" %%i
java -classpath %LOCALCLASSPATH% org.semanticdesktop.aperture.examples.inspector.FileInspectorFrame %1
