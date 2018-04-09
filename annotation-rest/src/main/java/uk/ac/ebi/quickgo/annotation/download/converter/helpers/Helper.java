package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import java.util.function.Function;

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

    public static final Function<String, String> nullToEmptyString = s -> s == null ? "" : s;
}
