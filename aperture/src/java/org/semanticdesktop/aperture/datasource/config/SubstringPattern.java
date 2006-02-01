/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.config;

import java.util.ArrayList;
import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.semanticdesktop.aperture.datasource.SourceVocabulary;

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

    public Collection getStatements(Resource subject) {
        ArrayList result = new ArrayList();

        result.add(new StatementImpl(subject, RDF.TYPE, SourceVocabulary.SUBSTRING_PATTERN));
        result.add(new StatementImpl(subject, RDF.VALUE, new LiteralImpl(substring, XMLSchema.STRING)));
        result.add(new StatementImpl(subject, SourceVocabulary.CONDITION, condition.toValue()));

        return result;
    }
}
