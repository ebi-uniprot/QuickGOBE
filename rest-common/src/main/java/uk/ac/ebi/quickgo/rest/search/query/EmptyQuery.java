package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Query that has no data assigned to it
 *
 */
class EmptyQuery extends QuickGOQuery {

    public EmptyQuery() { }

    public String getValue() {
        return null;
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

        EmptyQuery that = (EmptyQuery) o;

        return true;

    }

}
