package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import static uk.ac.ebi.quickgo.rest.search.query.CompositeQuery.QueryOp;

/**
 * Representation of a domain Query.
 */
public abstract class QuickGOQuery {
    public abstract <T> T accept(QueryVisitor<T> visitor);

    public QuickGOQuery and(QuickGOQuery... query) {
        Preconditions.checkArgument(query != null, "Query to AND against cannot be null or empty");

        Set<QuickGOQuery> queries = aggregateQueries(this, query);

        return new CompositeQuery(queries, QueryOp.AND);
    }

    public QuickGOQuery or(QuickGOQuery... query) {
        Preconditions.checkArgument(query != null && query.length > 0, "Query to OR against cannot be null or empty");

        Set<QuickGOQuery> queries = aggregateQueries(this, query);

        return new CompositeQuery(queries, QueryOp.OR);
    }

    public QuickGOQuery not() {
        return new CompositeQuery(Collections.singleton(this), QueryOp.NOT);
    }

    /**
     * <p>Aggregates the supplied queries into a {@link Set} of {@link QuickGOQuery}s.
     *
     * @implNote
     * The algorithm used chooses not to use the more fluent API of {@link Stream}s and their
     * concatenation, because these recursive operations can cause {@link StackOverflowError}s.
     * Instead, regular for-looping is used.
     *
     * @param originalQuery the original query which needs additional queries added to it
     * @param queries the queries to compose to the original query
     * @return a query comprising of the supplied queries
     */
    private Set<QuickGOQuery> aggregateQueries(QuickGOQuery originalQuery, QuickGOQuery... queries) {
        Set<QuickGOQuery> aggregate = new LinkedHashSet<>();
        aggregate.add(originalQuery);
        for (QuickGOQuery query : queries) {
            aggregate.add(query);
        }
        return aggregate;
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