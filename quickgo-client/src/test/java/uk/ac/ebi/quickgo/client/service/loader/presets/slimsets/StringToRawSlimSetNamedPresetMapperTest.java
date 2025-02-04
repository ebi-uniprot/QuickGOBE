package uk.ac.ebi.quickgo.client.service.loader.presets.slimsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.SourceColumnsFactory.createSlimSetColumns;

class StringToRawSlimSetNamedPresetMapperTest {

    private StringToRawSlimSetNamedPresetMapper mapper;
    private RawSlimSetNamedPresetColumnsBuilder.RawSlimSetNamedPresetColumnsImpl presetColumns;

    @BeforeEach
    void setUp() {
        this.presetColumns = createSlimSetColumns();
        this.mapper = new StringToRawSlimSetNamedPresetMapper(presetColumns);
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
        tokens[presetColumns.getNamePosition()] = null;
        tokens[presetColumns.getRolePosition()] = null;
        tokens[presetColumns.getTaxIdsPosition()] = null;
        tokens[presetColumns.getShortLabelPosition()] = null;
        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawSlimSetNamedPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[presetColumns.getNamePosition()]));
        assertThat(preset.role, is(tokens[presetColumns.getRolePosition()]));
        assertThat(preset.taxIds, is(tokens[presetColumns.getTaxIdsPosition()]));
        assertThat(preset.shortLabel, is(tokens[presetColumns.getShortLabelPosition()]));
    }

    @Test
    void convertFieldSetWithValidValues() {
        String[] tokens = new String[numColumns()];
        tokens[presetColumns.getNamePosition()] = "goslim_agr";
        tokens[presetColumns.getIdPosition()] = "GO:0005576";
        tokens[presetColumns.getDescriptionPosition()] = "C";
        tokens[presetColumns.getAssociationPosition()] = "extracellular region";
        tokens[presetColumns.getRolePosition()] = "Ribbon";
        tokens[presetColumns.getTaxIdsPosition()] = "1,33154";
        tokens[presetColumns.getShortLabelPosition()] = "Animals and fungi (Opisthokonta)";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawSlimSetNamedPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[presetColumns.getNamePosition()]));
        assertThat(preset.id, is(tokens[presetColumns.getIdPosition()]));
        assertThat(preset.description, is(tokens[presetColumns.getDescriptionPosition()]));
        assertThat(preset.association, is(tokens[presetColumns.getAssociationPosition()]));
        assertThat(preset.role, is(tokens[presetColumns.getRolePosition()]));
        assertThat(preset.taxIds, is(tokens[presetColumns.getTaxIdsPosition()]));
        assertThat(preset.shortLabel, is(tokens[presetColumns.getShortLabelPosition()]));
    }

    @Test
    void trimFieldsFromFieldSetWhenConverting() {
        String[] tokens = new String[numColumns()];
        tokens[presetColumns.getNamePosition()] = "      goslim_agr       ";
        tokens[presetColumns.getIdPosition()] = "        GO:0005576       ";
        tokens[presetColumns.getDescriptionPosition()] = "     C       ";
        tokens[presetColumns.getAssociationPosition()] = "      extracellular region      ";
        tokens[presetColumns.getRolePosition()] = "     Ribbon       ";
        tokens[presetColumns.getTaxIdsPosition()] = "     1, 33154          ";
        tokens[presetColumns.getShortLabelPosition()] = "      Animals and fungi (Opisthokonta)     ";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        RawSlimSetNamedPreset preset = mapper.mapFieldSet(fieldSet);

        assertThat(preset.name, is(tokens[presetColumns.getNamePosition()].trim()));
        assertThat(preset.id, is(tokens[presetColumns.getIdPosition()].trim()));
        assertThat(preset.description, is(tokens[presetColumns.getDescriptionPosition()].trim()));
        assertThat(preset.association, is(tokens[presetColumns.getAssociationPosition()].trim()));
        assertThat(preset.role, is(tokens[presetColumns.getRolePosition()].trim()));
        assertThat(preset.taxIds, is(tokens[presetColumns.getTaxIdsPosition()].trim()));
        assertThat(preset.shortLabel, is(tokens[presetColumns.getShortLabelPosition()].trim()));
    }

    private int numColumns() {
        return presetColumns.getMaxRequiredColumnCount();
    }
}
