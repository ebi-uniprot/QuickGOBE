package uk.ac.ebi.quickgo.client.presets.read;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.presets.read.assignedby.AssignedByPresetsConfig;
import uk.ac.ebi.quickgo.client.presets.read.evidence.EvidencePresetsConfig;
import uk.ac.ebi.quickgo.client.presets.read.geneproduct.GeneProductPresetsConfig;
import uk.ac.ebi.quickgo.client.presets.read.reference.ReferencePresetsConfig;
import uk.ac.ebi.quickgo.client.presets.read.withFrom.WithFromPresetsConfig;
import uk.ac.ebi.quickgo.common.SearchableDocumentFields;

import java.util.stream.Stream;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static uk.ac.ebi.quickgo.client.presets.read.PresetsConfigHelper.logJobListener;

/**
 * Exposes a Spring Batch {@link Job} that, when run, will read and populate a {@link CompositePreset}
 * instance, which provides user information about all preset data for QuickGO filtering.
 *
 * Created 18/05/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class,
        AssignedByPresetsConfig.class,
        ReferencePresetsConfig.class,
        EvidencePresetsConfig.class,
        WithFromPresetsConfig.class,
        GeneProductPresetsConfig.class})
public class PresetsConfig {

    private static final String PRESET_LOADING_JOB_NAME = "PresetReadingJob";
    static final String TAB = "\t";
    public static final int SKIP_LIMIT = 0;

    @Bean
    public Job presetsBuildJob(
            JobBuilderFactory jobBuilderFactory,
            Step assignedByStep,
            Step referenceGenericDbStep,
            Step referenceSpecificDbStep,
            Step evidenceStep,
            Step withFromDbStep,
            Step geneProductStep) {
        return jobBuilderFactory.get(PRESET_LOADING_JOB_NAME)
                .start(assignedByStep)
                .next(referenceGenericDbStep)
                .next(referenceSpecificDbStep)
                .next(evidenceStep)
                .next(withFromDbStep)
                .next(geneProductStep)
                .listener(logJobListener())
                .build();
    }

    @Bean
    public SearchableDocumentFields searchableDocumentFields() {
        return new NoSearchablePresetDocumentFields();
    }

    private static class NoSearchablePresetDocumentFields implements SearchableDocumentFields {
        @Override public boolean isDocumentSearchable(String field) {
            return false;
        }
        @Override public Stream<String> searchableDocumentFields() {
            return Stream.empty();
        }
    }
}
