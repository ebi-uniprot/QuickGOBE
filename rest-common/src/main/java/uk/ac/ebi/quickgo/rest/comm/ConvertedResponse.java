package uk.ac.ebi.quickgo.rest.comm;

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

    public ConversionContext getConversionContext() {
        return conversionContext;
    }

    public void setConvertedValue(V convertedValue) {
        this.convertedValue = convertedValue;
    }

    public void setConversionContext(ConversionContext conversionContext) {
        this.conversionContext = conversionContext;
    }
}
