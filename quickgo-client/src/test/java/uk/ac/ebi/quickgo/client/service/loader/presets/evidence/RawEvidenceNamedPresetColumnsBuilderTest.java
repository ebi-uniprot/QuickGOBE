package uk.ac.ebi.quickgo.client.service.loader.presets.evidence;

import uk.ac.ebi.quickgo.client.service.loader.presets.evidence.RawEvidenceNamedPresetColumnsBuilder
        .RawEvidenceNamedPresetColumnsImpl;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Tony Wardell
 * Date: 09/03/2018
 * Time: 09:56
 * Created with IntelliJ IDEA.
 */
public class RawEvidenceNamedPresetColumnsBuilderTest {

    @Test
            (expected = IllegalArgumentException.class)
    public void creatingWithNegativeNamePositionCausesException() {
        RawEvidenceNamedPresetColumnsBuilder.createWithNamePosition(-1);
    }

    @Test
            (expected = IllegalArgumentException.class)
    public void creatingWithNegativeRelevancyPositionCausesException() {
        RawEvidenceNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withRelevancyPosition(-1);
    }

    @Test
            (expected = IllegalArgumentException.class)
    public void creatingWithNegativeGoEvidencePositionCausesException() {
        RawEvidenceNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withGoEvidence(-1);
    }

    @Test
            (expected = IllegalArgumentException.class)
    public void creatingWithNegativeIdPositionCausesException() {
        RawEvidenceNamedPresetColumnsBuilder
                .createWithNamePosition(0)
                .withIdPosition(-1);
    }

    @Test
    public void canCreateWithValidNamePosition() {
        int position = 0;
        RawEvidenceNamedPresetColumnsImpl presetColumns =
                RawEvidenceNamedPresetColumnsBuilder.createWithNamePosition(position).build();
        assertThat(presetColumns.getNamePosition(), is(position));
    }

    @Test
    public void canCreateWithValidRelevancyPosition() {
        int position = 1;
        RawEvidenceNamedPresetColumnsImpl presetColumns = RawEvidenceNamedPresetColumnsBuilder.createWithNamePosition(0)
                .withRelevancyPosition(position)
                .build();
        assertThat(presetColumns.getRelevancyPosition(), is(position));
    }

    @Test
    public void canCreateWithValidIdPosition() {
        int position = 1;
        RawEvidenceNamedPresetColumnsImpl presetColumns = RawEvidenceNamedPresetColumnsBuilder.createWithNamePosition(0)
                .withIdPosition(position)
                .build();
        assertThat(presetColumns.getIdPosition(), is(position));
    }

    @Test
    public void canCreateWithGoEvidenceIdPosition() {
        int position = 1;
        RawEvidenceNamedPresetColumnsImpl presetColumns = RawEvidenceNamedPresetColumnsBuilder.createWithNamePosition(0)
                .withGoEvidence(position)
                .build();
        assertThat(presetColumns.getGoEvidencePosition(), is(position));
    }
}
