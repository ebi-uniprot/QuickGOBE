package uk.ac.ebi.quickgo.repo.solr.query.results;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Contains the facet information of a given query response {@link QueryResult}.
 * <p>
 * A facet provides a categorized summary of the query results, using a set of criteria. This criteria is usually
 * passed in by a user request.
 * <p>
 * As an example: Suppose we have set of X results returned from the query, and we would like to facet those results
 * on a field shared by all. The resulting facet would display all distinct values as well as a value that accounts for
 * the number of the results that match the field value.
 * <p>
 * <b>facet: fieldX</b>
 * <ul>
 *     <li>value1:5</li>
 *     <li>value2:7</li>
 *     ...
 *     <li>valueN:20</li>
 * </ul>
 *
 * Note: the above example is for a {@link FieldFacet}.
 */
public class Facet {
    private final Set<FieldFacet> fieldFacets;

    public Facet() {
        this.fieldFacets = new LinkedHashSet<>();
    }

    public Set<FieldFacet> getFacetFields() {
        return Collections.unmodifiableSet(fieldFacets);
    }

    public void addFacetField(FieldFacet fieldFacet) {
        fieldFacets.add(fieldFacet);
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Facet facet = (Facet) o;

        return fieldFacets.equals(facet.fieldFacets);

    }

    @Override public int hashCode() {
        return fieldFacets.hashCode();
    }

    @Override public String toString() {
        return "Facet{" +
                "facetFields=" + fieldFacets +
                '}';
    }
}