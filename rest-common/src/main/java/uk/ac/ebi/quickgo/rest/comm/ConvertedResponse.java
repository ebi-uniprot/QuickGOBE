package uk.ac.ebi.quickgo.rest.comm;

import java.util.Optional;

/**
 * Represents the result of a conversion, encapsulating the original converted value,
 * in addition to the meta-information associated with the conversion process.
 *
 * Created 09/08/16
 * @author Edd
 */
public class ConvertedResponse<V> {
    private V convertedValue;
    private QueryContext queryContext;

    /**
     * Retrieves the value converted
     * @return the converted value
     */
    public V getConvertedValue() {
        return convertedValue;
    }

    /**
     * Retrieves the meta-information associated with the conversion
     * @return the {@link QueryContext} containing meta-information associated with the conversion
     */
    public Optional<QueryContext> getQueryContext() {
        return Optional.of(queryContext);
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
     * @param queryContext the meta-information associated with the conversion
     */
    public void setQueryContext(QueryContext queryContext) {
        this.queryContext = queryContext;
    }

    /**
     * A convenience method used to create a {@link ConvertedResponse} that has no
     * meta-information associated with it.
     *
     * @param convertedValue the converted value
     * @param <T> the type of the converted value
     * @return the converted response
     */
    public static <T> ConvertedResponse<T> simpleConvertedResponse(T convertedValue) {
        ConvertedResponse<T> response = new ConvertedResponse<>();
        response.setConvertedValue(convertedValue);
        response.setQueryContext(null);
        return response;
    }
}
