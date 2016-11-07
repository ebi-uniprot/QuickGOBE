package uk.ac.ebi.quickgo.common;

import java.util.stream.Stream;

/**
 * Defines which fields are searchable for a given {@link QuickGODocument}.
 *
 * @author Ricardo Antunes
 */
public interface SearchableField {
    /**
     * Indicates whether the provided {@param field} is searchable or not.
     *
     * @param field the field to check
     * @return true if the field is searchable, false otherwise
     */
    boolean isSearchable(String field);

    /**
     * Returns all searchable fields of a concrete implementation of {@link QuickGODocument}.
     *
     * @return an {@link Stream} of all fields that are searchable for a specific implementation of
     * {@link QuickGODocument}
     */
    Stream<String> searchableFields();
}
