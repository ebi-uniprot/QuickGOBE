package uk.ac.ebi.quickgo.client.service.loader.presets;

import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.common.SearchableDocumentFields;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Exposes a Spring Batch {@link Job} that, when run, will read and populate a {@link CompositePresetImpl}
 * instance, which provides user information about all preset data for QuickGO filtering.
 *
 * Created 18/05/16
 * @author Edd
 */
@Configuration
@ComponentScan({
        "uk.ac.ebi.quickgo.client.service.loader.presets"})
public class PresetsConfig {

    private static final String PRESET_LOADING_JOB_NAME = "PresetReadingJob";
    static final String TAB = "\t";
    public static final int SKIP_LIMIT = 0;

    @Bean
    public Job presetsBuildJob(
            JobBuilderFactory jobBuilderFactory,
            List<Step> presetSteps) {
        JobBuilder jobBuilder = jobBuilderFactory.get(PRESET_LOADING_JOB_NAME);

        SimpleJobBuilder simpleJobBuilder = null;
        Iterator<Step> presetsIterator = presetSteps.iterator();

        // first job step
        if (presetsIterator.hasNext()) {
            Step firstStep = presetsIterator.next();
            simpleJobBuilder = jobBuilder.start(firstStep);
        }

        // other job steps
        if (simpleJobBuilder != null) {
            while (presetsIterator.hasNext()) {
                simpleJobBuilder.next(presetsIterator.next());
            }
            return simpleJobBuilder
                    .listener(PresetsConfigHelper.logJobListener())
                    .build();
        }

        throw new IllegalStateException("PresetsBuildJob could not be created");
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
