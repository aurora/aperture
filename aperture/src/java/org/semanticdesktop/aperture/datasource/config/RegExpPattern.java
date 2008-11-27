/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.util.ModelUtil;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;

/**
 * A UrlPattern implementation using a regular expression evaluation strategy.
 */
public class RegExpPattern extends UrlPattern {

	private Pattern pattern;

	public RegExpPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public RegExpPattern(String pattern) {
		this(Pattern.compile(pattern));
	}

	public Pattern getPattern() {
		return pattern;
	}

	public String getPatternString() {
		return pattern.pattern();
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = Pattern.compile(pattern);
	}

	public boolean matches(String url) {
		return pattern.matcher(url).matches();
	}

	public Collection<Statement> getStatements(Model model, Resource subject) {
		ArrayList<Statement> result = new ArrayList<Statement>();

		result.add(ModelUtil.createStatement(model, subject, RDF.type, DATASOURCE.RegExpPattern));
        
		try {
            result.add(ModelUtil.createStatement(model, subject, RDF.value, ModelUtil.createLiteral(model,
            	getPatternString(), XSD._string)));
        }
        catch (ModelException e) {
            // creation of a Literal failed, signaling a runtime exception
            throw new RuntimeException(e);
        }

		return result;
	}

    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        
        if(!result && obj instanceof RegExpPattern) {
            RegExpPattern other = (RegExpPattern)obj;
            result = getPatternString().equals(other.getPatternString());
        }
        
        return result;
    }

    @Override
    public int hashCode() {
        return getPatternString().hashCode();
    }

}
