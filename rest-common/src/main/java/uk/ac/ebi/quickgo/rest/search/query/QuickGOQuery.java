package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.ebi.quickgo.rest.search.query.CompositeQuery.QueryOp;

/**
 * Representation of a domain Query.
 */
public abstract class QuickGOQuery {
    /**
     * Performs a generalised disjunction (OR) over the supplied queries.
     *
     * @param queries queries to be placed into an OR query
     * @return a query representing the overall disjunction of queries
     */
    public static QuickGOQuery generalisedOr(QuickGOQuery... queries) {
        Preconditions.checkArgument(queries != null &&
                        arrayHasNoNullElements(queries) &&
                        queries.length > 1,
                "Queries to compose cannot be null or empty");
        return new CompositeQuery(Sets.newHashSet(queries), QueryOp.OR);
    }

    /**
     * Performs a generalised conjunction (AND) over the supplied queries.
     *
     * @param queries queries to be placed into an OR query
     * @return a query representing the overall conjunction of queries
     */
    public static QuickGOQuery generalisedAnd(QuickGOQuery... queries) {
        Preconditions.checkArgument(queries != null &&
                        arrayHasNoNullElements(queries) &&
                        queries.length > 1,
                "Queries to compose cannot be null or empty");
        return new CompositeQuery(Sets.newHashSet(queries), QueryOp.AND);
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

    public abstract <T> T accept(QueryVisitor<T> visitor);

    public QuickGOQuery and(QuickGOQuery... queriesToAnd) {
        Preconditions.checkArgument(queriesToAnd != null &&
                arrayHasNoNullElements(queriesToAnd) &&
                queriesToAnd.length > 0, "Query to AND against cannot be null or empty");

        Set<QuickGOQuery> queries = aggregateQueries(this, queriesToAnd);

        return new CompositeQuery(queries, QueryOp.AND);
    }

    public QuickGOQuery or(QuickGOQuery... queriesToOr) {
        Preconditions.checkArgument(queriesToOr != null &&
                arrayHasNoNullElements(queriesToOr) &&
                queriesToOr.length > 0, "Query to OR against cannot be null or empty");

        Set<QuickGOQuery> queries = aggregateQueries(this, queriesToOr);

        return new CompositeQuery(queries, QueryOp.OR);
    }

    public QuickGOQuery not() {
        return new CompositeQuery(Collections.singleton(this), QueryOp.NOT);
    }

    /**
     * <p>Aggregates the supplied queries into a {@link Set} of {@link QuickGOQuery}s.
     *
     * @param originalQuery the original query which needs additional queries added to it
     * @param queries       the queries to compose to the original query
     * @return a query comprising of the supplied queries
     */
    private Set<QuickGOQuery> aggregateQueries(QuickGOQuery originalQuery, QuickGOQuery... queries) {
        Set<QuickGOQuery> aggregate = new LinkedHashSet<>();
        aggregate.add(originalQuery);
        Collections.addAll(aggregate, queries);
        return aggregate;
    }

    private static <T> boolean arrayHasNoNullElements(T[] array) {
        for (T element : array) {
            if (element == null) {
                return false;
            }
        }

        return true;
    }
}