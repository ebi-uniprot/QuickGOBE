package uk.ac.ebi.quickgo.client.model.presets.impl;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PresetItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents preset information relating to valid and relevant {@link PresetItem} instances.
 *
 * Created 30/08/16
 * @author Edd
 */
public class PresetItemsBuilder implements ModifiablePresetItems {
    private final List<PresetItem> presets;

    public PresetItemsBuilder() {
        presets = new ArrayList<>();
    }

    @Override
    public void addPreset(PresetItem presetItem) {
        presets.add(presetItem);
    }

    @Override public String toString() {
        return "PresetItemsImpl{" +
                "presets=" + presets +
                '}';
    }

    public PresetItems build() {
        return new PresetItemsImpl(this);
    }

    private static class PresetItemsImpl implements PresetItems {
        private final List<PresetItem> presets;

        private PresetItemsImpl(PresetItemsBuilder builder) {
            presets = Collections.unmodifiableList(
                    builder.presets.stream()
                            .sorted((p1, p2) -> p1.getRelevancy().compareTo(p2.getRelevancy()))
                            .collect(Collectors.toList()));
        }

        @Override public List<PresetItem> getPresets() {
            return presets;
        }
    }

}
