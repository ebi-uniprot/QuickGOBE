package uk.ac.ebi.quickgo.index.geneproduct;

/**
 * Helper class to house methods used in various test classes.
 *
 * @author Ricardo Antunes
 */
class GeneProductUtil {
    private GeneProductUtil(){}

    static String createUnconvertedTaxonId(int taxonId) {
        return "taxon:" + taxonId;
    }

}
