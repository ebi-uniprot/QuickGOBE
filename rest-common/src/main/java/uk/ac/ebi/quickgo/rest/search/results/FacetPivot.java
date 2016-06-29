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
public class FacetPivot {
    private final String name;
    private final String catName;

    private final long count;

    private final Set<FacetPivot> pivots;

    public FacetPivot(String name, String catName, long count) {
        Preconditions.checkArgument(name != null && !name.trim().isEmpty(), "Name cannot be null or empty");
        Preconditions.checkArgument(catName != null && !catName.trim().isEmpty(), "Category name cannot be null or " +
                "empty");
        Preconditions.checkArgument(count >= 0, "Count cannot be negative");

        this.name = name;
        this.catName = catName;
        this.count = count;
        this.pivots = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getCatName() {
        return catName;
    }

    public long getCount() {
        return count;
    }

    public void addPivot(FacetPivot childPivot) {
        Preconditions.checkArgument(childPivot != null, "Cannot add null child pivot");
        pivots.add(childPivot);
    }

    /**
     * Returns an unmodifiable set of child pivots.
     *
     * @return an unmodifiable set
     */
    public Set<FacetPivot> getPivots() {
        return Collections.unmodifiableSet(pivots);
    }
}