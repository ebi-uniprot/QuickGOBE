package uk.ac.ebi.quickgo.rest.search.results;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashSet;
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
 * <b>Note: the above example is for a {@link FieldFacet}.</b>
 *
 * <p/>
 *
 * It is also possible to store pivoted facets, which allow for the storage of drill-down facets.
 * <p/>
 * <b>facet: field1, field2</b>
 * <ul>
 *     <li>field1:5</li>
 *     <ul>
 *         <li>field2:a:3</li>
 *         <li>field2:b:2</li>
 *     </ul>
 * </ul>
 *
 * <b>Note: the above example is for a {@link PivotFacet}.</b>
 */
public class Facet {
    private final Set<FieldFacet> fieldFacets;
    private final Set<PivotFacet> pivotFacets;

    public Facet() {
        this.fieldFacets = new HashSet<>();
        this.pivotFacets = new HashSet<>();
    }

    /**
     * Returns an unmodifiable set of {@link FieldFacet} instances.
     * @return an unmodifiable set.
     */
    public Set<FieldFacet> getFacetFields() {
        return Collections.unmodifiableSet(fieldFacets);
    }

    public void addFacetField(FieldFacet fieldFacet) {
        Preconditions.checkArgument(fieldFacet != null, "Cannot add null field facet.");

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

    public void addPivotFacet(PivotFacet pivotFacet) {
        Preconditions.checkArgument(pivotFacet != null, "Cannot add null pivot facet.");
        pivotFacets.add(pivotFacet);
    }

    /**
     * Returns an unmodifiable set of {@link PivotFacet} instances.
     * @return an unmodifiable set.
     */
    public Set<PivotFacet> getPivotFacets() {
        return Collections.unmodifiableSet(pivotFacets);
    }
}