package uk.ac.ebi.quickgo.client.model.presets;

import org.junit.Before;
import org.junit.Test;

/**
 * Created 19/09/16
 * @author Edd
 */
public class GroupedPresetItemsTest {
    private GroupedPresetItems groupedPresetItems;

    @Before
    public void setUp() {
        groupedPresetItems = new GroupedPresetItems();
    }

    @Test
    public void canAdd1AndRetrievePreset() {
        groupedPresetItems.addPreset(PresetItemBuilder.createWithName("name").withId("id").build());
        System.out.println(groupedPresetItems.getPresets());
    }

    @Test
    public void canAddGroupOf2PresetsAndRetrieveCorrectly() {
        groupedPresetItems.addPreset(PresetItemBuilder.createWithName("name").withId("id1").build());
        groupedPresetItems.addPreset(PresetItemBuilder.createWithName("name").withId("id2").build());
        System.out.println(groupedPresetItems.getPresets());
    }

    @Test
    public void canAddGroupOf2PresetsAndGroupOf1PresetsAndRetrieveCorrectly() {
        groupedPresetItems.addPreset(PresetItemBuilder.createWithName("name1").withId("id1").build());
        groupedPresetItems.addPreset(PresetItemBuilder.createWithName("name1").withId("id2").build());
        groupedPresetItems.addPreset(PresetItemBuilder.createWithName("name2").withId("id3").build());
        System.out.println(groupedPresetItems.getPresets());
    }

    @Test
    public void cannotAddPresetWithNullName() {

    }

    @Test
    public void cannotAddPresetWithEmptyName() {

    }

    @Test
    public void cannotAddPresetWithNullId() {

    }

    @Test
    public void cannotAddPresetWithEmptyId() {

    }
}