/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.config;

import java.util.ArrayList;
import java.util.Collection;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.util.ModelUtil;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * A UrlPattern implementation using a substring test evaluation strategy.
 */
public class SubstringPattern extends UrlPattern {

	private String substring;

	private SubstringCondition condition;

	public SubstringPattern(String substring, SubstringCondition condition) {
		this.substring = substring;
		this.condition = condition;
	}

	public String getSubstring() {
		return substring;
	}

	public void setSubstring(String substring) {
		this.substring = substring;
	}

	public SubstringCondition getCondition() {
		return condition;
	}

	public void setCondition(SubstringCondition condition) {
		this.condition = condition;
	}

	public boolean matches(String url) {
		return condition.test(url, substring);
	}

	public Collection<Statement> getStatements(Model model, Resource subject) {
		ArrayList<Statement> result = new ArrayList<Statement>();

		result.add(ModelUtil.createStatement(model, subject, RDF.type, DATASOURCE_GEN.SubstringPattern));
        
		try {
            result.add(ModelUtil.createStatement(model, subject, RDF.value, ModelUtil.createLiteral(model,
            	substring, XSD._string)));
        }
        catch (ModelException e) {
            // creation of a Literal failed, signaling a runtime exception
            throw new RuntimeException(e);
        }

        
		result.add(ModelUtil.createStatement(model, subject, DATASOURCE_GEN.condition, condition.toNode()));

		return result;
	}
}
