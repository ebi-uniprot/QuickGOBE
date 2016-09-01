package uk.ac.ebi.quickgo.client.presets.read;

import uk.ac.ebi.quickgo.client.presets.read.assignedby.AssignedByPresetsConfig;
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
 *
 *
 * Created 18/05/16
 * @author Edd
 */
@Configuration
@Import({PresetsCommonConfig.class, AssignedByPresetsConfig.class})
public class PresetsConfig {

    private static final String PRESET_LOADING_JOB_NAME = "PresetReadingJob";
    static final String TAB = "\t";
    public static final int SKIP_LIMIT = 0;

    @Bean
    public Job presetsBuildJob(JobBuilderFactory jobBuilderFactory, Step assignedByStep) {
        return jobBuilderFactory.get(PRESET_LOADING_JOB_NAME)
                .start(assignedByStep)
                .listener(logJobListener())
                .build();
    }

    @Bean
    public SearchableDocumentFields noSearchableDocumentFields() {
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
