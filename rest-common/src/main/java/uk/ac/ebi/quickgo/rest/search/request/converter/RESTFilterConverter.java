package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.comm.RESTRequesterImpl;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import com.google.common.base.Preconditions;
import com.jayway.jsonpath.JsonPath;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.springframework.web.client.RestOperations;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>Defines the conversion of a {@link FilterRequest} representing a REST request
 * to a corresponding {@link QuickGOQuery}.
 *
 * Created by Edd on 05/06/2016.
 */
class RESTFilterConverter implements FilterConverter {
    static final String HOST = "ip";
    static final String RESOURCE_FORMAT = "resourceFormat";
    static final String BODY_PATH = "responseBodyPath";
    static final String LOCAL_FIELD = "localField";

    private static final Logger LOGGER = getLogger(RESTFilterConverter.class);
    private static final String COMMA = ",";
    private static final String HTTP_HOST_PREFIX = "http://";
    private static final String FORWARD_SLASH = "/";

    private static final Pattern IP_REGEX = Pattern.compile(
            HTTP_HOST_PREFIX + "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(:[0-9]+)?");
    private static final Pattern HOSTNAME_REGEX = Pattern.compile(
            HTTP_HOST_PREFIX + "([a-zA-Z0-9](?:(?:[a-zA-Z0-9-]*|(?<!-)\\.(?![-.]))*[a-zA-Z0-9]+)?)(:[0-9]+)?");

    // todo: this should be externally configurable (add a property to yaml, and if not present use default?)
    private static final int TIMEOUT_MILLIS = 2000;
    private static final String FAILED_REST_FETCH_PREFIX = "Failed to fetch REST response";

    private final FilterConfig filterConfig;
    private final RestOperations restOperations;

    RESTFilterConverter(FilterConfig filterConfig, RestOperations restOperations) {
        Preconditions.checkArgument(filterConfig != null, "FilterConfig cannot be null");
        Preconditions.checkArgument(restOperations != null, "RestOperations cannot be null");

        this.filterConfig = filterConfig;
        this.restOperations = restOperations;

        checkMandatoryProperty(HOST);
        checkMandatoryProperty(RESOURCE_FORMAT);
        checkMandatoryProperty(BODY_PATH);
        checkMandatoryProperty(LOCAL_FIELD);
    }

    @Override public QuickGOQuery transform(FilterRequest request) {
        Preconditions.checkArgument(request != null, "FilterRequest cannot be null");

        // create REST request executor
        RESTRequesterImpl.Builder restRequesterBuilder = createRestRequesterBuilder();
        request.getProperties().entrySet().stream()
                .forEach(entry ->
                        restRequesterBuilder.addRequestParameter(
                                entry.getKey(),
                                entry.getValue().stream()
                                        .collect(Collectors.joining(COMMA)))
                );

        // apply request and store results
        JsonPath jsonPath = JsonPath.compile(filterConfig.getProperties().get(BODY_PATH));
        try {
            Optional<QuickGOQuery> compositeQuery = extractValues(
                    fetchResults(restRequesterBuilder.build()),
                    jsonPath).stream()
                    .map(value -> QuickGOQuery
                            .createQuery(filterConfig.getProperties().get(LOCAL_FIELD), value))
                    .reduce(QuickGOQuery::or);

            if (compositeQuery.isPresent()) {
                return compositeQuery.get();
            }

        } catch (ExecutionException e) {
            throwRetrievalException(FAILED_REST_FETCH_PREFIX, e);
        } catch (InterruptedException e) {
            throwRetrievalException(
                    FAILED_REST_FETCH_PREFIX + " due to an interruption whilst waiting for response", e);
        } catch (TimeoutException e) {
            throwRetrievalException(FAILED_REST_FETCH_PREFIX + " due to a timeout whilst waiting for response", e);
        }

        return null;
    }

    static String buildResourceTemplate(FilterConfig config) {
        return retrieveHostProperty(config) + retrieveResourceFormat(config);
    }

    private static String retrieveResourceFormat(FilterConfig config) {
        String resourceFormat = config.getProperties().get(RESOURCE_FORMAT);
        if (!resourceFormat.startsWith(FORWARD_SLASH)) {
            resourceFormat = FORWARD_SLASH + resourceFormat;
        }
        return resourceFormat;
    }

    private static String retrieveHostProperty(FilterConfig config) {
        String host = config.getProperties().get(HOST).trim();

        if (!host.startsWith(HTTP_HOST_PREFIX)) {
            host = HTTP_HOST_PREFIX + host;
        }
        if (host.endsWith(FORWARD_SLASH)) {
            host = host.substring(0, host.length() - 1);
        }

        if (!HOSTNAME_REGEX.matcher(host).matches() && !IP_REGEX.matcher(host).matches()) {
            String errorMessage = "Invalid host name specified: " + host;
            LOGGER.error(errorMessage);
            throw new InvalidHostNameException(errorMessage);
        }

        return host;
    }

    RESTRequesterImpl.Builder createRestRequesterBuilder() {
        return RESTRequesterImpl.newBuilder(restOperations, buildResourceTemplate(filterConfig));
    }

    private void throwRetrievalException(String errorMessage, Exception e) {
        LOGGER.error(errorMessage, e);
        throw new RetrievalException(errorMessage, e);
    }

    private void checkMandatoryProperty(String mandatoryProperty) {
        Preconditions.checkArgument(this.filterConfig.getProperties().containsKey(mandatoryProperty),
                "FilterConfig must have mandatory field: " + mandatoryProperty);
    }

    private Set<String> extractValues(String responseBody, JsonPath jsonPath) {
        Set<String> results = new HashSet<>();
        if (jsonPath.isDefinite()) {
            results.add(jsonPath.read(responseBody));
        } else {
            ((JSONArray) jsonPath.read(responseBody)).iterator()
                    .forEachRemaining(value -> results.add(value.toString()));
        }
        return results;
    }

    private String fetchResults(RESTRequesterImpl restRequester)
            throws ExecutionException, InterruptedException, TimeoutException {
        return restRequester
                .get(String.class)
                .get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
    }

    static class InvalidHostNameException extends RuntimeException {
        InvalidHostNameException(String message) {
            super(message);
        }
    }
}
