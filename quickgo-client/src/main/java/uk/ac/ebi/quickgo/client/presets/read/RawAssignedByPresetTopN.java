package uk.ac.ebi.quickgo.client.presets.read;

import uk.ac.ebi.quickgo.client.presets.read.assignedby.RawAssignedByPreset;

import org.springframework.batch.item.ItemProcessor;

/**
 * Created 31/08/16
 * @author Edd
 */
public class RawAssignedByPresetTopN implements ItemProcessor<RawAssignedByPreset, RawAssignedByPreset> {

    @Override public RawAssignedByPreset process(RawAssignedByPreset rawAssignedByPreset) throws Exception {
        return null;
    }
}
