package uk.ac.ebi.quickgo.rest.search.query;

/**
 * A version of {@link FieldQuery}, the use of which signifies that the requester is not looking for specific values
 * from a field, but all of the target data where the selected field has a value that isn't empty or null.
 */
public class AllNonEmptyFieldQuery extends FieldQuery {

    public AllNonEmptyFieldQuery(String field, String value) {
        super(field, value);
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

        AllNonEmptyFieldQuery that = (AllNonEmptyFieldQuery) o;

        if (!field.equals(that.field)) {
            return false;
        }

        return value.equals(that.value);
    }

    @Override public int hashCode() {
        int result = field.hashCode();
        result = 33 * result + value.hashCode();
        return result;
    }

    @Override public String toString() {
        return "AllNonEmptyFieldQuery{" +
                "field='" + field + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
