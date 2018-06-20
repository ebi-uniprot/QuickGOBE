package uk.ac.ebi.quickgo.client.model.presets.impl;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.*;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.client.model.presets.PresetItem.createWithName;
import static uk.ac.ebi.quickgo.client.model.presets.PresetType.ASSIGNED_BY;
import static uk.ac.ebi.quickgo.client.model.presets.PresetType.GO_SLIMS_SETS;
import static uk.ac.ebi.quickgo.client.model.presets.PresetType.QUALIFIERS;

/**
 * Created 03/10/16
 * @author Edd
 */
@RunWith(HierarchicalContextRunner.class)
public class CompositePresetImplTest {
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String ASSOC = "assoc";

    public class GroupingPresetIdsByName {

        private CompositePresetImpl presetBuilder;

        @Before
        public void setUp() {
            presetBuilder = new CompositePresetImpl();
        }

        @Test
        public void canAdd1AndRetrievePreset() {
            presetBuilder.addPreset(
                    GO_SLIMS_SETS,
                    createWithName(name(1))
                            .withProperty(ID, id(1))
                            .build());
            Collection<PresetItem> presets = presetBuilder.getGoSlimSets();

            assertThat(presets, hasSize(1));
            PresetItem presetItem = presets.stream().findFirst().orElse(null);
            assertThat(presetItem.getProperty(NAME), is(name(1)));
            assertThat(presetItem.getAssociations(), contains(presetItemWithId(1)));
        }

        @Test
        public void canAddGroupOf2PresetsAndRetrieveCorrectly() {
            presetBuilder.addPreset(
                    GO_SLIMS_SETS,
                    presetItemWith(name(1), id(1), assoc(10)));
            presetBuilder.addPreset(
                    GO_SLIMS_SETS,
                    presetItemWith(name(1), id(2), assoc(11)));

            Map<String, PresetItem> resultsMap = buildResultsMap(presetBuilder.getGoSlimSets());

            assertThat(resultsMap.keySet(), contains(name(1)));
            assertThat(resultsMap.values().stream()
                            .map(PresetItem::getAssociations)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList()),
                    contains(
                            presetItemWith(assoc(10), id(1)),
                            presetItemWith(assoc(11), id(2))));
        }

        private Map<String, PresetItem> buildResultsMap(Collection<PresetItem> presetItems) {
            Map<String, PresetItem> resultsMap = new HashMap<>();
            presetItems.forEach(p -> {
                if (resultsMap.containsKey(p.getProperty(NAME))) {
                    throw new IllegalStateException("Presets should not contain multiple items with the same name.");
                }
                resultsMap.put(p.getProperty(NAME), p);
            });

            return resultsMap;
        }

        @Test
        public void canAddGroupOf2PresetsAndGroupOf1PresetsAndRetrieveCorrectly() {
            presetBuilder.addPreset(
                    GO_SLIMS_SETS,
                    presetItemWith(name(1), id(1), assoc(10)));
            presetBuilder.addPreset(
                    GO_SLIMS_SETS,
                    presetItemWith(name(1), id(2), assoc(11)));
            presetBuilder.addPreset(
                    GO_SLIMS_SETS,
                    presetItemWith(name(2), id(3), assoc(20)));

            Map<String, PresetItem> resultsMap = buildResultsMap(presetBuilder.getGoSlimSets());

            assertThat(resultsMap.keySet(), containsInAnyOrder(name(1), name(2)));
            assertThat(resultsMap.get(name(1)).getAssociations(), containsInAnyOrder(
                    presetItemWith(assoc(10), id(1)),
                    presetItemWith(assoc(11), id(2))));
            assertThat(resultsMap.get(name(2)).getAssociations(), contains(
                    presetItemWith(assoc(20), id(3))));
        }

        private PresetItem presetItemWithId(int idValue) {
            return PresetItem
                    .createWithName(name(idValue))
                    .withProperty(ID, id(idValue))
                    .build();
        }

        private PresetItem presetItemWith(String name) {
            return PresetItem.createWithName(name).build();
        }

        private PresetItem presetItemWith(String name, String id) {
            return PresetItem.createWithName(name).withProperty(ID, id).build();
        }

        private PresetItem presetItemWith(String name, String id, String... assoc) {
            return PresetItem.createWithName(name)
                    .withProperty(ID, id)
                    .withAssociations(Arrays.stream(assoc)
                            .map(this::presetItemWith)
                            .collect(Collectors.toList()))
                    .build();
        }

        @Test
        public void presetsAreReturnedInAlphabeticalOrder() {
            presetBuilder.addPreset(GO_SLIMS_SETS,
                    createWithName("B").withProperty(ID, anyId()).build());
            presetBuilder.addPreset(GO_SLIMS_SETS,
                    createWithName("A").withProperty(ID, anyId()).build());
            presetBuilder.addPreset(GO_SLIMS_SETS,
                    createWithName("C").withProperty(ID, anyId()).build());
            presetBuilder.addPreset(GO_SLIMS_SETS,
                    createWithName("C").withProperty(ID, anyId()).build());
            presetBuilder.addPreset(GO_SLIMS_SETS,
                    createWithName("A").withProperty(ID, anyId()).build());

            Collection<PresetItem> presets = presetBuilder.getGoSlimSets();

            assertThat(presets.stream().map(p -> p.getProperty(NAME)).collect(Collectors.toList()),
                    contains("A", "B", "C"));
        }

        @Test(expected = IllegalArgumentException.class)
        public void cannotAddNullPreset() {
            presetBuilder.addPreset(GO_SLIMS_SETS, null);
        }

    }

    public class NonGroupedPresets {
        private CompositePresetImpl presetBuilder;

        @Before
        public void setUp() {
            this.presetBuilder = new CompositePresetImpl();
        }

        @Test
        public void canAdd1PresetAndRetrieveCorrectly() {
            presetBuilder.addPreset(ASSIGNED_BY, createWithName(name(1)).build());
            List<PresetItem> presets = presetBuilder.getAssignedBy();
            assertThat(presets, hasSize(1));

            PresetItem presetItem = presets.stream().findFirst().orElse(null);
            assertThat(presetItem.getProperty(NAME), is(name(1)));
        }

        @Test
        public void canAdd2PresetsAndRetrieveCorrectly() {
            presetBuilder.addPreset(ASSIGNED_BY, createWithName(name(1)).build());
            presetBuilder.addPreset(ASSIGNED_BY, createWithName(name(2)).build());
            List<PresetItem> presets = presetBuilder.getAssignedBy();
            assertThat(presets, hasSize(2));

            List<String> presetsNames =
                    presets.stream().map(p -> p.getProperty(NAME)).collect(Collectors.toList());
            assertThat(presetsNames, Matchers.contains(name(1), name(2)));
        }

        @Test
        public void presetsAreReturnedInRelevancyOrder() {
            presetBuilder.addPreset(ASSIGNED_BY, createWithName(name(3)).withRelevancy(3).build());
            presetBuilder.addPreset(ASSIGNED_BY, createWithName(name(1)).withRelevancy(1).build());
            presetBuilder.addPreset(ASSIGNED_BY, createWithName(name(2)).withRelevancy(2).build());
            presetBuilder.addPreset(ASSIGNED_BY, createWithName(name(4)).withRelevancy(4).build());

            List<PresetItem> presets = presetBuilder.getAssignedBy();
            assertThat(presets, hasSize(4));

            List<String> presetsNames =
                    presets.stream().map(p -> p.getProperty(NAME)).collect(Collectors.toList());
            assertThat(presetsNames, Matchers.contains(name(1), name(2), name(3), name(4)));
        }

        @Test
        public void qualifiersAreReturnedInAlphabeticalOrder() {
            presetBuilder.addPreset(QUALIFIERS, createWithName("BZZZ").withProperty(ID, anyId()).build());
            presetBuilder.addPreset(QUALIFIERS, createWithName("C").withProperty(ID, anyId()).build());
            presetBuilder.addPreset(QUALIFIERS, createWithName("A3423").withProperty(ID, anyId()).build());
            presetBuilder.addPreset(QUALIFIERS, createWithName("C0").withProperty(ID, anyId()).build());
            presetBuilder.addPreset(QUALIFIERS, createWithName("A").withProperty(ID, anyId()).build());

            Collection<PresetItem> presets = presetBuilder.getQualifiers();

            assertThat(presets.stream().map(p -> p.getProperty(NAME)).collect(Collectors.toList()),
                    contains("A", "A3423", "BZZZ", "C", "C0"));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddNullPreset() {
        CompositePresetImpl presetBuilder = new CompositePresetImpl();
        presetBuilder.addPreset(ASSIGNED_BY, null);
    }

    private static String name(int id) {
        return NAME + id;
    }

    private static String id(int id) {
        return ID + id;
    }

    private static String anyId() {
        return ID + Math.random();
    }



    private String assoc(int id) {
        return ASSOC + id;
    }
}