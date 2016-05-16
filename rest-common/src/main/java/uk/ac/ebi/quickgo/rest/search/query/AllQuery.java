package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Represents a selection of all the records within a data source.
 *
 * @author Tony Wardell.
 */
class AllQuery extends QuickGOQuery {

    @Override public <T> T accept(QueryVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
