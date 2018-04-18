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

        if (qualifier.contains("contributes_to") || qualifier.contains("colocalizes_with")) {
            if (qualifier.contains("not")) {
                return showNotInUpperCase(qualifier);
            } else {
                return qualifier;
            }
        } else {
            //For the qualifiers that are not displayed, if they are negated, then show (only the not as) NOT
            if (qualifier.contains("not")) {
                return "NOT";
            }
        }
        return EMPTY_STRING;
    }

    private static String showNotInUpperCase(String qualifier) {
        return qualifier.replace("not", "NOT");
    }
}
