package uk.ac.ebi.quickgo.index.common.datafile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;

/**
 * This class provides stateless methods to help process the contents of gene ontology annotation data files.
 *
 * Created 20/04/16
 * @author Edd
 */
public class GOADataFileParsingHelper {
    public static final String TAB = "\t";
    public static final String PIPE = "|";
    public static final String PIPE_SPLITER = "[|]";
    public static final String COMMA = ",";
    public static final String EQUALS = "=";
    public static final String COLON = ":";
    public static final String PIPE_SEPARATED_CSVs_FORMAT = "(%s(,%s)*)(\\|(%s(,%s)*))*";
    public static final String KEY_EQUALS_VALUE_FORMAT = ".*=.*";
    public static final String WORD_LBRACE_WORD_RBRACE_FORMAT = "[a-zA-Z0-9_:\\.-]+(\\([a-zA-Z0-9_:\\.-]+\\))?";

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

    /**
     * Splits a {@link String} value on occurrences of a {@link String} delimiter.
     * @param value the value to split
     * @param delimiter the delimiter on which splitting takes place
     * @return an array of {@link String} values
     */
    public static String[] splitValue(String value, String delimiter) {
        checkArgument(delimiter != null, "Delimiter cannot be null");

        return Optional.ofNullable(value)
                .filter(v -> !v.isEmpty())
                .map(v -> v.split(delimiter))
                .orElse(new String[0]);
    }

    /**
     * Splits a {@link String} value on occurrences of a {@link String} delimiter, into a list of {@link Integer}s.
     * @param value the value to split
     * @param delimiter the delimiter on which splitting takes place
     * @return a list of {@link Integer} values
     */
    public static List<Integer> splitValueToIntegerList(String value, String delimiter) {
        checkArgument(delimiter != null, "Delimiter cannot be null");

        return Optional.ofNullable(value)
                .filter(v -> !v.isEmpty())
                .map(v -> Stream.of(v.split(delimiter)).map(Integer::new).collect(Collectors.toList()))
                .orElse(emptyList());
    }
}
