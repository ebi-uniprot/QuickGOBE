package uk.ac.ebi.quickgo.rest.comm;

import java.util.Optional;

/**
 * Created 09/08/16
 * @author Edd
 */
public class ConvertedResponse<V> {
    private V convertedValue;
    private ConversionContext conversionContext;

    public V getConvertedValue() {
        return convertedValue;
    }

    public Optional<ConversionContext> getConversionContext() {
        return Optional.of(conversionContext);
    }

    public void setConvertedValue(V convertedValue) {
        this.convertedValue = convertedValue;
    }

    public void setConversionContext(ConversionContext conversionContext) {
        this.conversionContext = conversionContext;
    }

    public static <T> ConvertedResponse<T> simpleConvertedResponse(T convertedValue) {
        ConvertedResponse<T> response = new ConvertedResponse<>();
        response.setConvertedValue(convertedValue);
        response.setConversionContext(null);
        return response;
    }
}
