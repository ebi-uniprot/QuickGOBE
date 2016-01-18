package uk.ac.ebi.quickgo.repo.solr.query;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Set;

/**
 * Represents a query that is a result of a logical operation (i.e. AND, OR, NOT), of one or more
 * {@link GoQuery}.
 */
class CompositeQuery extends GoQuery {
    private QueryOp queryOperator;

    private Set<GoQuery> queries;

    public CompositeQuery(Set<GoQuery> queries, QueryOp operator) {
        Preconditions.checkArgument(queries != null && !queries.isEmpty(),
                "Queries to compose can not be null or empty");
        Preconditions.checkArgument(operator != null, "Logical query operator can not be null");

        this.queryOperator = operator;
        this.queries = Collections.unmodifiableSet(queries);
    }

    @Override public <T> T accept(QueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public QueryOp queryOperator() {
        return queryOperator;
    }

    public Set<GoQuery> queries() {
        return queries;
    }

    public enum QueryOp {
        OR,
        AND,
        NOT
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompositeQuery query = (CompositeQuery) o;

        if (queryOperator != query.queryOperator) {
            return false;
        }
        return queries.equals(query.queries);

    }

    @Override public int hashCode() {
        int result = queryOperator.hashCode();
        result = 31 * result + queries.hashCode();
        return result;
    }

    @Override public String toString() {
        return "CompositeQuery{" +
                "queryOperator=" + queryOperator +
                ", queries=" + queries +
                '}';
    }
}
