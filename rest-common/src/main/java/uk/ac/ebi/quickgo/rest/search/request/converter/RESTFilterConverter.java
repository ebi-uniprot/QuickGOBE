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
import java.util.stream.Collector;
import java.util.stream.Collectors;
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
    private static final String IP = "ip";
    private static final String RESOURCE_FORMAT = "resourceFormat";
    private static final String BODY_PATH = "responseBodyPath";

    private static final Logger LOGGER = getLogger(RESTFilterConverter.class);
    private static final Collector<CharSequence, ?, String> COMMA = Collectors.joining(",");
    private static final String LOCAL_FIELD = "localField";

    private final FilterConfig filterConfig;

    RESTFilterConverter(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;

        checkMandatoryProperty(RESOURCE_FORMAT);
        checkMandatoryProperty(BODY_PATH);
        checkMandatoryProperty(LOCAL_FIELD);
    }

    private void checkMandatoryProperty(String mandatoryProperty) {
        Preconditions.checkArgument(this.filterConfig.getProperties().containsKey(mandatoryProperty),
                "FilterConfig must have mandatory field: " + mandatoryProperty);
    }

    @Override public QuickGOQuery transform(FilterRequest request) {
        Preconditions.checkArgument(request != null, "FilterRequest cannot be null");

        // create REST request executor
        RESTRequesterImpl.Builder restRequesterBuilder = RESTRequesterImpl
                .newBuilder(buildResource());
        request.getProperties().entrySet().stream()
                .forEach(entry -> {
                    restRequesterBuilder.addRequestParameter(
                            entry.getKey(),
                            entry.getValue().stream()
                                    .collect(COMMA));
                });

        // apply request and store results
        JsonPath jsonPath = JsonPath.compile(filterConfig.getProperties().get(BODY_PATH));
        StringJoiner joinedValues = new StringJoiner(",");
        fetchResults(restRequesterBuilder.build())
                .ifPresent(responseBody -> insertValues(responseBody, jsonPath, joinedValues));

        return QuickGOQuery.createQuery(filterConfig.getProperties().get(LOCAL_FIELD), joinedValues.toString());
    }

    private void insertValues(String responseBody, JsonPath jsonPath, StringJoiner joinedValues) {
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

    private String buildResource() {
        return filterConfig.getProperties().get(IP) + filterConfig.getProperties().get(RESOURCE_FORMAT);
    }
}
