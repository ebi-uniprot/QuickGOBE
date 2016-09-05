package uk.ac.ebi.quickgo.client.model.presets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents preset information relating to valid and relevant "assigned by" values.
 *
 * Created 30/08/16
 * @author Edd
 */
public class AssignedByPresets {
    private final List<PresetItem> presets;

    AssignedByPresets() {
        presets = new ArrayList<>();
    }

    public Collection<PresetItem> getPresets() {
        return Collections.unmodifiableCollection(
                presets.stream()
                        .sorted((p1, p2) -> p1.getRelevancy().compareTo(p2.getRelevancy()))
                        .collect(Collectors.toList()));
    }

    public void addPreset(PresetItem presetItem) {
        presets.add(presetItem);
    }

    @Override public String toString() {
        return "AssignedByPresets{" +
                "presets=" + presets +
                '}';
    }
}
