package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

/**
 * A home for methods to represent the taxon id as something other than a simple number.
 */
public class Taxon {

    private static final String PIPE = "|";
    private static final String TAXON = "taxon:";
    private static final int MINIMUM_TAX_ID = 0;

    public static String taxonIdToCurie(int taxId, int interactingTaxonId) {
        StringBuilder taxonBuilder = new StringBuilder();
        taxonBuilder.append(taxId > MINIMUM_TAX_ID ? TAXON + taxId : "")
                .append((taxId > MINIMUM_TAX_ID) && (interactingTaxonId > MINIMUM_TAX_ID) ? PIPE : "")
                .append(interactingTaxonId > MINIMUM_TAX_ID ? TAXON + interactingTaxonId : "");
        return taxonBuilder.toString();
    }

    public static String taxonIdToString(int taxId) {
        return taxId > MINIMUM_TAX_ID ? Integer.toString(taxId) : "";
    }
}
