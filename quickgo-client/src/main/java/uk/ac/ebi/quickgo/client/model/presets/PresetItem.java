package uk.ac.ebi.quickgo.client.model.presets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A preset DTO. The class exposes a {@link Builder} interface, via {@link PresetItem#createWithName(String)} which
 * is to be used to
 * correctly construct a
 * new
 * instance.
 *
 * Created 04/10/16
 * @author Edd
 */
public class PresetItem {

    private final String name;
    private String description;
    private Integer relevancy;
    private String id;
    private String url;
    private List<String> associations;

    private PresetItem(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.url = builder.url;
        this.associations = builder.associations;
        this.id = builder.id;
        this.relevancy = builder.relevancy;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @JsonIgnore public Integer getRelevancy() {
        return relevancy;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getAssociations() {
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

    @Override public String toString() {
        return "PresetItem2{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", relevancy=" + relevancy +
                ", id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", associations=" + associations +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PresetItem that = (PresetItem) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (relevancy != null ? !relevancy.equals(that.relevancy) : that.relevancy != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        return associations != null ? associations.equals(that.associations) : that.associations == null;

    }

    @Override public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (relevancy != null ? relevancy.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (associations != null ? associations.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private static final int EQUAL_RELEVANCY = 0;
        private final String name;
        private Integer relevancy;
        private String description;
        private String id;
        private String url;
        private List<String> associations;

        private Builder(String name) {
            checkArgument(name != null && !name.isEmpty(), "Name cannot be null or empty");
            this.name = name;
            this.relevancy = EQUAL_RELEVANCY;
        }

        public Builder withDescription(String description) {
            checkArgument(description != null && !description.isEmpty(), "Description cannot be null or empty");
            this.description = description;
            return this;
        }

        public Builder withRelevancy(Integer relevancy) {
            checkArgument(relevancy != null && relevancy >= 0, "Relevancy cannot be null and must be greater than 0");
            this.relevancy = relevancy;
            return this;
        }

        public Builder withAssociations(List<String> associations) {
            checkArgument(associations != null, "Associations cannot be null");
            this.associations = associations;
            return this;
        }

        public Builder withId(String id) {
            checkArgument(id != null && !id.isEmpty(), "Id cannot be null or empty");
            this.id = id;
            return this;
        }

        public Builder withUrl(String url) {
            checkArgument(url != null && !url.isEmpty(), "Url cannot be null or empty");
            this.url = url;
            return this;
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
