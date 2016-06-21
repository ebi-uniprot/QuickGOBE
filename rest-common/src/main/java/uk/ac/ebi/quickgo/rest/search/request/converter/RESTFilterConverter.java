package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.comm.RESTRequesterImpl;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import com.google.common.base.Preconditions;
import com.jayway.jsonpath.JsonPath;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>Defines the conversion of a {@link FilterRequest} representing a REST request
 * to a corresponding {@link QuickGOQuery}.
 *
 * Created by Edd on 05/06/2016.
 */
class RESTFilterConverter implements FilterConverter {
    private static final Logger LOGGER = getLogger(RESTFilterConverter.class);
    static final String HOST = "ip";
    static final String RESOURCE_FORMAT = "resourceFormat";
    static final String BODY_PATH = "responseBodyPath";
    static final String LOCAL_FIELD = "localField";
    private static final String COMMA = ",";
    private static final String HTTP_HOST_PREFIX = "http://";
    private static final String FORWARD_SLASH = "/";

    private final FilterConfig filterConfig;

    RESTFilterConverter(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;

        checkMandatoryProperty(HOST);
        checkMandatoryProperty(RESOURCE_FORMAT);
        checkMandatoryProperty(BODY_PATH);
        checkMandatoryProperty(LOCAL_FIELD);
    }

    @Override public QuickGOQuery transform(FilterRequest request) {
        Preconditions.checkArgument(request != null, "FilterRequest cannot be null");

        // create REST request executor
        RESTRequesterImpl.Builder restRequesterBuilder =
                RESTRequesterImpl.newBuilder(buildResourceTemplate(filterConfig));
        request.getProperties().entrySet().stream()
                .forEach(entry ->
                    restRequesterBuilder.addRequestParameter(
                            entry.getKey(),
                            entry.getValue().stream()
                                    .collect(Collectors.joining(COMMA)))
                );

        // apply request and store results
        JsonPath jsonPath = JsonPath.compile(filterConfig.getProperties().get(BODY_PATH));
        StringJoiner joinedValues = new StringJoiner(COMMA);
        fetchResults(restRequesterBuilder.build())
                .ifPresent(responseBody -> extractValues(responseBody, jsonPath, joinedValues));

        return QuickGOQuery.createQuery(filterConfig.getProperties().get(LOCAL_FIELD), joinedValues.toString());
    }

    private void checkMandatoryProperty(String mandatoryProperty) {
        Preconditions.checkArgument(this.filterConfig.getProperties().containsKey(mandatoryProperty),
                "FilterConfig must have mandatory field: " + mandatoryProperty);
    }

    private void extractValues(String responseBody, JsonPath jsonPath, StringJoiner joinedValues) {
        ((JSONArray) jsonPath.read(responseBody)).iterator()
                .forEachRemaining(value -> joinedValues.add(value.toString()));
    }

    private Optional<String> fetchResults(RESTRequesterImpl restRequester) {
        try {
            return Optional.of(restRequester.get(String.class).get());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Failed to fetch results from REST endpoint: " + restRequester.toString(), e);
        }

        return Optional.empty();
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
        return host;
    }
}
