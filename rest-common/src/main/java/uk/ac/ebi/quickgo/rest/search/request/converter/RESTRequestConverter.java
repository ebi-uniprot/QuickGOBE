package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.RESTCommRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import com.google.common.base.Preconditions;
import java.util.function.Function;

/**
 * Created by edd on 05/06/2016.
 */
class RESTRequestConverter implements Function<RESTCommRequest, QuickGOQuery> {
    private static final String LOCAL_FIELD = "localField";

    private final RequestConfig requestConfig;
    private final String localField;

    RESTRequestConverter(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
        this.localField = requestConfig.getProperties().get(LOCAL_FIELD);
    }

    @Override
    public QuickGOQuery apply(RESTCommRequest requestFilter) {
        Preconditions.checkArgument(requestFilter != null, "RESTCommRequest cannot be null");

        // create REST request executor
        // configure using requestConfig
        // configure using requestFilter

        // apply request and store results
        String restResults = "";

        return QuickGOQuery.createQuery(localField, restResults);
    }
}
