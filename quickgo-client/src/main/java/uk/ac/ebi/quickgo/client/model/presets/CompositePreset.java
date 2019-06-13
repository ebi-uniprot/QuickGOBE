package uk.ac.ebi.quickgo.client.model.presets;

import com.fasterxml.jackson.annotation.JsonFilter;
import java.util.List;

/**
 * Represents preset information relating to different aspects of QuickGO.
 *
 * Created 20/09/16
 * @author Edd
 */
@JsonFilter("CompositePreset")
public interface CompositePreset {
    List<PresetItem> getAssignedBy();

    List<PresetItem> getReferences();

    List<PresetItem> getEvidences();

    List<PresetItem> getWithFrom();

    List<PresetItem> getGeneProducts();

    List<PresetItem> getGoSlimSets();

    List<PresetItem> getTaxons();

    List<PresetItem> getQualifiers();

    List<PresetItem> getExtRelations();

    List<PresetItem> getExtDatabases();
}
