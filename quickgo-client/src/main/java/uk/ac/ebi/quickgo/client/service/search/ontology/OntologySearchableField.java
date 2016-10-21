package uk.ac.ebi.quickgo.client.service.search.ontology;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyFields;

import java.util.stream.Stream;

/**
 * Checks if a given value is a searchable field
 */
public class OntologySearchableField implements SearchableField {
    @Override public boolean isSearchable(String field) {
        return OntologyFields.Searchable.isSearchable(field);
    }

    @Override public Stream<String> searchableFields() {
        return OntologyFields.Searchable.searchableFields().stream();
    }
}