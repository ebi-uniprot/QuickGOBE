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
    private final String name;
    private final String description;
    private final Integer relevancy;

    public PresetItem(String name, String description, Integer relevancy) {
        checkArgument(name != null && !name.isEmpty(), "Preset name cannot be null or empty");
        checkArgument(
                description != null && !description.isEmpty(),
                "Preset description cannot be null or empty");
        checkArgument(relevancy != null, "Integer relevancy cannot be null");

        this.name = name;
        this.description = description;
        this.relevancy = relevancy;
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
                '}';
    }
}
