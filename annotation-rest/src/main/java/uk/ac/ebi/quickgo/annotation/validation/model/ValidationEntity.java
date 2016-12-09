package uk.ac.ebi.quickgo.annotation.validation.model;

/**
 * Defines the methods required for validation value objects.
 *
 * @author Tony Wardell
 * Date: 22/11/2016
 * Time: 15:18
 * Created with IntelliJ IDEA.
 */
public interface ValidationEntity {

    /**
     * Does the parameter meet the requirements of the validating object.
     * @param value to be validated.
     * @return validation result.
     */
    boolean test(String value);

    /**
     * Request the String value used to identify this validation instance.
     * @return identifying value.
     */
    String keyValue();
}
