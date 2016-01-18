package uk.ac.ebi.quickgo.rest.controller;

import uk.ac.ebi.quickgo.solr.query.model.ontology.enums.TermField;

/**
 * Checks if a given value is a queryable field
 */
public class OntologyQueryableField implements QueryableField {
    @Override public boolean isQueryableField(String field) {
        if (field != null) {
            for (TermField termField : TermField.values()) {
                if (termField.getValue().equalsIgnoreCase(field)) {
                    return true;
                }
            }
        }

        return false;
    }
}
