package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

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

    private final FilterConfig filterConfig;
    private final String localField;

    RESTFilterConverter(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        this.localField = filterConfig.getProperties().get(LOCAL_FIELD);
    }

    @Override public QuickGOQuery transform(FilterRequest request) {
        Preconditions.checkArgument(request != null, "FilterRequest cannot be null");

        // create REST request executor
        // configure using requestConfig
        // configure using requestFilter

        // apply request and store results
        String restResults = "";

        return QuickGOQuery.createQuery(localField, restResults);
    }
}
