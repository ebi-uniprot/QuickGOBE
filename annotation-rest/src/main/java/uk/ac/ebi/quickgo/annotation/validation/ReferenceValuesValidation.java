package uk.ac.ebi.quickgo.annotation.validation;

import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Validate the reference values used to filter annotations.
 * There is a limited list of databases that can be used as the database portion of the identifier, defined within
 * this class.
 *
 * @author Tony Wardell
 * Date: 10/11/2016
 * Time: 13:22
 * Created with IntelliJ IDEA.
 */
class ReferenceValuesValidation implements ConstraintValidator<ReferenceValidator, String[]> {

    @Autowired
    private ReferenceDBXRefEntityValidation validator;

    @Override public void initialize(ReferenceValidator constraintAnnotation) {}

    @Override public boolean isValid(String[] values, ConstraintValidatorContext context) {
        return values == null || Stream.of(values).allMatch(validator::isValid);
    }
}
