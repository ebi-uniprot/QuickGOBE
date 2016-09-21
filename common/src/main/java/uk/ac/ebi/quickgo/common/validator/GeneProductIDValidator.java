package uk.ac.ebi.quickgo.common.validator;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Gene product IDs that don't match the regular expressions for recognised/supported gene product types should be
 * rejected with an error; those that do match should be treated as valid (plausible), even if they don't actually
 * identify a gene product in any of the supported databases.
 * The supported databases should be: UniProtKB, RNAcentral, IntAct, and any of the databases that are available in
 * the ID mapping function.
 *
 * @author Tony Wardell
 * Date: 14/06/2016
 * Time: 13:32
 * Created with IntelliJ IDEA.
 */
public class GeneProductIDValidator implements ConstraintValidator<GeneProductIDList, String[]> {
    private static final String MESSAGE = "At least one 'Gene Product ID' value is invalid: %s";

    @Autowired
    DbXRefEntityValidation xRefFormats;
    private Predicate<String> idValidator;

    @Override public void initialize(GeneProductIDList constraintAnnotation) {
        idValidator = xRefFormats::test;
    }

    @Override public boolean isValid(String[] geneProducts, ConstraintValidatorContext context) {
        if (geneProducts == null) {
            return true;
        }
        String invalid = Arrays.stream(geneProducts).filter(idValidator.negate()).collect
                (Collectors.joining(", "));

        if(invalid != null && invalid.isEmpty()){
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(String.format(MESSAGE, invalid))
                .addConstraintViolation();

        return false;
    }
}
