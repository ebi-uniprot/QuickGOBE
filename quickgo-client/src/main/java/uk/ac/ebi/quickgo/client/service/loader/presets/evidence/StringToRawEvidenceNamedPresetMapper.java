package uk.ac.ebi.quickgo.client.service.loader.presets.evidence;

import uk.ac.ebi.quickgo.client.service.loader.presets.evidence.RawEvidenceNamedPresetColumnsBuilder
        .RawEvidenceNamedPresetColumnsImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.StringToRawNamedPresetMapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.StringToRawNamedPresetMapper.extractStringValue;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.StringToRawNamedPresetMapper.trimIfNotNull;

/**
 * A specialisation of the {@link StringToRawNamedPresetMapper} for evidences, so the GO Evidence field can be
 * correctly named.
 * @author Tony Wardell
 * Date: 05/03/2018
 * Time: 10:13
 * Created with IntelliJ IDEA.
 */
public class StringToRawEvidenceNamedPresetMapper implements FieldSetMapper<RawEvidenceNamedPreset> {
    public final RawEvidenceNamedPresetColumnsImpl rawNamedPresetColumns;
    StringToRawNamedPresetMapper mapper;

    public StringToRawEvidenceNamedPresetMapper(RawEvidenceNamedPresetColumnsImpl rawEvidenceNamedPresetColumns) {
        this.rawNamedPresetColumns = rawEvidenceNamedPresetColumns;
        mapper = new StringToRawNamedPresetMapper(rawEvidenceNamedPresetColumns, RawEvidenceNamedPreset::new);
    }

    @Override
    public RawEvidenceNamedPreset mapFieldSet(FieldSet fieldSet) {
        RawEvidenceNamedPreset rawPreset = (RawEvidenceNamedPreset) mapper.mapFieldSet(fieldSet);
        rawPreset.goEvidence = trimIfNotNull(extractStringValue(fieldSet, rawNamedPresetColumns
                .getGoEvidencePosition()));
        return rawPreset;
    }
}
