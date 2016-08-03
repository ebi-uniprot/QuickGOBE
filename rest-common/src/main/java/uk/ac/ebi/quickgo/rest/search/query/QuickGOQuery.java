package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static uk.ac.ebi.quickgo.rest.search.query.CompositeQuery.QueryOp;

/**
 * Representation of a domain Query.
 */
public abstract class QuickGOQuery {
    public abstract <T> T accept(QueryVisitor<T> visitor);

    public QuickGOQuery and(QuickGOQuery... query) {
        Preconditions.checkArgument(query != null, "Query to AND against cannot be null or empty");

        Set<QuickGOQuery> queries = aggregateQueries(of(this), of(query));

        return new CompositeQuery(queries, QueryOp.AND);
    }

    public QuickGOQuery or(QuickGOQuery... query) {
        Preconditions.checkArgument(query != null && query.length > 0, "Query to OR against cannot be null or empty");

        Set<QuickGOQuery> queries = aggregateQueries(of(this), of(query));

        return new CompositeQuery(queries, QueryOp.OR);
    }

    public QuickGOQuery not() {
        return new CompositeQuery(Collections.singleton(this), QueryOp.NOT);
    }

    private Set<QuickGOQuery> aggregateQueries(Stream<QuickGOQuery> query1, Stream<QuickGOQuery> query2) {
        return concat(query1, query2).collect(Collectors.toSet());
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

    public static QuickGOQuery createJoinQuery(String joinFromTable, String joinFromAttribute, String joinToTable,
            String joinToAttribute) {
        return new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);
    }

    public static QuickGOQuery createJoinQueryWithFilter(String joinFromTable, String joinFromAttribute,
            String joinToTable, String joinToAttribute, QuickGOQuery filter) {
        return new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute, filter);
    }
}