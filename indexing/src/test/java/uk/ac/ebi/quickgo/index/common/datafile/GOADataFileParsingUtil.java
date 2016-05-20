package uk.ac.ebi.quickgo.index.common.datafile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to house common functions used by tests of GOA data file processing.
 *
 * Created 28/04/16
 * @author Edd
 */
public class GOADataFileParsingUtil {
    public static String concatStrings(List<String> values, String delimiter) {
        return values.stream().collect(Collectors.joining(delimiter));
    }

    public static String concatProperty(String key, String value, String delimiter) {
        return key + delimiter + value;
    }
}
