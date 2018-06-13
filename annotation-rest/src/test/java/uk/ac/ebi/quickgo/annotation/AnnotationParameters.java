package uk.ac.ebi.quickgo.annotation;

/**
 * Enumeration of all the parameters accepted by the annotation search endpoint
 */
public enum AnnotationParameters {
    ASSIGNED_BY_PARAM("assignedBy"),
    GO_EVIDENCE_PARAM("goIdEvidence"),
    REF_PARAM("reference"),
    QUALIFIER_PARAM("qualifier"),
    EVIDENCE_CODE_PARAM("evidenceCode"),
    EVIDENCE_CODE_USAGE_RELATIONS_PARAM("evidenceCodeUsageRelationships"),
    EVIDENCE_CODE_USAGE_PARAM("evidenceCodeUsage"),
    PAGE_PARAM("page"),
    LIMIT_PARAM("limit"),
    TAXON_ID_PARAM("taxonId"),
    TAXON_USAGE_PARAM("taxonUsage"),
    GO_ID_PARAM("goId"),
    GO_USAGE_RELATIONS_PARAM("goUsageRelationships"),
    GO_USAGE_PARAM("goUsage"),
    WITHFROM_PARAM("withFrom"),
    GENE_PRODUCT_ID_PARAM("geneProductId"),
    GENE_PRODUCT_TYPE_PARAM("geneProductType"),
    GP_SUBSET_PARAM("geneProductSubset"),
    TARGET_SET_PARAM("targetSet"), GO_ASPECT_PARAM("aspect"), EXTENSION_PARAM("extension"),
    INCLUDE_FIELD_PARAM("includeFields"), SELECTED_FIELD_PARAM("selectedFields"), PROTEOME_PARAM("proteome");

    private String name;

    AnnotationParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
