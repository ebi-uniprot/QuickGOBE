package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.comm.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import com.google.common.base.Preconditions;
import java.util.List;

import static java.util.Arrays.asList;
import static uk.ac.ebi.quickgo.rest.comm.ConvertedFilter.simpleConvertedResponse;

/**
 * Defines the conversion of a join request to a corresponding {@link QuickGOQuery}.
 *
 * Created by Edd on 05/06/2016.
 */
class JoinFilterConverter implements FilterConverter<FilterRequest, QuickGOQuery> {

    static final String FROM_TABLE_NAME = "fromTable";
    static final String FROM_ATTRIBUTE_NAME = "fromAttribute";
    static final String TO_TABLE_NAME = "toTable";
    static final String TO_ATTRIBUTE_NAME = "toAttribute";

    private static final List<String> REQUIRED_PROPERTY_KEYS =
            asList(FROM_TABLE_NAME, FROM_ATTRIBUTE_NAME, TO_TABLE_NAME, TO_ATTRIBUTE_NAME);
    private final String fromTable;
    private final String fromAttribute;
    private final String toTable;
    private final String toAttribute;

    private final FilterConfig filterConfig;

    /**
     * A join request converter uses the {@link FilterConfig} instance passed as parameter
     * to retrieve the join parameters: from/to which tables, and on which attributes.
     * @param filterConfig the execution configuration details associated with client requests
     */
    JoinFilterConverter(FilterConfig filterConfig) {
        validateRequestConfig(filterConfig);

        this.filterConfig = filterConfig;

        this.fromTable = this.filterConfig.getProperties().get(FROM_TABLE_NAME);
        this.fromAttribute = this.filterConfig.getProperties().get(FROM_ATTRIBUTE_NAME);
        this.toTable = this.filterConfig.getProperties().get(TO_TABLE_NAME);
        this.toAttribute = this.filterConfig.getProperties().get(TO_ATTRIBUTE_NAME);
    }

    /**
     * Converts a given {@link FilterRequest} into a {@link QuickGOQuery} that represents
     * a join. If {@code request} has no values, a query with no filter is created. Otherwise,
     * a query is created with a filter corresponding to the {@code request}.
     *
     * @param request the client request
     * @return a {@link QuickGOQuery} corresponding to a join query, representing the original client request
     */
    @Override public ConvertedFilter<QuickGOQuery> transform(FilterRequest request) {
        Preconditions.checkArgument(request != null, "ClientRequest cannot be null");

        if (request.getValues().isEmpty()) {
            return simpleConvertedResponse(
                    QuickGOQuery.createJoinQuery(fromTable, fromAttribute, toTable, toAttribute));
        } else {
            return simpleConvertedResponse(
                    QuickGOQuery.createJoinQueryWithFilter(
                            fromTable,
                            fromAttribute,
                            toTable,
                            toAttribute,
                            new SimpleFilterConverter(filterConfig).transform(request).getConvertedValue()));
        }
    }

    private void validateRequestConfig(FilterConfig filterConfig) {
        Preconditions.checkArgument(filterConfig != null, "RequestConfig cannot be null");

        for (String requiredProperty : REQUIRED_PROPERTY_KEYS) {
            Preconditions.checkArgument(
                    filterConfig.getProperties().containsKey(requiredProperty)
                            && !filterConfig.getProperties().get(requiredProperty).trim().isEmpty(),
                    "RequestConfig properties should contain " + requiredProperty + " key.");
        }
    }
}
