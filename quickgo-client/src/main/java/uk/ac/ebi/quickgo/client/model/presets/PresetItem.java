package uk.ac.ebi.quickgo.client.model.presets;

import com.google.common.base.Preconditions;

/**
 * Created 30/08/16
 * @author Edd
 */
public class PresetItem {
    public PresetItem(String name, String description) {
        Preconditions.checkArgument(name != null && !name.isEmpty(), "Preset name cannot be null or empty");
        Preconditions.checkArgument(
                description != null && !description.isEmpty(),
                "Preset description cannot be null or empty");

        this.name = name;
        this.description = description;
    }

    public String name;
    public String description;
}
