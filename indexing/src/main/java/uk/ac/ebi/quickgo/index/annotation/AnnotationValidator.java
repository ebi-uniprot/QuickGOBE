package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import java.util.regex.Pattern;
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

    // TODO: goa_uniprot.gpa column def differs from actual values, enables/involved_in ...
    private static final Pattern QUALIFIER_FORMAT = Pattern.compile(
            "^(NOT|involved_in|enables|part_of|contributes_to|colocalizes_with)(\\|" +
                    "(NOT|involved_in|enables|part_of|contributes_to|colocalizes_with))*$");

    // TODO: goa_uniprot.gpa column def differs from actual values, see | or ,
    private static final Pattern WITH_FORMAT = Pattern.compile("^([A-Z]+:[a-zA-Z0-9]+)(,([A-Z]+:[a-zA-Z0-9]+))*$");

    @Override public void validate(Annotation annotation) throws ValidationException {
        if (annotation == null) {
            throw new DocumentReaderException("Annotation cannot be null");
        }

        // required fields
        checkIsNullOrEmpty(annotation.db, COLUMN_DB.getName());
        checkIsNullOrEmpty(annotation.dbObjectId, COLUMN_DB_OBJECT_ID.getName());
        checkQualifier(annotation);
        checkIsNullOrEmpty(annotation.goId, COLUMN_GO_ID.getName());
        checkIsNullOrEmpty(annotation.dbReferences, COLUMN_DB_REFERENCES.getName());
        checkIsNullOrEmpty(annotation.eco, COLUMN_ECO.getName());
        checkIsNullOrEmpty(annotation.date, COLUMN_DATE.getName());
        checkIsNullOrEmpty(annotation.assignedBy, COLUMN_ASSIGNED_BY.getName());

        // optional fields
        checkWith(annotation);
    }

    private void checkQualifier(Annotation annotation) {
        checkIsNullOrEmpty(annotation.qualifier, COLUMN_QUALIFIER.getName());
        if (!QUALIFIER_FORMAT.matcher(annotation.qualifier).matches()) {
            throw new ValidationException("Qualifier, '" + annotation.qualifier + "' does not match: " +
                    QUALIFIER_FORMAT.pattern());
        }
    }

    private void checkWith(Annotation annotation) {
        if (!WITH_FORMAT.matcher(annotation.with).matches()) {
            throw new ValidationException("Qualifier, '" + annotation.with + "' does not match: " +
                    WITH_FORMAT.pattern());
        }
    }
}
