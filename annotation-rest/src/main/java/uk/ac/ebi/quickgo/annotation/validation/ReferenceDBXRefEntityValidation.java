package uk.ac.ebi.quickgo.annotation.validation;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
class ReferenceDBXRefEntityValidation extends DBXRefEntityValidation {

    private List<String> referenceDatabases = Arrays.asList("pmid","doi","go_ref","reactome");

    @Override
    boolean isValid(String value) {
        Preconditions.checkArgument(Objects.nonNull(value), "The value for id cannot be null");

        if(value.trim().isEmpty()){
            return false;
        }

        if(!value.contains(":")){
            return true;
        }

        if (!referenceDatabases.contains(db(value))){
            return false;
        }

        return super.isValid(value);
    }
}
