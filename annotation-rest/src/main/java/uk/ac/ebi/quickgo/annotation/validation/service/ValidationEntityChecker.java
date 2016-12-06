package uk.ac.ebi.quickgo.annotation.validation.service;

import uk.ac.ebi.quickgo.annotation.validation.model.ValidationEntities;
import uk.ac.ebi.quickgo.annotation.validation.model.ValidationEntity;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.Objects;

/**
 * Use the database cross reference information contained in this class to verify a list of potential database cross
 * reference identifiers.
 *
 * @author Tony Wardell
 * Date: 08/11/2016
 * Time: 14:41
 * Created with IntelliJ IDEA.
 */

public class ValidationEntityChecker {

    private final ValidationEntities validationEntities;

    public ValidationEntityChecker(ValidationEntities validationEntities) {
        Preconditions.checkArgument(Objects.nonNull(validationEntities), "ValidationEntities instance cannot be null" +
                ".");
        this.validationEntities = validationEntities;
    }

    /**
     * If the value passed to this method can be successfully validated isValid will return true.
     * @param value a potential database cross reference identifier.
     * @return validation result.
     */

    public boolean isValid(String value) {
        return Objects.nonNull(value) && (!value.contains(":") || isValidAgainstEntity(value));
    }

    private boolean isValidAgainstEntity(String value) {
        final List<ValidationEntity> entities = validationEntities.get(DbCrossReferenceId.db(value).toLowerCase());
        return entities != null && entities.stream().anyMatch(e -> e.test(DbCrossReferenceId.id(value)));
    }
}
