package uk.ac.ebi.quickgo.client.model.presets;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A {@link PresetItem} builder used for conveniently creating instances with different fields.
 *
 * Created 15/09/16
 * @author Edd
 */
public class PresetItemBuilder {
    private static final int EQUAL_RELEVANCY = 0;
    private final String name;
    private Integer relevancy;
    private String description;
    private String id;
    private String url;
    private List<String> associations;

    private PresetItemBuilder(String name) {
        checkArgument(name != null && !name.isEmpty(), "Name cannot be null or empty");
        this.name = name;
        this.relevancy = EQUAL_RELEVANCY;
    }

    public static PresetItemBuilder createWithName(String name) {
        return new PresetItemBuilder(name);
    }

    public PresetItemBuilder withDescription(String description) {
        checkArgument(description != null && !description.isEmpty(), "Description cannot be null or empty");
        this.description = description;
        return this;
    }

    public PresetItemBuilder withRelevancy(Integer relevancy) {
        checkArgument(relevancy != null && relevancy >= 0, "Relevancy cannot be null and must be greater than 0");
        this.relevancy = relevancy;
        return this;
    }

    public PresetItemBuilder withAssociations(List<String> associations) {
        checkArgument(associations != null, "Associations cannot be null");
        this.associations = associations;
        return this;
    }

    public PresetItemBuilder withId(String id) {
        checkArgument(id != null && !id.isEmpty(), "Id cannot be null or empty");
        this.id = id;
        return this;
    }

    public PresetItemBuilder withUrl(String url) {
        checkArgument(url != null && !url.isEmpty(), "Url cannot be null or empty");
        this.url = url;
        return this;
    }

    public PresetItem build() {
        return new PresetItemImpl(this);
    }

    private static class PresetItemImpl implements PresetItem {
        private final String name;
        private String description;
        private Integer relevancy;
        private String id;
        private String url;
        private List<String> associations;

        private PresetItemImpl(PresetItemBuilder builder) {
            this.name = builder.name;
            this.description = builder.description;
            this.url = builder.url;
            this.associations = builder.associations;
            this.id = builder.id;
            this.relevancy = builder.relevancy;
        }

        @Override public String getId() {
            return id;
        }

        @Override public String getName() {
            return name;
        }

        @Override public String getDescription() {
            return description;
        }

        @Override public Integer getRelevancy() {
            return relevancy;
        }

        @Override public String getUrl() {
            return url;
        }

        @Override public List<String> getAssociations() {
            return associations;
        }

        @Override public String toString() {
            return "PresetItemImpl{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", relevancy=" + relevancy +
                    ", id='" + id + '\'' +
                    ", url='" + url + '\'' +
                    ", associations=" + associations +
                    '}';
        }
    }
}
