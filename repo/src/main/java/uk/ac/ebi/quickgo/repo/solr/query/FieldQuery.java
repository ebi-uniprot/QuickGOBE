package uk.ac.ebi.quickgo.repo.solr.query;

import com.google.common.base.Preconditions;

/**
 * Represents the simplest of {@link GoQuery} objects, containing just the field and values to query against.
 */
class FieldQuery extends GoQuery {
    private String field;
    private String value;

    public FieldQuery(String field, String value) {
        checkArgumentNullOrEmpty(field, "Field");
        checkArgumentNullOrEmpty(value, "Value");

        this.field = field;
        this.value = value;
    }

    private void checkArgumentNullOrEmpty(String input, String inputType) {
        Preconditions.checkArgument(input != null && input.length() > 0, inputType + " type can not be null");
    }

    @Override public <T> T accept(QueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String field() {
        return field;
    }

    public String value() {
        return value;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FieldQuery that = (FieldQuery) o;

        if (!field.equals(that.field)) {
            return false;
        }

        return value.equals(that.value);
    }

    @Override public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override public String toString() {
        return "FieldQuery{" +
                "field='" + field + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}