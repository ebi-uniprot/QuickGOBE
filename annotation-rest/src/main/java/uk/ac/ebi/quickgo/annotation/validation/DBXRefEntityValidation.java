package uk.ac.ebi.quickgo.annotation.validation;

import uk.ac.ebi.quickgo.annotation.validation.model.DBXRefEntity;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.batch.item.ItemWriter;

import static java.util.stream.Collectors.groupingBy;
import static uk.ac.ebi.quickgo.annotation.validation.IdValidation.*;

/**
 * Use the database cross reference information contained in this class to verify a list of potential database cross
 * reference identifiers.
 *
 * @author Tony Wardell
 * Date: 08/11/2016
 * Time: 14:41
 * Created with IntelliJ IDEA.
 */
class DBXRefEntityValidation implements ConstraintValidator<WithFromValidator, String[]> {

    private static Map<String, List<DBXRefEntity>> mappedEntities = new HashMap<>();

    @Override public void initialize(WithFromValidator constraintAnnotation) {}

    /**
     * If the entire list of values passed to this method can be successfully validated, or not validated at all then
     * isValid will return true.
     * @param values list of potential database cross reference identifiers. Can be null.
     * @param context of the isValid call.
     * @return validation result as boolean.
     */
    @Override public boolean isValid(String[] values, ConstraintValidatorContext context) {
        return values == null || Stream.of(values)
                .allMatch(this::valueIsValid);
    }

    private boolean valueIsValid(String value) {
        return !value.contains(":") || isValidForDb(value);
    }

    private boolean isValidForDb(String value) {
        final List<DBXRefEntity> entities = mappedEntities.get(db(value));
        return entities != null && entities.stream().anyMatch(e -> e.test(id(value)));
    }

    static class DBXRefEntityAggregator implements ItemWriter<DBXRefEntity> {

        @Override public void write(List<? extends DBXRefEntity> items) {
            Preconditions.checkArgument(items != null, "The list of DBXRefEntity written to DBXRefEntityAggregator " +
                    "cannot be null.");

            mappedEntities.putAll(items.stream()
                    .filter(Objects::nonNull)
                    .filter(i -> i.database != null)
                    .collect(groupingBy(i -> i.database.toLowerCase())));
        }
    }
}
