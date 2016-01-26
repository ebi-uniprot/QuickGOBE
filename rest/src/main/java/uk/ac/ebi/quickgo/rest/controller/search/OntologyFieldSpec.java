package uk.ac.ebi.quickgo.rest.controller.search;

import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyFields;

import org.springframework.stereotype.Component;

/**
 * Checks if a given value is a searchable field
 */
@Component
public class OntologyFieldSpec implements SearchableField {
    @Override public boolean isSearchable(String field) {
        return OntologyFields.Searchable.VALUES.contains(field);
    }

}
