package uk.ac.ebi.quickgo.client.presets.read.ff;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created 05/09/16
 * @author Edd
 */
public class StringToRawNamedPresetMapperTest {
    private StringToRawNamedPresetMapper mapper;
    private RawNamedPresetColumns presetColumns;

    @Before
    public void setUp() {
        this.presetColumns = RawNamedPresetColumnsBuilder.createWithNamePosition(0)
                .withDescriptionPosition(1)
                .build();
        this.mapper = new StringToRawNamedPresetMapper(presetColumns);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFieldSetThrowsException() throws Exception {
        mapper.mapFieldSet(null);
    }

    @Test(expected = IncorrectTokenCountException.class)
    public void fieldSetWithInsufficientValuesThrowsException() throws Exception {
        String[] tokens = new String[numColumns() - 1];
        FieldSet fieldSet = new DefaultFieldSet(tokens);

        mapper.mapFieldSet(fieldSet);
    }

    @Test
    public void convertFieldSetWithNullValues() throws Exception {
        String[] tokens = new String[numColumns()];
        tokens[presetColumns.getDescriptionPosition()] = null;
        tokens[presetColumns.getNamePosition()] = null;

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawNamedPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[presetColumns.getNamePosition()]));
        assertThat(preset.description, is(tokens[presetColumns.getDescriptionPosition()]));
    }

    @Test
    public void convertFieldSetWithValidValues() throws Exception {
        String[] tokens = new String[numColumns()];
        tokens[presetColumns.getNamePosition()] = "UniProt";
        tokens[presetColumns.getDescriptionPosition()] = "The Universal Protein Resource";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawNamedPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[presetColumns.getNamePosition()]));
        assertThat(preset.description, is(tokens[presetColumns.getDescriptionPosition()]));
    }

    @Test
    public void trimFieldsFromFieldSetWhenConverting() throws Exception {
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