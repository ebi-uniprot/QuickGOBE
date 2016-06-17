package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.ClientRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import com.google.common.base.Preconditions;

/**
 * Defines the conversion of a join request to a corresponding {@link QuickGOQuery}.
 *
 * Created by Edd on 05/06/2016.
 */
class JoinRequestConverter implements RequestConverter  {

    static final String FROM_TABLE_NAME = "fromTable";
    static final String FROM_ATTRIBUTE_NAME = "fromAttribute";
    static final String TO_TABLE_NAME = "toTable";
    static final String TO_ATTRIBUTE_NAME = "toAttribute";

    private final String fromTable;
    private final String fromAttribute;
    private final String toTable;
    private final String toAttribute;

    private final RequestConfig requestConfig;

    /**
     * A join request converter uses the {@link RequestConfig} instance passed as parameter
     * to retrieve the join parameters: from/to which tables, and on which attributes.
     * @param requestConfig the execution configuration details associated with client requests
     */
    JoinRequestConverter(RequestConfig requestConfig) {
        Preconditions.checkArgument(requestConfig != null, "RequestConfig cannot be null");

        this.requestConfig = requestConfig;

        this.fromTable = this.requestConfig.getProperties().get(FROM_TABLE_NAME);
        this.fromAttribute = this.requestConfig.getProperties().get(FROM_ATTRIBUTE_NAME);
        this.toTable = this.requestConfig.getProperties().get(TO_TABLE_NAME);
        this.toAttribute = this.requestConfig.getProperties().get(TO_ATTRIBUTE_NAME);
    }
    /**
     * Converts a given {@link ClientRequest} into a {@link QuickGOQuery} that represents
     * a join. If {@code request} has no values, a query with no filter is created. Otherwise,
     * a query is created with a filter corresponding to the {@code request}.
     *
     * @param request the client request
     * @return a {@link QuickGOQuery} corresponding to a join query, representing the original client request
     */
    @Override public QuickGOQuery transform(ClientRequest request) {
        Preconditions.checkArgument(request != null, "ClientRequest cannot be null");

        if (request.getValues().isEmpty()) {
            return QuickGOQuery.createJoinQuery(fromTable, fromAttribute, toTable, toAttribute);
        } else {
            return QuickGOQuery.createJoinQueryWithFilter(
                    fromTable,
                    fromAttribute,
                    toTable,
                    toAttribute,
                    new SimpleRequestConverter(requestConfig).transform(request));
        }
    }
}
