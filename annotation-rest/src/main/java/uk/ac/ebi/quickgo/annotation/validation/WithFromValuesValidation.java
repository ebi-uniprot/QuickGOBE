package uk.ac.ebi.quickgo.annotation.validation;

import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Tony Wardell
 * Date: 21/11/2016
 * Time: 16:37
 * Created with IntelliJ IDEA.
 */
public class WithFromValuesValidation implements ConstraintValidator<WithFromValidator, String[]> {

    @Autowired
    DBXRefEntityValidation validator;

    @Override public void initialize(WithFromValidator constraintAnnotation) {}

    /**
     * If the entire list of values passed to this method can be successfully validated, or not validated at all then
     * isValid will return true.
     * @param values list of potential database cross reference identifiers. Can be null.
     * @param context of the isValid call.
     * @return validation result as boolean.
     */
    @Override public boolean isValid(String[] values, ConstraintValidatorContext context) {
        return values == null || Stream.of(values).allMatch(validator::isValid);
    }
}
