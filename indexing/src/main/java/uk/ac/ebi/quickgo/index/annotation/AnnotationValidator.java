package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import com.google.common.base.Strings;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import static org.slf4j.LoggerFactory.getLogger;
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

    private static final Logger LOGGER = getLogger(AnnotationValidator.class);

    // TODO: goa_uniprot.gpa column def differs from actual values, enables/involved_in ...
    private static final Pattern QUALIFIER_FORMAT = Pattern.compile(
            "^(NOT|involved_in|enables|part_of|contributes_to|colocalizes_with)(\\|" +
                    "(NOT|involved_in|enables|part_of|contributes_to|colocalizes_with))*$");

    // TODO: goa_uniprot.gpa column def differs from actual values, see | or ,
    private static final Pattern WITH_FORMAT = Pattern.compile("^([A-Za-z]+:[a-zA-Z0-9]+)(,([A-Za-z]+:[a-zA-Z0-9]+))*$");
    private static final Pattern DATE_FORMAT = Pattern.compile("^((19)|(20))[0-9]{2}[01][0-9][0-3][0-9]$");;

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
        checkDate(annotation);
        checkIsNullOrEmpty(annotation.assignedBy, COLUMN_ASSIGNED_BY.getName());

        // optional fields
        checkWith(annotation);
    }

    private void checkQualifier(Annotation annotation) {
        checkIsNullOrEmpty(annotation.qualifier, COLUMN_QUALIFIER.getName());
        if (!QUALIFIER_FORMAT.matcher(annotation.qualifier).matches()) {
            String errorMessage = "Qualifier field, '" + annotation.qualifier + "' does not match: " +
                    QUALIFIER_FORMAT.pattern();
            LOGGER.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void checkWith(Annotation annotation) {
        if (!Strings.isNullOrEmpty(annotation.with) && !WITH_FORMAT.matcher(annotation.with).matches()) {
            String errorMessage = "With field, '" + annotation.with + "' does not match: " +
                    WITH_FORMAT.pattern();
            LOGGER.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void checkDate(Annotation annotation) {
        checkIsNullOrEmpty(annotation.date, COLUMN_DATE.getName());
        if (!DATE_FORMAT.matcher(annotation.date).matches()) {
            String errorMessage = "Date field, '" + annotation.date + "' does not match: " +
                    DATE_FORMAT.pattern();
            LOGGER.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }
}
