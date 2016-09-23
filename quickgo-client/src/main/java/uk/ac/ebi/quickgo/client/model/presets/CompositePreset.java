package uk.ac.ebi.quickgo.client.model.presets;

/**
 * Represents preset information relating to different aspects of QuickGO.
 *
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

    PresetItems getTaxons();

    PresetItems getQualifiers();
}