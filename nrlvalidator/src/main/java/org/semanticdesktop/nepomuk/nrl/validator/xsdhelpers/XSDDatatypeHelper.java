package org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers.BooleanChecker;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers.DateChecker;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers.DateTimeChecker;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers.DayTimeDurationChecker;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers.DecimalChecker;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers.DurationChecker;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers.IntegerChecker;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers.LongChecker;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers.NonNegativeIntegerChecker;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers.PositiveIntegerChecker;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers.StringChecker;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers.YearMonthDurationChecker;

/**
 * Contains helper methods to deal with XSD datatypes.
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class XSDDatatypeHelper {
    
    /**
     * Change this when these constants appear in the XSD vocabulary class
     * @deprecated
     */
    public static final URI XSD_DAY_TIME_DURATION = new URIImpl(XSD.XSD_NS + "dayTimeDuration");

    /**
     * Change this when these constants appear in the XSD vocabulary class
     * @deprecated
     */
    public static final URI XSD_YEAR_MONTH_DURATION = new URIImpl(XSD.XSD_NS + "yearMonthDuration");
    
    static {
        ancestorsMap = prepareAncestorsMap();
        checkersMap = prepareCheckersMap();
    }
    
    private static Map<URI,Set<URI>> ancestorsMap;
    private static CheckersMap checkersMap;
    
    private static class CheckersMap extends HashMap<URI, DatatypeValueChecker> {
        public CheckersMap() {super();}
        public void putChecker(DatatypeValueChecker checker) {
            put(checker.getType(), checker);
        }
    }
    
    /**
     * Returns true if the actual XSD corresponds to the given formal type.
     * @param actualType the actual XSD type
     * @param formalType the formal XSD type
     * @return true if the actual type is equal to the formal type or if the
     *         formal type is on the list of ancestors of the actual type
     */
    public static boolean isCorrect(URI actualType, URI formalType) {
        Set<URI> ancestorsSet = null;
        if (actualType.equals(formalType)) {
            return true;
        } else if ((ancestorsSet = ancestorsMap.get(actualType)) != null){
            return ancestorsSet.contains(formalType);
        } else {
            return false;
        }
    }
    
    /**
     * Checks the given literal if the value conforms to the given datatype.
     * @param literal
     * @return
     */
    public static boolean isCorrectString(DatatypeLiteral literal) {
        URI type = literal.getDatatype();
        String value = literal.getValue();
        DatatypeValueChecker checker = checkersMap.get(type);
        if (checker == null) {
            throw new RuntimeException("No checker found for type " + type);
        } else {
            return checker.checkString(value);
        }
    }

    private static Map<URI, Set<URI>> prepareAncestorsMap() {
        Map<URI, Set<URI>> result = new HashMap<URI, Set<URI>>();
        result.put(XSD._normalizedString, createSet(XSD._string));
        result.put(XSD._token, createSet(XSD._normalizedString,XSD._string));
        result.put(XSD._language, createSet(XSD._token, XSD._normalizedString,XSD._string));
        result.put(XSD._Name, createSet(XSD._token, XSD._normalizedString,XSD._string));
        result.put(XSD._NCName, createSet(XSD._Name,XSD._token, XSD._normalizedString,XSD._string));
        result.put(XSD._ID, createSet(XSD._NCName,XSD._Name,XSD._token, XSD._normalizedString,XSD._string));
        result.put(XSD._IDREF, createSet(XSD._NCName,XSD._Name,XSD._token, XSD._normalizedString,XSD._string));
        result.put(XSD._IDREFS, createSet(XSD._IDREF,XSD._NCName,XSD._Name,XSD._token, XSD._normalizedString,XSD._string));
        result.put(XSD._ENTITY, createSet(XSD._NCName,XSD._Name,XSD._token, XSD._normalizedString,XSD._string));
        result.put(XSD._ENTITIES, createSet(XSD._ENTITY,XSD._NCName,XSD._Name,XSD._token, XSD._normalizedString,XSD._string));
        result.put(XSD._NMTOKEN, createSet(XSD._token, XSD._normalizedString,XSD._string));
        result.put(XSD._NMTOKENS, createSet(XSD._NMTOKEN, XSD._token, XSD._normalizedString,XSD._string));     
        result.put(XSD._integer, createSet(XSD._decimal));
        result.put(XSD._nonPositiveInteger, createSet(XSD._integer,XSD._decimal));
        result.put(XSD._negativeInteger, createSet(XSD._nonPositiveInteger,XSD._integer,XSD._decimal));
        result.put(XSD._long, createSet(XSD._integer,XSD._decimal));        
        result.put(XSD._int, createSet(XSD._long,XSD._integer,XSD._decimal));
        result.put(XSD._short, createSet(XSD._int,XSD._long, XSD._integer,XSD._decimal));
        result.put(XSD._byte, createSet(XSD._short,XSD._int,XSD._long,XSD._integer,XSD._decimal));
        result.put(XSD._nonNegativeInteger, createSet(XSD._integer,XSD._decimal));
        result.put(XSD._unsignedLong, createSet(XSD._nonNegativeInteger, XSD._integer,XSD._decimal));
        result.put(XSD._positiveInteger, createSet(XSD._nonNegativeInteger, XSD._integer,XSD._decimal));
        result.put(XSD._unsignedInt, createSet(XSD._unsignedLong, XSD._nonNegativeInteger, XSD._integer,XSD._decimal));
        result.put(XSD._unsignedShort, createSet(XSD._unsignedInt, XSD._unsignedLong, XSD._nonNegativeInteger, XSD._integer,XSD._decimal));
        result.put(XSD._unsignedByte, createSet(XSD._unsignedShort, XSD._unsignedInt, XSD._unsignedLong, XSD._nonNegativeInteger, XSD._integer,XSD._decimal));
        result.put(XSD_DAY_TIME_DURATION, createSet(XSD._duration));
        result.put(XSD_YEAR_MONTH_DURATION, createSet(XSD._duration));
        return result;
    }
    
    private static CheckersMap prepareCheckersMap() {
        CheckersMap checkers = new CheckersMap();
        checkers.putChecker(new BooleanChecker());
        checkers.putChecker(new DateTimeChecker());
        checkers.putChecker(new DecimalChecker());
        checkers.putChecker(new IntegerChecker());
        checkers.put(XSD._int, new IntegerChecker());
        checkers.putChecker(new LongChecker());
        checkers.putChecker(new NonNegativeIntegerChecker());
        checkers.putChecker(new PositiveIntegerChecker());
        checkers.putChecker(new StringChecker());
        checkers.putChecker(new DateChecker());
        checkers.putChecker(new DurationChecker());
        checkers.putChecker(new DayTimeDurationChecker());
        checkers.putChecker(new YearMonthDurationChecker());
        return checkers;
    }
    
    private static Set<URI> createSet(URI ... uris) {
        Set <URI> result = new HashSet<URI>();
        for (URI uri : uris) {
            result.add(uri);
        }
        return result;
    }
}
