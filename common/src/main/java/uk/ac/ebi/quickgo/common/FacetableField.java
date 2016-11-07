package uk.ac.ebi.quickgo.common;

import java.util.stream.Stream;

/**
 * Defines which fields are facetable for a given {@link QuickGODocument}.
 * @author Ricardo Antunes
 */
public interface FacetableField {
    /**
     * Indicates whether the provided {@param field} is facetable or not.
     *
     * @param field the field to check
     * @return true if the field is facetable, false otherwise
     */
    boolean isFacetable(String field);

    /**
     * Returns all facetable fields of a concrete implementation of {@link QuickGODocument}.
     *
     * @return an {@link Stream} of all fields that are facetable for a specific implementation of
     * {@link QuickGODocument}
     */
    Stream<String> facetableFields();
}
