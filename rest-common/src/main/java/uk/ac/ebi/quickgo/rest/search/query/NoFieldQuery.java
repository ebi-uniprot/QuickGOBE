package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;

/**
 * Query that has no field assigned to it.
 *
 * Note: This query should only be used if the data source that it is  being queried against has a default field set.
 */
public class NoFieldQuery extends QuickGOQuery {
    private final String value;

    public NoFieldQuery(String value) {
        Preconditions.checkArgument(value != null && !value.trim().isEmpty(), "Value cannot be null or empty");
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override public <T> T accept(QueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NoFieldQuery that = (NoFieldQuery) o;

        return value.equals(that.value);

    }

    @Override public int hashCode() {
        return value.hashCode();
    }

    @Override public String toString() {
        return "NoFieldQuery{" +
                "value='" + value + '\'' +
                '}';
    }
}
