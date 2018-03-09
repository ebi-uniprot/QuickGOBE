package uk.ac.ebi.quickgo.client.model.presets;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A preset DTO. The class exposes a {@link Builder} interface, via {@link PresetItem#createWithName(String)} which
 * is to be used to correctly construct a new instance.
 *
 * Created 04/10/16
 * @author Edd
 */
public class PresetItem implements Comparable<PresetItem> {
    private final Map<String, String> properties;
    private Integer relevancy;
    private List<PresetItem> associations;

    protected PresetItem(Builder builder) {
        this.properties = ImmutableMap.copyOf(builder.properties);
        this.relevancy = builder.relevancy;
        this.associations = builder.associations;
    }

    @Override public int hashCode() {
        int result = relevancy != null ? relevancy.hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        result = 31 * result + (associations != null ? associations.hashCode() : 0);
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PresetItem that = (PresetItem) o;

        if (relevancy != null ? !relevancy.equals(that.relevancy) : that.relevancy != null) {
            return false;
        }
        if (properties != null ? !properties.equals(that.properties) : that.properties != null) {
            return false;
        }
        return associations != null ? associations.equals(that.associations) : that.associations == null;

    }

    @Override public String toString() {
        return "PresetItem{" +
                "relevancy=" + relevancy +
                ", properties=" + properties +
                ", associations=" + associations +
                '}';
    }

    @JsonAnyGetter public Map<String, String> getProperties() {
        return properties;
    }

    @JsonIgnore public String getProperty(String key) {
        if (properties != null && properties.containsKey(key)) {
            return properties.get(key);
        } else {
            return null;
        }
    }

    @JsonIgnore public String getProperty(Property property) {
        return getProperty(property.getKey());
    }

    @JsonIgnore public Integer getRelevancy() {
        return relevancy;
    }

    public List<PresetItem> getAssociations() {
        return associations;
    }

    /**
     * Creates a builder of {@link PresetItem} with the specified {@code name}.
     * @param name the name
     * @return an instance of {@link Builder}
     */
    public static Builder createWithName(String name) {
        return new Builder(name);
    }

    /**
     * <p>Presets are ordered by the following criteria:
     * <ol>
     *     <li>natural ordering (low to high) by {@link PresetItem#getRelevancy()}</li>
     *     <li>alphabetically by the {@link Property#NAME} property</li>
     * </ol>
     *
     * @param presetItem the preset item to compare
     * @return when comparing {@code presetItem} against this object, return:
     * <ul>
     *     <li>-1, if it is less than this object</li>
     *     <li>0, if they are the same</li>
     *     <li>1, if it is greater than this object</li>
     * </ul>;
     */
    @Override public int compareTo(PresetItem presetItem) {
        int relevancyComparison = this.getRelevancy().compareTo(presetItem.getRelevancy());
        if (relevancyComparison != 0) {
            return relevancyComparison;
        } else {
            return this.getProperties().get(Property.NAME.getKey())
                    .compareTo(presetItem.getProperties().get(Property.NAME.getKey()));
        }
    }

    /**
     * Standard properties applicable to {@link PresetItem} instances.
     */
    public enum Property {
        DESCRIPTION("description"),
        NAME("name"),
        ID("id"),
        URL("url");

        private String key;

        Property(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public static class Builder {
        private static final int EQUAL_RELEVANCY = 0;
        private Integer relevancy;

        private Map<String, String> properties;
        private List<PresetItem> associations;

        private Builder(String name) {
            checkArgument(name != null && !name.isEmpty(), "Name cannot be null or empty");

            this.properties = new LinkedHashMap<>();
            this.properties.put(Property.NAME.getKey(), name);
            this.properties.put(Property.ID.getKey(), name);
            this.relevancy = EQUAL_RELEVANCY;
        }

        public Builder withRelevancy(Integer relevancy) {
            checkArgument(relevancy != null && relevancy >= 0, "Relevancy cannot be null and must be greater than 0");
            this.relevancy = relevancy;
            return this;
        }

        public Builder withAssociations(List<PresetItem> associations) {
            checkArgument(associations != null, "Associations cannot be null");
            this.associations = associations;
            return this;
        }

        public Builder withProperty(String key, String value) {
            checkArgument(key != null && !key.isEmpty(), "Key cannot be null or empty");
            checkArgument(value != null && !value.isEmpty(), "Value cannot be null or empty");
            properties.put(key, value);
            return this;
        }

        public Builder withProperty(Property property, String value) {
            return withProperty(property.getKey(), value);
        }

        /**
         * Builds the {@link PresetItem}.
         * @return the {@link PresetItem}
         */
        public PresetItem build() {
            return new PresetItem(this);
        }
    }
}
