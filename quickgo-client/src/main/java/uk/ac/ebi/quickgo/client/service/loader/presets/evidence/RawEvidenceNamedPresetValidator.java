package uk.ac.ebi.quickgo.client.service.loader.presets.evidence;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsValidationHelper.checkIsNullOrEmpty;

/**
 * Validates the {@link RawEvidenceNamedPreset} instances being read from flat-files and mapped via
 * {@link StringToRawEvidenceNamedPresetMapper}.
 *
 * Created 05/03/18
 * @author Tony Wardell
 */
public class RawEvidenceNamedPresetValidator implements ItemProcessor<RawEvidenceNamedPreset, RawEvidenceNamedPreset> {
    @Override public RawEvidenceNamedPreset process(RawEvidenceNamedPreset rawNamedPreset) {
        if (rawNamedPreset == null) {
            throw new ValidationException("RawDBPreset cannot be null or empty");
        }

        checkIsNullOrEmpty(rawNamedPreset.name);

        return rawNamedPreset;
    }

}
