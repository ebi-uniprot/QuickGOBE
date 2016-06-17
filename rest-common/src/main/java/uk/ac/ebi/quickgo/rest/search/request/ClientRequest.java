package uk.ac.ebi.quickgo.rest.search.request;

import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfigRetrieval;

import com.google.common.base.Preconditions;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * The contract required by a client's query request.
 *
 * Created 02/06/16
 * @author Edd
 */
public class ClientRequest {
    private final Map<String, List<String>> properties;

    private ClientRequest(Builder builder) {
        this.properties = builder.properties;
    }

    /**
     * Get the value(s) associated with the supplied {@code key}.
     *
     * @param key the key whose associated values are being required
     * @return the value(s) associated with {@code key}
     */
    public Optional<List<String>> getValue(String key) {
        if (properties.containsKey(key)) {
            return Optional.of(properties.get(key));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Retrieve all values associated with this request. This is convenient
     * if the caller knows, e.g., that the request only has 1 property
     *
     * @return all values associated with this request
     */
    public Collection<List<String>> getValues() {
        return properties.values();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * <p>This method produces a signature that identifies
     * this particular request. The returned value is a {@link Set}
     * of {@link String}s, indicating the fields associated with this
     * request.
     *
     * <p>NB. The purpose of the signature is to allow one to identify
     * additional configuration details associated with this client request,
     * which are retrieved by {@link RequestConfigRetrieval}
     *
     * @return the signature associated with this client request
     */
    public Set<String> getSignature() {
        return this.properties.keySet();
    }

    public static class Builder {
        private Map<String, List<String>> properties;
        private Builder() {
            properties = new HashMap<>();
        }

        public Builder addProperty(String name, String... values) {
            Preconditions.checkArgument(name != null && !name.trim().isEmpty(),
                    "Property name cannot be null or empty");
            this.properties.put(name, asList(values));
            return this;
        }

        public ClientRequest build() {
            return new ClientRequest(this);
        }
    }
}
