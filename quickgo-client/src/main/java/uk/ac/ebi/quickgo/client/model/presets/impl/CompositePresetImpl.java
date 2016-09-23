package uk.ac.ebi.quickgo.client.model.presets.impl;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItems;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Component;

import static uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl.StaticAspects.createAspectBuilder;
import static uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl.StaticGeneProductTypes
        .createGeneProductTypeBuilder;

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
    @JsonIgnore
    private final PresetItemsBuilder aspectBuilder;
    @JsonIgnore
    private final PresetItemsBuilder geneProductTypeBuilder;

    public CompositePresetImpl() {
        assignedByBuilder = new PresetItemsBuilder();
        referencesBuilder = new PresetItemsBuilder();
        evidencesBuilder = new PresetItemsBuilder();
        withFromBuilder = new PresetItemsBuilder();
        geneProductsBuilder = new PresetItemsBuilder();
        goSlimSetsBuilder = new GroupedPresetItemsBuilder();
        taxonBuilder = new PresetItemsBuilder();
        qualifierBuilder = new PresetItemsBuilder();
        aspectBuilder = createAspectBuilder();
        geneProductTypeBuilder = createGeneProductTypeBuilder();
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

    @Override public PresetItems getAspects() {
        return aspectBuilder.build();
    }

    @Override public PresetItems getGeneProductTypes() {
        return geneProductTypeBuilder.build();
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

    static class StaticAspects {

        static final String MOLECULAR_FUNCTION = "Molecular Function";
        static final String FUNCTION = "function";
        static final String BIOLOGICAL_PROCESS = "Biological Process";
        static final String PROCESS = "process";
        static final String CELLULAR_COMPONENT = "Cellular Component";
        static final String COMPONENT = "component";

        static PresetItemsBuilder createAspectBuilder() {
            PresetItemsBuilder itemsBuilder = new PresetItemsBuilder();
            itemsBuilder.addPreset(PresetItemBuilder
                    .createWithName(MOLECULAR_FUNCTION)
                    .withId(FUNCTION).build());
            itemsBuilder.addPreset(PresetItemBuilder
                    .createWithName(BIOLOGICAL_PROCESS)
                    .withId(PROCESS).build());
            itemsBuilder.addPreset(PresetItemBuilder
                    .createWithName(CELLULAR_COMPONENT)
                    .withId(COMPONENT).build());
            return itemsBuilder;
        }
    }

    static class StaticGeneProductTypes {

        static final String PROTEINS = "Proteins";
        static final String PROTEIN_ID = "protein";
        static final String RNA = "RNA";
        static final String RNA_ID = "rna";
        static final String COMPLEXES = "Complexes";
        static final String COMPLEXES_ID = "complex";

        static PresetItemsBuilder createGeneProductTypeBuilder() {
            PresetItemsBuilder itemsBuilder = new PresetItemsBuilder();
            itemsBuilder.addPreset(PresetItemBuilder
                    .createWithName(PROTEINS)
                    .withId(PROTEIN_ID).build());
            itemsBuilder.addPreset(PresetItemBuilder
                    .createWithName(RNA)
                    .withId(RNA_ID).build());
            itemsBuilder.addPreset(PresetItemBuilder
                    .createWithName(COMPLEXES)
                    .withId(COMPLEXES_ID).build());
            return itemsBuilder;
        }
    }
}