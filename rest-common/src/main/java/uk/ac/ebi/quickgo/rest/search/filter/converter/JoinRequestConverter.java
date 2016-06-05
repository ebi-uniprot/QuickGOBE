package uk.ac.ebi.quickgo.rest.search.filter.converter;

import com.google.common.base.Preconditions;
import uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig;
import uk.ac.ebi.quickgo.rest.search.filter.request.SimpleRequestFilter;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.function.Function;

/**
 * Created by edd on 05/06/2016.
 */
public class JoinRequestConverter implements Function<SimpleRequestFilter, QuickGOQuery> {

    static final String FROM_TABLE_NAME = "fromTable";
    static final String FROM_ATTRIBUTE_NAME = "fromAttribute";
    static final String TO_TABLE_NAME = "toTable";
    static final String TO_ATTRIBUTE_NAME = "toAttribute";

    private final String fromTable;
    private final String fromAttribute;
    private final String toTable;
    private final String toAttribute;


    private final RequestFilterConfig requestConfig;

    public JoinRequestConverter(RequestFilterConfig requestFilterConfig) {
        this.requestConfig = requestFilterConfig;

        this.fromTable = requestConfig.getProperties().get(FROM_TABLE_NAME);
        this.fromAttribute = requestConfig.getProperties().get(FROM_ATTRIBUTE_NAME);
        this.toTable = requestConfig.getProperties().get(TO_TABLE_NAME);
        this.toAttribute = requestConfig.getProperties().get(TO_ATTRIBUTE_NAME);
    }

    @Override
    public QuickGOQuery apply(SimpleRequestFilter simpleRequestFilter) {
        Preconditions.checkArgument(simpleRequestFilter != null, "SimpleRequestFilter cannot be null");

        if (simpleRequestFilter.getValues().isEmpty()) {
            return QuickGOQuery.createJoinQuery(fromTable, fromAttribute, toTable, toAttribute);
        } else {
            return QuickGOQuery.createJoinQueryWithFilter(fromTable, fromAttribute, toTable, toAttribute,
                    new SimpleRequestConverter(requestConfig).apply(simpleRequestFilter));

        }
    }
}
