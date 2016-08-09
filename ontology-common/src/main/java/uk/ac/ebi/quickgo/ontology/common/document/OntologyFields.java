package uk.ac.ebi.quickgo.ontology.common.document;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The fields of an ontology document.
 *
 * Created 05/01/16
 * @author Edd
 */
public class OntologyFields {

    // all fields from ontology's schema.xml
    public final static String ID = "id";
    public final static String ONTOLOGY_TYPE = "ontologyType";
    public final static String NAME = "name";
    public final static String SECONDARY_ID = "secondaryId";
    public final static String IS_OBSOLETE = "isObsolete";
    public final static String REPLACEMENTS = "replacements";
    public final static String REPLACES = "replaces";
    public final static String DEFINITION = "definition";
    public final static String DEFINITION_XREFS = "definitionXref";
    public final static String COMMENT = "comment";
    public final static String ASPECT = "aspect";
    public final static String USAGE = "usage";
    public final static String SUBSET = "subset";
    public final static String CHILDREN = "children";
    public final static String SYNONYM = "synonym";
    public final static String SYNONYM_NAME = "synonymName";
    public final static String ANCESTOR = "ancestor";
    public final static String HISTORY = "history";
    public final static String XREF = "xref";
    public final static String ANNOTATION_GUIDELINE = "annotationGuideline";
    public final static String XRELATION = "xRelation";
    public final static String TAXON_CONSTRAINT = "taxonConstraint";
    public final static String BLACKLIST = "blacklist";
    public final static String EDGE_NAME = "edge_name";
    public final static String EDGE_SYNONYM = "edge_synonym";
    public final static String EXACT_NAME = "exact_name";
    public final static String EXACT_SYNONYM = "exact_synonym";
    public final static String GO_DISCUSSIONS = "goDiscussions";
    public final static String CREDITS = "credits";

    /**
     * Ontology fields that are stored, and can therefore be retrieved.
     */
    public static final class Retrievable extends OntologyFields {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String ID = storeAndGet(VALUES, OntologyFields.ID);
        public static final String ONTOLOGY_TYPE = storeAndGet(VALUES, OntologyFields.ONTOLOGY_TYPE);
        public static final String NAME = storeAndGet(VALUES, OntologyFields.NAME);
        public static final String IS_OBSOLETE = storeAndGet(VALUES, OntologyFields.IS_OBSOLETE);
        public static final String REPLACES = storeAndGet(VALUES, OntologyFields.REPLACES);
        public static final String REPLACEMENTS = storeAndGet(VALUES, OntologyFields.REPLACEMENTS);
        public static final String DEFINITION = storeAndGet(VALUES, OntologyFields.DEFINITION);
        public static final String COMMENT = storeAndGet(VALUES, OntologyFields.COMMENT);
        public static final String ASPECT = storeAndGet(VALUES, OntologyFields.ASPECT);
        public static final String USAGE = storeAndGet(VALUES, OntologyFields.USAGE);
        public static final String SUBSET = storeAndGet(VALUES, OntologyFields.SUBSET);
        public static final String CHILDREN = storeAndGet(VALUES, OntologyFields.CHILDREN);
        public static final String SYNONYM = storeAndGet(VALUES, OntologyFields.SYNONYM);
        public static final String HISTORY = storeAndGet(VALUES, OntologyFields.HISTORY);
        public static final String XREF = storeAndGet(VALUES, OntologyFields.XREF);
        public static final String ANNOTATION_GUIDELINE = storeAndGet(VALUES, OntologyFields.ANNOTATION_GUIDELINE);
        public static final String TAXON_CONSTRAINT = storeAndGet(VALUES, OntologyFields.TAXON_CONSTRAINT);
        public static final String XRELATION = storeAndGet(VALUES, OntologyFields.XRELATION);
        public static final String BLACKLIST = storeAndGet(VALUES, OntologyFields.BLACKLIST);
        public static final String SECONDARY_ID = storeAndGet(VALUES, OntologyFields.SECONDARY_ID);
        public static final String DEFINITION_XREF = storeAndGet(VALUES, OntologyFields.DEFINITION_XREFS);
        public static final String ANCESTOR = storeAndGet(VALUES, OntologyFields.ANCESTOR);
        public static final String GO_DISCUSSIONS = storeAndGet(VALUES, OntologyFields.GO_DISCUSSIONS);
        public static final String CREDITS = storeAndGet(VALUES, OntologyFields.CREDITS);

        public static boolean isRetrievable(String field) {
            return VALUES.contains(field);
        }

        private static Set<String> retrievableFields() {
            return Collections.unmodifiableSet(VALUES);
        }
    }

    /**
     * Ontology fields that are indexed, and can therefore be searched.
     */
    public static final class Searchable {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String ASPECT = storeAndGet(VALUES, OntologyFields.ASPECT);
        public static final String ID = storeAndGet(VALUES, OntologyFields.ID);
        public static final String DEFINITION = storeAndGet(VALUES, OntologyFields.DEFINITION);
        public static final String NAME = storeAndGet(VALUES, OntologyFields.NAME);
        public static final String ONTOLOGY_TYPE = storeAndGet(VALUES, OntologyFields.ONTOLOGY_TYPE);
        public static final String SYNONYM_NAME = storeAndGet(VALUES, OntologyFields.SYNONYM_NAME);

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