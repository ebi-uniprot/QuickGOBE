package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Represents the simplest of {@link QuickGOQuery} objects, containing just the field and values to query against.
 */
public class AllQuery extends QuickGOQuery {
    public AllQuery() { }

    @Override public <T> T accept(QueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public String toString() {
        return "AllQuery{} ";
    }

    @Override public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }

    @Override public int hashCode() {
        return super.hashCode();
    }
}
