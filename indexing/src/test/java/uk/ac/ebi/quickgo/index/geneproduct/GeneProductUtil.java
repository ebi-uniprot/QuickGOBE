package uk.ac.ebi.quickgo.index.geneproduct;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class that houses several methods used in various tests classes.
 *
 * @author Ricardo Antunes
 */
class GeneProductUtil {
    private GeneProductUtil(){}

    static String createUnconvertedTaxonId(int taxonId) {
        return "taxon:" + taxonId;
    }

    static String concatStrings(List<String> values, String delimiter) {
        return values.stream().collect(Collectors.joining(delimiter));
    }

    static String concatProperty(String key, String value, String delimiter) {
        return key + delimiter + value;
    }
}
