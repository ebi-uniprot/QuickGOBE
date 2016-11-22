package uk.ac.ebi.quickgo.annotation.validation;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation that allows a list of strings, separated by commas to be validated as Reference Ids (full or partial).
 * @author Tony Wardell
 * Date: 14/06/2016
 * Time: 12:03
 * Created with IntelliJ IDEA.
 */

@Constraint(validatedBy = ReferenceValuesValidation.class)
@Documented
@Target({ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferenceValidator {

    String message() default "The reference value is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
