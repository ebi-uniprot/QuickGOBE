package uk.ac.ebi.quickgo.common.validator;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
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
    public static final String DEFAULT_ERROR_MESSAGE = "The 'Gene Product ID' parameter contains invalid values: %s";

    @Autowired
    private DbXRefEntityValidation xRefFormats;
    private Predicate<String> idValidator;

    @Override public void initialize(GeneProductIDList constraintAnnotation) {
        idValidator = xRefFormats;
    }

    @Override public boolean isValid(String[] geneProducts, ConstraintValidatorContext context) {
        String invalidGpIds = null;

        if (geneProducts != null) {
            invalidGpIds = Arrays.stream(geneProducts)
                    .filter(idValidator.negate())
                    .collect(Collectors.joining(", "));

            if (!invalidGpIds.isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(DEFAULT_ERROR_MESSAGE.formatted(invalidGpIds))
                        .addConstraintViolation();
            }
        }

        return invalidGpIds == null || invalidGpIds.isEmpty();
    }
}