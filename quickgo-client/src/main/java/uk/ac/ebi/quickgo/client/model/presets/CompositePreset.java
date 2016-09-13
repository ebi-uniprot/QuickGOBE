package uk.ac.ebi.quickgo.client.model.presets;

/**
 * Represents preset information relating to different aspects of QuickGO.
 *
 * Created 30/08/16
 * @author Edd
 */
public class CompositePreset {
    public final PresetItemsImpl assignedBy;
    public final PresetItemsImpl references;
    public final PresetItemsImpl evidences;

    public CompositePreset() {
        assignedBy = new PresetItemsImpl();
        references = new PresetItemsImpl();
        evidences = new PresetItemsImpl();
    }

    @Override public String toString() {
        return "CompositePreset{" +
                "assignedBy=" + assignedBy +
                '}';
    }
}
