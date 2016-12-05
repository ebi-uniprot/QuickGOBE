package uk.ac.ebi.quickgo.client.model.presets;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A DTO that allows for the definition of dynamic properties.
 *
 * Created 05/12/16
 * @author Edd
 */
public class PropertiesItem {
    private final String id;
    private final HashMap<String, String> properties;

    private PropertiesItem(Builder builder) {
        this.id = builder.id;
        this.properties = builder.properties;
    }

    public String getId() {
        return id;
    }

    @JsonAnyGetter public Map<String, String> getProperties() {
        return properties;
    }

    public static Builder createWithId(String id) {
        return new Builder(id);
    }

    public static class Builder {
        private final String id;
        private final HashMap<String, String> properties;

        Builder(String id) {
            checkArgument(id != null && !id.isEmpty(), "Id cannot be null or empty");
            this.id = id;
            this.properties = new HashMap<>();
        }

        public Builder withProperty(String key, String value) {
            checkArgument(key != null && !key.isEmpty(), "Key cannot be null or empty");
            properties.put(key, value);
            return this;
        }

        /**
         * Builds the {@link PresetItem}.
         * @return the {@link PresetItem}
         */
        public PropertiesItem build() {
            return new PropertiesItem(this);
        }
    }
}
