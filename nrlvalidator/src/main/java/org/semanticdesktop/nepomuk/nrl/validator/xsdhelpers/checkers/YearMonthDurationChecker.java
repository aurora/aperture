package org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers;

import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.DatatypeValueChecker;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.XSDDatatypeHelper;

/**
 * Datatype checker for xsd:yearMonthDuration
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class YearMonthDurationChecker extends DatatypeValueChecker {
    
    private Pattern pattern = Pattern.compile(
        "-?P" +
        "([0-9]+Y)?([0-9]+M)?");
    
    private Pattern negativePattern1 = Pattern.compile("-?P");
    
    public boolean checkString(String string) {
        return 
            pattern.matcher(string).matches() && 
           !negativePattern1.matcher(string).matches();
    }
    
    public URI getType() {
        return XSDDatatypeHelper.XSD_YEAR_MONTH_DURATION;
    }
}
