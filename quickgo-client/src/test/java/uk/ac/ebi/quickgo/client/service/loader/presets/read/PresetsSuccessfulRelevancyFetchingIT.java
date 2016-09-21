package uk.ac.ebi.quickgo.client.service.loader.presets.read;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.PresetItems;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.assignedby.AssignedByPresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.evidence.EvidencePresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.geneproduct.GeneProductPresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.reference.ReferencePresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.slimsets.GOSlimSetPresetsConfig;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.ac.ebi.quickgo.client.service.loader.presets.read.MockPresetDataConfig.*;

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
    private CompositePresetImpl preset;

    @Test
    public void loadAssignedByPresetsAfterSuccessfulRESTInfoFetching() throws Exception {
        assertThat(preset.getAssignedBy().getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(AssignedByPresetsConfig.ASSIGNED_BY_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(preset.getAssignedBy(), PresetItem::getName),
                contains(UNIPROT_KB, ENSEMBL));
    }

    @Test
    @DirtiesContext
    public void loadGenericReferencePresetsAfterSuccessfulRESTInfoFetching() throws Exception {
        assertThat(preset.getReferences().getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(ReferencePresetsConfig.CORE_REFERENCE_DB_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(preset.getReferences(), PresetItem::getName),
                contains(DOI, REACTOME));
    }

    @Test
    @DirtiesContext
    public void loadSpecificReferencePresetsAfterSuccessfulRESTInfoFetching() throws Exception {
        assertThat(preset.getReferences().getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(ReferencePresetsConfig.SPECIFIC_REFERENCE_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(preset.getReferences(), PresetItem::getName),
                is(GO_REFS_FROM_RESOURCE));
    }

    @Test
    public void loadEvidencesPresets() throws Exception {
        assertThat(preset.getEvidences().getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(EvidencePresetsConfig.EVIDENCE_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(preset.getEvidences().getPresets(), hasSize(22));

        PresetItem firstPresetItem = extractFirstPreset(preset.getEvidences());
        assertThat(firstPresetItem.getName(), is(PRESET_ECO_32.getName()));
        assertThat(firstPresetItem.getId(), is(PRESET_ECO_32.getId()));
        assertThat(firstPresetItem.getDescription(), is(PRESET_ECO_32.getDescription()));
        assertThat(firstPresetItem.getRelevancy(), is(PRESET_ECO_32.getRelevancy()));
    }

    @Test
    public void loadWithFromPresets() throws Exception {
        assertThat(preset.getWithFrom().getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(WithFromPresetsConfig.WITH_FROM_DB_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(preset.getWithFrom().getPresets(), hasSize(7));

        PresetItem lastPresetItem = extractLastPreset(preset.getWithFrom());
        assertThat(lastPresetItem.getName(), is(PRESET_DICTY_BASE.getName()));
        assertThat(lastPresetItem.getId(), is(PRESET_DICTY_BASE.getId()));
        assertThat(lastPresetItem.getDescription(), is(PRESET_DICTY_BASE.getDescription()));
        assertThat(lastPresetItem.getRelevancy(), is(PRESET_DICTY_BASE.getRelevancy()));
    }

    @Test
    public void loadGeneProductPresets() throws Exception {
        assertThat(preset.getGeneProducts().getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(GeneProductPresetsConfig.GENE_PRODUCT_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(preset.getGeneProducts().getPresets(), hasSize(5));

        PresetItem firstPresetItem = extractFirstPreset(preset.getGeneProducts());
        assertThat(firstPresetItem.getName(), is(PRESET_BHF_UCL.getName()));
        assertThat(firstPresetItem.getId(), is(PRESET_BHF_UCL.getId()));
        assertThat(firstPresetItem.getDescription(), is(PRESET_BHF_UCL.getDescription()));
        assertThat(firstPresetItem.getRelevancy(), is(PRESET_BHF_UCL.getRelevancy()));
    }

    @Test
    public void loadGOSlimSetPresets() throws Exception {
        assertThat(preset.getGoSlimSets().getPresets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(GOSlimSetPresetsConfig.GO_SLIM_SET_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(preset.getGoSlimSets().getPresets(), hasSize(3));

        List<PresetItem> presetItems = extractPresets(preset.getGoSlimSets());
        assertThat(presetItems.get(0), is(equalTo(PRESET_GO_SLIM_METAGENOMICS)));
        assertThat(presetItems.get(1), is(equalTo(PRESET_GO_SLIM_POMBE)));
        assertThat(presetItems.get(2), is(equalTo(PRESET_GO_SLIM_SYNAPSE)));
    }

    private <T> List<T> extractPresetValues(PresetItems presets, Function<PresetItem, T> extractor) {
        return presets.getPresets().stream().map(extractor).collect(Collectors.toList());
    }

    private List<PresetItem> extractPresets(PresetItems presets) {
        return presets.getPresets().stream().collect(Collectors.toList());
    }

    private PresetItem extractFirstPreset(PresetItems presets) {
        return presets.getPresets().stream().findFirst().orElse(null);
    }

    private PresetItem extractLastPreset(PresetItems presets) {
        return presets.getPresets().stream().reduce((first, second) -> second).orElse(null);
    }
}