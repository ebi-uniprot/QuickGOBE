package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import java.util.Objects;

/**
 /**
 * Return a version of the qualifier suitable for output to the user, replacing nulls with empty strings, and
 * ensuring the 'not' appears in upper case.
 * @author Tony Wardell
 * Date: 10/04/2018
 * Time: 10:46
 * Created with IntelliJ IDEA.
 */
public class Qualifier {

    private static final String EMPTY_STRING = "";
    private static final String CONTRIBUTES_TO = "contributes_to";
    private static final String COLOCALIZES_WITH = "colocalizes_with";
    private static final String NOT_LOWERCASE = "not";
    private static final String NOT_UPPERCASE = "NOT";

    private Qualifier() {}

    /**
     * For GAF, there are a limited set of usable values, otherwise show a empty string.
     * @param qualifier input string
     * @return string representation for GAF format.
     */
    public static String gafQualifierAsString(String qualifier) {

        if (Objects.isNull(qualifier)) {
            return EMPTY_STRING;
        }

        String lcQualifier = qualifier.toLowerCase();

        if (lcQualifier.contains(CONTRIBUTES_TO) || lcQualifier.contains(COLOCALIZES_WITH)) {
            if (lcQualifier.contains(NOT_LOWERCASE)) {
                return showNotInUpperCase(qualifier);
            } else {
                return qualifier;
            }
        } else {
            //For the qualifiers that are not displayed, if they are negated, then show (only the not as) NOT
            if (lcQualifier.contains(NOT_LOWERCASE)) {
                return NOT_UPPERCASE;
            }
        }
        return EMPTY_STRING;
    }

    private static String showNotInUpperCase(String qualifier) {
        return qualifier.replace(NOT_LOWERCASE, NOT_UPPERCASE);
    }
}
