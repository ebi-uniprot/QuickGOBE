package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.ebi.quickgo.rest.search.query.CompositeQuery.QueryOp;

/**
 * Representation of a domain Query.
 */
public abstract class QuickGOQuery {
    public abstract <T> T accept(QueryVisitor<T> visitor);

    public QuickGOQuery and(QuickGOQuery query) {
        Preconditions.checkArgument(query != null, "Query to AND against is null");

        Set<QuickGOQuery> queries = aggregateQueries(this, query);

        return new CompositeQuery(queries, QueryOp.AND);
    }

    public QuickGOQuery or(QuickGOQuery query) {
        Preconditions.checkArgument(query != null, "Query to OR against is null");

        Set<QuickGOQuery> queries = aggregateQueries(this, query);

        return new CompositeQuery(queries, QueryOp.OR);
    }

    public QuickGOQuery not() {
        return new CompositeQuery(Collections.singleton(this), QueryOp.NOT);
    }

    private Set<QuickGOQuery> aggregateQueries(QuickGOQuery query1, QuickGOQuery query2) {
        Set<QuickGOQuery> queries = new LinkedHashSet<>();
        queries.add(query1);
        queries.add(query2);

        return queries;
    }

    public static QuickGOQuery createQuery(String field, String value) {
        return new FieldQuery(field, value);
    }

    public static QuickGOQuery createQuery(String value) {
        return new NoFieldQuery(value);
    }


    public static QuickGOQuery createAllQuery() {
        return new AllQuery();
    }
}
