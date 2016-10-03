package uk.ac.ebi.quickgo.client.model.presets.impl;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.mapping;
import static uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl.PresetType.GO_SLIMS_SETS;

/**
 * <p>Represents preset information relating to different aspects of QuickGO.
 *
 * <p>Presets returned are ordered by three criteria:
 * <ol>
 *     <li>natural ordering (low to high) by {@link PresetItem#getRelevancy()}</li>
 *     <li>alphabetically by {@link PresetItem#getName()}}</li>
 *     <li>by insertion order</li>
 * </ol>
 *
 * <p>Presets with the same name will be grouped by {@code name}, and
 * the {@code id}s grouped into {@code associations}.
 *
 * <p>For example, by adding:
 * <ul>
 *     <li>{@link PresetItem} with {@code name} = "n1", {@code id} = "id1"</li>
 *     <li>{@link PresetItem} with {@code name} = "n1", {@code id} = "id2"</li>
 *     <li>{@link PresetItem} with {@code name} = "n2", {@code id} = "id3"</li>
 * </ul>
 *
 * <p>The corresponding grouped presets will be:
 * <ul>
 *     <li>{@link PresetItem} with {@code name} = "n1", {@code associations} = ["id1", "id2"]</li>
 *     <li>{@link PresetItem} with {@code name} = "n3", {@code associations} = ["id3"]</li>
 * </ul>
 *
 *
 * Created 30/08/16
 * @author Edd
 */
public class CompositePresetImpl implements CompositePreset {
    private final Map<PresetType, Set<PresetItem>> presetsMap;

    public CompositePresetImpl() {
        presetsMap = new HashMap<>();

        for (PresetType presetType : PresetType.values()) {
            presetsMap.put(presetType, new LinkedHashSet<>());
        }
    }

    public void addPreset(PresetType presetType, PresetItem presetItem) {
        checkArgument(presetItem != null, "PresetItem cannot be null");

        presetsMap.get(presetType).add(presetItem);
    }

    @Override public List<PresetItem> getAssignedBy() {
        return sortedPresetItems(PresetType.ASSIGNED_BY);
    }

    @Override public List<PresetItem> getReferences() {
        return sortedPresetItems(PresetType.REFERENCES);
    }

    @Override public List<PresetItem> getEvidences() {
        return sortedPresetItems(PresetType.EVIDENCES);
    }

    @Override public List<PresetItem> getWithFrom() {
        return sortedPresetItems(PresetType.WITH_FROM);
    }

    @Override public List<PresetItem> getGeneProducts() {
        return sortedPresetItems(PresetType.GENE_PRODUCT);
    }

    @Override public List<PresetItem> getGoSlimSets() {
        return sortedPresetItems(GO_SLIMS_SETS);
    }

    private List<PresetItem> sortedPresetItems(PresetType presetType) {
        return presetsMap.get(presetType).stream()
                .collect(Collectors.groupingBy(
                        PresetItem::getName,
                        mapping(p -> p, Collectors.toList())))
                .entrySet().stream()
                .map(groupedEntry -> {
                    PresetItemBuilder presetBuilder = PresetItemBuilder
                            .createWithName(groupedEntry.getKey());

                    ifTrueApply(groupedEntry.getValue(),
                            p -> p != null && p.getRelevancy() != 0,
                            p -> presetBuilder.withRelevancy(p.getRelevancy()));

                    ifTrueApply(groupedEntry.getValue(),
                            p -> p.getDescription() != null && !p.getDescription().trim().isEmpty(),
                            p -> presetBuilder.withDescription(p.getDescription()));

                    ifTrueApply(groupedEntry.getValue(),
                            p -> p.getUrl() != null && !p.getUrl().trim().isEmpty(),
                            p -> presetBuilder.withUrl(p.getUrl()));

                    if (presetType == GO_SLIMS_SETS) {
                        presetBuilder.withAssociations(groupedEntry.getValue().stream()
                                .map(PresetItem::getId)
                                .collect(Collectors.toList()));
                    } else {
                        ifTrueApply(groupedEntry.getValue(),
                                p -> p.getId() != null && !p.getId().trim().isEmpty(),
                                p -> presetBuilder.withId(p.getId()));
                    }

                    return presetBuilder.build();
                })
                .sorted((p1, p2) -> {
                    int relevancyComparison = p1.getRelevancy().compareTo(p2.getRelevancy());
                    if (relevancyComparison != 0) {
                        return relevancyComparison;
                    } else {
                        return p1.getName().compareTo(p2.getName());
                    }
                })
                .collect(Collectors.toList());
    }

    private void ifTrueApply(
            List<PresetItem> presets,
            Predicate<PresetItem> presetPredicate,
            Consumer<PresetItem> target) {
        presets.stream()
                .filter(presetPredicate)
                .findFirst()
                .ifPresent(target);
    }

    public enum PresetType {
        ASSIGNED_BY,
        REFERENCES,
        EVIDENCES,
        WITH_FROM,
        GENE_PRODUCT,
        GO_SLIMS_SETS
    }
}