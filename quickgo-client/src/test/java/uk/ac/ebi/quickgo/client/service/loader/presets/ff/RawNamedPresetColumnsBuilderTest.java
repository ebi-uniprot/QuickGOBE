package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 13/09/16
 * @author Edd
 */
class RawNamedPresetColumnsBuilderTest {
    @Test
    void creatingWithNegativeNamePositionCausesException() {
        assertThrows(IllegalArgumentException.class, () -> RawNamedPresetColumnsBuilder.createWithNamePosition(-1));
    }

    @Test
    void creatingWithNegativeRelevancyPositionCausesException() {
        assertThrows(IllegalArgumentException.class, () -> RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withRelevancyPosition(-1));
    }

    @Test
    void creatingWithNegativeDescriptionPositionCausesException() {
        assertThrows(IllegalArgumentException.class, () -> RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withDescriptionPosition(-1));
    }

    @Test
    void creatingWithNegativeIdPositionCausesException() {
        assertThrows(IllegalArgumentException.class, () -> RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withIdPosition(-1));
    }

    @Test
    void creatingWithNegativeURLPositionCausesException() {
        assertThrows(IllegalArgumentException.class, () -> RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withURLPosition(-1));
    }

    @Test
    void canCreateWithValidNamePosition() {
        int position = 0;
        RawNamedPresetColumns presetColumns = RawNamedPresetColumnsBuilder.createWithNamePosition(position).build();
        assertThat(presetColumns.getNamePosition(), is(position));
    }

    @Test
    void canCreateWithValidRelevancyPosition() {
        int position = 0;
        RawNamedPresetColumns presetColumns = RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withRelevancyPosition(position)
                .build();
        assertThat(presetColumns.getRelevancyPosition(), is(position));
    }

    @Test
    void canCreateWithValidDescriptionPosition() {
        int position = 0;
        RawNamedPresetColumns presetColumns = RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withDescriptionPosition(position)
                .build();
        assertThat(presetColumns.getDescriptionPosition(), is(position));
    }

    @Test
    void canCreateWithValidIdPosition() {
        int position = 0;
        RawNamedPresetColumns presetColumns = RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withIdPosition(position)
                .build();
        assertThat(presetColumns.getIdPosition(), is(position));
    }

    @Test
    void canCreateWithValidURLPosition() {
        int position = 0;
        RawNamedPresetColumns presetColumns = RawNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withURLPosition(position)
                .build();
        assertThat(presetColumns.getURLPosition(), is(position));
    }
}