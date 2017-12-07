package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.comm.FilterContext;

import com.google.common.base.Preconditions;
import java.util.Optional;

/**
 * Represents the result of a filter conversion, encapsulating the original converted value,
 * in addition to the meta-information associated with the conversion process.
 *
 * Created 09/08/16
 * @author Edd
 */
public class ConvertedFilter<V> {
    private V convertedValue;
    private FilterContext filterContext;

    public ConvertedFilter(V convertedValue) {
        this(convertedValue, null);
    }

    public ConvertedFilter(V convertedValue, FilterContext filterContext) {
        Preconditions.checkArgument(convertedValue != null, "Cannot create a ConvertedFilter without supplying a " +
                "non-null value");
        this.convertedValue = convertedValue;
        this.filterContext = filterContext;
    }

    /**
     * Retrieves the value converted
     * @return the converted value
     */
    public V getConvertedValue() {
        return convertedValue;
    }

    /**
     * Retrieves the meta-information associated with the conversion
     * @return the {@link FilterContext} containing meta-information associated with the conversion
     */
    public Optional<FilterContext> getFilterContext() {
        if (filterContext == null) {
            return Optional.empty();
        } else {
            return Optional.of(filterContext);
        }
    }
}
