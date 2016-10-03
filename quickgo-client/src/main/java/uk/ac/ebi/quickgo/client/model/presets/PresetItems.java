package uk.ac.ebi.quickgo.client.model.presets;

import java.util.List;

/**
 * Contract for containers of {@link PresetItem} instances
 *
 * Created 07/09/16
 * @author Edd
 */
public interface PresetItems {
    /**
     * Retrieve the {@link List} of {@link PresetItem}s.
     * @return the {@link List} of {@link PresetItem}s
     */
    List<PresetItem> getPresets();

}
