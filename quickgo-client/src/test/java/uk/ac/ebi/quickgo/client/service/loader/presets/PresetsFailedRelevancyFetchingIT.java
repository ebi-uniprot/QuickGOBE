package uk.ac.ebi.quickgo.client.service.loader.presets;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.service.loader.presets.assignedby.AssignedByPresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.qualifier.QualifierPresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.withFrom.WithFromPresetsConfig;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

/**
 * Tests the population of the preset relevancy information despite a failure in REST communication to an
 * end-point that provides the relevancy ordering.
 *
 * Created 31/08/16
 * @author Edd
 */
@SpringBootTest(classes = {PresetsConfig.class, MockPresetDataConfig.class, JobTestRunnerConfig.class})
@WebAppConfiguration
@ActiveProfiles(profiles = {MockPresetDataConfig.FAILED_FETCHING, MockPresetDataConfig.NO_SEARCH_ATTRIBUTES})
class PresetsFailedRelevancyFetchingIT {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private CompositePreset presets;

    @Test
    void loadDefaultAssignedByPresetsAfterFailedRESTInfoFetching() {
        assertThat(presets.getAssignedBy(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(AssignedByPresetsConfig.ASSIGNED_BY_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getAssignedBy(), p -> p.getProperty(PresetItem.Property.NAME.getKey())),
                hasSize(24));
    }

    @Test
    void loadDefaultQualifierPresetsAfterFailedRESTInfoFetching() {
        assertThat(presets.getQualifiers(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(QualifierPresetsConfig.QUALIFIER_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getQualifiers(), p -> p.getProperty(PresetItem.Property.NAME.getKey())),
                is(empty()));
    }

    @Test
    void loadDefaultWithFromPresetsAfterFailedRESTInfoFetching() {
        assertThat(presets.getWithFrom(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(WithFromPresetsConfig.WITH_FROM_DB_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getWithFrom(), p -> p.getProperty(PresetItem.Property.NAME.getKey())),
                hasSize(24));
    }

    private <T> List<T> extractPresetValues(List<PresetItem> presets, Function<PresetItem, T> extractor) {
        return presets.stream().map(extractor).collect(Collectors.toList());
    }

}
