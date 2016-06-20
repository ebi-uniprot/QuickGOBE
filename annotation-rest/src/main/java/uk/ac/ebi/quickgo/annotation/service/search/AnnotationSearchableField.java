package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.common.SearchableDocumentFields;
import uk.ac.ebi.quickgo.rest.search.SearchableField;

import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 * Checks if a given field is searchable.
 *
 * @author Ricardo Antunes
 */
@Component
public class AnnotationSearchableField implements SearchableField, SearchableDocumentFields {
    @Override public boolean isDocumentSearchable(String field) {
        return AnnotationFields.Searchable.isSearchable(field);
    }

    @Override public Stream<String> searchableDocumentFields() {
        return AnnotationFields.Searchable.searchableFields().stream();
    }

    @Override public boolean isSearchable(String field) {
        return AnnotationFields.Searchable.isSearchable(field);
    }
}
