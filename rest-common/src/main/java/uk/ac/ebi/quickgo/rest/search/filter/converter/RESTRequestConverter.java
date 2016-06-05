package uk.ac.ebi.quickgo.rest.search.filter.converter;

import com.google.common.base.Preconditions;
import uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig;
import uk.ac.ebi.quickgo.rest.search.filter.request.RESTCommRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.function.Function;

/**
 * Created by edd on 05/06/2016.
 */
public class RESTRequestConverter implements Function<RESTCommRequest, QuickGOQuery> {
    public static final String LOCAL_FIELD = "localField";

    private final RequestFilterConfig requestConfig;
    private final String localField;

    public RESTRequestConverter(RequestFilterConfig requestConfig) {
        this.requestConfig = requestConfig;
        this.localField = requestConfig.getProperties().get(LOCAL_FIELD);
    }

    @Override
    public QuickGOQuery apply(RESTCommRequest requestFilter) {
        Preconditions.checkArgument(requestFilter != null, "RESTCommRequestFilter cannot be null");

        // create REST request executor
        // configure using requestConfig
        // configure using requestFilter

        // apply request and store results
        String restResults = "";

        return QuickGOQuery.createQuery(localField, restResults);
    }
}
