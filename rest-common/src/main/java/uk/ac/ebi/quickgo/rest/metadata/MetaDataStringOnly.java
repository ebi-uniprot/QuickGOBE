package uk.ac.ebi.quickgo.rest.metadata;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.HashMap;
import java.util.Map;

/**
 * Create MetaData from Strings.
 *
 * @author Tony Wardell
 * Date: 10/03/2017
 * Time: 13:39
 * Created with IntelliJ IDEA.
 */
public class MetaDataStringOnly implements MetaDataMarker  {
    private Map<String, String> properties = new HashMap<>();
    public void add(String key, String value) {
        properties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return properties;
    }
}
