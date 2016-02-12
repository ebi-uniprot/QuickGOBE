package uk.ac.ebi.quickgo.client.service.search.ontology;

import uk.ac.ebi.quickgo.rest.search.SearchableField;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyFields;

import org.springframework.stereotype.Component;

/**
 * Checks if a given value is a searchable field
 */
@Component
public class OntologySearchableField implements SearchableField {
    @Override public boolean isSearchable(String field) {
        return OntologyFields.Searchable.VALUES.contains(field);
    }
}
