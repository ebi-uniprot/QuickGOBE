package uk.ac.ebi.quickgo.rest.comm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created 09/08/16
 * @author Edd
 */
public class ConversionContext {
    private Map<Object, Object> properties;

    public ConversionContext() {
        properties = new HashMap<>();
    }

    public Map<Object, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<Object, Object> properties) {
        this.properties = properties;
    }
}
