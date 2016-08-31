package uk.ac.ebi.quickgo.client.model.presets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created 30/08/16
 * @author Edd
 */
public class AssignedByPresets {
    public final List<PresetItem> presets;

    AssignedByPresets() {
        presets = new ArrayList<>();
    }

    @Override public String toString() {
        return "AssignedByPresets{" +
                "presets=" + presets +
                '}';
    }
}
