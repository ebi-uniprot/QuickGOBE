package uk.ac.ebi.quickgo.rest.comm;

import java.util.Optional;

/**
 * Created 09/08/16
 * @author Edd
 */
public class ConvertedResponse<V> {
    private V convertedValue;
    private QueryContext queryContext;

    public V getConvertedValue() {
        return convertedValue;
    }

    public Optional<QueryContext> getQueryContext() {
        return Optional.of(queryContext);
    }

    public void setConvertedValue(V convertedValue) {
        this.convertedValue = convertedValue;
    }

    public void setQueryContext(QueryContext queryContext) {
        this.queryContext = queryContext;
    }

    public static <T> ConvertedResponse<T> simpleConvertedResponse(T convertedValue) {
        ConvertedResponse<T> response = new ConvertedResponse<>();
        response.setConvertedValue(convertedValue);
        response.setQueryContext(null);
        return response;
    }
}
