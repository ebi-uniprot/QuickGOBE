package uk.ac.ebi.quickgo.annotation.common.document;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The fields of an annotation document.
 *
 * Created 14/04/16
 * @author Edd
 */
public class AnnotationFields {

    // all fields from annotations's schema.xml
    public final static String ID = "id";
    public final static String GENE_PRODUCT_ID = "geneProductId";
    public final static String QUALIFIER = "qualifier";
    public final static String GO_ID = "goId";
    public final static String GO_EVIDENCE = "goEvidence";
    public final static String ECO_ID = "ecoId";
    public final static String REFERENCE = "reference";
    public final static String WITH_FROM = "withFrom";
    public final static String TAXON_ID = "taxonId";
    public final static String ASSIGNED_BY = "assignedBy";
    public final static String EXTENSION = "extension";

    /**
     * Annotation fields that are stored, and can therefore be retrieved.
     */
    public static final class Retrievable extends AnnotationFields {
        private static final Set<String> VALUES = new HashSet<>();

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

        public static boolean isSearchable(String field) {
            return VALUES.contains(field);
        }

        public static Set<String> searchableFields() {
            return Collections.unmodifiableSet(VALUES);
        }
    }

    private static String storeAndGet(Set<String> values, String value) {
        values.add(value);
        return value;
    }
}
