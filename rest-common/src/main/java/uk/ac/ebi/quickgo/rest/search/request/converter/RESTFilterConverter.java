package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.comm.RESTRequesterImpl;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import com.google.common.base.Preconditions;
import com.jayway.jsonpath.JsonPath;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.springframework.web.client.RestOperations;

import static org.slf4j.LoggerFactory.getLogger;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.not;

/**
 * <p>Defines the conversion of a {@link FilterRequest} representing a REST request
 * to a corresponding {@link QuickGOQuery}.
 * <p>
 * Created by Edd on 05/06/2016.
 */
class RESTFilterConverter implements FilterConverter {
    static final String HOST = "ip";
    static final String RESOURCE_FORMAT = "resourceFormat";
    static final String BODY_PATH = "responseBodyPath";
    static final String LOCAL_FIELD = "localField";
    static final String TIMEOUT = "timeout";

    private static final Logger LOGGER = getLogger(RESTFilterConverter.class);
    private static final String COMMA = ",";
    private static final String HTTP_HOST_PREFIX = "http://";
    private static final String FORWARD_SLASH = "/";

    private static final Pattern IP_REGEX = Pattern.compile(
            HTTP_HOST_PREFIX + "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(:[0-9]+)?");
    private static final Pattern HOSTNAME_REGEX = Pattern.compile(
            HTTP_HOST_PREFIX + "([a-zA-Z0-9](?:(?:[a-zA-Z0-9-]*|(?<!-)\\.(?![-.]))*[a-zA-Z0-9]+)?)(:[0-9]+)?");
    private static final String FAILED_REST_FETCH_PREFIX = "Failed to fetch REST response";
    private static final int DEFAULT_TIMEOUT_MILLIS = 2000;

    private final FilterConfig filterConfig;
    private final RestOperations restOperations;
    private int timeoutMillis;

    RESTFilterConverter(FilterConfig filterConfig, RestOperations restOperations) {
        Preconditions.checkArgument(filterConfig != null, "FilterConfig cannot be null");
        Preconditions.checkArgument(restOperations != null, "RestOperations cannot be null");

        this.filterConfig = filterConfig;
        this.restOperations = restOperations;

        checkMandatoryProperty(HOST);
        checkMandatoryProperty(RESOURCE_FORMAT);
        checkMandatoryProperty(BODY_PATH);
        checkMandatoryProperty(LOCAL_FIELD);

        initialiseTimeout();
    }

    @Override
    public QuickGOQuery transform(FilterRequest request) {
        Preconditions.checkArgument(request != null, "FilterRequest cannot be null");

        // create REST request executor
        RESTRequesterImpl.Builder restRequesterBuilder = createRestRequesterBuilder();
        request.getProperties().entrySet().forEach(entry ->
                restRequesterBuilder.addRequestParameter(
                        entry.getKey(),
                        entry.getValue().stream()
                                .collect(Collectors.joining(COMMA)))
        );

        QuickGOQuery nothingMatchesQuery = not(QuickGOQuery.createAllQuery());

        // apply request and store results
        JsonPath jsonPath = JsonPath.compile(filterConfig.getProperties().get(BODY_PATH));
        try {
            Set<QuickGOQuery> queries = retrieveThenConvertResponse(
                    fetchResults(restRequesterBuilder.build()),
                    jsonPath,
                    responseString -> QuickGOQuery
                            .createQuery(filterConfig.getProperties().get(LOCAL_FIELD), responseString));

            if (queries.size() > 0) {
                return QuickGOQuery.or(queries.toArray(new QuickGOQuery[queries.size()]));
            }

            return nothingMatchesQuery;
        } catch (ExecutionException e) {
            throwRetrievalException(FAILED_REST_FETCH_PREFIX, e);
        } catch (InterruptedException e) {
            throwRetrievalException(
                    FAILED_REST_FETCH_PREFIX + " due to an interruption whilst waiting for response", e);
        } catch (TimeoutException e) {
            throwRetrievalException(FAILED_REST_FETCH_PREFIX + " due to a timeout whilst waiting for response", e);
        }

        return nothingMatchesQuery;
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

    private void initialiseTimeout() {
        if (filterConfig.getProperties().containsKey(TIMEOUT)) {
            boolean validTimeout = true;
            String timeoutValue = filterConfig.getProperties().get(TIMEOUT);
            try {
                timeoutMillis = Integer.parseInt(timeoutValue);
            } catch (NumberFormatException nfe) {
                validTimeout = false;
            }
            Preconditions
                    .checkArgument(validTimeout, "FilterConfig's 'TIMEOUT' property must be a number: " + timeoutValue);
        } else {
            timeoutMillis = DEFAULT_TIMEOUT_MILLIS;
            LOGGER.debug("No " + TIMEOUT + " property specified in yaml configuration. RESTFilterConverter will use " +
                    "default timeout of: " + timeoutMillis);
        }
    }

    private void throwRetrievalException(String errorMessage, Exception e) {
        LOGGER.error(errorMessage, e);
        throw new RetrievalException(errorMessage, e);
    }

    private void checkMandatoryProperty(String mandatoryProperty) {
        Preconditions.checkArgument(this.filterConfig.getProperties().containsKey(mandatoryProperty),
                "FilterConfig must have mandatory field: " + mandatoryProperty);
    }

    private <T> Set<T> retrieveThenConvertResponse(String responseBody,
            JsonPath jsonPath,
            Function<String, T> converter) {
        Set<T> results = new HashSet<>();
        if (jsonPath.isDefinite()) {
            results.add(converter.apply(jsonPath.read(responseBody)));
        } else {
            ((JSONArray) jsonPath.read(responseBody)).iterator()
                    .forEachRemaining(s -> results.add(converter.apply(s.toString())));
        }
        return results;
    }

    private String fetchResults(RESTRequesterImpl restRequester)
            throws ExecutionException, InterruptedException, TimeoutException {
        return restRequester
                .get(String.class)
                .get(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    static class InvalidHostNameException extends RuntimeException {
        InvalidHostNameException(String message) {
            super(message);
        }
    }
}
