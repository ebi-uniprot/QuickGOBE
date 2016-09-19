package uk.ac.ebi.quickgo.client.model.presets;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created 19/09/16
 * @author Edd
 */
class GroupedPresetItems implements PresetItems {
    private final Map<String, List<String>> groupedPresetItems;

    GroupedPresetItems() {
        this.groupedPresetItems = new HashMap<>();
    }

    @Override public Collection<PresetItem> getPresets() {
        return groupedPresetItems.entrySet()
                .stream()
                .map(groupedPresetEntry -> PresetItemBuilder
                        .createWithName(groupedPresetEntry.getKey())
                        .withAssociations(groupedPresetEntry.getValue()))
                .map(PresetItemBuilder::build)
                .collect(Collectors.toList());
    }

    @Override public void addPreset(PresetItem presetItem) {
        checkArgument(presetItem != null, "PresetItem cannot be null");
        checkArgument(presetItem.getName() != null && !presetItem.getName().isEmpty(), "PresetItem's name cannot be " +
                "null or empty");
        checkArgument(presetItem.getId() != null && !presetItem.getId().isEmpty(), "PresetItem's ID cannot be " +
                "null or empty");

        if (!groupedPresetItems.containsKey(presetItem.getName())) {
            groupedPresetItems.put(presetItem.getName(), new ArrayList<>());
        }

        groupedPresetItems.get(presetItem.getName()).add(presetItem.getId());
    }
}
