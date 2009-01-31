package org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers;

import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.DatatypeValueChecker;

/**
 * Datatype checker for xsd:datetime
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class DateTimeChecker extends DatatypeValueChecker {

    private Pattern pattern = Pattern
        .compile(
            // the date part
            "-?(\\d{4,})-((0[1-9])|1[012])-((0[1-9])|([12]\\d)|(3[01]))" +
            // T in the middle
            "T" +
            // the time part
            "(([01]\\d)|([2][0123])):([012345]\\d):([012345]\\d)(\\.\\d+)?" +
            // the timezone part (optional)
            "(((\\+|-)([012345]\\d):([012345]\\d))|Z)?");
    
    public boolean checkString(String string) {
        return pattern.matcher(string).matches();
    }
    
    public URI getType() {
        return XSD._dateTime;
    }
}
