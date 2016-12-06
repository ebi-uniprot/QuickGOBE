package uk.ac.ebi.quickgo.client.model.presets.impl;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PropertiesItem;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.mapping;
import static uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl.PresetType.*;

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
    private final EnumMap<PresetType, Set<PresetItem>> presetsMap;

    public CompositePresetImpl() {
        presetsMap = new EnumMap<>(PresetType.class);

        for (PresetType presetType : PresetType.values()) {
            presetsMap.put(presetType, new LinkedHashSet<>());
        }

        initialiseStaticPresets();
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

    @Override public List<PresetItem> getTaxons() {
        return sortedPresetItems(TAXONS);
    }

    @Override public List<PresetItem> getQualifiers() {
        return sortedPresetItems(QUALIFIERS);
    }

    @Override public List<PresetItem> getAspects() {
        return sortedPresetItems(ASPECTS);
    }

    @Override public List<PresetItem> getGeneProductTypes() {
        return sortedPresetItems(GENE_PRODUCT_TYPES);
    }

    private void initialiseStaticPresets() {
        presetsMap.put(ASPECTS, StaticAspects.createAspects());
        presetsMap.put(GENE_PRODUCT_TYPES, StaticGeneProductTypes.createGeneProductTypes());
    }

    /**
     * Sorts the presets according to the ordering rules defined in the class description.
     * @param presetType the {@link PresetType} whose list of {@link PresetItem}s are to be to returned.
     * @return the list of {@link PresetItem}s corresponding to the specified {@code presetType}.
     */
    private List<PresetItem> sortedPresetItems(PresetType presetType) {
        return presetsMap.get(presetType).stream()
                .collect(Collectors.groupingBy(
                        PresetItem::getName,
                        mapping(Function.identity(), Collectors.toList())))
                .entrySet().stream()
                .map(groupedEntry -> transformGroupedEntryToPresetItem(presetType, groupedEntry))
                .sorted()
                .collect(Collectors.toList());
    }

    private PresetItem transformGroupedEntryToPresetItem(PresetType presetType,
            Map.Entry<String, List<PresetItem>> groupedEntry) {
        PresetItem.Builder presetBuilder = PresetItem.createWithName(groupedEntry.getKey());

        if (presetType == GO_SLIMS_SETS) {
            presetBuilder.withAssociations(groupedEntry.getValue().stream()
                    .map(this::presetItemToPropertiesItem)
                    .collect(Collectors.toList())
            );
        } else {
            ifPresetItemMatchesThenApply(groupedEntry.getValue(),
                    p -> p.getId() != null && !p.getId().trim().isEmpty(),
                    p -> presetBuilder.withId(p.getId()));

            ifPresetItemMatchesThenApply(groupedEntry.getValue(),
                    p -> p != null && p.getRelevancy() != 0,
                    p -> presetBuilder.withRelevancy(p.getRelevancy()));

            ifPresetItemMatchesThenApply(groupedEntry.getValue(),
                    p -> p.getDescription() != null && !p.getDescription().trim().isEmpty(),
                    p -> presetBuilder.withDescription(p.getDescription()));

            ifPresetItemMatchesThenApply(groupedEntry.getValue(),
                    p -> p.getUrl() != null && !p.getUrl().trim().isEmpty(),
                    p -> presetBuilder.withUrl(p.getUrl()));
        }

        return presetBuilder.build();
    }

    private PropertiesItem presetItemToPropertiesItem(PresetItem presetItem) {
        PropertiesItem.Builder propertiesItem = PropertiesItem
                .createWithId(presetItem.getId());
        if (presetItem.getAssociations() != null) {
            presetItem.getAssociations().stream()
                    .findFirst()
                    .ifPresent(item -> propertiesItem.withProperty(SlimAdditionalProperty.NAME.name, item.getId()));
        }

        StaticAspects.Aspect.findByAbbrev(presetItem.getDescription())
                .ifPresent(aspect -> propertiesItem.withProperty(SlimAdditionalProperty.ASPECT.name, aspect.name));

        return propertiesItem.build();
    }

    /**
     * Given a list of {@link PresetItem}s, if a specified
     * {@link Predicate} is true for a given element of the list, apply some action,
     * defined as a {@link Consumer}.
     * @param presets the list of {@link PresetItem}s
     * @param presetPredicate the {@link Predicate} which must be true for {@code itemConsumer} to be applied
     * @param itemConsumer the {@link Consumer} action to apply to an item
     */
    private void ifPresetItemMatchesThenApply(
            List<PresetItem> presets,
            Predicate<PresetItem> presetPredicate,
            Consumer<PresetItem> itemConsumer) {

        presets.stream()
                .filter(presetPredicate)
                .findFirst()
                .ifPresent(itemConsumer);
    }

    public enum PresetType {
        ASSIGNED_BY,
        REFERENCES,
        EVIDENCES,
        WITH_FROM,
        GENE_PRODUCT,
        GENE_PRODUCT_TYPES,
        GO_SLIMS_SETS,
        TAXONS,
        QUALIFIERS,
        ASPECTS
    }

    private enum SlimAdditionalProperty {
        NAME("name"), ASPECT("aspect");

        private final String name;

        SlimAdditionalProperty(String name) {
            this.name = name;
        }
    }

    private static class StaticAspects {

        private enum Aspect {
            FUNCTION("Molecular Function", "function", "molecular_function", "F"),
            PROCESS("Biological Process", "process", "biological_process", "P"),
            COMPONENT("Cellular Component", "component", "cellular_component", "C");

            private final String name;
            private final String shortName;
            private final String scientificName;
            private final String abbrev;

            Aspect(String name, String shortName, String scientificName, String abbrev) {
                this.name = name;
                this.shortName = shortName;
                this.scientificName = scientificName;
                this.abbrev = abbrev;
            }

            private static Optional<Aspect> findByAbbrev(String abbrev) {
                for (Aspect aspect : Aspect.values()) {
                    if (aspect.abbrev.equals(abbrev)) {
                        return Optional.of(aspect);
                    }
                }
                return Optional.empty();
            }
        }

        static Set<PresetItem> createAspects() {
            Set<PresetItem> presetAspects = new HashSet<>();
            Arrays.stream(Aspect.values())
                    .forEach(aspect -> insertAspect(presetAspects, aspect));
            return presetAspects;
        }

        private static void insertAspect(Set<PresetItem> presets, Aspect aspect) {
            presets.add(PresetItem
                    .createWithName(aspect.name)
                    .withId(aspect.scientificName).build());
        }
    }

    private static class StaticGeneProductTypes {

        private enum GeneProductType {
            PROTEINS("Proteins", "protein"),
            RNA("RNA", "miRNA"),
            COMPLEXES("Complexes", "complex");

            private final String name;
            private final String shortName;

            GeneProductType(String name, String shortName) {
                this.name = name;
                this.shortName = shortName;
            }
        }

        static Set<PresetItem> createGeneProductTypes() {
            Set<PresetItem> presetAspects = new HashSet<>();
            Arrays.stream(GeneProductType.values())
                    .forEach(aspect -> insertGeneProductType(presetAspects, aspect));
            return presetAspects;
        }

        private static void insertGeneProductType(Set<PresetItem> presets, GeneProductType geneProductType) {
            presets.add(PresetItem
                    .createWithName(geneProductType.name)
                    .withId(geneProductType.shortName).build());
        }
    }
}
