package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import com.google.common.base.Preconditions;

/**
 * <p>Defines the conversion of a REST request to a corresponding {@link QuickGOQuery}.
 *
 * <p>NB. This class is a placeholder for a real implementation
 *
 * Created by Edd on 05/06/2016.
 */
class RESTFilterConverter implements FilterConverter {
    private static final String LOCAL_FIELD = "localField";

    private final RequestConfig requestConfig;
    private final String localField;

    RESTFilterConverter(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
        this.localField = requestConfig.getProperties().get(LOCAL_FIELD);
    }

    @Override public QuickGOQuery transform(FilterRequest request) {
        Preconditions.checkArgument(request != null, "ClientRequest cannot be null");

        // create REST request executor
        // configure using requestConfig
        // configure using requestFilter

        // apply request and store results
        String restResults = "";

        return QuickGOQuery.createQuery(localField, restResults);
    }
}
