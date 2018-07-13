package uk.ac.ebi.quickgo.index.geneproduct;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to aid in the population of the Gene Product objects.
 */
final class GeneProductParsingHelper {
    public static final int DEFAULT_TAXON_ID = 0;

    static final String TAXON_NAME_KEY = "taxon_name";
    static final String PROTEOME_KEY = "proteome";
    static final String IS_ANNOTATED_KEY = "is_annotated";
    static final String DATABASE_SUBSET_KEY = "db_subset";
    static final String TARGET_SET_KEY = "target_set";
    static final String TRUE_STRING = "Y";
    static final String FALSE_STRING = "N";

    private static final Pattern TAXON_ID_PATTERN = Pattern.compile("taxon:([0-9]+)");

    private GeneProductParsingHelper() {}

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
        return Optional.ofNullable(value).map(TAXON_ID_PATTERN::matcher);
    }
}
