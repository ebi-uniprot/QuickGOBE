package uk.ac.ebi.quickgo.rest.search.results;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains a tree like facet response. Where each node of the the same level reports on values that belong to the
 * same category. Lower level nodes (child nodes) will report on values of a different category. The lower level
 * values are a break down of the distinct values that occur within the current node. This allows the facets to
 * perform a drill-down of the distribution of data.
 *
 * {@link QueryResult}.
 *
 * @author Edd Turner, Ricardo Antunes
 */
public class PivotFacet {
    private final String field;
    private final String value;

    private final long count;

    private final Set<PivotFacet> pivots;

    public PivotFacet(String field, String value, long count) {
        Preconditions.checkArgument(field != null && !field.trim().isEmpty(), "Field cannot be null or empty");
        Preconditions.checkArgument(value != null && !value.trim().isEmpty(), "Value cannot be null or empty");
        Preconditions.checkArgument(count >= 0, "Count cannot be negative");

        this.field = field;
        this.value = value;
        this.count = count;
        this.pivots = new HashSet<>();
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    public long getCount() {
        return count;
    }

    public void addPivot(PivotFacet childPivot) {
        Preconditions.checkArgument(childPivot != null, "Cannot add null child pivot");
        pivots.add(childPivot);
    }

    /**
     * Returns an unmodifiable set of child pivots.
     *
     * @return an unmodifiable set
     */
    public Set<PivotFacet> getPivots() {
        return Collections.unmodifiableSet(pivots);
    }
}