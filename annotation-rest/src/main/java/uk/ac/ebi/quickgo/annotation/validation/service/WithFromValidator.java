package uk.ac.ebi.quickgo.annotation.validation.service;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Annotation that allows a list of strings, separated by commas to be validated With/From values.
 * @author Tony Wardell
 * Date: 14/06/2016
 * Time: 12:03
 * Created with IntelliJ IDEA.
 */

@Constraint(validatedBy = WithFromValuesValidation.class)
@Documented
@Target({ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithFromValidator {

    String message() default "The with/from value is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
