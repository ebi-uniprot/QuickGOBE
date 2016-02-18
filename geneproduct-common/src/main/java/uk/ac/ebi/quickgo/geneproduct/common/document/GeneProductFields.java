package uk.ac.ebi.quickgo.geneproduct.common.document;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    public static final String COMPLETE_PROTEOME = "completeProteome";
    public static final String REFERENCE_POTEOME = "referenceProteome";

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
        public static final String TAXON_NAME = storeAndGet(VALUES, GeneProductFields.TAXON_NAME);
        public static final String DATABASE_SUBSET = storeAndGet(VALUES, GeneProductFields.DATABASE_SUBSET);
        public static final String COMPLETE_PROTEOME = storeAndGet(VALUES, GeneProductFields.COMPLETE_PROTEOME);
        public static final String REFERENCE_POTEOME = storeAndGet(VALUES, GeneProductFields.REFERENCE_POTEOME);

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