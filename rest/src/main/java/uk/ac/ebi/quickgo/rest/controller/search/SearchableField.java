package uk.ac.ebi.quickgo.rest.controller.search;

/**
 * Interface used on the Field enumerations. It checks whether a string is one of
 * the searchable fields of the enumeration.
 */
public interface SearchableField {
    boolean isSearchable(String field);
}
