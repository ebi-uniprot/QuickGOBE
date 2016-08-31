package uk.ac.ebi.quickgo.client.model.presets;

/**
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
