package uk.ac.ebi.quickgo.client.presets.read;

import uk.ac.ebi.quickgo.client.presets.read.assignedby.RawAssignedByPreset;

import java.util.function.Predicate;
import org.springframework.batch.item.ItemProcessor;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created 31/08/16
 * @author Edd
 */
public class RawAssignedByPresetTopN implements ItemProcessor<RawAssignedByPreset, RawAssignedByPreset> {
    private static final RawAssignedByPreset INSIGNIFICANT_PRESET = null;
    private final Predicate<String> presetChecker;

    public RawAssignedByPresetTopN(Predicate<String> presetChecker) {
        checkArgument(presetChecker != null, "The Predicate<RawAssignedByPreset> presetChecker cannot be null");
        this.presetChecker = presetChecker;
    }

    @Override public RawAssignedByPreset process(RawAssignedByPreset rawAssignedByPreset) throws Exception {
        if (presetChecker.test(rawAssignedByPreset.name)) {
            return rawAssignedByPreset;
        } else {
            return INSIGNIFICANT_PRESET;
        }
    }
}
