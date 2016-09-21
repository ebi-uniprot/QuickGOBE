package uk.ac.ebi.quickgo.client.model.presets.impl;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;

/**
 * Created 19/09/16
 * @author Edd
 */
public class GroupedPresetItemsBuilderTest {
    private static final String NAME = "name";
    private static final String ID = "id";
    private GroupedPresetItemsBuilder presetBuilder;

    @Before
    public void setUp() {
        presetBuilder = new GroupedPresetItemsBuilder();
    }

    @Test
    public void canAdd1AndRetrievePreset() {
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(1)).withId(id(1)).build());
        Collection<PresetItem> presets = presetBuilder.build().getPresets();

        assertThat(presets, hasSize(1));
        PresetItem presetItem = presets.stream().findFirst().orElse(null);
        assertThat(presetItem.getName(), is(name(1)));
        assertThat(presetItem.getAssociations(), contains(id(1)));
    }

    @Test
    public void canAddGroupOf2PresetsAndRetrieveCorrectly() {
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(1)).withId(id(1)).build());
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(1)).withId(id(2)).build());
        System.out.println(presetBuilder.build().getPresets());

        Map<String, PresetItem> resultsMap = buildResultsMap(presetBuilder.build().getPresets());

        assertThat(resultsMap.keySet(), contains(name(1)));
        assertThat(resultsMap.values().stream()
                .map(PresetItem::getAssociations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()), contains(id(1), id(2)));
    }

    private static Map<String, PresetItem> buildResultsMap(Collection<PresetItem> presetItems) {
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
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(1)).withId(id(1)).build());
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(1)).withId(id(2)).build());
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(2)).withId(id(3)).build());
        System.out.println(presetBuilder.build().getPresets());

        Map<String, PresetItem> resultsMap = buildResultsMap(presetBuilder.build().getPresets());

        assertThat(resultsMap.keySet(), containsInAnyOrder(name(1), name(2)));
        assertThat(resultsMap.get(name(1)).getAssociations(), containsInAnyOrder(id(1), id(2)));
        assertThat(resultsMap.get(name(2)).getAssociations(), contains(id(3)));
    }

    @Test
    public void presetsAreReturnedInAlphabeticalOrder() {
        presetBuilder.addPreset(PresetItemBuilder.createWithName("B").withId(anyId()).build());
        presetBuilder.addPreset(PresetItemBuilder.createWithName("A").withId(anyId()).build());
        presetBuilder.addPreset(PresetItemBuilder.createWithName("C").withId(anyId()).build());
        presetBuilder.addPreset(PresetItemBuilder.createWithName("C").withId(anyId()).build());
        presetBuilder.addPreset(PresetItemBuilder.createWithName("A").withId(anyId()).build());

        Collection<PresetItem> presets = presetBuilder.build().getPresets();

        assertThat(presets.stream().map(PresetItem::getName).collect(Collectors.toList()),
                contains("A", "B", "C"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddPresetWithNullName() {
        presetBuilder.addPreset(new FakePresetItem() {
            @Override public String getName() {
                return null;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddPresetWithEmptyName() {
        presetBuilder.addPreset(new FakePresetItem() {
            @Override public String getName() {
                return "";
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddPresetWithNullId() {
        presetBuilder.addPreset(new FakePresetItem() {
            @Override public String getId() {
                return null;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddPresetWithEmptyId() {
        presetBuilder.addPreset(new FakePresetItem() {
            @Override public String getId() {
                return "";
            }
        });
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

    private static class FakePresetItem implements PresetItem {
        @Override public String getId() {
            return null;
        }

        @Override public String getName() {
            return null;
        }

        @Override public String getDescription() {
            return null;
        }

        @Override public Integer getRelevancy() {
            return null;
        }

        @Override public String getUrl() {
            return null;
        }

        @Override public List<String> getAssociations() {
            return null;
        }
    }
}