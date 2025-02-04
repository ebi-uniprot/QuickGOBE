package uk.ac.ebi.quickgo.client.service.loader.presets.slimsets;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsValidationHelper.checkIsNullOrEmpty;

/**
 * Validates the {@link RawSlimSetNamedPreset} instances being read from flat-files and mapped via
 * {@link StringToRawSlimSetNamedPresetMapper}.
 */
public class RawSlimSetNamedPresetValidator implements ItemProcessor<RawSlimSetNamedPreset, RawSlimSetNamedPreset> {
    @Override public RawSlimSetNamedPreset process(RawSlimSetNamedPreset rawSlimSetNamedPreset) {
        if (rawSlimSetNamedPreset == null) {
            throw new ValidationException("RawSlimSetNamedPreset cannot be null or empty");
        }

        checkIsNullOrEmpty(rawSlimSetNamedPreset.name);

        return rawSlimSetNamedPreset;
    }

}
