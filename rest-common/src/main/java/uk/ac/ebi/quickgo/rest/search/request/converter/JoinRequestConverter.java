package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.SimpleRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import com.google.common.base.Preconditions;
import java.util.function.Function;

/**
 * Created by edd on 05/06/2016.
 */
class JoinRequestConverter implements Function<SimpleRequest, QuickGOQuery> {

    static final String FROM_TABLE_NAME = "fromTable";
    static final String FROM_ATTRIBUTE_NAME = "fromAttribute";
    static final String TO_TABLE_NAME = "toTable";
    static final String TO_ATTRIBUTE_NAME = "toAttribute";

    private final String fromTable;
    private final String fromAttribute;
    private final String toTable;
    private final String toAttribute;


    private final RequestConfig requestConfig;

    JoinRequestConverter(RequestConfig requestConfig) {
        Preconditions.checkArgument(requestConfig != null, "RequestConfig cannot be null");

        this.requestConfig = requestConfig;

        this.fromTable = this.requestConfig.getProperties().get(FROM_TABLE_NAME);
        this.fromAttribute = this.requestConfig.getProperties().get(FROM_ATTRIBUTE_NAME);
        this.toTable = this.requestConfig.getProperties().get(TO_TABLE_NAME);
        this.toAttribute = this.requestConfig.getProperties().get(TO_ATTRIBUTE_NAME);
    }

    @Override
    public QuickGOQuery apply(SimpleRequest simpleRequest) {
        Preconditions.checkArgument(simpleRequest != null, "SimpleRequest cannot be null");

        if (simpleRequest.getValues().isEmpty()) {
            return QuickGOQuery.createJoinQuery(fromTable, fromAttribute, toTable, toAttribute);
        } else {
            return QuickGOQuery.createJoinQueryWithFilter(
                    fromTable,
                    fromAttribute,
                    toTable,
                    toAttribute,
                    new SimpleRequestConverter(requestConfig).apply(simpleRequest));
        }
    }
}
