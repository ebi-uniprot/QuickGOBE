package uk.ac.ebi.quickgo.geneproduct.common.document;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static uk.ac.ebi.quickgo.common.DocumentFieldsHelper.storeAndGet;

/**
 * The fields of a Gene Product document.
 *
 * @author Ricardo Antunes
 */
public class GeneProductFields {
    public static final String ID = "id";
    public static final String DATABASE = "database";
    public static final String SYMBOL = "symbol";
    public static final String NAME = "name";
    public static final String SYNONYM = "synonym";
    public static final String TYPE = "type";
    public static final String TAXON_ID = "taxonId";
    public static final String TAXON_NAME = "taxonName";
    public static final String DATABASE_SUBSET = "dbSubset";
    public static final String COMPLETE_PROTEOME = "isCompleteProteome";
    public static final String REFERENCE_PROTEOME = "referenceProteome";
    public static final String IS_ISOFORM = "isIsoform";
    public static final String IS_ANNOTATED = "isAnnotated";
    public static final String PARENT_ID = "parentId";
    public static final String TARGET_SET = "targetSet";

    /**
     * GeneProduct fields that are stored, and can therefore be retrieved.
     */
    public static final class Retrievable extends GeneProductFields {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String ID = storeAndGet(VALUES, GeneProductFields.ID);
        public static final String DATABASE = storeAndGet(VALUES, GeneProductFields.DATABASE);
        public static final String SYMBOL = storeAndGet(VALUES, GeneProductFields.SYMBOL);
        public static final String NAME = storeAndGet(VALUES, GeneProductFields.NAME);
        public static final String SYNONYM = storeAndGet(VALUES, GeneProductFields.SYNONYM);
        public static final String TYPE = storeAndGet(VALUES, GeneProductFields.TYPE);
        public static final String TAXON_ID = storeAndGet(VALUES, GeneProductFields.TAXON_ID);
        public static final String DATABASE_SUBSET = storeAndGet(VALUES, GeneProductFields.DATABASE_SUBSET);

        public static boolean isRetrievable(String field) {
            return VALUES.contains(field);
        }

        public static Set<String> retrievableFields() {
            return Collections.unmodifiableSet(VALUES);
        }
    }

    public static final class Searchable extends GeneProductFields {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String ID = storeAndGet(VALUES, GeneProductFields.ID);
        public static final String SYMBOL = storeAndGet(VALUES, GeneProductFields.SYMBOL);
        public static final String NAME = storeAndGet(VALUES, GeneProductFields.NAME);
        public static final String SYNONYM = storeAndGet(VALUES, GeneProductFields.SYNONYM);
        public static final String TYPE = storeAndGet(VALUES, GeneProductFields.TYPE);
        public static final String TAXON_ID = storeAndGet(VALUES, GeneProductFields.TAXON_ID);
        public static final String TAXON_NAME = storeAndGet(VALUES, GeneProductFields.TAXON_NAME);
        public static final String TARGET_SET = storeAndGet(VALUES, GeneProductFields.TARGET_SET);

        public static boolean isSearchable(String field) {
            return VALUES.contains(field);
        }

        public static Set<String> searchableFields() {
            return Collections.unmodifiableSet(VALUES);
        }
    }
}
