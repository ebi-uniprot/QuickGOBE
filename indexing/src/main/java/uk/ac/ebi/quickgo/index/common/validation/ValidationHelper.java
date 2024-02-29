package uk.ac.ebi.quickgo.index.common.validation;

import org.slf4j.Logger;
import org.springframework.batch.item.validator.ValidationException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * This class defines common validation logic used during the validation of
 * objects before they are stored.
 *
 * Created 20/04/16
 * @author Edd
 */
public class ValidationHelper {
    private static final Logger LOGGER = getLogger(ValidationHelper.class);

    /**
     * Checks whether the expression holds and if it does, a {@link ValidationException} is thrown.
     *
     * @param expression the expression to check
     * @param message the message that will be appended to the exception
     */
    public static void checkExpression(boolean expression, String message) {
        if (expression) {
            throw new ValidationException(message);
        }
    }

    /**
     * Checks whether a specified {@code value} in a {@code field} is null, and if it is
     * a {@link ValidationException} is thrown.
     *
     * @param value the value to check for nullity
     * @param field the field associated with the {@code value}
     */
    public static void checkIsNull(String value, String field) {
        checkExpression(value == null, "Found null value in field: " + field);
    }

    /**
     * Checks whether a specified {@code value} in a {@code field} is empty, and if it is
     * a {@link ValidationException} is thrown.
     *
     * @param value the value to check for emptiness
     * @param field the field associated with the {@code value}
     */
    public static void checkIsEmpty(String value, String field) {
        checkExpression(value.isEmpty(), "Found empty value in field: " + field);
    }

    /**
     * Checks whether a specified {@code value} in a {@code field} is either null or empty,
     * and if it is a {@link ValidationException} is thrown.
     *
     * @param value the value to check for nullity or emptiness
     * @param field the field associated with the {@code value}
     */
    public static void checkIsNullOrEmpty(String value, String field) {
        checkIsNull(value, field);
        checkIsEmpty(value, field);
    }

    /**
     * Upon a {@link java.util.regex.Pattern} mis-match, log the error with traceable detail, and
     * throw a {@link ValidationException}.
     *
     * @param fieldName field name
     * @param fieldValue field value
     * @param pattern the pattern, which {@code fieldValue} does not match
     * @param rawObject the object containing the field, which is printed for traceability
     * @throws ValidationException
     */
    public static void handleFieldPatternMismatchError(String fieldName, Object fieldValue, String pattern, Object
            rawObject) {
        String errorMessage = 
                "%s field, <%s> does not match: <%s>. See, %s".formatted(
                fieldName, fieldValue, pattern, rawObject.toString());
        LOGGER.error(errorMessage);
        throw new ValidationException(errorMessage);
    }
}
