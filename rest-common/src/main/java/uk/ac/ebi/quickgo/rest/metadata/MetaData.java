package uk.ac.ebi.quickgo.rest.metadata;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple data structure for a service's meta data.
 *
 * @author Tony Wardell
 * Date: 07/03/2017
 * Time: 11:03
 * Created with IntelliJ IDEA.
 */
public class MetaData {

    //A list of common, but not exclusive keys with which to populate the properties map
    public static final String VERSION = "version";
    public static final String TIMESTAMP = "timestamp";

    private Map<String, MetaDataMarker> properties = new HashMap<>();
    public void add(String key, MetaDataMarker value) {
        properties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, MetaDataMarker> getProperties() {
        return properties;
    }
}
