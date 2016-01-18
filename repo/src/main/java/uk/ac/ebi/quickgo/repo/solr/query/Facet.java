package uk.ac.ebi.quickgo.repo.solr.query;

import com.google.common.base.Preconditions;

/**
 * Expresses a view of a query result.
 *
 * A facet will break down the result set of a query into categories. Where each category is a distinct value in the
 * chosen field.
 *
 * Think of a facet, as a field in a SQL group by clause.
 */
public class Facet {
    private String field;

    public Facet(String field) {
        Preconditions.checkArgument(field != null && field.length() > 0, "Facet field can not be null or empty");

        this.field = field;
    }

    public String getField() {
        return this.field;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Facet facet = (Facet) o;

        return field.equals(facet.field);

    }

    @Override public int hashCode() {
        return field.hashCode();
    }

    @Override public String toString() {
        return "Facet{" +
                "field='" + field + '\'' +
                '}';
    }
}
