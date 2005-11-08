@echo off
set AP_ROOT=../
set AP_LIB=%AP_ROOT%lib/
set AP_JAR=%AP_ROOT%build/dist/
set CLASSPATH=%CLASSPATH%;%AP_LIB%junit-3.8.1.jar
set CLASSPATH=%CLASSPATH%;%AP_LIB%pdfbox-0.7.2.jar
set CLASSPATH=%CLASSPATH%;%AP_LIB%htmlparser-1.5.jar
set CLASSPATH=%CLASSPATH%;%AP_LIB%openrdf-model-2.0-alpha-1.jar
set CLASSPATH=%CLASSPATH%;%AP_LIB%openrdf-util-2.0-alpha-1.jar
set CLASSPATH=%CLASSPATH%;%AP_LIB%rio-2.0-alpha-1.jar
set CLASSPATH=%CLASSPATH%;%AP_LIB%sesame-2.0-alpha-1.jar
set CLASSPATH=%CLASSPATH%;%AP_LIB%winlaf-0.5.1.jar


set CLASSPATH=%CLASSPATH%;%AP_JAR%aperture-2005.1-dev.jar
set CLASSPATH=%CLASSPATH%;%AP_JAR%aperture-examples-2005.1-dev.jar
set CLASSPATH=%CLASSPATH%;%AP_JAR%aperture-test-2005.1-dev.jar
@echo on

java -cp %CLASSPATH% org.semanticdesktop.aperture.examples.inspector.FileInspectorFrame