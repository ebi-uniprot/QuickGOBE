package uk.ac.ebi.quickgo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 *
 *
 * Created 01/12/15
 * @author Edd
 */
public interface FieldConverter<T extends FieldType> extends Function<String, Optional<T>> {
    default List<T> convertField(List<String> list) {
        List<T> ags = new ArrayList<>();
        if (list != null) {
            list.stream()
                    .map(this::apply)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(ags::add);
        }
        return ags;
    }

    default String nullOrString(String convertedField) {
        if (convertedField != null) {
            return (convertedField.trim().equals("")) ? null : convertedField.trim();
        } else {
            return null;
        }
    }
}
