package uk.ac.ebi.quickgo.ontology.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static uk.ac.ebi.quickgo.common.DocumentFieldsHelper.storeAndGet;

/**
 * The fields of an ontology document.
 *
 * Created 05/01/16
 * @author Edd
 */
public class OntologyFields {

    // all fields from ontology's schema.xml
    static final String ID = "id";
    static final String ONTOLOGY_TYPE = "ontologyType";
    static final String NAME = "name";
    static final String SECONDARY_ID = "secondaryId";
    static final String IS_OBSOLETE = "isObsolete";
    static final String REPLACEMENTS = "replacements";
    static final String REPLACES = "replaces";
    static final String DEFINITION = "definition";
    static final String DEFINITION_XREFS = "definitionXref";
    static final String COMMENT = "comment";
    static final String ASPECT = "aspect";
    static final String USAGE = "usage";
    static final String SUBSET = "subset";
    static final String SYNONYM = "synonym";
    static final String SYNONYM_NAME = "synonymName";
    static final String ANCESTOR = "ancestor";
    static final String HISTORY = "history";
    static final String XREF = "xref";
    static final String ANNOTATION_GUIDELINE = "annotationGuideline";
    static final String XRELATION = "xRelation";
    static final String TAXON_CONSTRAINT = "taxonConstraint";
    static final String BLACKLIST = "blacklist";
    static final String GO_DISCUSSIONS = "goDiscussions";
    static final String CREDITS = "credits";

    /*
     * The following fields are declared solely for use by the OntologyRepository queries. These are necessary given
     * that annotations can only accept constant values. So declaring something like OntologyFields.Facetable.ASPECT
     * is not valid. The Java compiler will give an error, even though the searchable aspect is a constant.
     */
    static final String ID_LOWERCASE = "id_lowercase";
    static final String SECONDARY_ID_LOWERCASE = "secondaryId_lowercase";
    static final String ONTOLOGY_TYPE_LOWERCASE = "ontologyType_lowercase";

    /**
     * Ontology fields that are indexed, and can therefore be searched.
     */
    public static final class Searchable {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String ASPECT = storeAndGet(VALUES, "aspect_lowercase");
        public static final String ID = storeAndGet(VALUES, ID_LOWERCASE);
        public static final String ONTOLOGY_TYPE = storeAndGet(VALUES, ONTOLOGY_TYPE_LOWERCASE);

        public static boolean isSearchable(String field) {
            return VALUES.contains(field);
        }

        public static Set<String> searchableFields() {
            return Collections.unmodifiableSet(VALUES);
        }
    }

    public static class Facetable {
        private static final Set<String> VALUES = new HashSet<>();

        public static final String ASPECT = storeAndGet(VALUES, OntologyFields.ASPECT);
        public static final String ONTOLOGY_TYPE = storeAndGet(VALUES, OntologyFields.ONTOLOGY_TYPE);

        public static boolean isFacetable(String field) {
            return VALUES.contains(field);
        }

        public static Set<String> facetableFields() {
            return Collections.unmodifiableSet(VALUES);
        }
    }
}