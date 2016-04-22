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

    // E.g., a,b,c|d,e
    private static final String PIPE_SEPARATED_COMMA_DELIMITED_VALUES = "(%s(,%s)*)(\\|(%s(,%s)*))*";
    private static final String DB_COLON_REF = "[A-Za-z]+:[a-zA-Z0-9]+";
    private static final String KEY_EQUALS_VALUE = ".*=.*";
    private static final String ANYTHING = "[a-zA-Z0-9_]+\\([a-zA-Z0-9_:-]+\\)";

    private static final Pattern WITH_FORMAT =
            Pattern.compile(String.format(PIPE_SEPARATED_COMMA_DELIMITED_VALUES,
                    DB_COLON_REF, DB_COLON_REF, DB_COLON_REF, DB_COLON_REF));
    private static final Pattern QUALIFIER_FORMAT = Pattern.compile(
            "^(NOT\\|)?(involved_in|enables|part_of|contributes_to|colocalizes_with)$");
    private static final Pattern ANNOTATION_EXTENSION_FORMAT =
            Pattern.compile(String.format(PIPE_SEPARATED_COMMA_DELIMITED_VALUES,
                    ANYTHING, ANYTHING, ANYTHING, ANYTHING));
    private static final Pattern ANNOTATION_PROPERTIES_FORMAT =
            Pattern.compile(String.format(PIPE_SEPARATED_COMMA_DELIMITED_VALUES,
                    KEY_EQUALS_VALUE, KEY_EQUALS_VALUE, KEY_EQUALS_VALUE, KEY_EQUALS_VALUE));

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
        checkIsNullOrEmpty(annotation.ecoId, COLUMN_ECO.getName());
        checkIsNullOrEmpty(annotation.assignedBy, COLUMN_ASSIGNED_BY.getName());

        // optional fields
        checkExtensions(annotation);
        checkProperties(annotation);
        checkWith(annotation);
    }

    private void checkProperties(Annotation annotation) {
        if (!Strings.isNullOrEmpty(annotation.with) &&
                !ANNOTATION_PROPERTIES_FORMAT.matcher(annotation.annotationProperties).matches()) {
            handlePatternMismatchError(
                    "Annotation Extension",
                    annotation.annotationExtension,
                    ANNOTATION_PROPERTIES_FORMAT.pattern(),
                    annotation);
        }
    }

    private void checkExtensions(Annotation annotation) {
        if (!Strings.isNullOrEmpty(annotation.annotationExtension) &&
                !ANNOTATION_EXTENSION_FORMAT.matcher(annotation.annotationExtension).matches()) {
            handlePatternMismatchError(
                    "Annotation Extension",
                    annotation.annotationExtension,
                    ANNOTATION_EXTENSION_FORMAT.pattern(),
                    annotation);
        }
    }

    private void checkQualifier(Annotation annotation) {
        checkIsNullOrEmpty(annotation.qualifier, COLUMN_QUALIFIER.getName());
        if (!QUALIFIER_FORMAT.matcher(annotation.qualifier).matches()) {
            handlePatternMismatchError(
                    "Qualifier",
                    annotation.qualifier,
                    QUALIFIER_FORMAT.pattern(),
                    annotation);
        }
    }

    private void checkWith(Annotation annotation) {
        if (!Strings.isNullOrEmpty(annotation.with) && !WITH_FORMAT.matcher(annotation.with).matches()) {
            handlePatternMismatchError(
                    "With",
                    annotation.with,
                    WITH_FORMAT.pattern(),
                    annotation);
        }
    }

    private void handlePatternMismatchError(String fieldName, Object fieldValue, String pattern, Object rawObject) {
        String errorMessage = fieldName + " field, '" + fieldValue +
                "' does not match: " + pattern +
                " -- see: " + rawObject.toString();
        LOGGER.error(errorMessage);
        throw new ValidationException(errorMessage);
    }
}
