package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import org.springframework.batch.item.ItemProcessor;

/**
 * Created 31/08/16
 * @author Edd
 */
class RawAssignedByPresetValidator implements ItemProcessor<RawAssignedByPreset, RawAssignedByPreset> {
    @Override public RawAssignedByPreset process(RawAssignedByPreset rawAssignedByPreset) throws Exception {
        return rawAssignedByPreset;
    }
}
