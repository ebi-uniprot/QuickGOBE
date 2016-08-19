package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.SlimResultsTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Captures meta-information found during the processing of, {@link DescendantsFilterConverter#transform};
 * mapping GO ids to their ancestor GO ids. This provides the information necessary for
 * transforming results as defined in {@link SlimResultsTransformer}.
 *
 * Created 12/08/16
 * @author Edd
 */
public class SlimmingConversionInfo {
    private Map<String, List<String>> descendantToTermMap = new HashMap<>();

    public void addOriginal2SlimmedGOIdMapping(String originalGOId, String slimmedGOId) {
        if (!descendantToTermMap.containsKey(originalGOId)) {
            descendantToTermMap.put(originalGOId, new ArrayList<>());
        }
        descendantToTermMap.get(originalGOId).add(slimmedGOId);
    }

    public Map<String, List<String>> getInfo() {
        return descendantToTermMap;
    }
}
