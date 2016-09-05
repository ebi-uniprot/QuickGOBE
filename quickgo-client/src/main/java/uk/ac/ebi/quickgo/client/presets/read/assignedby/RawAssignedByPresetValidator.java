package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

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

    public void checkIsNullOrEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new ValidationException("Value cannot be null or empty");
        }
    }
}
