package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import org.springframework.batch.item.ItemProcessor;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created 31/08/16
 * @author Edd
 */
class RawAssignedByPresetValidator implements ItemProcessor<RawAssignedByPreset, RawAssignedByPreset> {
    @Override public RawAssignedByPreset process(RawAssignedByPreset rawAssignedByPreset) throws Exception {
        checkArgument(rawAssignedByPreset != null, "RawAssignedByPreset cannot be null");
        checkArgument(rawAssignedByPreset.name != null, "RawAssignedByPreset's name cannot be null");

        return rawAssignedByPreset;
    }
}
