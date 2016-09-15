package uk.ac.ebi.quickgo.client.model.presets;

/**
 * Represents preset information relating to different aspects of QuickGO.
 *
 * Created 30/08/16
 * @author Edd
 */
public class CompositePreset {
    public final PresetItems assignedBy;
    public final PresetItems references;
    public final PresetItems evidences;
    public final PresetItems withFrom;
    public final PresetItems geneProducts;

    public CompositePreset() {
        assignedBy = new PresetItemsImpl();
        references = new PresetItemsImpl();
        evidences = new PresetItemsImpl();
        withFrom = new PresetItemsImpl();
        geneProducts = new PresetItemsImpl();
    }

    @Override public String toString() {
        return "CompositePreset{" +
                "assignedBy=" + assignedBy +
                ", references=" + references +
                ", evidences=" + evidences +
                ", withFrom=" + withFrom +
                ", geneProducts=" + geneProducts +
                '}';
    }
}
