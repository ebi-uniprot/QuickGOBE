package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

import static uk.ac.ebi.quickgo.client.presets.read.PresetsValidationHelper.checkIsNullOrEmpty;

/**
 * Validates the assignedBy values being read in from flat-files.
 *
 * Created 31/08/16
 * @author Edd
 */
class RawAssignedByPresetValidator implements ItemProcessor<RawAssignedByPreset, RawAssignedByPreset> {
    @Override public RawAssignedByPreset process(RawAssignedByPreset rawAssignedByPreset) throws Exception {
        if (rawAssignedByPreset == null) {
            throw new ValidationException("RawAssignedByPreset cannot be null or empty");
        }

        checkIsNullOrEmpty(rawAssignedByPreset.name);

        return rawAssignedByPreset;
    }

}
