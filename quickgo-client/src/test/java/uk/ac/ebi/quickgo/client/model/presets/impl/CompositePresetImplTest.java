package uk.ac.ebi.quickgo.client.model.presets.impl;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PropertiesItem;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import static uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl.PresetType.ASSIGNED_BY;
import static uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl.PresetType.GO_SLIMS_SETS;

/**
 * Created 03/10/16
 * @author Edd
 */
@RunWith(HierarchicalContextRunner.class)
public class CompositePresetImplTest {
    private static final String NAME = "name";
    private static final String ID = "id";

    public class GroupingPresetIdsByName {

        private CompositePresetImpl presetBuilder;

        @Before
        public void setUp() {
            presetBuilder = new CompositePresetImpl();
        }

        @Test
        public void canAdd1AndRetrievePreset() {
            presetBuilder.addPreset(GO_SLIMS_SETS, createWithName(name(1)).withId(id(1)).build());
            Collection<PresetItem> presets = presetBuilder.getGoSlimSets();

            assertThat(presets, hasSize(1));
            PresetItem presetItem = presets.stream().findFirst().orElse(null);
            assertThat(presetItem.getName(), is(name(1)));
            assertThat(presetItem.getAssociations(), contains(propertiesItemWithId(1)));
        }

        @Test
        public void canAddGroupOf2PresetsAndRetrieveCorrectly() {
            presetBuilder.addPreset(GO_SLIMS_SETS, createWithName(name(1)).withId(id(1)).build());
            presetBuilder.addPreset(GO_SLIMS_SETS, createWithName(name(1)).withId(id(2)).build());

            Map<String, PresetItem> resultsMap = buildResultsMap(presetBuilder.getGoSlimSets());

            assertThat(resultsMap.keySet(), contains(name(1)));
            assertThat(resultsMap.values().stream()
                    .map(PresetItem::getAssociations)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()), contains(propertiesItemWithId(1), propertiesItemWithId(2)));
        }

        private Map<String, PresetItem> buildResultsMap(Collection<PresetItem> presetItems) {
            Map<String, PresetItem> resultsMap = new HashMap<>();
            presetItems.forEach(p -> {
                if (resultsMap.containsKey(p.getName())) {
                    throw new IllegalStateException("Presets should not contain multiple items with the same name.");
                }
                resultsMap.put(p.getName(), p);
            });

            return resultsMap;
        }

        @Test
        public void canAddGroupOf2PresetsAndGroupOf1PresetsAndRetrieveCorrectly() {
            presetBuilder.addPreset(GO_SLIMS_SETS, createWithName(name(1)).withId(id(1)).build());
            presetBuilder.addPreset(GO_SLIMS_SETS, createWithName(name(1)).withId(id(2)).build());
            presetBuilder.addPreset(GO_SLIMS_SETS, createWithName(name(2)).withId(id(3)).build());

            Map<String, PresetItem> resultsMap = buildResultsMap(presetBuilder.getGoSlimSets());

            assertThat(resultsMap.keySet(), containsInAnyOrder(name(1), name(2)));
            assertThat(resultsMap.get(name(1)).getAssociations(), containsInAnyOrder(
                    propertiesItemWithId(1),
                    propertiesItemWithId(2)));
            assertThat(resultsMap.get(name(2)).getAssociations(), contains(propertiesItemWithId(3)));
        }

        private PropertiesItem propertiesItemWithId(int idValue) {
            return PropertiesItem.createWithId(id(idValue)).build();
        }

        @Test
        public void presetsAreReturnedInAlphabeticalOrder() {
            presetBuilder.addPreset(GO_SLIMS_SETS, createWithName("B").withId(anyId()).build());
            presetBuilder.addPreset(GO_SLIMS_SETS, createWithName("A").withId(anyId()).build());
            presetBuilder.addPreset(GO_SLIMS_SETS, createWithName("C").withId(anyId()).build());
            presetBuilder.addPreset(GO_SLIMS_SETS, createWithName("C").withId(anyId()).build());
            presetBuilder.addPreset(GO_SLIMS_SETS, createWithName("A").withId(anyId()).build());

            Collection<PresetItem> presets = presetBuilder.getGoSlimSets();

            assertThat(presets.stream().map(PresetItem::getName).collect(Collectors.toList()),
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
            presetBuilder.addPreset(ASSIGNED_BY, createWithName(name
                    (1)).build());
            List<PresetItem> presets = presetBuilder.getAssignedBy();
            assertThat(presets, hasSize(1));

            PresetItem presetItem = presets.stream().findFirst().orElse(null);
            assertThat(presetItem.getName(), is(name(1)));
        }

        @Test
        public void canAdd2PresetsAndRetrieveCorrectly() {
            presetBuilder.addPreset(ASSIGNED_BY, createWithName(name(1)).build());
            presetBuilder.addPreset(ASSIGNED_BY, createWithName(name(2)).build());
            List<PresetItem> presets = presetBuilder.getAssignedBy();
            assertThat(presets, hasSize(2));

            List<String> presetsNames = presets.stream().map(PresetItem::getName).collect(Collectors.toList());
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

            List<String> presetsNames = presets.stream().map(PresetItem::getName).collect(Collectors.toList());
            assertThat(presetsNames, Matchers.contains(name(1), name(2), name(3), name(4)));
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
}