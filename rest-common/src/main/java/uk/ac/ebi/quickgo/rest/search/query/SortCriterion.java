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

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SortCriterion that = (SortCriterion) o;

        if (sortOrder != that.sortOrder) {
            return false;
        }
        return field != null ? field.equals(that.field) : that.field == null;
    }

    @Override public int hashCode() {
        int result = sortOrder != null ? sortOrder.hashCode() : 0;
        result = 31 * result + (field != null ? field.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "SortCriterion{" +
                "sortOrder=" + sortOrder +
                ", field=" + field +
                '}';
    }
}
