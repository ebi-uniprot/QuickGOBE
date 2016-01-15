package uk.ac.ebi.quickgo.service.converter;

import com.google.common.base.Strings;
import uk.ac.ebi.quickgo.service.model.FieldType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Converts a {@link uk.ac.ebi.quickgo.repo.solr.document.QuickGODocument}'s field String value into the
 * corresponding field DTO. Used when converting repository search results
 * to an object model which is serialised at the RESTful layer.
 *
 * Created 01/12/15
 * @author Edd
 */
public interface FieldConverter<T extends FieldType> extends Function<String, Optional<T>> {

    /**
     * Converts a list of field values into a list of corresponding DTO values.
     * Added as a default method since it preserves possibility for multiple
     * implementations of {@link FieldConverter}s, where this behaviour remains
     * common.
     *
     * @param list the list of field {@link String} values
     * @return the list of corresponding DTO values created via the
     *         {@link FieldConverter} implementation's {@code apply} method.
     */
    default List<T> convertFieldList(List<String> list) {
        if (list != null) {
            return list.stream()
                    .map(this::apply)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * Converts a field {@link String} value to one
     * compatible with a DTO. Non-empty fields
     * are trimmed; otherwise {@code null} is returned.
     *
     * @param field the field value
     * @return the cleaned value. Non-empty fields are trimmed; otherwise {@code null}.
     */
    default String cleanFieldValue(String field) {
        return Strings.emptyToNull(Strings.nullToEmpty(field).trim());
    }
}
