package uk.ac.ebi.quickgo.client.service.loader.presets.read;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PresetItems;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.assignedby.AssignedByPresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.taxon.TaxonPresetsConfig;

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
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.ac.ebi.quickgo.client.service.loader.presets.read.MockPresetDataConfig.FAILED_FETCHING;
import static uk.ac.ebi.quickgo.client.service.loader.presets.read.MockPresetDataConfig.UNIPROT_KB;

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
@ActiveProfiles(profiles = FAILED_FETCHING)
public class PresetsFailedRelevancyFetchingIT {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private CompositePresetImpl preset;

    @Test
    public void loadDefaultAssignedByPresetsAfterFailedRESTInfoFetching() throws Exception {
        assertThat(preset.getAssignedBy().getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(AssignedByPresetsConfig.ASSIGNED_BY_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(preset.getAssignedBy(), PresetItem::getName),
                contains(UNIPROT_KB));
    }

    @Test
    public void loadDefaultTaxonPresetsAfterFailedRESTInfoFetching() throws Exception {
        assertThat(preset.getTaxons().getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(TaxonPresetsConfig.TAXON_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(preset.getTaxons(), PresetItem::getName),
                is(empty()));
    }

    private <T> List<T> extractPresetValues(PresetItems presets, Function<PresetItem, T> extractor) {
        return presets.getPresets().stream().map(extractor).collect(Collectors.toList());
    }

}