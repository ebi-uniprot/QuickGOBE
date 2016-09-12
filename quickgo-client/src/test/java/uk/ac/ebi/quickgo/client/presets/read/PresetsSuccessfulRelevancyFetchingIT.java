package uk.ac.ebi.quickgo.client.presets.read;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.presets.read.assignedby.AssignedByPresetsConfig;
import uk.ac.ebi.quickgo.client.presets.read.reference.ReferencePresetsConfig;

import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.ac.ebi.quickgo.client.presets.read.MockPresetDataConfig.*;

/**
 * Created 31/08/16
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {PresetsConfig.class, MockPresetDataConfig.class, JobTestRunnerConfig.class},
        loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
@ActiveProfiles(profiles = SUCCESSFUL_FETCHING)
public class PresetsSuccessfulRelevancyFetchingIT {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private CompositePreset preset;

    @Test
    public void loadAssignedByPresetsAfterSuccessfulRESTInfoFetching() throws Exception {
        assertThat(preset.assignedBy.getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(AssignedByPresetsConfig.ASSIGNED_BY_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                preset.assignedBy.getPresets().stream().map(PresetItem::getName).collect(Collectors.toList()),
                contains(UNIPROT_KB, ENSEMBL));
    }

    @Test
    @DirtiesContext
    public void loadGenericReferencePresetsAfterSuccessfulRESTInfoFetching() throws Exception {
        assertThat(preset.references.getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(ReferencePresetsConfig.CORE_REFERENCE_DB_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                preset.references.getPresets().stream().map(PresetItem::getName).collect(Collectors.toList()),
                contains(DOI, REACTOME));
    }

    @Test
    @DirtiesContext
    public void loadSpecificReferencePresetsAfterSuccessfulRESTInfoFetching() throws Exception {
        assertThat(preset.references.getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(ReferencePresetsConfig.SPECIFIC_REFERENCE_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                preset.references.getPresets().stream().map(PresetItem::getName).collect(Collectors.toList()),
                is(GO_REFS_FROM_RESOURCE));
    }
}