package uk.ac.ebi.quickgo.client.model.presets;

/**
 * Created 20/09/16
 * @author Edd
 */
public interface CompositePreset {
    PresetItems getAssignedBy();

    PresetItems getReferences();

    PresetItems getEvidences();

    PresetItems getWithFrom();

    PresetItems getGeneProducts();

    PresetItems getGoSlimSets();
}
