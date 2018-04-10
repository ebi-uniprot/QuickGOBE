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

    /**
     * For GAF, there are a limited set of usable values, otherwise show a empty string.
     * @param qualifier
     * @return string representation for GAF format.
     */
    public static String gafQualifierAsString(String qualifier) {

        if (Objects.isNull(qualifier)) {
            return EMPTY_STRING;
        }

        if (qualifier.contains("contributes_to") || qualifier.contains("colocalizes_with")) {
            if (qualifier.contains("not")) {
                return qualifier.replace("not", "NOT");
            } else {
                return qualifier;
            }
        } else {
            if (qualifier.contains("not")) {
                return "NOT";
            }
        }
        return EMPTY_STRING;
    }
}
