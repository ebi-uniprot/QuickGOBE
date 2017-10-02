package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.comm.RESTRequesterImpl;
import uk.ac.ebi.quickgo.rest.comm.ResponseType;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.springframework.web.client.RestOperations;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>Defines the conversion of a {@link FilterRequest} representing a REST request
 * to a corresponding instance of type {@code T}.
 *
 * Created by Edd on 05/06/2016.
 */
class RESTFilterConverter<T> implements FilterConverter<FilterRequest, T> {
    static final String HOST = "ip";
    static final String RESOURCE_FORMAT = "resourceFormat";
    static final String TIMEOUT = "timeout";
    static final String RESPONSE_CLASS = "responseClass";
    static final String RESPONSE_CONVERTER_CLASS = "responseConverter";
    static final String HTTPS_PROTOCOL = "https://";
    static final String HTTP_PROTOCOL = "http://";

    private static final Logger LOGGER = getLogger(RESTFilterConverter.class);
    private static final String COMMA = ",";
    private static final String FORWARD_SLASH = "/";
    private static final String HTTP_HOST_PREFIX = "http(s)?://";

    private static final Pattern IP_REGEX = Pattern.compile(
            HTTP_HOST_PREFIX + "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(:[0-9]+)?");
    private static final Pattern HOSTNAME_REGEX = Pattern.compile(
            HTTP_HOST_PREFIX + "([a-zA-Z0-9](?:(?:[a-zA-Z0-9-]*|(?<!-)\\.(?![-.]))*[a-zA-Z0-9]+)?)(:[0-9]+)?");
    private static final String FAILED_REST_FETCH_PREFIX = "Failed to fetch REST response";
    private static final int DEFAULT_TIMEOUT_MILLIS = 2000;

    private final FilterConfig filterConfig;
    private final RestOperations restOperations;
    private final Map<String, Constructor<?>> constructorMap;
    private int timeoutMillis;

    RESTFilterConverter(FilterConfig filterConfig, RestOperations restOperations) {
        Preconditions.checkArgument(filterConfig != null, "FilterConfig cannot be null");
        Preconditions.checkArgument(restOperations != null, "RestOperations cannot be null");

        this.filterConfig = filterConfig;
        this.restOperations = restOperations;

        checkMandatoryProperty(HOST);
        checkMandatoryProperty(RESOURCE_FORMAT);
        checkMandatoryProperty(RESPONSE_CLASS);
        checkMandatoryProperty(RESPONSE_CONVERTER_CLASS);

        this.constructorMap = new HashMap<>();
        this.timeoutMillis = loadTimeout();
    }

    @Override public ConvertedFilter<T> transform(FilterRequest request) {
        Preconditions.checkArgument(request != null, "FilterRequest cannot be null");
        RESTRequesterImpl.Builder restRequesterBuilder = initRequestBuilder(request);

        try {
            Class<?> restResponseType = loadResponseType();
            FilterConverter<ResponseType, T> converter = createConverter();
            ResponseType results = (ResponseType) fetchResults(restRequesterBuilder.build(), restResponseType);
            return converter.transform(results);
        } catch (Exception e) {
            String errorMessage = FAILED_REST_FETCH_PREFIX + " due to: ";
            if (e.getMessage() != null && !e.getMessage().trim().isEmpty()) {
                errorMessage += e.getMessage();
            } else {
                errorMessage += e.getClass().getSimpleName();
            }
            LOGGER.error(errorMessage, e);
            throw new RetrievalException(errorMessage, e);
        }
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


        if (!host.startsWith(HTTPS_PROTOCOL)) {
            if (host.startsWith(HTTP_PROTOCOL)) {
                host = HTTPS_PROTOCOL + host.substring(HTTP_PROTOCOL.length());
            } else {
                host = HTTPS_PROTOCOL + host;
            }
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

    private Class<?> loadResponseType() {
        String responseClassName = filterConfig.getProperties().get(RESPONSE_CLASS);
        try {
            return Class.forName(responseClassName);
        } catch (ClassNotFoundException e) {
            String errorMessage = "Could not load REST response type class: " + responseClassName;
            LOGGER.error(errorMessage, e);
            throw new IllegalStateException(errorMessage, e);
        }
    }

    private FilterConverter<ResponseType, T> createConverter()
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
                   InstantiationException {
        String converterClassName = filterConfig.getProperties().get(RESPONSE_CONVERTER_CLASS);

        Constructor<?> declaredConstructor;
        if (constructorMap.containsKey(converterClassName)) {
            declaredConstructor = constructorMap.get(converterClassName);
        } else {
            Class<?> converterClass = Class.forName(converterClassName);
            declaredConstructor = converterClass.getDeclaredConstructor();
            constructorMap.put(converterClassName, declaredConstructor);
        }

        return (FilterConverter<ResponseType, T>) declaredConstructor.newInstance();
    }

    private RESTRequesterImpl.Builder initRequestBuilder(FilterRequest request) {
        RESTRequesterImpl.Builder restRequesterBuilder = createRestRequesterBuilder();

        request.getProperties().forEach((key, value) ->
                restRequesterBuilder.addRequestParameter(
                        key,
                        value.stream()
                                .collect(Collectors.joining(COMMA))));

        return restRequesterBuilder;
    }

    private int loadTimeout() {
        int timeout = DEFAULT_TIMEOUT_MILLIS;

        if (filterConfig.getProperties().containsKey(TIMEOUT)) {
            boolean validTimeout = true;
            String timeoutValue = filterConfig.getProperties().get(TIMEOUT);
            try {
                timeout = Integer.parseInt(timeoutValue);
            } catch (NumberFormatException nfe) {
                validTimeout = false;
            }
            Preconditions
                    .checkArgument(validTimeout, "FilterConfig's 'TIMEOUT' property must be a number: " + timeoutValue);
        } else {
            LOGGER.debug("No " + TIMEOUT + " property specified in yml configuration. RESTFilterConverter will use " +
                    "default timeout of: " + timeoutMillis);
        }

        return timeout;
    }

    private void checkMandatoryProperty(String mandatoryProperty) {
        Preconditions.checkArgument(this.filterConfig.getProperties().containsKey(mandatoryProperty),
                "FilterConfig must have mandatory field: " + mandatoryProperty);
    }

    private <R> R fetchResults(RESTRequesterImpl restRequester, Class<R> responseType)
            throws ExecutionException, InterruptedException, TimeoutException {
        return restRequester
                .get(responseType)
                .get(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    static class InvalidHostNameException extends RuntimeException {
        InvalidHostNameException(String message) {
            super(message);
        }
    }
}
