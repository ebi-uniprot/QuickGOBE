package uk.ac.ebi.quickgo.rest.search;

/**
 * Interface that defines methods that check whether the field is searchable, within its runtime context.
 *
 * @author Edd Turner
 */
public interface SearchableField {
    /**
     * Checks whether the provided {@param field} is a searchable field.
     * @param field the field to check
     * @return true if the provided field is searchable, false otherwise
     */
    boolean isSearchable(String field);
}
