package uk.ac.ebi.quickgo.rest.search.query;

/**
 * A version of {@link FieldQuery}, the use of which signifies that the requester is not looking for specific or exact values
 * from a field, but a contain or like operation needed same in SQL. This will apply * in front and back of value
 */
public class ContainsFieldQuery extends FieldQuery {

    public ContainsFieldQuery(String field, String value) {
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

        ContainsFieldQuery that = (ContainsFieldQuery) o;

        if (!field.equals(that.field)) {
            return false;
        }

        return value.equals(that.value);
    }

    @Override public int hashCode() {
        int result = field.hashCode();
        result = 34 * result + value.hashCode();
        return result;
    }

    @Override public String toString() {
        return "ContainsFieldQuery{field='" + field + "', value='*" + value + "*'}";
    }
}
