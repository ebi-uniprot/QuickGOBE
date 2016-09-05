package uk.ac.ebi.quickgo.client.model.presets;

/**
 * Represents preset information relating to different aspects of QuickGO.
 *
 * Created 30/08/16
 * @author Edd
 */
public class CompositePreset {
    public final AssignedByPresets assignedBy;

    public CompositePreset() {
        assignedBy = new AssignedByPresets();
    }

    @Override public String toString() {
        return "CompositePreset{" +
                "assignedBy=" + assignedBy +
                '}';
    }
}
