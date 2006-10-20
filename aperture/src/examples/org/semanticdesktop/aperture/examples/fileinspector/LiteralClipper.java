/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.fileinspector;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

/**
 * A LiteralClipper wraps a RDFHandler and makes sure that literals with a label exceeding a certain
 * specified maximum length are clipped appropriately. This is useful when the output of the RDFHandler
 * needs to be displayed in a GUI, which may become unstable when very long lines are displayed (e.g.
 * JTextArea exposes this behaviour).
 */
public class LiteralClipper implements RDFHandler {

    public static final int DEFAULT_MAX_LENGTH = 100;
    
    private static final String SUFFIX = "...";
    
    private RDFHandler wrappedHandler;
    
    private int maxLength;

    public LiteralClipper(RDFHandler handler) {
        wrappedHandler = handler;
        maxLength = DEFAULT_MAX_LENGTH;
    }
    
    public RDFHandler getWrapperHandler() {
        return wrappedHandler;
    }
    
    public void setMaxLength(int maxLength) {
        // negative values are permitted, i.e. they basically disable the effect of this wrapper
        this.maxLength = maxLength;
    }
    
    public int getMaxLength() {
        return maxLength;
    }
    
    public void startRDF() throws RDFHandlerException {
        wrappedHandler.startRDF();
    }

    public void endRDF() throws RDFHandlerException {
        wrappedHandler.endRDF();
    }

    public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
        wrappedHandler.handleNamespace(prefix, uri);
    }

    public void handleStatement(Statement statement) throws RDFHandlerException {
        wrappedHandler.handleStatement(clippedStatement(statement));
    }

    public void handleStatement(Statement statement, Resource context) throws RDFHandlerException {
        wrappedHandler.handleStatement(clippedStatement(statement), context);
    }

    private Statement clippedStatement(Statement statement) {
        Value object = statement.getObject();
        if (object instanceof Literal) {
            Literal oldLiteral = (Literal) object;
            String oldLabel = oldLiteral.getLabel();
            int length = oldLabel.length();
            
            if (length > maxLength) {
                int maxIndex = Math.max(0, maxLength - SUFFIX.length());
                String newLabel = oldLabel.substring(0, maxIndex) + SUFFIX;

                Literal newLiteral;
                String language = oldLiteral.getLanguage();
                if (language == null) {
                    newLiteral = new LiteralImpl(newLabel, oldLiteral.getDatatype());
                }
                else {
                    newLiteral = new LiteralImpl(newLabel, language);
                }
                
                return new StatementImpl(statement.getSubject(), statement.getPredicate(), newLiteral);
            }
        }
        
        return statement;
    }
}
