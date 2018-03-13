package uk.ac.ebi.quickgo.annotation.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static uk.ac.ebi.quickgo.common.DocumentFieldsHelper.storeAndGet;

/**
 * The fields of an annotation document.
 *
 * Created 14/04/16
 * @author Edd
 */
public class AnnotationFields {

    // fields from annotations's schema.xml
    static final String ID = "id";
    static final String ASSIGNED_BY = "assignedBy";
    static final String SYMBOL = "symbol";
    static final String DATE = "date";
    static final String EVIDENCE_CODE = "evidenceCode";
    static final String EXTENSION = "extension";
    static final String GENE_PRODUCT_ID = "geneProductId";
    static final String GENE_PRODUCT_TYPE = "geneProductType";
    static final String GENE_PRODUCT_SUBSET = "geneProductSubset";
    static final String GO_EVIDENCE = "goEvidence";
    static final String GO_ASPECT = "goAspect";
    static final String GO_ID = "goId";
    static final String QUALIFIER = "qualifier";
    static final String REFERENCE = "reference";
    static final String TARGET_SET = "targetSet";
    static final String TAXON_ID = "taxonId";
    static final String TAXON_ANCESTORS = "taxonAncestors";
    static final String WITH_FROM = "withFrom";
    static final String INTERACTING_TAXON_ID = "interactingTaxonId";
    static final String WITH_FROM_DB = "withFrom_db";

    /**
     * Annotation fields that are indexed, and can therefore be searched.
     */
    public static final class Searchable {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String ASSIGNED_BY = storeAndGet(VALUES, "assignedBy_unsorted");
        public static final String QUALIFIER = storeAndGet(VALUES, "qualifier_unsorted");
        public static final String EVIDENCE_CODE = storeAndGet(VALUES, "evidenceCode_unsorted");
        public static final String GENE_PRODUCT_ID = storeAndGet(VALUES, "geneProductId_search");
        public static final String GENE_PRODUCT_TYPE = storeAndGet(VALUES, "geneProductType_unsorted");
        public static final String GENE_PRODUCT_SUBSET = storeAndGet(VALUES, "geneProductSubset_unsorted");
        public static final String GO_EVIDENCE = storeAndGet(VALUES, "goEvidence_unsorted");
        public static final String GO_ASPECT = storeAndGet(VALUES, "goAspect_search");
        public static final String GO_ID = storeAndGet(VALUES, "goId_unsorted");
        public static final String REFERENCE = storeAndGet(VALUES, "reference_search");
        public static final String TARGET_SET = storeAndGet(VALUES, "targetSet_unsorted");
        public static final String TAXON_ID = storeAndGet(VALUES, "taxonId_unsorted");
        public static final String TAXON_ANCESTORS = storeAndGet(VALUES, "taxonAncestors_unsorted");
        public static final String WITH_FROM = storeAndGet(VALUES, "withFrom_search");
        public static final String EXTENSION = storeAndGet(VALUES, "extension_search");

        public static boolean isSearchable(String field) {
            return VALUES.contains(field);
        }

        public static Set<String> searchableFields() {
            return Collections.unmodifiableSet(VALUES);
        }
    }

    /**
     * Annotation fields that are indexed, and can therefore be searched.
     */
    public static final class Facetable {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String ASSIGNED_BY = storeAndGet(VALUES, AnnotationFields.ASSIGNED_BY);
        public static final String EVIDENCE_CODE = storeAndGet(VALUES, AnnotationFields.EVIDENCE_CODE);
        public static final String GENE_PRODUCT_ID = storeAndGet(VALUES, AnnotationFields.GENE_PRODUCT_ID);
        public static final String GO_ASPECT = storeAndGet(VALUES, AnnotationFields.GO_ASPECT);
        public static final String GO_ID = storeAndGet(VALUES, AnnotationFields.GO_ID);
        public static final String ID = storeAndGet(VALUES, AnnotationFields.ID);
        public static final String REFERENCE = storeAndGet(VALUES, AnnotationFields.REFERENCE);
        public static final String TAXON_ID = storeAndGet(VALUES, AnnotationFields.TAXON_ID);
    }
}
