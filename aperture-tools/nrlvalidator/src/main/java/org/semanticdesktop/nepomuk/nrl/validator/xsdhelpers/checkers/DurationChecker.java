package org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers;

import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.DatatypeValueChecker;

/**
 * Datatype checker for xsd:datetime
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class DurationChecker extends DatatypeValueChecker {
    
    private Pattern pattern = Pattern.compile(
        "-?P" +
        "(([0-9]+Y)?([0-9]+M)?([0-9]+D)?)?" +
        "(T([0-9]+H)?([0-9]+M)?([0-9]+(\\.[0-9]+)?S)?)?");
    
    private Pattern negativePattern1 = Pattern.compile("-?PT?");
    
    private Pattern negativePattern2 = Pattern.compile("[^T]*T");
    
    public boolean checkString(String string) {
        return 
            pattern.matcher(string).matches() && 
           !negativePattern1.matcher(string).matches() &&
           !negativePattern2.matcher(string).matches();
    }
    
    public URI getType() {
        return XSD._duration;
    }
}
