package uk.ac.ebi.quickgo.client.service.loader.presets.slimsets;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.client.service.loader.presets.slimsets.RawSlimSetNamedPresetColumnsBuilder.RawSlimSetNamedPresetColumnsImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RawSlimSetNamedPresetColumnsBuilderTest {
    private RawSlimSetNamedPresetColumnsBuilder builder = RawSlimSetNamedPresetColumnsBuilder.createWithNamePosition(0);
    @Nested
    class InvalidPositions {
        @Test
        void creatingWithNegativeNamePositionCausesException() {
            assertThrows(IllegalArgumentException.class, () -> RawSlimSetNamedPresetColumnsBuilder.createWithNamePosition(-1));
        }

        @Test
        void creatingWithNegativeTaxidsPositionCausesException() {
            assertThrows(IllegalArgumentException.class, () -> builder.withTaxIdsPosition(-1));
        }

        @Test
        void creatingWithNegativeRolePositionCausesException() {
            assertThrows(IllegalArgumentException.class, () -> builder.withRolePosition(-1));
        }

        @Test
        void creatingWithNegativeIdPositionCausesException() {
            assertThrows(IllegalArgumentException.class, () -> builder.withIdPosition(-1));
        }

        @Test
        void creatingWithNegativeShortLabelPositionCausesException() {
            assertThrows(IllegalArgumentException.class, () -> builder.withShortLabelPosition(-1));
        }
    }

    @Test
    void canCreateWithValidNamePosition() {
        int position = 0;
        RawSlimSetNamedPresetColumnsImpl presetColumns =
          RawSlimSetNamedPresetColumnsBuilder.createWithNamePosition(position).build();
        assertThat(presetColumns.getNamePosition(), is(position));
    }

    @Test
    void canCreateWithValidRolePosition() {
        int position = 1;
        RawSlimSetNamedPresetColumnsImpl presetColumns = builder.withRolePosition(position).build();
        assertThat(presetColumns.getRolePosition(), is(position));
    }

    @Test
    void canCreateWithValidIdPosition() {
        int position = 1;
        RawSlimSetNamedPresetColumnsImpl presetColumns = builder.withIdPosition(position).build();
        assertThat(presetColumns.getIdPosition(), is(position));
    }

    @Test
    void canCreateWithTaxIdsPosition() {
        int position = 1;
        RawSlimSetNamedPresetColumnsImpl presetColumns = builder.withTaxIdsPosition(position).build();
        assertThat(presetColumns.getTaxIdsPosition(), is(position));
    }

    @Test
    void canCreateWithShortLabelPosition() {
        int position = 1;
        RawSlimSetNamedPresetColumnsImpl presetColumns = builder.withShortLabelPosition(position).build();
        assertThat(presetColumns.getShortLabelPosition(), is(position));
    }
}
