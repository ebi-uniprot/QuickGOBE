package uk.ac.ebi.quickgo.index.geneproduct;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to aid in the population of the Gene Product objects.
 */
final class GeneProductParsingHelper {
    public static final int DEFAULT_TAXON_ID = 0;

    static final String TAXON_NAME_KEY = "taxon_name";
    static final String COMPLETE_PROTEOME_KEY = "proteome";
    static final String REFERENCE_PROTEOME_KEY = "reference_proteome";
    static final String IS_ANNOTATED_KEY = "is_annotated";
    static final String IS_ISOFORM_KEY = "is_isoform";
    static final String DATABASE_SUBSET_KEY = "db_subsets";

    static final String TRUE_STRING = "Y";
    static final String FALSE_STRING = "N";

    private static final Pattern TAXON_ID_PATTERN = Pattern.compile("taxon:([0-9]+)");

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
                            propMap.put(splitProp[0], splitProp.length == 2 ? splitProp[1] : "");
                        });
            }
        }

        return propMap;
    }

    static String[] splitValue(String value, String delimiter) {
        assert delimiter != null : "Delimiter can not be null";

        String[] splitValues;

        if (value != null && value.trim().length() > 0) {
            splitValues = value.split(delimiter);
        } else {
            splitValues = new String[0];
        }

        return splitValues;
    }

    static int extractTaxonIdFromValue(String value) {
        return taxonIdMatcher(value)
                .filter(Matcher::matches)
                .map(matcher -> Integer.parseInt(matcher.group(1)))
                .orElse(DEFAULT_TAXON_ID);
    }

    static boolean taxonIdMatchesRegex(String value) {
        return taxonIdMatcher(value)
                .map(Matcher::matches)
                .orElse(false);
    }

    private static Optional<Matcher> taxonIdMatcher(String value) {
        Optional<Matcher> matcherOpt;

        if (value != null) {
            matcherOpt = Optional.of(TAXON_ID_PATTERN.matcher(value));
        } else {
            matcherOpt = Optional.empty();
        }

        return matcherOpt;
    }
}