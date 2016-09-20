package uk.ac.ebi.quickgo.client.model.presets;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * <p>Represents presets that are grouped by {@code name}. Each group is represented by a
 * single {@link PresetItem} whose {@code name} corresponds to the name added originally,
 * and whose {@code associations} are the {@code id}s of the original {@link PresetItem}s added.
 *
 * <p>For example, by adding:
 * <ul>
 *     <li>{@link PresetItem} with {@code name} = "n1", {@code id} = "id1"</li>
 *     <li>{@link PresetItem} with {@code name} = "n1", {@code id} = "id2"</li>
 *     <li>{@link PresetItem} with {@code name} = "n2", {@code id} = "id3"</li>
 * </ul>
 *
 * <p>The corresponding grouped presets are:
 * <ul>
 *     <li>{@link PresetItem} with {@code name} = "n1", {@code associations} = ["id1", "id2"]</li>
 *     <li>{@link PresetItem} with {@code name} = "n3", {@code associations} = ["id3"]</li>
 * </ul>
 *
 *
 * Created 19/09/16
 * @author Edd
 */
class GroupedPresetItemsImpl implements PresetItems {
    private final Map<String, List<String>> groupedPresetItems;

    GroupedPresetItemsImpl() {
        this.groupedPresetItems = new HashMap<>();
    }

    @Override public Collection<PresetItem> getPresets() {
        return groupedPresetItems.entrySet()
                .stream()
                .map(groupedPresetEntry -> PresetItemBuilder
                        .createWithName(groupedPresetEntry.getKey())
                        .withAssociations(groupedPresetEntry.getValue()))
                .map(PresetItemBuilder::build)
                .sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
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
