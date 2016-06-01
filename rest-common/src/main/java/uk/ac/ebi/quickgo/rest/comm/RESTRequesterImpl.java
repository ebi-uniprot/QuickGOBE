package uk.ac.ebi.quickgo.rest.comm;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.springframework.web.client.RestTemplate;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created 31/05/16
 * @author Edd
 */
class RESTRequesterImpl implements RESTRequester {
    private static final String EMPTY_URL = "";
    private static final Logger LOGGER = getLogger(RESTRequesterImpl.class);
    private final String url;
    private Map<String, List<String>> requestParameters;

    private RESTRequesterImpl() {
        this.url = EMPTY_URL;
        this.requestParameters = new HashMap<>();
    }

    private RESTRequesterImpl(String url, Map<String, List<String>> requestParameters) {
        this.url = url;
        this.requestParameters = requestParameters;
    }

    @Override
    public <T> CompletableFuture<T> get(Class<T> responseType) {
        return get(new RestTemplate(), responseType);
    }

    static Builder newBuilder(String url) {
        return new Builder(url);
    }

    <T> CompletableFuture<T> get(RestTemplate template, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() ->
                template.getForObject(url, responseType, requestParameters));
    }

    public static class Builder {

        private String url;
        private Map<String, List<String>> requestParameters;

        Builder(String url) {
            checkURL(url);

            this.url = url;
            this.requestParameters = new LinkedHashMap<>();
        }

        public RESTRequesterImpl build() {
            return new RESTRequesterImpl(url, requestParameters);
        }

        public Builder resetURL(String url) {
            checkURL(url);

            this.url = url;
            return this;
        }

        public Builder addRequestParameter(String name, String value) {
            Preconditions.checkArgument(name != null && !name.trim().isEmpty(), "Name cannot be null or empty");
            Preconditions.checkArgument(value != null && !value.trim().isEmpty(), "Value cannot be null or empty");

            if (!requestParameters.containsKey(name)) {
                requestParameters.put(name, new ArrayList<>());
            }
            requestParameters.get(name).add(value);

            return this;
        }

        public Builder setRequestParameters(Map<String, List<String>> requestParameters) {
            Preconditions.checkArgument(requestParameters != null, "RequestParameters cannot be null");

            this.requestParameters = requestParameters;

            return this;
        }

        private void checkURL(String url) {
            Preconditions.checkArgument(url != null && !url.trim().isEmpty(), "URL cannot be null or empty");
        }
    }
}