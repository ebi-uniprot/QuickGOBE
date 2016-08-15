package uk.ac.ebi.quickgo.rest.comm;

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

    /**
     * Sets the converted value
     * @param convertedValue the converted value
     */
    public void setConvertedValue(V convertedValue) {
        this.convertedValue = convertedValue;
    }

    /** Sets the meta-information associated with the conversion
     *
     * @param filterContext the meta-information associated with the conversion
     */
    public void setFilterContext(FilterContext filterContext) {
        this.filterContext = filterContext;
    }

    /**
     * A convenience method used to create a {@link ConvertedFilter} that has no
     * meta-information associated with it.
     *
     * @param convertedValue the converted value
     * @param <T> the type of the converted value
     * @return the converted response
     */
    public static <T> ConvertedFilter<T> simpleConvertedResponse(T convertedValue) {
        ConvertedFilter<T> response = new ConvertedFilter<>();
        response.setConvertedValue(convertedValue);
        response.setFilterContext(null);
        return response;
    }
}
