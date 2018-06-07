package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

import uk.ac.ebi.quickgo.client.service.loader.presets.RestValuesRetriever;

import java.util.HashSet;
import java.util.Set;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

import static uk.ac.ebi.quickgo.client.service.loader.presets.PresetsValidationHelper.checkIsNullOrEmpty;

/**
 * Provide ItemProcessor instances to be used in the creation of preset list information.
 */
public class ItemProcessorFactory {

    /**
     * An item processor to ensure there are no duplicates in a presets list
     * @return item processor
     */
    public static ItemProcessor<RawNamedPreset, RawNamedPreset> duplicateCheckingItemProcessor() {
        return new ItemProcessor<RawNamedPreset, RawNamedPreset>() {
            private final Set<String> duplicatePrevent = new HashSet<>();

            @Override
            public RawNamedPreset process(RawNamedPreset rawNamedPreset) throws Exception {
                return duplicatePrevent.add(rawNamedPreset.name.toLowerCase()) ? rawNamedPreset : null;
            }
        };
    }

    /**
     * An item processor to ensure validation conditions are met.
     * @return item processor
     */
    public static ItemProcessor<RawNamedPreset, RawNamedPreset> validatingItemProcessor() {
        return new ItemProcessor<RawNamedPreset, RawNamedPreset>() {
            @Override
            public RawNamedPreset process(RawNamedPreset rawNamedPreset) throws Exception {
                if (rawNamedPreset == null) {
                    throw new ValidationException("RawDBPreset cannot be null or empty");
                }

                checkIsNullOrEmpty(rawNamedPreset.name);

                return rawNamedPreset;
            }
        };
    }

    /**
     * An item processor to ensure preset values are actually in use.
     * @param restValuesRetriever the source of the 'in-use data' to check.
     * @param retrieveKey the key used to retrieve the 'in-use data' from it's source.
     * @return item processor
     */
    public static ItemProcessor<RawNamedPreset, RawNamedPreset> checkPresetIsUsedItemProcessor(RestValuesRetriever
                                                                                                       restValuesRetriever,
                                                                                               String retrieveKey) {
        final Set<String> usedValues = restValuesRetriever.retrieveValues(retrieveKey);
        return rawNamedPreset -> {
            if (usedValues.isEmpty()) {
                //Wasn't possible to load from values used from source and check usage, so OK preset value so we have
                // something to show.
                return rawNamedPreset;
            }
            return usedValues.contains(rawNamedPreset.name) ? rawNamedPreset : null;
        };
    }
}
