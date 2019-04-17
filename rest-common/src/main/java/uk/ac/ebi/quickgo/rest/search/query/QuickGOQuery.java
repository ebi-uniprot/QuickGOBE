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
    public static final String SELECT_ALL_WHERE_FIELD_IS_NOT_EMPTY = "*";

    /**
     * Performs a generalised disjunction (OR) over the supplied queries.
     *
     * @param queries queries to be placed into an OR query
     * @return a query representing the overall disjunction of queries
     */
    public static QuickGOQuery or(QuickGOQuery... queries) {
        Preconditions.checkArgument(queries != null && arrayHasNoNullElements(queries),
                                    "Queries to compose cannot be null");
        if (queries.length == 1) {
            return queries[0];
        } else {
            Set<QuickGOQuery> queriesSet = new LinkedHashSet<>();
            Collections.addAll(queriesSet, queries);
            return new CompositeQuery(queriesSet, QueryOp.OR);
        }
    }

    /**
     * Performs a generalised conjunction (AND) over the supplied queries.
     *
     * @param queries queries to be placed into an OR query
     * @return a query representing the overall conjunction of queries
     */
    public static QuickGOQuery and(QuickGOQuery... queries) {
        Preconditions.checkArgument(queries != null && arrayHasNoNullElements(queries),
                                    "Queries to compose cannot be null");
        if (queries.length == 1) {
            return queries[0];
        } else {
            Set<QuickGOQuery> queriesSet = new LinkedHashSet<>();
            Collections.addAll(queriesSet, queries);
            return new CompositeQuery(queriesSet, QueryOp.AND);
        }
    }

    public static QuickGOQuery not(QuickGOQuery query) {
        Preconditions.checkArgument(query != null);

        return new CompositeQuery(Collections.singleton(query), QueryOp.NOT);
    }
    public static QuickGOQuery createQuery(String field, String value) {
        if (value.equals(SELECT_ALL_WHERE_FIELD_IS_NOT_EMPTY)) {
            return new AllNonEmptyFieldQuery(field, value);
        }
        return new FieldQuery(field, value);
    }

    public static QuickGOQuery createQuery(String value) {
        return new NoFieldQuery(value);
    }

    public static QuickGOQuery createAllQuery() {
        return new AllQuery();
    }

    public static QuickGOQuery createContainQuery(String field, String value) {
        return new ContainsFieldQuery(field, value);
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

    private static <T> boolean arrayHasNoNullElements(T[] array) {
        for (T element : array) {
            if (element == null) {
                return false;
            }
        }

        return true;
    }
}
