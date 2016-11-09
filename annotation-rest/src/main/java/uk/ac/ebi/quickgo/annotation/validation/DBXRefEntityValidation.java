package uk.ac.ebi.quickgo.annotation.validation;

import uk.ac.ebi.quickgo.annotation.validation.model.DBXRefEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.batch.item.ItemWriter;

/**
 * @author Tony Wardell
 * Date: 08/11/2016
 * Time: 14:41
 * Created with IntelliJ IDEA.
 */
public class DBXRefEntityValidation implements ConstraintValidator<WithFromValidator, String[]> {

    private final static Map<String, List<DBXRefEntity>> mappedEntities = new HashMap<>();

    @Override public void initialize(WithFromValidator constraintAnnotation) {

    }

    @Override public boolean isValid(String[] values, ConstraintValidatorContext context) {
        return values == null ? true : Stream.of(values)
                .allMatch(this::valueIsValid);
    }

    private boolean valueIsValid(String value) {
        if( value.contains(":")){
            return (isValidForDb(value));
        }else{
            return true;
        }
    }

    private boolean isValidForDb(String value) {
        final String dbKey = value.substring(0, value.indexOf(":")).toLowerCase();
        final String id = value.substring(value.indexOf(":")+1);
        List<DBXRefEntity> entities = mappedEntities.get(dbKey);

        if(entities!=null){
            for(DBXRefEntity entity : entities){
                if(entity.test(id)){
                    return true;
                }
            }
        }
        return false;
    }

    static class DBXRefEntityAggregator implements ItemWriter<DBXRefEntity> {

        @Override public void write(List<? extends DBXRefEntity> items) throws Exception {

            for (DBXRefEntity item : items) {
                mappedEntities.computeIfAbsent(item.database.toLowerCase(), k -> new ArrayList<>()).add(item);
            }
        }
    }
}
