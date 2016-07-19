package uk.ac.ebi.quickgo.rest.search.results.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created 19/07/16
 * @author Edd
 */
//@Component
//@ConfigurationProperties(prefix = "model.repo2domainFieldNameTransformations")
public class FieldNameTransformer {
    private Map<String, String> transformations = new HashMap<>();
    //
    //    public FieldNameTransformer() {
    //        transformations = new HashMap<>();
    //    }

    @Override public String toString() {
        return "FieldNameTransformer{" +
                "transformations=" + transformations +
                '}';
    }
}
