package uk.ac.ebi.quickgo.client.model.presets.impl;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;

import java.util.Collection;

/**
 * Created 20/09/16
 * @author Edd
 */
public interface ModifiablePresetItems {
    /**
     * Add a {@link PresetItem} instance to the encapsulated {@link PresetItem} {@link Collection}.
     * @param presetItem the {@link PresetItem} to add
     */
    void addPreset(PresetItem presetItem);
}
