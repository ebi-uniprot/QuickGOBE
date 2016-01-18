package uk.ac.ebi.quickgo.repo.solr.query;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.ebi.quickgo.repo.solr.query.CompositeQuery.QueryOp;

/**
 * Representation of a domain Query.
 */
public abstract class GoQuery {
    public abstract <T> T accept(QueryVisitor<T> visitor);

    public GoQuery and(GoQuery query) {
        Preconditions.checkArgument(query != null, "Query to AND against is null");

        Set<GoQuery> queries = aggregateQueries(this, query);

        return new CompositeQuery(queries, QueryOp.AND);
    }

    public GoQuery or(GoQuery query) {
        Preconditions.checkArgument(query != null, "Query to OR against is null");

        Set<GoQuery> queries = aggregateQueries(this, query);

        return new CompositeQuery(queries, QueryOp.OR);
    }

    public GoQuery not() {
        return new CompositeQuery(Collections.singleton(this), QueryOp.NOT);
    }

    private Set<GoQuery> aggregateQueries(GoQuery query1, GoQuery query2) {
        Set<GoQuery> queries = new LinkedHashSet<>();
        queries.add(query1);
        queries.add(query2);

        return queries;
    }

    public static GoQuery createQuery(String field, String value) {
        return new FieldQuery(field, value);
    }

    public static GoQuery createQuery(String value) {
        return new NoFieldQuery(value);
    }
}