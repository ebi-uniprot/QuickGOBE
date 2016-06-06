package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.RESTRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import com.google.common.base.Preconditions;
import java.util.function.Function;

/**
 * <p>Defines the conversion of a REST request to a corresponding {@link QuickGOQuery}.
 *
 * <p>NB. This class is a placeholder for a real implementation
 *
 * Created by Edd on 05/06/2016.
 */
class RESTRequestConverter implements Function<RESTRequest, QuickGOQuery> {
    private static final String LOCAL_FIELD = "localField";

    private final RequestConfig requestConfig;
    private final String localField;

    RESTRequestConverter(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
        this.localField = requestConfig.getProperties().get(LOCAL_FIELD);
    }

    @Override
    public QuickGOQuery apply(RESTRequest requestFilter) {
        Preconditions.checkArgument(requestFilter != null, "RESTCommRequest cannot be null");

        // create REST request executor
        // configure using requestConfig
        // configure using requestFilter

        // apply request and store results
        String restResults = "";

        return QuickGOQuery.createQuery(localField, restResults);
    }
}
