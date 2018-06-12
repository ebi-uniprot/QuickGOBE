package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

import java.util.List;
import org.springframework.batch.item.ItemProcessor;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Checks the validity of the relevancy associated with a {@link RawNamedPreset}.
 *
 * Created 31/08/16
 * @author Edd
 */
public class RawNamedPresetRelevanceAssignation implements ItemProcessor<RawNamedPreset, RawNamedPreset> {
    private static final RawNamedPreset INSIGNIFICANT_PRESET = null;
    private final List<String> presetsOrderedByRelevance;

    public RawNamedPresetRelevanceAssignation(List<String> presetsOrderedByRelevance) {
        checkArgument(presetsOrderedByRelevance != null, "The parameter, presetsOrderedByRelevance, cannot be null");
        this.presetsOrderedByRelevance = presetsOrderedByRelevance;
    }

    @Override public RawNamedPreset process(RawNamedPreset rawNamedPreset) {
        int relevancyPosition = 0;
        for (String preset : presetsOrderedByRelevance) {
            if (preset.equalsIgnoreCase(rawNamedPreset.name)) {
                rawNamedPreset.relevancy = relevancyPosition;
                return rawNamedPreset;
            }
            relevancyPosition++;
        }

        return INSIGNIFICANT_PRESET;
    }
}
