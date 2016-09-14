package uk.ac.ebi.quickgo.client.model.presets;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents a specific preset datum
 *
 * Created 30/08/16
 * @author Edd
 */
public class PresetItem {
    private static final int EQUAL_RELEVANCY = 0;
    private final String name;
    private final String description;
    private final Integer relevancy;
    private final String id;

    public PresetItem(String id, String name, String description, Integer relevancy) {
        checkArgument(id == null || !id.isEmpty(), "Preset id cannot be empty");
        checkArgument(name != null && !name.isEmpty(), "Preset name cannot be null or empty");
        checkArgument(
                description != null && !description.isEmpty(),
                "Preset description cannot be null or empty");
        checkArgument(relevancy != null, "Integer relevancy cannot be null");

        this.id = id;
        this.name = name;
        this.description = description;
        this.relevancy = relevancy;
    }

    public PresetItem(String name, String description) {
        this(null, name, description, EQUAL_RELEVANCY);
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

    @JsonIgnore
    public Integer getRelevancy() {
        return relevancy;
    }

    @Override public String toString() {
        return "PresetItem{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", relevancy=" + relevancy +
                ", id='" + id + '\'' +
                '}';
    }
}
