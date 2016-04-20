package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import static uk.ac.ebi.quickgo.index.annotation.Columns.*;
import static uk.ac.ebi.quickgo.index.common.validation.ValidationHelper.checkIsNullOrEmpty;

/**
 * Validates the contents of an {@link Annotation} object. The validation
 * step takes place before allowing an {@link Annotation} object to be
 * indexed -- as part of a Spring Batch job.
 *
 * Created 20/04/16
 * @author Edd
 */
public class AnnotationValidator implements Validator<Annotation> {

    @Override public void validate(Annotation annotation) throws ValidationException {
        if (annotation == null) {
            throw new DocumentReaderException("Annotation cannot be null");
        }

        checkIsNullOrEmpty(annotation.db, COLUMN_DB.getName());
        checkIsNullOrEmpty(annotation.dbObjectId, COLUMN_DB_OBJECT_ID.getName());
        checkIsNullOrEmpty(annotation.qualifier, COLUMN_QUALIFIER.getName());
        checkIsNullOrEmpty(annotation.goId, COLUMN_GO_ID.getName());
        checkIsNullOrEmpty(annotation.dbReferences, COLUMN_DB_REFERENCES.getName());
        checkIsNullOrEmpty(annotation.date, COLUMN_DATE.getName());
        checkIsNullOrEmpty(annotation.assignedBy, COLUMN_ASSIGNED_BY.getName());
    }
}
