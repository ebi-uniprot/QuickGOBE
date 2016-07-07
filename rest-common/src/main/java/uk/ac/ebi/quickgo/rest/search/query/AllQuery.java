package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Represents the simplest of {@link QuickGOQuery} objects, containing just the field and values to query against.
 */
class AllQuery extends QuickGOQuery {
    AllQuery() { }

    @Override public <T> T accept(QueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public String toString() {
        return "AllQuery{} ";
    }
}
