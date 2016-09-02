package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import java.util.List;
import org.springframework.batch.item.ItemProcessor;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created 31/08/16
 * @author Edd
 */
class RawAssignedByPresetRelevanceChecker implements ItemProcessor<RawAssignedByPreset, RawAssignedByPreset> {
    private static final RawAssignedByPreset INSIGNIFICANT_PRESET = null;
    private final List<String> presetsOrderedByRelevance;

    RawAssignedByPresetRelevanceChecker(List<String> presetsOrderedByRelevance) {
        checkArgument(presetsOrderedByRelevance != null, "The parameter, presetsOrderedByRelevance, cannot be null");
        this.presetsOrderedByRelevance = presetsOrderedByRelevance;
    }

    @Override public RawAssignedByPreset process(RawAssignedByPreset rawAssignedByPreset) throws Exception {
        int relevancyPosition = presetsOrderedByRelevance.indexOf(rawAssignedByPreset.name);

        if (relevancyPosition >= 0) {
            rawAssignedByPreset.relevancy = relevancyPosition;
            return rawAssignedByPreset;
        } else {
            return INSIGNIFICANT_PRESET;
        }
    }
}
