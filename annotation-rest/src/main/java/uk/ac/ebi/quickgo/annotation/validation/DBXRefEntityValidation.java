package uk.ac.ebi.quickgo.annotation.validation;

import uk.ac.ebi.quickgo.annotation.validation.model.DBXRefEntity;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.batch.item.ItemWriter;

import static java.util.stream.Collectors.groupingBy;

/**
 * Use the database cross reference information contain in this class to verify a list of potential database cross
 * reference identifiers.
 *
 * @author Tony Wardell
 * Date: 08/11/2016
 * Time: 14:41
 * Created with IntelliJ IDEA.
 */
public class DBXRefEntityValidation implements ConstraintValidator<WithFromValidator, String[]> {

    private static Map<String, List<DBXRefEntity>> mappedEntities = new HashMap<>();

    private static Function<String, String> toDb = (value) -> value.substring(0, value.indexOf(":")).toLowerCase();
    private static Function<String, String> toId = (value) -> value.substring(value.indexOf(":") + 1);

    @Override public void initialize(WithFromValidator constraintAnnotation) {

    }

    /**
     * If the entire list of values passed to isValid can be successfully validated, or not validated at all then
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
        return !value.contains(":") || (isValidForDb(value));
    }

    private boolean isValidForDb(String value) {
        final List<DBXRefEntity> entities = mappedEntities.get(toDb.apply(value));
        return entities != null && entities.stream().anyMatch(e -> e.test(toId.apply(value)));
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
