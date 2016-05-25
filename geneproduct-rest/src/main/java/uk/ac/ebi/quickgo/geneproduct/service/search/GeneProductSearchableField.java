package uk.ac.ebi.quickgo.geneproduct.service.search;

import uk.ac.ebi.quickgo.common.SearchableDocumentFields;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductFields;
import uk.ac.ebi.quickgo.rest.search.SearchableField;

import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 * Checks if a given value is a searchable field.
 *
 * @author Edd Turner
 */
@Component
public class GeneProductSearchableField implements SearchableField, SearchableDocumentFields {
    @Override public boolean isSearchable(String field) {
        return GeneProductFields.Searchable.isSearchable(field);
    }

    @Override public boolean isDocumentSearchable(String field) {
        return isSearchable(field);
    }

    @Override public Stream<String> searchableDocumentFields() {
        return GeneProductFields.Searchable.searchableFields().stream();
    }
}
