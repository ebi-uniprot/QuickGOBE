package uk.ac.ebi.quickgo.rest.search.request;

import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfigRetrieval;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a client's filter request.
 *
 * Created 02/06/16
 * @author Edd
 */
public class FilterRequest {
    private final Map<String, List<String>> properties;

    private FilterRequest(Builder builder) {
        this.properties = Collections.unmodifiableMap(builder.properties);
    }

    /**
     * Get all of the properties associated with this filter request.
     * @return the properties of this filter request.
     */
    public Map<String, List<String>> getProperties() {
        return properties;
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
     * which are retrieved by {@link FilterConfigRetrieval}
     *
     * @return the signature associated with this client request
     */
    public Set<String> getSignature() {
        return this.properties.keySet();
    }

    @Override public int hashCode() {
        return properties != null ? properties.hashCode() : 0;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FilterRequest that = (FilterRequest) o;

        return properties != null ? properties.equals(that.properties) : that.properties == null;

    }

    @Override public String toString() {
        return "FilterRequest{" +
                "properties=" + properties +
                '}';
    }

    public static class Builder {
        private Map<String, List<String>> properties;

        private Builder() {
            properties = new HashMap<>();
        }

        public Builder addProperty(String name, String... values) {
            Preconditions.checkArgument(name != null && !name.trim().isEmpty(),
                    "Property name cannot be null or empty");

            List<String> valuesList = new ArrayList<>();

            if (values != null) {
                Stream.of(values).filter(Objects::nonNull).forEach(valuesList::add);
            }

            this.properties.put(name, valuesList);
            return this;
        }

        public FilterRequest build() {
            return new FilterRequest(this);
        }
    }
}
