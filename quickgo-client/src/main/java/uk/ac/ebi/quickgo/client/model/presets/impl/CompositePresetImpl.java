package uk.ac.ebi.quickgo.client.model.presets.impl;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItems;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Component;

/**
 * Represents preset information relating to different aspects of QuickGO.
 *
 * Created 30/08/16
 * @author Edd
 */
@Component
public class CompositePresetImpl implements CompositePreset {
    @JsonIgnore
    public final PresetItemsBuilder assignedByBuilder;
    @JsonIgnore
    public final PresetItemsBuilder referencesBuilder;
    @JsonIgnore
    public final PresetItemsBuilder evidencesBuilder;
    @JsonIgnore
    public final PresetItemsBuilder withFromBuilder;
    @JsonIgnore
    public final PresetItemsBuilder geneProductsBuilder;
    @JsonIgnore
    public final GroupedPresetItemsBuilder goSlimSetsBuilder;
    @JsonIgnore
    public final PresetItemsBuilder taxonBuilder;
    @JsonIgnore
    public final PresetItemsBuilder qualifierBuilder;

    public CompositePresetImpl() {
        assignedByBuilder = new PresetItemsBuilder();
        referencesBuilder = new PresetItemsBuilder();
        evidencesBuilder = new PresetItemsBuilder();
        withFromBuilder = new PresetItemsBuilder();
        geneProductsBuilder = new PresetItemsBuilder();
        goSlimSetsBuilder = new GroupedPresetItemsBuilder();
        taxonBuilder = new PresetItemsBuilder();
        qualifierBuilder = new PresetItemsBuilder();
    }

    @Override public PresetItems getAssignedBy() {
        return assignedByBuilder.build();
    }

    @Override public PresetItems getReferences() {
        return referencesBuilder.build();
    }

    @Override public PresetItems getEvidences() {
        return evidencesBuilder.build();
    }

    @Override public PresetItems getWithFrom() {
        return withFromBuilder.build();
    }

    @Override public PresetItems getGeneProducts() {
        return geneProductsBuilder.build();
    }

    @Override public PresetItems getGoSlimSets() {
        return goSlimSetsBuilder.build();
    }

    @Override public PresetItems getTaxons() {
        return taxonBuilder.build();
    }

    @Override public PresetItems getQualifiers() {
        return qualifierBuilder.build();
    }

    @Override public String toString() {
        return "CompositePresetImpl{" +
                "assignedByBuilder=" + assignedByBuilder +
                ", referencesBuilder=" + referencesBuilder +
                ", evidencesBuilder=" + evidencesBuilder +
                ", withFromBuilder=" + withFromBuilder +
                ", geneProductsBuilder=" + geneProductsBuilder +
                ", goSlimSetsBuilder=" + goSlimSetsBuilder +
                ", taxonBuilder=" + taxonBuilder +
                ", qualifierBuilder=" + qualifierBuilder +
                '}';
    }
}