package uk.ac.ebi.quickgo.client.model.presets.impl;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

/**
 * Created 21/09/16
 * @author Edd
 */
public class PresetItemsBuilderTest {
    private static final String NAME = "name";
    private static final String ID = "id";

    private PresetItemsBuilder presetBuilder;

    @Before
    public void setUp() {
        this.presetBuilder = new PresetItemsBuilder();
    }

    @Test
    public void canAdd1PresetAndRetrieveCorrectly() {
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(1)).build());
        List<PresetItem> presets = presetBuilder.build().getPresets();
        assertThat(presets, hasSize(1));

        PresetItem presetItem = presets.stream().findFirst().orElse(null);
        assertThat(presetItem.getName(), is(name(1)));
    }

    @Test
    public void canAdd2PresetsAndRetrieveCorrectly() {
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(1)).build());
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(2)).build());
        List<PresetItem> presets = presetBuilder.build().getPresets();
        assertThat(presets, hasSize(2));

        List<String> presetsNames = presets.stream().map(PresetItem::getName).collect(Collectors.toList());
        assertThat(presetsNames, contains(name(1), name(2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddNullPreset() {
        presetBuilder.addPreset(null);
    }

    @Test
    public void presetsAreReturnedInRelevancyOrder() {
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(3)).withRelevancy(3).build());
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(1)).withRelevancy(1).build());
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(2)).withRelevancy(2).build());
        presetBuilder.addPreset(PresetItemBuilder.createWithName(name(4)).withRelevancy(4).build());

        List<PresetItem> presets = presetBuilder.build().getPresets();
        assertThat(presets, hasSize(4));

        List<String> presetsNames = presets.stream().map(PresetItem::getName).collect(Collectors.toList());
        assertThat(presetsNames, contains(name(1), name(2), name(3), name(4)));
    }

    private static String name(int id) {
        return NAME + id;
    }

    private static String id(int id) {
        return ID + id;
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