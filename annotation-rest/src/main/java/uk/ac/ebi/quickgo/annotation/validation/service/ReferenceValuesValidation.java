package uk.ac.ebi.quickgo.annotation.validation.service;

import uk.ac.ebi.quickgo.annotation.validation.model.ValidationProperties;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.stream.Stream;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import static uk.ac.ebi.quickgo.annotation.validation.service.DbCrossReferenceId.db;

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
@EnableConfigurationProperties(ValidationProperties.class)
public class ReferenceValuesValidation implements ConstraintValidator<ReferenceValidator, String[]> {

    private final ValidationEntityChecker validator;
    private final ValidationProperties validationLoadProperties;

    public ReferenceValuesValidation(ValidationEntityChecker validator, ValidationProperties validationLoadProperties) {
        Preconditions.checkArgument(Objects.nonNull(validator), "The ValidationEntityChecker instance cannot be null" +
                ".");
        Preconditions.checkArgument(Objects.nonNull(validationLoadProperties), "The ValidationProperties instance " +
                "cannot be null.");
        this.validator = validator;
        this.validationLoadProperties = validationLoadProperties;
    }

    @Override public void initialize(ReferenceValidator constraintAnnotation) {}

    @Override public boolean isValid(String[] values, ConstraintValidatorContext context) {
        return values == null || Stream.of(values).allMatch(this::isValid);
    }

    private boolean isValid(String value){
        if(Objects.isNull(value)){
            return false;
        }

        if (value.trim().isEmpty()) {
            return false;
        }

        final String db = db(value);

        if(Objects.isNull(db)){
            return true;
        }

        if (!validationLoadProperties.getReferenceDbs().contains(db.toLowerCase())) {
            return false;
        }

        return validator.isValid(value);
    }
}
