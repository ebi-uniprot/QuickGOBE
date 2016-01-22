package uk.ac.ebi.quickgo.repo.solr.document.ontology;

import java.util.HashSet;
import java.util.Set;

/**
 * The fields of an ontology document.
 *
 * Created 05/01/16
 * @author Edd
 */
public class OntologyField {

    // all fields from ontology's schema.xml
    public final static String ID = "id";
    public final static String ONTOLOGY_TYPE = "ontologyType";
    public final static String NAME = "name";
    public final static String SECONDARY_ID = "secondaryId";
    public final static String IS_OBSOLETE = "isObsolete";
    public final static String CONSIDER = "consider";
    public final static String REPLACED_BY = "replacedBy";
    public final static String DEFINITION = "definition";
    public final static String COMMENT = "comment";
    public final static String ASPECT = "aspect";
    public final static String USAGE = "usage";
    public final static String SUBSET = "subset";
    public final static String CHILDREN = "children";
    public final static String ANCESTOR = "ancestor";
    public final static String SYNONYM = "synonym";
    public final static String SYNONYM_NAME = "synonymName";
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

    /**
     * Ontology fields that are stored, and can therefore be retrieved.
     */
    public static final class Retrievable extends OntologyField {
        public static final Set<String> VALUES = new HashSet<>();

        public static final String ID = storeAndGet(VALUES, OntologyField.ID);
        public static final String ONTOLOGY_TYPE = storeAndGet(VALUES, OntologyField.ONTOLOGY_TYPE);
        public static final String NAME = storeAndGet(VALUES, OntologyField.NAME);
        public static final String IS_OBSOLETE = storeAndGet(VALUES, OntologyField.IS_OBSOLETE);
        public static final String CONSIDER = storeAndGet(VALUES, OntologyField.CONSIDER);
        public static final String REPLACED_BY = storeAndGet(VALUES, OntologyField.REPLACED_BY);
        public static final String DEFINITION = storeAndGet(VALUES, OntologyField.DEFINITION);
        public static final String COMMENT = storeAndGet(VALUES, OntologyField.COMMENT);
        public static final String ASPECT = storeAndGet(VALUES, OntologyField.ASPECT);
        public static final String USAGE = storeAndGet(VALUES, OntologyField.USAGE);
        public static final String SUBSET = storeAndGet(VALUES, OntologyField.SUBSET);
        public static final String CHILDREN = storeAndGet(VALUES, OntologyField.CHILDREN);
        public static final String ANCESTOR = storeAndGet(VALUES, OntologyField.ANCESTOR);
        public static final String SYNONYM = storeAndGet(VALUES, OntologyField.SYNONYM);
        public static final String HISTORY = storeAndGet(VALUES, OntologyField.HISTORY);
        public static final String XREF = storeAndGet(VALUES, OntologyField.XREF);
        public static final String ANNOTATION_GUIDELINE = storeAndGet(VALUES, OntologyField.ANNOTATION_GUIDELINE);
        public static final String TAXON_CONSTRAINT = storeAndGet(VALUES, OntologyField.TAXON_CONSTRAINT);
        public static final String XRELATION = storeAndGet(VALUES, OntologyField.XRELATION);
        public static final String BLACKLIST = storeAndGet(VALUES, OntologyField.BLACKLIST);
        public static final String SECONDARY_ID = storeAndGet(VALUES, OntologyField.SECONDARY_ID);
    }

    /**
     * Ontology fields that are indexed, and can therefore be searched.
     */
    public static final class Searchable {
        public static final Set<String> VALUES = new HashSet<>();

        public static final String ID = storeAndGet(VALUES, OntologyField.ID);
        public static final String NAME = storeAndGet(VALUES, OntologyField.NAME);
        public static final String DEFINITION = storeAndGet(VALUES, OntologyField.DEFINITION);
        public static final String SYNONYM_NAME = storeAndGet(VALUES, OntologyField.SYNONYM_NAME);
    }
    
    private static String storeAndGet(Set<String> values, String value) {
        values.add(value);
        return value;
    }
}
