package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.client.presets.read.assignedby.DBColumns.COLUMN_DATABASE;
import static uk.ac.ebi.quickgo.client.presets.read.assignedby.DBColumns.COLUMN_NAME;
import static uk.ac.ebi.quickgo.client.presets.read.assignedby.DBColumns.numColumns;

/**
 * Created 05/09/16
 * @author Edd
 */
public class StringToAssignedByMapperTest {
    private StringToAssignedByMapper mapper;

    @Before
    public void setUp() {
        this.mapper = new StringToAssignedByMapper();
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
        tokens[COLUMN_DATABASE.getPosition()] = null;
        tokens[COLUMN_NAME.getPosition()] = null;

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawAssignedByPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[COLUMN_DATABASE.getPosition()]));
        assertThat(preset.description, is(tokens[COLUMN_NAME.getPosition()]));
    }

    @Test
    public void convertFieldSetWithValidValues() throws Exception {
        String[] tokens = new String[numColumns()];
        tokens[COLUMN_DATABASE.getPosition()] = "UniProt";
        tokens[COLUMN_NAME.getPosition()] = "The Universal Protein Resource";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawAssignedByPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[COLUMN_DATABASE.getPosition()]));
        assertThat(preset.description, is(tokens[COLUMN_NAME.getPosition()]));
    }

    @Test
    public void trimFieldsFromFieldSetWhenConverting() throws Exception {
        String[] tokens = new String[numColumns()];
        tokens[COLUMN_DATABASE.getPosition()] = "  UniProt";
        tokens[COLUMN_NAME.getPosition()] = "   The Universal Protein Resource   ";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawAssignedByPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[COLUMN_DATABASE.getPosition()].trim()));
        assertThat(preset.description, is(tokens[COLUMN_NAME.getPosition()].trim()));
    }

}