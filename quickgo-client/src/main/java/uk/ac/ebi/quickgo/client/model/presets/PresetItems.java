package uk.ac.ebi.quickgo.client.model.presets;

import java.util.Collection;

/**
 * Contract for containers of {@link PresetItem} instances
 *
 * Created 07/09/16
 * @author Edd
 */
interface PresetItems {
    /**
     * Retrieve the {@link Collection} of {@link PresetItem}s.
     * @return the {@link Collection} of {@link PresetItem}s
     */
    Collection<PresetItem> getPresets();

    /**
     * Add a {@link PresetItem} instance to the encapsulated {@link PresetItem} {@link Collection}.
     * @param presetItem the {@link PresetItem} to add
     */
    void addPreset(PresetItem presetItem);
}
