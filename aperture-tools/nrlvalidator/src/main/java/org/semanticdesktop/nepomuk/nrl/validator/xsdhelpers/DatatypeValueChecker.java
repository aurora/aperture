package org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers;

import org.ontoware.rdf2go.model.node.URI;

/**
 * Checks if the string value conforms to the datatype represented by this checker.
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public abstract class DatatypeValueChecker {

    /**
     * Checks the string for conformance with the constraints alloted to this
     * datatype.
     * @param string the string to be checked
     * @return true if the string is valid for this datatype, false otherwise
     * 
     */
    public abstract boolean checkString(String string);
    
    /**
     * Returns the URI of the XSD datatype this checker belongs to.
     * @return the URI of the XSD datatype this checker belongs to.
     */
    public abstract URI getType();
    
}
