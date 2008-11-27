/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource.config;

import org.ontoware.rdf2go.model.node.Node;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;

/**
 * Instances of this class indicate how a substring test needs to be performed and are able to evaluate
 * the test. Subclasses embody a particular kind of substring test, e.g. "starts with", "ends with" or
 * "contains".
 */
public abstract class SubstringCondition {

    public static final StartsWith STARTS_WITH = new StartsWith();
    
    public static final EndsWith ENDS_WITH = new EndsWith();
    
    public static final Contains CONTAINS = new Contains();
    
    public static final DoesNotContain DOES_NOT_CONTAIN = new DoesNotContain();
    
    /**
     * Tests the substring condition embodied by the implementing class on a String.
     * 
     * @param string The String to test the substring condition on.
     * @param substring The String to test the substring condition with.
     * @return 'true' when the string contains the substring in the way embodied by the implementing
     *         class, 'false' otherwise.
     */
    public abstract boolean test(String string, String substring);

    /**
     * Return the Value used to encode this SubstringCondition in an RDF model.
     */
    public abstract Node toNode();
    
    public static class StartsWith extends SubstringCondition {

        public String toString() {
            return "StartsWith";
        }
        
        public boolean test(String string, String substring) {
            return string.startsWith(substring);
        }
        
        public Node toNode() {
            return DATASOURCE.STARTS_WITH;
        }
    }
    
    public static class EndsWith extends SubstringCondition {

        public String toString() {
            return "EndsWith";
        }
        
        public boolean test(String string, String substring) {
            return string.endsWith(substring);
        }
        
        public Node toNode() {
            return DATASOURCE.ENDS_WITH;
        }
    }
    
    public static class Contains extends SubstringCondition {

        public String toString() {
            return "Contains";
        }
        
        public boolean test(String string, String substring) {
            return string.indexOf(substring) >= 0;
        }
        
        public Node toNode() {
            return DATASOURCE.CONTAINS;
        }
    }
    
    public static class DoesNotContain extends SubstringCondition {

        public String toString() {
            return "DoesNotContain";
        }
        
        public boolean test(String string, String substring) {
            return string.indexOf(substring) < 0;
        }
        
        public Node toNode() {
            return DATASOURCE.DOES_NOT_CONTAIN;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        
        if(!result && obj instanceof SubstringCondition) {
            SubstringCondition other = (SubstringCondition)obj;
            result = toString().equals(other.toString());
        }
        
        return result;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
}
