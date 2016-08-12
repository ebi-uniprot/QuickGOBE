package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
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
