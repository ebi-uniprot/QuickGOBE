package uk.ac.ebi.quickgo.geneproduct;

/**
 * Enumeration of all the parameters accepted by the gene product search endpoint
 */
public enum GeneProductParameters {
    TYPE_PARAM("type"),
    TAXON_ID_PARAM("taxonId"),
    DB_SUBSET_PARAM("dbSubset"),
    PROTEOME_PARAM("proteome");

    private final String name;

    GeneProductParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
