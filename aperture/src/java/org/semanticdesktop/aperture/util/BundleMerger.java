/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class BundleMerger {

	public static final String MANIFEST_OPTION = "-m";

	public static final String OUTPUT_OPTION = "-o";

	private static final Object ACTIVATOR_OPTION = "-a";

	private Manifest outputManifest;

	private File outputFile;

	private List<JarFile> inputJarFiles;
	
	private List<String> classesToAdd;

	public BundleMerger() {
		inputJarFiles = new LinkedList<JarFile>();
		classesToAdd = new LinkedList<String>();
	}

	public void setOutputManifest(Manifest manifest) throws FileNotFoundException {
		this.outputManifest = manifest;
	}

	public void setOutputFile(File file) {
		this.outputFile = file;
	}

	public void addInputJarFile(JarFile file) {
		inputJarFiles.add(file);
	}
	
	public void addClass(String className) {
		classesToAdd.add(className);
	}

	public void doMerge() throws Exception {
		if (outputManifest == null) {
			throw new Exception("Manifest not specified");
		}

		if (outputFile == null) {
			throw new Exception("Output file not specified");
		}

		if (inputJarFiles.size() == 0) {
			throw new Exception("No input jars specified");
		}
		
		for (String className : classesToAdd) {
			Class activatorClazz = Class.forName(className);
			if (activatorClazz == null) {
				throw new Exception("Couldn't find the class: " + className); 
			} 
		}
		
		List<Manifest> inputManifests = gatherManifests(inputJarFiles);
		Set<String> imports = gatherValues(inputManifests, "Import-Package");
		
		System.out.println(setToString(imports));
		
		removeSelfProvidedImports(imports, inputJarFiles);
		
		System.out.println(setToString(imports));
		
		Set<String> exports = gatherValues(inputManifests, "Export-Package");
		Set<String> classpath = gatherValues(inputManifests, "Bundle-Classpath");
		

		System.out.println(setToString(exports));
		System.out.println(setToString(classpath));

		outputManifest.getMainAttributes().putValue("Import-Package", setToString(imports));
		outputManifest.getMainAttributes().putValue("Export-Package", setToString(exports));
		outputManifest.getMainAttributes().putValue("Bundle-Classpath", setToString(classpath));
		
		JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputFile), outputManifest);
		addFilesToJar(inputJarFiles, jos);

	}

	private void removeSelfProvidedImports(Set<String> imports, List<JarFile> inputJarFiles) {
		Iterator<JarFile> iterator = inputJarFiles.iterator();
		while (iterator.hasNext()) {
			JarFile file = iterator.next();
			Enumeration entries = file.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = (JarEntry) entries.nextElement();
				if (!entry.getName().contains("META-INF") && !entry.isDirectory()) {
					String name = entry.getName();
					name = name.replaceAll("/", ".");
					name = name.substring(0,name.lastIndexOf("."));
					if (name.lastIndexOf("$") != -1) {
						name = name.substring(0,name.lastIndexOf("$"));
					}
					name = name.substring(0,name.lastIndexOf("."));
					imports.remove(name);
				}
			}
		}
	}

	private void addFilesToJar(List<JarFile> files, JarOutputStream jos) throws Exception {
		byte[] buffer = new byte[1024];
		
		for (String className : classesToAdd) {
			addClassToJar(className, jos);
		}
		
		Iterator<JarFile> iterator = files.iterator();
		while (iterator.hasNext()) {
			JarFile file = iterator.next();
			Enumeration entries = file.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = (JarEntry) entries.nextElement();
				if (!entry.getName().contains("META-INF") && !entry.isDirectory()) {
					jos.putNextEntry(entry);
					InputStream is = file.getInputStream(entry);
					int readBytes;
					while ((readBytes = is.read(buffer)) != -1) {
						jos.write(buffer, 0, readBytes);
					}
					is.close();
				}
			}	
		}
		jos.close();
	}

	private void addClassToJar(String className, JarOutputStream jos) throws Exception {
		String resourcePath = className.replaceAll("\\.", "/") + ".class";
		File classFile = new File(ClassLoader.getSystemResource(resourcePath).toString());
		JarEntry jarEntry = new JarEntry(resourcePath);
		jarEntry.setSize(classFile.length());
		jarEntry.setTime(classFile.lastModified());
		jarEntry.setMethod(JarEntry.DEFLATED);
		jos.putNextEntry(jarEntry);
		InputStream is = ClassLoader.getSystemResourceAsStream(resourcePath);
		int readBytes;
		byte [] buffer = new byte[1024];
		while ((readBytes = is.read(buffer)) != -1) {
			jos.write(buffer, 0, readBytes);
		}
		is.close();
	}

	public List<Manifest> gatherManifests(List<JarFile> jarFileList) throws IOException {
		Iterator<JarFile> iterator = jarFileList.iterator();
		List<Manifest> result = new LinkedList<Manifest>();
		while (iterator.hasNext()) {
			JarFile file = iterator.next();
			Manifest manifest = file.getManifest();
			if (manifest != null) {
				result.add(manifest);
			}
		}
		return result;
	}

	private Set<String> gatherValues(List<Manifest> inputManifests, String name) {
		Iterator<Manifest> iterator = inputManifests.iterator();
		Set<String> resultSet = new TreeSet<String>();
		while (iterator.hasNext()) {
			Manifest manifest = iterator.next();
			String current = manifest.getMainAttributes().getValue(name);
			if (current != null) {
				String[] values = current.split(", ");
				for (int i = 0; i < values.length; i++) {
					resultSet.add(values[i]);
				}
			}
		}
		return resultSet;
	}
	
	private String setToString(Set<String> set) {
		StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for (String element : set) {
			if (first) {
				buffer.append(element);
				first = false;
			}
			else {
				buffer.append(", ");
				buffer.append(element);
			}

		}
		return buffer.toString();
	}

	public static void main(String[] args) throws Exception {

		BundleMerger merger = new BundleMerger();

		int i = 0;
		while (i < args.length) {
			String currentArg = args[i];
			if (currentArg.equals(MANIFEST_OPTION)) {
				i++;
				String manifestPath = args[i];
				Manifest manifest = new Manifest(new FileInputStream(new File(manifestPath)));
				merger.setOutputManifest(manifest);
			}
			else if (currentArg.equals(OUTPUT_OPTION)) {
				i++;
				String outputPath = args[i];
				File outputFile = new File(outputPath);
				merger.setOutputFile(outputFile);
			} else if (currentArg.equals(ACTIVATOR_OPTION)) {
				i++;
				String classToAdd = args[i];
				merger.addClass(classToAdd);
			}
			else {
				JarFile jarFile = new JarFile(args[i]);
				merger.addInputJarFile(jarFile);
			}
			i++;
		}

		merger.doMerge();
	}
}
