package uk.ac.ebi.quickgo.client.service.loader.presets.evidence;

import uk.ac.ebi.quickgo.client.service.loader.presets.evidence.RawEvidenceNamedPresetColumnsBuilder
        .RawEvidenceNamedPresetColumnsImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Tony Wardell
 * Date: 09/03/2018
 * Time: 11:19
 * Created with IntelliJ IDEA.
 */
class StringToRawEvidenceNamedPresetMapperTest {

    private StringToRawEvidenceNamedPresetMapper mapper;
    private RawEvidenceNamedPresetColumnsImpl presetColumns;

    @BeforeEach
    void setUp() {
        this.presetColumns = RawEvidenceNamedPresetColumnsBuilder.createWithNamePosition(1).withIdPosition(0)
                .withGoEvidence(2)
                .build();
        this.mapper = new StringToRawEvidenceNamedPresetMapper(presetColumns);
    }

    @Test
    void nullFieldSetThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> mapper.mapFieldSet(null));
    }

    @Test
    void fieldSetWithInsufficientValuesThrowsException() {
        String[] tokens = new String[numColumns() - 1];
        FieldSet fieldSet = new DefaultFieldSet(tokens);
        assertThrows(IncorrectTokenCountException.class, () -> mapper.mapFieldSet(fieldSet));
    }

    @Test
    void convertFieldSetWithNullValues() {
        String[] tokens = new String[numColumns()];
        tokens[presetColumns.getGoEvidencePosition()] = null;
        tokens[presetColumns.getNamePosition()] = null;
        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawNamedPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[presetColumns.getNamePosition()]));
        assertThat(preset.description, is(tokens[presetColumns.getGoEvidencePosition()]));
    }

    @Test
    void convertFieldSetWithValidValues() {
        String[] tokens = new String[numColumns()];
        tokens[presetColumns.getIdPosition()] = "ECO:0000247";
        tokens[presetColumns.getNamePosition()] = "computational combinatorial evidence used in manual assertion";
        tokens[presetColumns.getGoEvidencePosition()] = "RCA";
        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawEvidenceNamedPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.id, is(tokens[presetColumns.getIdPosition()]));
        assertThat(preset.name, is(tokens[presetColumns.getNamePosition()]));
        assertThat(preset.goEvidence, is(tokens[presetColumns.getGoEvidencePosition()]));
    }

    @Test
    void trimFieldsFromFieldSetWhenConverting() {
        String[] tokens = new String[numColumns()];
        tokens[presetColumns.getNamePosition()] = "  computational combinatorial evidence used in manual assertion";
        tokens[presetColumns.getGoEvidencePosition()] = "   RCA   ";
        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawEvidenceNamedPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[presetColumns.getNamePosition()].trim()));
        assertThat(preset.goEvidence, is(tokens[presetColumns.getGoEvidencePosition()].trim()));
    }

    private int numColumns() {
        return presetColumns.getMaxRequiredColumnCount();
    }
}
