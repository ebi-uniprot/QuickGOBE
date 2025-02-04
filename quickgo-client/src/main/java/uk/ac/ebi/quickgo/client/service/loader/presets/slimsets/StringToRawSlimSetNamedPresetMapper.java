package uk.ac.ebi.quickgo.client.service.loader.presets.slimsets;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.StringToRawNamedPresetMapper;
import uk.ac.ebi.quickgo.client.service.loader.presets.slimsets.RawSlimSetNamedPresetColumnsBuilder.RawSlimSetNamedPresetColumnsImpl;

import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.StringToRawNamedPresetMapper.extractStringValue;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.StringToRawNamedPresetMapper.trimIfNotNull;

/**
 * A specialisation of the {@link StringToRawNamedPresetMapper} for Slim sets, so the GO slims sets field can be
 * correctly named.
 */
public class StringToRawSlimSetNamedPresetMapper implements FieldSetMapper<RawSlimSetNamedPreset> {
    private final RawSlimSetNamedPresetColumnsImpl rawNamedPresetColumns;
    private StringToRawNamedPresetMapper mapper;

    StringToRawSlimSetNamedPresetMapper(RawSlimSetNamedPresetColumnsImpl rawSlimSetNamedPresetColumns) {
        this.rawNamedPresetColumns = rawSlimSetNamedPresetColumns;
        mapper = StringToRawNamedPresetMapper
                .createWithSupplier(rawSlimSetNamedPresetColumns, RawSlimSetNamedPreset::new);
    }

    @Override
    public RawSlimSetNamedPreset mapFieldSet(FieldSet fieldSet) {
        RawSlimSetNamedPreset rawPreset = (RawSlimSetNamedPreset) mapper.mapFieldSet(fieldSet);
        rawPreset.role = trimIfNotNull(extractStringValue(fieldSet, rawNamedPresetColumns.getRolePosition()));
        rawPreset.taxIds = trimIfNotNull(extractStringValue(fieldSet, rawNamedPresetColumns.getTaxIdsPosition()));
        rawPreset.shortLabel = trimIfNotNull(extractStringValue(fieldSet, rawNamedPresetColumns.getShortLabelPosition()));
        return rawPreset;
    }
}
