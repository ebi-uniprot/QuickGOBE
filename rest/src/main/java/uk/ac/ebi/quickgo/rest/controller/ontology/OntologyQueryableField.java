package uk.ac.ebi.quickgo.rest.controller.ontology;

import uk.ac.ebi.quickgo.rest.controller.QueryableField;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Checks if a given value is a queryable field
 */
public class OntologyQueryableField implements QueryableField {
    private final static Set<String> INDEXABLE_FIELDS = new HashSet<>(
            Arrays.asList(
            "synonymName",
            "id"         ,
            "name"       ,
            "definition"  )
    );

    @Override public boolean isQueryableField(String field) {
        return field != null && INDEXABLE_FIELDS.contains(field);
    }

}
