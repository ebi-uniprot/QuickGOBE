package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.comm.RESTRequesterImpl;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import com.google.common.base.Preconditions;
import com.jayway.jsonpath.JsonPath;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>Defines the conversion of a REST request to a corresponding {@link QuickGOQuery}.
 *
 * <p>NB. This class is a placeholder for a real implementation
 *
 * Created by Edd on 05/06/2016.
 */
class RESTFilterConverter implements FilterConverter {
    private static final String LOCAL_FIELD = "localField";
    private static final String IP = "ip";
    private static final String ENDPOINT = "endpoint";

    private static final Logger LOGGER = getLogger(RESTFilterConverter.class);

    private final RequestConfig requestConfig;
    private final String localField;

    RESTFilterConverter(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
        this.localField = requestConfig.getProperties().get(LOCAL_FIELD);
    }

    @Override public QuickGOQuery transform(FilterRequest request) {
        Preconditions.checkArgument(request != null, "FilterRequest cannot be null");

        // create REST request executor
        RESTRequesterImpl restRequester = RESTRequesterImpl
                .newBuilder(buildResource(request))
                .build();

        // configure using requestConfig

        // configure using requestFilter

        // apply request and store results
        fetchResults(restRequester).ifPresent(
                responseBody -> {
                    JsonPath jsonPath = JsonPath.compile(requestConfig.getProperties().get("responseBodyPath"));
                    Iterator<Object> pathVars = ((JSONArray) jsonPath.read(responseBody)).iterator();
                    pathVars.forEachRemaining(System.out::println);
                }
        );

        String restResults = "asdf";

        return QuickGOQuery.createQuery("whatever", restResults);
    }

    private Optional<String> fetchResults(RESTRequesterImpl restRequester) {
        try {
            return Optional.of(restRequester.get(String.class).get());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Failed to fetch results from REST endpoint: " + restRequester.toString(), e);
        }

        return Optional.empty();
    }

    private String buildResource(FilterRequest request) {
        return requestConfig.getProperties().get(IP) + "/" + requestConfig.getProperties().get(ENDPOINT)
                + "/" + request.getValue("id").get().get(0);
    }
}
