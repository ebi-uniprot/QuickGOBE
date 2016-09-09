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

    public CompositePreset() {
        assignedBy = new PresetItemsImpl();
        references = new PresetItemsImpl();
    }

    @Override public String toString() {
        return "CompositePreset{" +
                "assignedBy=" + assignedBy +
                '}';
    }
}
