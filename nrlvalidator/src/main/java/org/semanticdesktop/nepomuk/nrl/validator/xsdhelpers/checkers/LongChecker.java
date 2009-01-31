package org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers;

import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.DatatypeValueChecker;

/**
 * Datatype checker for xsd:datetime
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class LongChecker extends DatatypeValueChecker {

    private Pattern pattern = Pattern.compile("-?\\d+");
    
    public boolean checkString(String string) {
        if (pattern.matcher(string).matches()) {
            try {
                long parsed = Long.parseLong(string);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false; 
        }
    }
    
    public URI getType() {
        return XSD._long;
    }
}
