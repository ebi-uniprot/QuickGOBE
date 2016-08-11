package uk.ac.ebi.quickgo.rest.comm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created 09/08/16
 * @author Edd
 */
public class ConversionContext {
    private Map<Class<?>, Object> properties;

    public ConversionContext() {
        properties = new HashMap<>();
    }

    public <T> void put(Class<T> key, T value) {
        properties.put(key, value);
    }

    public <T> T get(Class<T> key) {
        return key.cast(properties.get(key));
    }

    private Map<Class<?>, Object> getProperties() {
        return properties;
    }

    public ConversionContext merge(ConversionContext context) {
        ConversionContext conversionContext = new ConversionContext();

        conversionContext.getProperties().putAll(this.getProperties());
        conversionContext.getProperties().putAll(context.getProperties());

        return conversionContext;
    }
}
