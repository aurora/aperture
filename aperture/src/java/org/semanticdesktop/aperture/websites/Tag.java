/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.websites;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Tag-Implementation
 * 
 * @author grimnes, horak, mylka
 */
public class Tag extends AbstractMap<String, String> 
	implements Comparable<Tag>, Map<String, String> {
	
	public static final Logger log = LoggerFactory.getLogger(Tag.class);

	/**
	 * used to implement the map methods only initiated if the map methods are
	 * called
	 */
	private Set<Map.Entry<String, String>> mapSet = null;

	private String uri, name, description, classUri, className;
	
	public Tag(Map<String, String> map)
	{
		super();
		putAll(map);
	}

	public Tag(String uri, String name, String classUri) {
		this.uri = uri;
		this.name = name;
		this.classUri = classUri;
		description = "";
	}

	public Tag(String uri, String name, String classUri, String className) {
		this.uri = uri;
		this.name = name;
		this.classUri = classUri;
		this.className = className;
		description = "";
	}

//	private TagImpl(URI thing, Repository rep) {
//		this.uri=thing.toString();
//		try { 
//			this.name=SesameUtils.getProperty(thing, RDFS.LABEL, rep).toString();
//		} catch(NoValuesException e) {
//			//ignore - leave name=null
//		}
//		try {
//			this.classUri=SesameUtils.getProperty(thing, RDF.TYPE, rep).toString();
//		} catch(NoValuesException e) {
//			//ignore - leave type=null
//		}
////		Classname?
//	}

	@Override
	public int hashCode() {
		if (uri != null) {
			return uri.hashCode();
		} else {
			return super.hashCode();
		}
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o instanceof Tag) {
			Tag tag = (Tag) o;
			return tag.getUri().equals(this.getUri());
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gnowsis.pimo.Tag#getUri()
	 */
	public String getUri() {
		return uri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gnowsis.pimo.Tag#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gnowsis.pimo.Tag#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gnowsis.pimo.Tag#getClassUri()
	 */
	public String getClassUri() {
		return classUri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gnowsis.pimo.Tag#getClassName()
	 */
	public String getClassName() {
		return className;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[(name," + name + ")");
		buffer.append("(uri," + uri + ")");
		buffer.append("(class," + classUri + ")]");
		return buffer.toString();
	}

	/**
	 * Compares two tags lexicographicallly.
	 * If names are equal, URIs are compared
	 * 
	 * @see java.lang.Comparable#compareTo(Object)
	 * @param tag 
	 * @return as per the general {@link Comparable#compareTo(Object)} contract
	 */
	public int compareTo(Tag tag) {
		if (equals(tag)) {
			return 0;
		} else
		{
			int result = getName().compareTo(tag.getName());
			if (result != 0)
				return result;
			result = getUri().compareTo(tag.getUri());
			return result;
		}
	}

	@Override
	public Set<Map.Entry<String, String>> entrySet() {
		if (mapSet == null) {
			mapSet = new TreeSet<Map.Entry<String, String>>();
			// only add if non-null
			if (getUri() != null)
				mapSet.add(new TagEntryEntry("uri", getUri()));
			if (getName() != null)
				mapSet.add(new TagEntryEntry("name", getName()));
			if (getDescription() != null)
				mapSet.add(new TagEntryEntry("description", getDescription()));
			if (getClassUri() != null)
				mapSet.add(new TagEntryEntry("classuri", getClassUri()));
			if (getClassName() != null)
				mapSet.add(new TagEntryEntry("classname", getClassName()));
		}
		return mapSet;
	}
	
	@Override
	public String put(String key, String value) {
		if ("uri".equals(key))
			uri = value;
		else if ("name".equals(key))
			name = value;
		else if ("description".equals(key))
			description = value;
		else if ("classuri".equals(key))
			classUri = value;
		else if ("classname".equals(key))
			className = value;
		else
			// Gunnar calmed this down a bit from warning to fine...
			log.debug("trying to read an undefined parameter '"+key+"' with value '"+value+"'. Discarding this");
		// HACK: the return value should be the previous value
		return null;
	}

	protected class TagEntryEntry implements Map.Entry<String, String>,
			Comparable<TagEntryEntry> {
		String key;

		String value;

		public TagEntryEntry(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public String setValue(String value) {
			throw new UnsupportedOperationException(
					"do not implement setting values in Tag via the map interface");
		}

		public int compareTo(TagEntryEntry o) {
			int result = o.key.compareTo(key);
			if (result != 0)
				return result;
			else {
				if (value == null)
					if (o.value == null)
						return 0;
					else
						return 1;
				else if (o.value == null)
					return 1;
				else
					return o.value.compareTo(value);
			}
		}

	}
}
