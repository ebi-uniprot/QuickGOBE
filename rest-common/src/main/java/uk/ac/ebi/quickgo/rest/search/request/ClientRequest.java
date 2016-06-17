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

    public Optional<List<String>> getValue(String name) {
        if (properties.containsKey(name)) {
            return Optional.of(properties.get(name));
        } else {
            return Optional.empty();
        }
    }

    public Collection<List<String>> getValues() {
        return properties.values();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * <p>This method produces a unique signature that is
     * associated with this particular type of client request.
     *
     * <p>NB. The purpose of this signature is to allow one to identify
     * possible additional configuration details that are associated
     * with this client request, which are retrieved by {@link RequestConfigRetrieval}
     * @return the unique signature associated with this type of client request
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
