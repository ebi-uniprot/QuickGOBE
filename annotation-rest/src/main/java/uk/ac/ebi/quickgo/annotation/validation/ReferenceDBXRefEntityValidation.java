package uk.ac.ebi.quickgo.annotation.validation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import static uk.ac.ebi.quickgo.annotation.validation.IdValidation.db;

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
class ReferenceDBXRefEntityValidation implements ConstraintValidator<ReferenceValidator, String[]> {

    @Autowired
    DBXRefEntityValidation dbxRefEntityValidation;

    List<String> referenceDatabases = Arrays.asList("pmid","doi","go_ref","reactome");

    @Override public void initialize(ReferenceValidator constraintAnnotation) {}


    @Override public boolean isValid(String[] values, ConstraintValidatorContext context) {
        return values == null ||
                Stream.of(values).allMatch(this::validReference) && dbxRefEntityValidation.isValid(values, context);
    }

    private boolean validReference(String s) {

        if(s == null){
            return false;
        }

        if(s.trim().isEmpty()){
            return false;
        }

        if(!s.contains(":")){
            return true;
        }

        return referenceDatabases.contains(db(s));
    }
}
