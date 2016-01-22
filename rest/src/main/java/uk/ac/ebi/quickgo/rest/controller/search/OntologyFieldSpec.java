package uk.ac.ebi.quickgo.rest.controller.search;

import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyField;

import org.springframework.stereotype.Component;

/**
 * Checks if a given value is a searchable field
 */
@Component
public class OntologyFieldSpec implements SearchableField {
    @Override public boolean isSearchable(String field) {
        for (String searchableField : OntologyField.Searchable.VALUES) {
            if (searchableField.equals(field)) {
                return true;
            }
        }
        return false;
    }

}
