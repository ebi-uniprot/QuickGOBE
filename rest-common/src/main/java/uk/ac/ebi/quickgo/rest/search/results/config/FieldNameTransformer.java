package uk.ac.ebi.quickgo.rest.search.results.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Captures mappings between field names used within an application, and
 * the associated field name that should be used at the model level, which
 * makes sense to a client.
 *
 * Created 19/07/16
 * @author Edd
 */
@Component
@ConfigurationProperties(prefix = "model.repo-to-domain-field-name-transformations")
public class FieldNameTransformer {
    private Map<String, String> transformations;

    public FieldNameTransformer() {
        transformations = new HashMap<>();
    }

    public Map<String, String> getTransformations() {
        return transformations;
    }

    public void setTransformations(Map<String, String> transformations) {
        if (transformations != null) {
            this.transformations = transformations;
        }
    }

    @Override public String toString() {
        return "FieldNameTransformer{" +
                "transformations=" + transformations +
                '}';
    }
}
