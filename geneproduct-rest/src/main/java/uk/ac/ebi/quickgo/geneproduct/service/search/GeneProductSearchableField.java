package uk.ac.ebi.quickgo.geneproduct.service.search;

import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductFields;
import uk.ac.ebi.quickgo.rest.search.SearchableField;

import org.springframework.stereotype.Component;

/**
 * Checks if a given value is a searchable field
 */
@Component
public class GeneProductSearchableField implements SearchableField {
    @Override public boolean isSearchable(String field) {
        return GeneProductFields.Searchable.isSearchable(field);
    }
}
