package uk.ac.ebi.quickgo.common.converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return toCSV(Arrays.stream(values));
    }

    public static String toCSV(List<String> values) {
        return values == null ? "" : toCSV(values.stream());
    }

    public static String toCSV(Stream<String> stream){
        return stream.collect(Collectors.joining(","));
    }

    public static String toCSV(Stream<String> stream, int quantityRequired){
        return stream.limit(quantityRequired)
                     .collect(Collectors.joining(","));
    }
}
