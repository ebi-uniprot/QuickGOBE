package uk.ac.ebi.quickgo.geneproduct.common;

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
    /*
     * Note: Do not modify the access of the following constants, they are to remain package private.
     * If you need access to any of the field names please refer to the static inner classes for access.
     */
    static final String ID = "id";
    static final String DATABASE = "database";
    static final String SYMBOL = "symbol";
    static final String NAME = "name";
    static final String SYNONYM = "synonym";
    static final String TYPE = "type";
    static final String TAXON_ID = "taxonId";
    static final String TAXON_NAME = "taxonName";
    static final String DATABASE_SUBSET = "dbSubset";
    static final String PARENT_ID = "parentId";
    static final String TARGET_SET = "targetSet";
    static final String PROTEOME = "proteome";
    /**
     * GeneProduct fields that are stored, and can therefore be retrieved.
     */
    public static final class Retrievable {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String ID = storeAndGet(VALUES, GeneProductFields.ID);
        public static final String NAME = storeAndGet(VALUES, GeneProductFields.NAME);
        public static final String SYNONYM = storeAndGet(VALUES, GeneProductFields.SYNONYM);
        public static final String SYMBOL = storeAndGet(VALUES, GeneProductFields.SYMBOL);
        public static final String DATABASE = storeAndGet(VALUES, GeneProductFields.DATABASE);
        public static final String TYPE = storeAndGet(VALUES, GeneProductFields.TYPE);
        public static final String TAXON_ID = storeAndGet(VALUES, GeneProductFields.TAXON_ID);
        public static final String DATABASE_SUBSET = storeAndGet(VALUES, GeneProductFields.DATABASE_SUBSET);
        public static final String PROTEOME = storeAndGet(VALUES, GeneProductFields.PROTEOME);
        public static boolean isRetrievable(String field) {
            return VALUES.contains(field);
        }

        public static Set<String> retrievableFields() {
            return Collections.unmodifiableSet(VALUES);
        }
    }

    public static final class Searchable {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String ID = storeAndGet(VALUES, "id_lowercase");
        public static final String TYPE = storeAndGet(VALUES, "type_lowercase");
        public static final String TAXON_ID = storeAndGet(VALUES, GeneProductFields.TAXON_ID);
        public static final String DATABASE_SUBSET = storeAndGet(VALUES, "dbSubset_lowercase");
        public static final String TARGET_SET = storeAndGet(VALUES, GeneProductFields.TARGET_SET);
        public static final String PROTEOME = storeAndGet(VALUES, GeneProductFields.PROTEOME);

        public static boolean isSearchable(String field) {
            return VALUES.contains(field);
        }

        public static Set<String> searchableFields() {
            return Collections.unmodifiableSet(VALUES);
        }
    }

    public static final class Facetable {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String TYPE = storeAndGet(VALUES, GeneProductFields.TYPE);
        public static final String TAXON_ID = storeAndGet(VALUES, GeneProductFields.TAXON_ID);
        public static final String DATABASE_SUBSET = storeAndGet(VALUES, GeneProductFields.DATABASE_SUBSET);
        public static final String PROTEOME = storeAndGet(VALUES, GeneProductFields.PROTEOME);
        public static boolean isFacetable(String field) {
            return VALUES.contains(field);
        }

        public static Set<String> facetableFields() {
            return Collections.unmodifiableSet(VALUES);
        }
    }
}
