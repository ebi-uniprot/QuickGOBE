package uk.ac.ebi.quickgo.annotation.validation.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

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
public class ReferenceValuesValidation implements ConstraintValidator<ReferenceValidator, String[]> {

    private final ValidationEntityChecker validator;

    @Autowired
    public ReferenceValuesValidation(ValidationEntityChecker validator) {
        this.validator = validator;
    }

    private final List<String> referenceDatabases = Arrays.asList("pmid","doi","go_ref","reactome");

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

        if (!value.contains(":")) {
            return true;
        }

        if (!referenceDatabases.contains(db(value))) {
            return false;
        }

        return validator.isValid(value);
    }
}
