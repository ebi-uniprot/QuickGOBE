package uk.ac.ebi.quickgo.rest.search.query;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents a criterion for sorting, which captures the field to sort by, and the type
 * of sorting that is required.
 *
 * Created 16/01/17
 * @author Edd
 */
public class SortCriterion {
    private final SortOrder sortOrder;
    private final SortField field;

    public SortCriterion(String field, SortOrder sortOrder) {
        checkArgument(sortOrder != null, "Sort type cannot be null");
        this.field = new SortField(field);
        this.sortOrder = sortOrder;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public SortField getSortField() {
        return field;
    }

    /**
     * Captures the sort order required, e.g., ascending.
     */
    public enum SortOrder {
        ASC, DESC;
    }
}
