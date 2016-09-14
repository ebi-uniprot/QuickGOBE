package uk.ac.ebi.quickgo.client.presets.read;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.presets.read.assignedby.AssignedByPresetsConfig;
import uk.ac.ebi.quickgo.client.presets.read.evidence.EvidencePresetsConfig;
import uk.ac.ebi.quickgo.client.presets.read.reference.ReferencePresetsConfig;
import uk.ac.ebi.quickgo.client.presets.read.withFrom.WithFromPresetsConfig;

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
 * Tests the population of preset information.
 *
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

    @Test
    public void loadEvidencesPresets() throws Exception {
        assertThat(preset.evidences.getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(EvidencePresetsConfig.EVIDENCE_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(preset.evidences.getPresets(), hasSize(22));

        PresetItem firstPresetItem = preset.evidences.getPresets().stream().findFirst().orElse(null);
        assertThat(firstPresetItem.getName(), is(PRESET_ECO_32.getName()));
        assertThat(firstPresetItem.getId(), is(PRESET_ECO_32.getId()));
        assertThat(firstPresetItem.getDescription(), is(PRESET_ECO_32.getDescription()));
        assertThat(firstPresetItem.getRelevancy(), is(PRESET_ECO_32.getRelevancy()));
    }

    @Test
    public void loadWithFromPresets() throws Exception {
        assertThat(preset.withFrom.getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(WithFromPresetsConfig.WITH_FROM_DB_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(preset.withFrom.getPresets(), hasSize(7));

        PresetItem lastPresetItem =
                preset.withFrom.getPresets().stream().reduce((first, second) -> second).orElse(null);
        assertThat(lastPresetItem.getName(), is(PRESET_DICTY_BASE.getName()));
        assertThat(lastPresetItem.getId(), is(PRESET_DICTY_BASE.getId()));
        assertThat(lastPresetItem.getDescription(), is(PRESET_DICTY_BASE.getDescription()));
        assertThat(lastPresetItem.getRelevancy(), is(PRESET_DICTY_BASE.getRelevancy()));
    }
}