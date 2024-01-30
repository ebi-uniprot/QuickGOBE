package uk.ac.ebi.quickgo.client.service.loader.presets.evidence;

import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.client.service.loader.presets.evidence.RawEvidenceNamedPresetColumnsBuilder
        .RawEvidenceNamedPresetColumnsImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Tony Wardell
 * Date: 09/03/2018
 * Time: 09:56
 * Created with IntelliJ IDEA.
 */
class RawEvidenceNamedPresetColumnsBuilderTest {

    @Test
    void creatingWithNegativeNamePositionCausesException() {
        assertThrows(IllegalArgumentException.class, () -> RawEvidenceNamedPresetColumnsBuilder.createWithNamePosition(-1));
    }

    @Test
    void creatingWithNegativeRelevancyPositionCausesException() {
        assertThrows(IllegalArgumentException.class, () -> RawEvidenceNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withRelevancyPosition(-1));
    }

    @Test
    void creatingWithNegativeGoEvidencePositionCausesException() {
        assertThrows(IllegalArgumentException.class, () -> RawEvidenceNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withGoEvidence(-1));
    }

    @Test
    void creatingWithNegativeIdPositionCausesException() {
        assertThrows(IllegalArgumentException.class, () -> RawEvidenceNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withIdPosition(-1));
    }

    @Test
    void canCreateWithValidNamePosition() {
        int position = 0;
        RawEvidenceNamedPresetColumnsImpl presetColumns =
                RawEvidenceNamedPresetColumnsBuilder.createWithNamePosition(position).build();
        assertThat(presetColumns.getNamePosition(), is(position));
    }

    @Test
    void canCreateWithValidRelevancyPosition() {
        int position = 1;
        RawEvidenceNamedPresetColumnsImpl presetColumns = RawEvidenceNamedPresetColumnsBuilder.createWithNamePosition(0)
                .withRelevancyPosition(position)
                .build();
        assertThat(presetColumns.getRelevancyPosition(), is(position));
    }

    @Test
    void canCreateWithValidIdPosition() {
        int position = 1;
        RawEvidenceNamedPresetColumnsImpl presetColumns = RawEvidenceNamedPresetColumnsBuilder.createWithNamePosition(0)
                .withIdPosition(position)
                .build();
        assertThat(presetColumns.getIdPosition(), is(position));
    }

    @Test
    void canCreateWithGoEvidenceIdPosition() {
        int position = 1;
        RawEvidenceNamedPresetColumnsImpl presetColumns = RawEvidenceNamedPresetColumnsBuilder.createWithNamePosition(0)
                .withGoEvidence(position)
                .build();
        assertThat(presetColumns.getGoEvidencePosition(), is(position));
    }
}
