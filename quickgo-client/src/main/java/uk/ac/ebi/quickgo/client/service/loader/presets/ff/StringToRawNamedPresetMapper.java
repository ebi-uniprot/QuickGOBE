package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.validation.BindException;

import static com.google.common.base.Preconditions.checkArgument;
import static uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPresetColumnsBuilder.UNINITIALIZED_POSITION;

/**
 * Class responsible for mapping a {@link FieldSet} that contains an entity with both
 * name and description, to a corresponding instance of {@link RawNamedPreset} encapsulating this information.
 *
 * Created 31/08/16
 * @author Edd
 */
public class StringToRawNamedPresetMapper implements FieldSetMapper<RawNamedPreset> {
    private final RawNamedPresetColumns rawNamedPresetColumns;

    public StringToRawNamedPresetMapper(RawNamedPresetColumns rawNamedPresetColumns) {
        checkArgument(rawNamedPresetColumns != null, "RawPresetColumns cannot be null");

        this.rawNamedPresetColumns = rawNamedPresetColumns;
    }

    @Override public RawNamedPreset mapFieldSet(FieldSet fieldSet) {
        if (fieldSet == null) {
            throw new IllegalArgumentException("Provided field set is null");
        }

        if (fieldSet.getFieldCount() < rawNamedPresetColumns.getMaxRequiredColumnCount()) {
            throw new IncorrectTokenCountException(
                    "Incorrect number of columns, expected: " + rawNamedPresetColumns.getMaxRequiredColumnCount() +
                            "; " +
                            "found: " + fieldSet.getFieldCount(), rawNamedPresetColumns.getMaxRequiredColumnCount(),
                    fieldSet.getFieldCount());
        }

        RawNamedPreset rawPreset = new RawNamedPreset();
        rawPreset.name = trimIfNotNull(extractStringValue(fieldSet, rawNamedPresetColumns.getNamePosition()));
        rawPreset.description =
                trimIfNotNull(extractStringValue(fieldSet, rawNamedPresetColumns.getDescriptionPosition()));
        rawPreset.relevancy = extractRelevancy(fieldSet);
        rawPreset.id = trimIfNotNull(extractStringValue(fieldSet, rawNamedPresetColumns.getIdPosition()));
        rawPreset.url = trimIfNotNull(extractStringValue(fieldSet, rawNamedPresetColumns.getURLPosition()));
        rawPreset.association =
                trimIfNotNull(extractStringValue(fieldSet, rawNamedPresetColumns.getAssociationPosition()));
        rawPreset.goEvidence =
                trimIfNotNull(extractStringValue(fieldSet, rawNamedPresetColumns.getGoEvidencePosition()));
        return rawPreset;
    }

    private int extractRelevancy(FieldSet fieldSet) {
        if (rawNamedPresetColumns.getRelevancyPosition() > UNINITIALIZED_POSITION) {
            return fieldSet.readInt(rawNamedPresetColumns.getRelevancyPosition());
        } else {
            return 0;
        }
    }

    private String extractStringValue(FieldSet fieldSet, int position) {
        if (position > UNINITIALIZED_POSITION) {
            return fieldSet.readString(position);
        } else {
            return null;
        }
    }

    private String trimIfNotNull(String value) {
        return value == null ? null : value.trim();
    }

}
