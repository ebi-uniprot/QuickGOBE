package uk.ac.ebi.quickgo.rest.controller.search;

import org.springframework.stereotype.Component;

/**
 * Checks if a given value is a searchable field
 */
@Component
public class OntologyFieldSpec implements SearchableField {
    public enum Search {
        synonymName,
        id,
        name,
        definition,
        aspect,
        ontologyType
    }

    @Override public boolean isSearchable(String field) {
        for (Search searchableField : Search.values()) {
            if (searchableField.name().equals(field)) {
                return true;
            }
        }
        return false;
    }

}
