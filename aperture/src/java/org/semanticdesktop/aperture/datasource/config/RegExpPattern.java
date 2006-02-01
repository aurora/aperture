/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.openrdf.model.Resource;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.semanticdesktop.aperture.datasource.SourceVocabulary;

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

    public Collection getStatements(Resource subject) {
        ArrayList result = new ArrayList();
        
        result.add(new StatementImpl(subject, RDF.TYPE, SourceVocabulary.REGEXP_PATTERN));
        result.add(new StatementImpl(subject, RDF.VALUE,
                new LiteralImpl(getPatternString(), XMLSchema.STRING)));
        
        return result;
    }

}
