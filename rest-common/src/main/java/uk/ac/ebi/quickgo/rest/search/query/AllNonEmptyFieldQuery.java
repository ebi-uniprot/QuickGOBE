package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Represents the simplest of {@link QuickGOQuery} objects, containing just the field and values to query against.
 */
public class AllNonEmptyFieldQuery extends FieldQuery {

    public AllNonEmptyFieldQuery(String field, String value) {
        super(field, value);
    }

    @Override public <T> T accept(QueryVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
