package uk.ac.ebi.quickgo.rest.search.results;

import com.google.common.base.Preconditions;
import java.util.*;

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
    private String[] fields;
    private Set<Pivot> pivots;

    public PivotFacet(String... fields) {
        Preconditions.checkArgument(fields != null && fields.length > 0, "Pivot fields cannot be null or empty");

        this.fields = Arrays.copyOf(fields, fields.length);
        pivots = new LinkedHashSet<>();
    }

    public void addPivot(Pivot pivot) {
        Preconditions.checkArgument(pivot != null, "Cannot add null pivot to facet");
        pivots.add(pivot);
    }

    public Set<Pivot> getPivots() {
        return pivots;
    }

    public String[] getFields() {
        return fields;
    }

    public static class Pivot {
        private final String field;
        private final String value;

        private final long count;

        private final Set<Pivot> pivots;

        public Pivot(String field, String value, long count) {
            Preconditions.checkArgument(field != null && !field.trim().isEmpty(), "Field cannot be null or empty");
            Preconditions.checkArgument(value != null && !value.trim().isEmpty(), "Value cannot be null or empty");
            Preconditions.checkArgument(count >= 0, "Count cannot be negative");

            this.field = field;
            this.value = value;
            this.count = count;
            this.pivots = new LinkedHashSet<>();
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

        public void addPivot(Pivot childPivot) {
            Preconditions.checkArgument(childPivot != null, "Cannot add null child pivot");
            pivots.add(childPivot);
        }

        /**
         * Returns an unmodifiable set of child pivots.
         *
         * @return an unmodifiable set
         */
        public Set<Pivot> getPivots() {
            return Collections.unmodifiableSet(pivots);
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Pivot pivot = (Pivot) o;

            if (count != pivot.count) {
                return false;
            }
            if (!field.equals(pivot.field)) {
                return false;
            }
            if (!value.equals(pivot.value)) {
                return false;
            }
            return pivots.equals(pivot.pivots);

        }

        @Override public int hashCode() {
            int result = field.hashCode();
            result = 31 * result + value.hashCode();
            result = 31 * result + (int) (count ^ (count >>> 32));
            result = 31 * result + pivots.hashCode();
            return result;
        }

        @Override public String toString() {
            return "Pivot{" +
                    "field='" + field + '\'' +
                    ", value='" + value + '\'' +
                    ", count=" + count +
                    ", pivots=" + pivots +
                    '}';
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PivotFacet that = (PivotFacet) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(fields, that.fields)) {
            return false;
        }
        return pivots.equals(that.pivots);

    }

    @Override public int hashCode() {
        int result = Arrays.hashCode(fields);
        result = 31 * result + pivots.hashCode();
        return result;
    }

    @Override public String toString() {
        return "PivotFacet{" +
                "fields=" + Arrays.toString(fields) +
                ", pivots=" + pivots +
                '}';
    }
}