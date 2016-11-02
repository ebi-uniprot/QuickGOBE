package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;
import uk.ac.ebi.quickgo.common.SearchableField;

import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 * Checks if a given field is searchable.
 *
 * @author Ricardo Antunes
 */
@Component
public class AnnotationSearchableField implements SearchableField {
    @Override public boolean isSearchable(String field) {
        return AnnotationFields.Searchable.isSearchable(field);
    }

    @Override public Stream<String> searchableFields() {
        return AnnotationFields.Searchable.searchableFields().stream();
    }
}