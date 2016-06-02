package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import com.google.common.base.Preconditions;
import java.util.Map;

/**
 * Converts a {@link RequestFilterOld} into a {@link QuickGOQuery} that represents a join between two tables/collections.
 *
 * @author Ricardo Antunes
 */
class JoinFilterConverter implements FilterConverter {
    static final String FROM_TABLE_NAME = "fromTable";
    static final String FROM_ATTRIBUTE_NAME = "fromAttribute";
    static final String TO_TABLE_NAME = "toTable";
    static final String TO_ATTRIBUTE_NAME = "toAttribute";

    private final String fromTable;
    private final String fromAttribute;
    private final String toTable;
    private final String toAttribute;

    private FilterConverter filterConverter;

    private JoinFilterConverter(String fromTable, String fromAttribute, String toTable, String toAttribute) {
        this.fromTable = fromTable;
        this.fromAttribute = fromAttribute;
        this.toTable = toTable;
        this.toAttribute = toAttribute;
    }

    private JoinFilterConverter(String fromTable, String fromAttribute, String toTable, String toAttribute,
            FilterConverter filterConverter) {
        this(fromTable, fromAttribute, toTable, toAttribute);

        Preconditions.checkArgument(filterConverter != null, "Converter for the filter query cannot be null.");

        this.filterConverter = filterConverter;
    }

    @Override public QuickGOQuery transform() {
        QuickGOQuery query;

        if (filterConverter == null) {
            query = QuickGOQuery.createJoinQuery(fromTable, fromAttribute, toTable, toAttribute);
        } else {
            query = QuickGOQuery.createJoinQueryWithFilter(fromTable, fromAttribute, toTable, toAttribute,
                    filterConverter.transform());
        }

        return query;
    }

    /**
     * Factory method that creates a {@link JoinFilterConverter} by extracting the joining attributes found within
     * {@param joinProperties}, and uses the filter query that will come out of the provided
     * {@link FilterConverter#transform()} to further filter the joining collection/table.
     *
     * @param joinProperties a map containing the attributes necessary to execute a join between two collections/tables.
     * @param filterConverter a converter which holds a query that will further filter down the join to collection/table
     * @return a converter that knows how to transform the data into a join {@link QuickGOQuery}
     */
    static JoinFilterConverter createJoinConverterUsingMap(Map<String, String> joinProperties, FilterConverter
            filterConverter) {
        Preconditions.checkArgument(joinProperties != null, "Map containing join properties cannot be null.");

        String fromTable = joinProperties.get(FROM_TABLE_NAME);
        String fromAttribute = joinProperties.get(FROM_ATTRIBUTE_NAME);
        String toTable = joinProperties.get(TO_TABLE_NAME);
        String toAttribute = joinProperties.get(TO_ATTRIBUTE_NAME);

        return new JoinFilterConverter(fromTable, fromAttribute, toTable, toAttribute, filterConverter);
    }

    /**
     * Factory method that creates a {@link JoinFilterConverter} using the joining attributes found
     * and uses the filter query that will come out of the provided
     * {@link FilterConverter#transform()} to further filter the joining collection/table.
     *
     * @param fromTable the collection/table to join from
     * @param fromAttribute a joining attribute found in {@param fromTable}
     * @param toTable the collection/table to join to
     * @param toAttribute a joining attribute found in {@param toTable}
     * @param filterConverter converter that holds information to further filter the {@param toTable}
     * @return a converter that knows how to transform the data into a join {@link QuickGOQuery}
     */
    static JoinFilterConverter createJoinConverterUsingParameters(String fromTable, String fromAttribute,
            String toTable, String toAttribute, FilterConverter filterConverter) {
        return new JoinFilterConverter(fromTable, fromAttribute, toTable, toAttribute, filterConverter);
    }

    /**
     * Factory method that creates a {@link JoinFilterConverter} using the joining attributes found.
     *
     * @param fromTable the collection/table to join from
     * @param fromAttribute a joining attribute found in {@param fromTable}
     * @param toTable the collection/table to join to
     * @param toAttribute a joining attribute found in {@param toTable}
     * @return a converter that knows how to transform the data into a join {@link QuickGOQuery}
     */
    static JoinFilterConverter createJoinConverterUsingParametersWithoutFilter(String fromTable, String fromAttribute,
            String toTable, String toAttribute) {
        return new JoinFilterConverter(fromTable, fromAttribute, toTable, toAttribute);
    }
}