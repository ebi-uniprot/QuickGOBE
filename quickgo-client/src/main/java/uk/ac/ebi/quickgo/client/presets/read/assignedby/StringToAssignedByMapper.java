package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.validation.BindException;

import static uk.ac.ebi.quickgo.client.presets.read.assignedby.DBColumns.COLUMN_DATABASE;
import static uk.ac.ebi.quickgo.client.presets.read.assignedby.DBColumns.COLUMN_NAME;
import static uk.ac.ebi.quickgo.client.presets.read.assignedby.DBColumns.numColumns;

/**
 * Created 31/08/16
 * @author Edd
 */
class StringToAssignedByMapper implements FieldSetMapper<RawAssignedByPreset> {
    @Override public RawAssignedByPreset mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null) {
            throw new IllegalArgumentException("Provided field set is null");
        }

        if (fieldSet.getFieldCount() < numColumns()) {
            throw new IncorrectTokenCountException("Incorrect number of columns, expected: " + numColumns() + "; " +
                    "found: " + fieldSet.getFieldCount(), numColumns(), fieldSet.getFieldCount());
        }

        RawAssignedByPreset rawPreset = new RawAssignedByPreset();
        rawPreset.name = trimIfNotNull(fieldSet.readString(COLUMN_DATABASE.getPosition()));
        rawPreset.description = trimIfNotNull(fieldSet.readString(COLUMN_NAME.getPosition()));

        return rawPreset;
    }

    private String trimIfNotNull(String value) {
        return value == null ? null : value.trim();
    }
}
