package org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers;

import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.DatatypeValueChecker;

/**
 * Datatype checker for xsd:datetime
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class DateChecker extends DatatypeValueChecker {

    private Pattern pattern = Pattern
        .compile("-?(\\d{4,})-((0[1-9])|1[012])-((0[1-9])|([12]\\d)|(3[01]))");
    
    public boolean checkString(String string) {
        return pattern.matcher(string).matches();
    }
    
    public URI getType() {
        return XSD._date;
    }
}
