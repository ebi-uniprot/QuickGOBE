package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import com.google.common.base.Preconditions;

/**
 * Converts a {@link RequestFilter} into a {@link QuickGOQuery} that represents a join between two tables/collections.
 *
 * @author Ricardo Antunes
 */
class JoinFilterConverter implements FilterConverter {
    private final String fromTable;
    private final String fromAttribute;
    private final String toTable;
    private final String toAttribute;

    private QuickGOQuery filter;

    JoinFilterConverter(String fromTable, String fromAttribute, String toTable, String toAttribute) {
        this.fromTable = fromTable;
        this.fromAttribute = fromAttribute;
        this.toTable = toTable;
        this.toAttribute = toAttribute;
    }

    JoinFilterConverter(String fromTable, String fromAttribute, String toTable, String toAttribute,
            QuickGOQuery filter) {
        this(fromTable, fromAttribute, toTable, toAttribute);

        Preconditions.checkArgument(filter != null, "RequestFilter can not be null.");

        this.filter = filter;
    }

    @Override public QuickGOQuery transform() {
        QuickGOQuery query;

        if (filter == null) {
            query = QuickGOQuery.createJoinQuery(fromTable, fromAttribute, toTable, toAttribute);
        } else {
            query = QuickGOQuery.createJoinQueryWithFilter(fromTable, fromAttribute, toTable, toAttribute, filter);
        }

        return query;
    }
}