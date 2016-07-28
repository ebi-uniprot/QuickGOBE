package uk.ac.ebi.quickgo.common.converter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A useful class for converting stuff.
 *
 * @author Tony Wardell
 * Date: 28/07/2016
 * Time: 13:51
 * Created with IntelliJ IDEA.
 */
public class HelpfulConverter {

    /**
     * Turns "AAA", "BBB", "CCC" into "AAA,BBB,CCC".
     * @param values
     * @return
     */

    public static String toCSV(String... values) {
        return Arrays.stream(values).collect(Collectors.joining(","));
    }
}
