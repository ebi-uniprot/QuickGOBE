package uk.ac.ebi.quickgo.client.model.presets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents preset information relating to valid and relevant {@link PresetItem} instances.
 *
 * Created 30/08/16
 * @author Edd
 */
public class PresetItemsImpl implements PresetItems {
    private final List<PresetItem> presets;

    PresetItemsImpl() {
        presets = new ArrayList<>();
    }

    @Override
    public Collection<PresetItem> getPresets() {
        return Collections.unmodifiableCollection(
                presets.stream()
                        .sorted((p1, p2) -> p1.getRelevancy().compareTo(p2.getRelevancy()))
                        .collect(Collectors.toList()));
    }

    @Override
    public void addPreset(PresetItem presetItem) {
        presets.add(presetItem);
    }

    @Override public String toString() {
        return "AssignedByPresets{" +
                "presets=" + presets +
                '}';
    }
}
