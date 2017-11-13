package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.annotation.SlimResultsTransformer;

import com.google.common.base.Preconditions;
import java.util.*;

/**
 * Captures meta-information found during the processing of, {@link DescendantsFilterConverter#transform};
 * mapping GO ids to their ancestor GO ids. This provides the information necessary for
 * transforming results as defined in {@link SlimResultsTransformer}.
 *
 * Created 12/08/16
 * @author Edd
 */
public class SlimmingConversionInfo {
    private final Map<String, List<String>> descendantToTermMap;

    public SlimmingConversionInfo() {
        this.descendantToTermMap = new HashMap<>();
    }

    public void addOriginal2SlimmedGOIdMapping(String originalGOId, String slimmedGOId) {
        Preconditions.checkArgument(originalGOId != null && !originalGOId.isEmpty(),
                "Original GO id cannot be null / empty");
        Preconditions.checkArgument(slimmedGOId != null && !slimmedGOId.isEmpty(),
                "Original GO id (" + originalGOId + ") cannot be mapped to null / empty slimmed GO id");

        if (!descendantToTermMap.containsKey(originalGOId)) {
            descendantToTermMap.put(originalGOId, new ArrayList<>());
        }
        descendantToTermMap.get(originalGOId).add(slimmedGOId);
    }

    public Map<String, List<String>> getInfo() {
        return Collections.unmodifiableMap(descendantToTermMap);
    }
}
