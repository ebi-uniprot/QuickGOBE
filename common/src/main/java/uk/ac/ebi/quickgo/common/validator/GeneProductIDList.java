package uk.ac.ebi.quickgo.common.validator;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Annotation that allows a list of strings, separated by commas to be validated as Gene Product IDs
 * @author Tony Wardell
 * Date: 14/06/2016
 * Time: 12:03
 * Created with IntelliJ IDEA.
 */

@Constraint(validatedBy = GeneProductIDValidator.class)
@Documented
@Target({ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneProductIDList {

    String message() default "The gene product ID is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
