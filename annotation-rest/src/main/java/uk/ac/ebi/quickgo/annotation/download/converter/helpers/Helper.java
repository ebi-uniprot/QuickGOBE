package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import java.util.Objects;

/**
 * A holder for general helper code.
 *
 * @author Tony Wardell
 * Date: 09/04/2018
 * Time: 15:02
 * Created with IntelliJ IDEA.
 */
public class Helper {
    private Helper() {}

    /**
     * Return the reference if it is no null or empty, otherwise return an empty string.
     * @param reference a string value.
     * @return the reference or empty string.
     */
    public static String nullToEmptyString(String reference){
        return Objects.nonNull(reference) ? reference : "";
    }
}
