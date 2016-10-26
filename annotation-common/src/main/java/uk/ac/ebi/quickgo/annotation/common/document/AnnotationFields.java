package uk.ac.ebi.quickgo.annotation.common.document;

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
    public final static String ASSIGNED_BY = "assignedBy";
    public final static String QUALIFIER = "qualifier";
    public static final String SYMBOL = "symbol";
    public final static String EVIDENCE_CODE = "evidenceCode";
    public final static String EXTENSION = "extension";
    public final static String GENE_PRODUCT_ID = "geneProductId";
    public static final String GENE_PRODUCT_TYPE = "geneProductType";
    public static final String GENE_PRODUCT_SUBSET = "geneProductSubset";
    public final static String GO_EVIDENCE = "goEvidence";
    public final static String GO_ID = "goId";
    public final static String GO_ID_INDEXED_ORIGINAL = "goId_join";
    public final static String ID = "id";
    public final static String INTERACTING_TAXON_ID = "interactingTaxonId";
    public final static String REFERENCE = "reference";
    public static final String TARGET_SET = "targetSet";
    public static final String TAXON_ID = "taxonId";
    public final static String WITH_FROM = "withFrom";
    public final static String REFERENCE_SEARCH = "referenceSearch";
    public final static String WITH_FROM_SEARCH = "withFromSearch";
    public final static String GO_ASPECT = "goAspect";
    public final static String DATE = "date";

    /**
     * Annotation fields that are stored, and can therefore be retrieved.
     */
    public static final class Retrievable extends AnnotationFields {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String DATE = storeAndGet(VALUES, AnnotationFields.DATE);

        public static boolean isRetrievable(String field) {
            return VALUES.contains(field);
        }

        private static Set<String> retrievableFields() {
            return Collections.unmodifiableSet(VALUES);
        }
    }

    /**
     * Annotation fields that are indexed, and can therefore be searched.
     */
    public static final class Searchable {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String ASSIGNED_BY = storeAndGet(VALUES, AnnotationFields.ASSIGNED_BY);
        public static final String DB_SUBSET = storeAndGet(VALUES, AnnotationFields.GENE_PRODUCT_SUBSET);
        public static final String EVIDENCE_CODE = storeAndGet(VALUES, AnnotationFields.EVIDENCE_CODE);
        public static final String GENE_PRODUCT_ID = storeAndGet(VALUES, AnnotationFields.GENE_PRODUCT_ID);
        public static final String GENE_PRODUCT_TYPE = storeAndGet(VALUES, AnnotationFields.GENE_PRODUCT_TYPE);
        public static final String GO_EVIDENCE = storeAndGet(VALUES, AnnotationFields.GO_EVIDENCE);
        public static final String GO_ID = storeAndGet(VALUES, AnnotationFields.GO_ID);
        public static final String GO_ID_INDEXED_ORIGINAL =
                storeAndGet(VALUES, AnnotationFields.GO_ID_INDEXED_ORIGINAL);
        public static final String QUALIFIER = storeAndGet(VALUES, AnnotationFields.QUALIFIER);
        public static final String REFERENCE_SEARCH = storeAndGet(VALUES, AnnotationFields.REFERENCE_SEARCH);
        public static final String TARGET_SET = storeAndGet(VALUES, AnnotationFields.TARGET_SET);
        public static final String TAXON_ID = storeAndGet(VALUES, AnnotationFields.TAXON_ID);
        public static final String WITH_FROM_SEARCH = storeAndGet(VALUES, AnnotationFields.WITH_FROM_SEARCH);
        public static final String GO_ASPECT = storeAndGet(VALUES, AnnotationFields.GO_ASPECT);

        public static boolean isSearchable(String field) {
            return VALUES.contains(field);
        }

        public static Set<String> searchableFields() {
            return Collections.unmodifiableSet(VALUES);
        }
    }
}
