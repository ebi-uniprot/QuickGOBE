package uk.ac.ebi.quickgo.client.presets.read.ff;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created 13/09/16
 * @author Edd
 */
public class RawNamedPresetColumnsBuilderTest {
    @Test(expected = IllegalArgumentException.class)
    public void creatingWithNegativeNamePositionCausesException() {
        RawNamedPresetColumnsBuilder.createWithNamePosition(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingWithNegativeRelevancyPositionCausesException() {
        RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withRelevancyPosition(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingWithNegativeDescriptionPositionCausesException() {
        RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withDescriptionPosition(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingWithNegativeIdPositionCausesException() {
        RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withIdPosition(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingWithNegativeURLPositionCausesException() {
        RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withURLPosition(-1);
    }

    @Test
    public void canCreateWithValidNamePosition() {
        int position = 0;
        RawNamedPresetColumns presetColumns = RawNamedPresetColumnsBuilder.createWithNamePosition(position).build();
        assertThat(presetColumns.getNamePosition(), is(position));
    }

    @Test
    public void canCreateWithValidRelevancyPosition() {
        int position = 0;
        RawNamedPresetColumns presetColumns = RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withRelevancyPosition(position)
                .build();
        assertThat(presetColumns.getRelevancyPosition(), is(position));
    }

    @Test
    public void canCreateWithValidDescriptionPosition() {
        int position = 0;
        RawNamedPresetColumns presetColumns = RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withDescriptionPosition(position)
                .build();
        assertThat(presetColumns.getDescriptionPosition(), is(position));
    }

    @Test
    public void canCreateWithValidIdPosition() {
        int position = 0;
        RawNamedPresetColumns presetColumns = RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withIdPosition(position)
                .build();
        assertThat(presetColumns.getIdPosition(), is(position));
    }

    @Test
    public void canCreateWithValidURLPosition() {
        int position = 0;
        RawNamedPresetColumns presetColumns = RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withURLPosition(position)
                .build();
        assertThat(presetColumns.getURLPosition(), is(position));
    }
}