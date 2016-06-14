package uk.ac.ebi.quickgo.client.service.search.ontology;

import uk.ac.ebi.quickgo.common.SearchableDocumentFields;
import uk.ac.ebi.quickgo.rest.search.SearchableField;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyFields;

import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 * Checks if a given value is a searchable field
 */
@Component
public class OntologySearchableField implements SearchableField, SearchableDocumentFields {
    @Override public boolean isSearchable(String field) {
        return OntologyFields.Searchable.isSearchable(field);
    }

    @Override public boolean isDocumentSearchable(String field) {
        return isSearchable(field);
    }

    @Override public Stream<String> searchableDocumentFields() {
        return OntologyFields.Searchable.searchableFields().stream();
    }
}
