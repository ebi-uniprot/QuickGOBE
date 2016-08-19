package uk.ac.ebi.quickgo.common;

import com.google.common.base.Preconditions;
import java.util.Set;

/**
 * This class contains methods that aid the representation of documents and their fields.
 *
 * Created 17/08/16
 * @author Edd
 */
public final class DocumentFieldsHelper {
    /**
     * Add a specified {@link String} value to a set, and return that value.
     *
     * @param values the set to add to
     * @param value the value to add to the set
     * @return the value
     */
    public static String storeAndGet(Set<String> values, String value) {
        Preconditions.checkArgument(values != null, "Supplied values cannot be null");
        Preconditions.checkArgument(value != null && !value.isEmpty(), "Supplied value cannot be null or empty");

        values.add(value);
        return value;
    }
}
