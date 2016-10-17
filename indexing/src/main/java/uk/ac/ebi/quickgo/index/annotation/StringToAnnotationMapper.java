package uk.ac.ebi.quickgo.index.annotation;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.validation.BindException;

import static uk.ac.ebi.quickgo.index.annotation.Columns.*;

/**
 * Converts a String representing an annotation into an {@link Annotation} object.
 *
 * Created 19/04/16
 * @author Edd
 */
class StringToAnnotationMapper implements FieldSetMapper<Annotation> {
    @Override public Annotation mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null) {
            throw new IllegalArgumentException("Provided field set is null");
        }

        if (fieldSet.getFieldCount() < numColumns()) {
            throw new IncorrectTokenCountException("Incorrect number of columns, expected: " + numColumns() + "; " +
                    "found: " + fieldSet.getFieldCount(), numColumns(), fieldSet.getFieldCount());
        }

        Annotation annotation = new Annotation();

        annotation.db = trimIfNotNull(fieldSet.readString(COLUMN_DB.getPosition()));
        annotation.dbObjectId = trimIfNotNull(fieldSet.readString(COLUMN_DB_OBJECT_ID.getPosition()));
        annotation.qualifier = trimIfNotNull(fieldSet.readString(COLUMN_QUALIFIER.getPosition()));
        annotation.goId = trimIfNotNull(fieldSet.readString(COLUMN_GO_ID.getPosition()));
        annotation.dbReferences = trimIfNotNull(fieldSet.readString(COLUMN_DB_REFERENCES.getPosition()));
        annotation.evidenceCode = trimIfNotNull(fieldSet.readString(COLUMN_EVIDENCE_CODE.getPosition()));
        annotation.with = trimIfNotNull(fieldSet.readString(COLUMN_WITH.getPosition()));
        annotation.interactingTaxonId = trimIfNotNull(fieldSet.readString(COLUMN_INTERACTING_TAXON_ID.getPosition()));
        annotation.assignedBy = trimIfNotNull(fieldSet.readString(COLUMN_ASSIGNED_BY.getPosition()));
        annotation.annotationExtension = trimIfNotNull(fieldSet.readString(COLUMN_ANNOTATION_EXTENSION.getPosition()));
        annotation.annotationProperties = trimIfNotNull(fieldSet.readString(COLUMN_ANNOTATION_PROPERTIES.getPosition()));

        return annotation;
    }

    private String trimIfNotNull(String value) {
        return value == null ? null : value.trim();
    }
}
