package uk.ac.ebi.quickgo.index.common.datafile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides stateless methods to help process the contents of gene ontology annotation data files.
 *
 * Created 20/04/16
 * @author Edd
 */
public class GOADataFileParsingHelper {
    public static final String TAB = "\t";
    public static final String PIPE = "\\|";
    public static final String COMMA = ",";
    public static final String EQUALS = "=";
    public static final String COLON = ":";

    /**
     * Converts a string of key/value pairs into a corresponding {@link Map}.
     *
     * @param propsText the string containing the key value pairs
     * @param interValueDelimiter the delimiter that splits the pairs from each other
     * @param intraValueDelimiter the delimiter that split the key from the value within a pair
     * @return a map of key/value pairs
     */
    public static Map<String, String> convertLinePropertiesToMap(String propsText, String interValueDelimiter, String
            intraValueDelimiter) {
        assert interValueDelimiter != null : "InterValueDelimiter cannot be null";
        assert intraValueDelimiter != null : "IntraValueDelimiter cannot be null";

        Map<String, String> propMap = new HashMap<>();

        if (propsText != null) {

            String[] unformattedProps = splitValue(propsText, interValueDelimiter);

            if (propsText.length() > 0) {
                Arrays.stream(unformattedProps)
                        .forEach(unformattedProp -> {
                            String[] splitProp = splitValue(unformattedProp, intraValueDelimiter);
                            propMap.put(splitProp[0], splitProp.length == 2 ? splitProp[1] : "");
                        });
            }
        }

        return propMap;
    }

    public static String[] splitValue(String value, String delimiter) {
        assert delimiter != null : "Delimiter cannot be null";

        String[] splitValues;

        if (value != null && value.trim().length() > 0) {
            splitValues = value.split(delimiter);
        } else {
            splitValues = new String[0];
        }

        return splitValues;
    }
}
