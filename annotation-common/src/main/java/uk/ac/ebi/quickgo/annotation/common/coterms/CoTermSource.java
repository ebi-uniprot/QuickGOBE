package uk.ac.ebi.quickgo.annotation.common.coterms;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * Types of CoTerm data by annotation source.
 *
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 15:49
 * Created with IntelliJ IDEA.
 */
public enum CoTermSource {

    ALL,
    MANUAL;

    /**
     * Test to see if a value exists as a CoTermSource.
     */
    public static boolean isValidValue(String value) {
       return Arrays.stream(CoTermSource.values())
                .map(CoTermSource::name)
                .anyMatch(n -> n.equals(value));
    }

    /**
     * Populate a String of CoTerm source values.
     * @return list of CoTermSource values.
     */
    public static String valuesAsCSV() {
        return Arrays.stream(CoTermSource.values())
                .map(CoTermSource::name)
                .collect(Collectors.joining(", "));
    }
}
