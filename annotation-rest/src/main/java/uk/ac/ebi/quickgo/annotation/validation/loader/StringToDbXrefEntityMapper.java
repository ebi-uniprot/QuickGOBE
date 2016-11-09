package uk.ac.ebi.quickgo.annotation.validation.loader;

import uk.ac.ebi.quickgo.annotation.validation.model.DBXRefEntity;

import java.util.regex.Pattern;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.validation.BindException;

import static uk.ac.ebi.quickgo.annotation.validation.loader.DBXrefEntityColumns.*;
import static uk.ac.ebi.quickgo.annotation.validation.loader.DBXrefEntityColumns.numColumns;

/**
 * Class responsible for mapping a {@link FieldSet} that contains data related to Database Cross references
 * to a corresponding instance of {@link DBXRefEntity} encapsulating this information.
 *
 * @author Tony Wardell
 * Date: 07/11/2016
 * Time: 18:12
 * Created with IntelliJ IDEA.
 */
public class StringToDbXrefEntityMapper implements FieldSetMapper<DBXRefEntity> {

    @Override public DBXRefEntity mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null) {
            throw new IllegalArgumentException("Provided field set is null");
        }

        if (fieldSet.getFieldCount() < numColumns()) {
            throw new IncorrectTokenCountException("Incorrect number of columns, expected: " + numColumns() + "; " +
                    "found: " + fieldSet.getFieldCount(), numColumns(), fieldSet.getFieldCount());
        }

        DBXRefEntity DBXRefEntity = new DBXRefEntity();
        DBXRefEntity.database = trimIfNotNull(fieldSet.readString(COLUMN_DB.getPosition()));
        DBXRefEntity.entityType = trimIfNotNull(fieldSet.readString(COLUMN_ENTITY_TYPE_ID.getPosition()));
        DBXRefEntity.databaseURL = trimIfNotNull(fieldSet.readString(COLUMN_URL_SYNTAX.getPosition()));
        DBXRefEntity.entityTypeName = trimIfNotNull(fieldSet.readString(COLUMN_ENTITY_TYPE_NAME.getPosition()));
        DBXRefEntity.idValidationPattern = Pattern.compile(replaceEscapeIfExists(trimIfNotNull(fieldSet.readString
                (COLUMN_LOCAL_ID_SYNTAX.getPosition()))));

        return DBXRefEntity;

    }

    private String trimIfNotNull(String value) {
        return value == null ? null : value.trim();
    }

    private String replaceEscapeIfExists(String value){
        return value == null ? null : value.replace("\\\\", "\\");
    }
}
