package uk.ac.ebi.quickgo.client.presets.read.ff;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

import static uk.ac.ebi.quickgo.client.presets.read.PresetsValidationHelper.checkIsNullOrEmpty;

/**
 * Validates the {@link RawNamedPreset} instances being read from flat-files and mapped via
 * {@link StringToRawNamedPresetMapper}.
 *
 * Created 31/08/16
 * @author Edd
 */
public class RawNamedPresetValidator implements ItemProcessor<RawNamedPreset, RawNamedPreset> {
    @Override public RawNamedPreset process(RawNamedPreset rawNamedPreset) throws Exception {
        if (rawNamedPreset == null) {
            throw new ValidationException("RawDBPreset cannot be null or empty");
        }

        checkIsNullOrEmpty(rawNamedPreset.name);

        return rawNamedPreset;
    }

}
