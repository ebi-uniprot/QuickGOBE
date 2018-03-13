package uk.ac.ebi.quickgo.client.service.loader.presets;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.service.loader.presets.assignedby.AssignedByPresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.qualifier.QualifierPresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.withFrom.WithFromPresetsConfig;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

/**
 * Tests the population of the preset relevancy information despite a failure in REST communication to an
 * end-point that provides the relevancy ordering.
 *
 * Created 31/08/16
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {PresetsConfig.class, MockPresetDataConfig.class, JobTestRunnerConfig.class},
        loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
@ActiveProfiles(profiles = {MockPresetDataConfig.FAILED_FETCHING, MockPresetDataConfig.NO_SEARCH_ATTRIBUTES})
public class PresetsFailedRelevancyFetchingIT {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private CompositePreset presets;

    @Test
    public void loadDefaultQualifierPresetsAfterFailedRESTInfoFetching() {
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
    public void loadDefaultAssignedByPresetsAfterFailedRESTInfoFetching() {
        assertThat(presets.getAssignedBy(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(AssignedByPresetsConfig.ASSIGNED_BY_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getAssignedBy(), p -> p.getProperty(PresetItem.Property.NAME.getKey())),
                is(empty()));
    }

    @Test
    public void loadDefaultWithFromPresetsAfterFailedRESTInfoFetching() {
        assertThat(presets.getWithFrom(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(WithFromPresetsConfig.WITH_FROM_DB_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getWithFrom(), p -> p.getProperty(PresetItem.Property.NAME.getKey())),
                is(empty()));
    }

    private <T> List<T> extractPresetValues(List<PresetItem> presets, Function<PresetItem, T> extractor) {
        return presets.stream().map(extractor).collect(Collectors.toList());
    }

}
