package uk.ac.ebi.quickgo.annotation.controller;

/**
 * Enumeration of all the parameters accepted by the annotation search endpoint
 */
enum AnnotationParameters {
    ASSIGNED_BY_PARAM("assignedBy"),
    GO_EVIDENCE_PARAM("goIdEvidence"),
    REF_PARAM("reference"),
    QUALIFIER_PARAM("qualifier"),
    EVIDENCE_CODE_PARAM("evidenceCode"),
    PAGE_PARAM("page"),
    LIMIT_PARAM("limit"),
    TAXON_ID_PARAM("taxonId"),
    GO_ID_PARAM("goId"),
    WITHFROM_PARAM("withFrom"),
    GENE_PRODUCT_ID_PARAM("geneProductId"),
    GENE_PRODUCT_TYPE_PARAM("geneProductType"),
    GP_SUBSET_PARAM("geneProductSubset"),
    TARGET_SET_PARAM("targetSet"),
    GO_ASPECT_PARAM("aspect");

    private String name;

    AnnotationParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}