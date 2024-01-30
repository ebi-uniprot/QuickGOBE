package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 05/09/16
 * @author Edd
 */
class StringToRawNamedPresetMapperTest {
    private StringToRawNamedPresetMapper mapper;
    private RawNamedPresetColumns presetColumns;

    @BeforeEach
    void setUp() {
        this.presetColumns = RawNamedPresetColumnsBuilder.createWithNamePosition(0)
                .withDescriptionPosition(1)
                .build();
        this.mapper = StringToRawNamedPresetMapper.create(presetColumns);
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
        tokens[presetColumns.getDescriptionPosition()] = null;
        tokens[presetColumns.getNamePosition()] = null;

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawNamedPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[presetColumns.getNamePosition()]));
        assertThat(preset.description, is(tokens[presetColumns.getDescriptionPosition()]));
    }

    @Test
    void convertFieldSetWithValidValues() {
        String[] tokens = new String[numColumns()];
        tokens[presetColumns.getNamePosition()] = "UniProt";
        tokens[presetColumns.getDescriptionPosition()] = "The Universal Protein Resource";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawNamedPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[presetColumns.getNamePosition()]));
        assertThat(preset.description, is(tokens[presetColumns.getDescriptionPosition()]));
    }

    @Test
    void trimFieldsFromFieldSetWhenConverting() {
        String[] tokens = new String[numColumns()];
        tokens[presetColumns.getNamePosition()] = "  UniProt";
        tokens[presetColumns.getDescriptionPosition()] = "   The Universal Protein Resource   ";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawNamedPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[presetColumns.getNamePosition()].trim()));
        assertThat(preset.description, is(tokens[presetColumns.getDescriptionPosition()].trim()));
    }

    private int numColumns() {
        return presetColumns.getMaxRequiredColumnCount();
    }

}
