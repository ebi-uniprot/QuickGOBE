package uk.ac.ebi.quickgo.rest.controller.request;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AllowableFacetsImpl.class})
@Documented
public @interface AllowableFacets {
    String DEFAULT_ERROR_MESSAGE = "The facet parameter contains invalid values";

    String message() default DEFAULT_ERROR_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
