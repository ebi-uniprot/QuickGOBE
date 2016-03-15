package uk.ac.ebi.quickgo.index.geneproduct;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to aid in the population of the Gene Product objects.
 */
public final class GeneProductParsingHelper {
    static final String TAXON_NAME_KEY = "taxon_name";
    static final String COMPLETE_PROTEOME_KEY = "proteome";
    static final String REFERENCE_PROTEOME_KEY = "reference_proteome";
    static final String IS_ANNOTATED_KEY = "is_annotated";
    static final String IS_ISOFORM = "is_isoform";
    static final String DATABASE_SUBSET_KEY = "db_subsets";

    static final String TRUE_STRING = "Y";
    static final String FALSE_STRING = "N";


    private GeneProductParsingHelper() {}

    /**
     * Converts a string of key value pairs into a map.
     *
     * @param propsText the string containing the key value pairs
     * @param interValueDelimiter the delimiter that splits the pairs from each other
     * @param intraValueDelimiter the delimiter that split the key from the value within a pair
     * @return a list of key value pairs
     */
    static Map<String, String> convertToMap(String propsText, String interValueDelimiter, String intraValueDelimiter) {
        assert interValueDelimiter != null : "InterValueDelimiter can not be null";
        assert intraValueDelimiter != null : "IntraValueDelimiter can not be null";

        Map<String, String> propMap = new HashMap<>();

        if (propsText != null) {

            String[] unformattedProps = splitValue(propsText, interValueDelimiter);

            if (propsText.length() > 0) {
                Arrays.stream(unformattedProps)
                        .forEach(unformattedProp -> {
                            String[] splitProp = splitValue(unformattedProp, intraValueDelimiter);
                            propMap.put(splitProp[0], splitProp[1]);
                        });
            }
        }

        return propMap;
    }

    static String[] splitValue(String value, String delimiter) {
        assert delimiter != null : "Delimiter can not be null";

        String[] splitValues;

        if (value != null) {

            splitValues = value.split(delimiter);
        } else {
            splitValues = new String[0];
        }

        return splitValues;
    }
}