package uk.ac.ebi.quickgo.client.service.loader.presets;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.service.loader.presets.assignedby.AssignedByPresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.evidence.EvidencePresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.geneproduct.GeneProductPresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.qualifier.QualifierPresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.reference.ReferencePresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.slimsets.GOSlimSetPresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.taxon.TaxonPresetsConfig;
import uk.ac.ebi.quickgo.client.service.loader.presets.withFrom.WithFromPresetsConfig;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsIterableContainingInOrder;
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
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

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
@ActiveProfiles(profiles = MockPresetDataConfig.SUCCESSFUL_FETCHING)
public class PresetsSuccessfulRelevancyFetchingIT {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private CompositePreset presets;

    @Test
    public void loadAssignedByPresetsAfterSuccessfulRESTInfoFetching() throws Exception {
        assertThat(presets.getAssignedBy(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(AssignedByPresetsConfig.ASSIGNED_BY_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getAssignedBy(), PresetItem::getName),
                IsIterableContainingInOrder.contains(MockPresetDataConfig.UNIPROT_KB, MockPresetDataConfig.ENSEMBL));
    }

    @Test
    @DirtiesContext
    public void loadGenericReferencePresetsAfterSuccessfulRESTInfoFetching() throws Exception {
        assertThat(presets.getReferences(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(ReferencePresetsConfig.CORE_REFERENCE_DB_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getReferences(), PresetItem::getName),
                IsIterableContainingInOrder.contains(MockPresetDataConfig.DOI, MockPresetDataConfig.REACTOME));
    }

    @Test
    @DirtiesContext
    public void loadSpecificReferencePresetsAfterSuccessfulRESTInfoFetching() throws Exception {
        assertThat(presets.getReferences(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(ReferencePresetsConfig.SPECIFIC_REFERENCE_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getReferences(), PresetItem::getName),
                CoreMatchers.is(MockPresetDataConfig.GO_REFS_FROM_RESOURCE));
    }

    @Test
    public void loadEvidencesPresets() throws Exception {
        assertThat(presets.getEvidences(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(EvidencePresetsConfig.EVIDENCE_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(presets.getEvidences(), hasSize(22));

        PresetItem firstPresetItem = extractFirstPreset(presets.getEvidences());
        assertThat(firstPresetItem.getName(), CoreMatchers.is(MockPresetDataConfig.PRESET_ECO_32.getName()));
        assertThat(firstPresetItem.getId(), CoreMatchers.is(MockPresetDataConfig.PRESET_ECO_32.getId()));
        assertThat(firstPresetItem.getDescription(),
                CoreMatchers.is(MockPresetDataConfig.PRESET_ECO_32.getDescription()));
        assertThat(firstPresetItem.getRelevancy(), CoreMatchers.is(MockPresetDataConfig.PRESET_ECO_32.getRelevancy()));
    }

    @Test
    public void loadWithFromPresets() throws Exception {
        assertThat(presets.getWithFrom(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(WithFromPresetsConfig.WITH_FROM_DB_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(presets.getWithFrom(), hasSize(7));

        PresetItem lastPresetItem = extractLastPreset(presets.getWithFrom());
        assertThat(lastPresetItem.getName(), CoreMatchers.is(MockPresetDataConfig.PRESET_DICTY_BASE.getName()));
        assertThat(lastPresetItem.getId(), CoreMatchers.is(MockPresetDataConfig.PRESET_DICTY_BASE.getId()));
        assertThat(lastPresetItem.getDescription(),
                CoreMatchers.is(MockPresetDataConfig.PRESET_DICTY_BASE.getDescription()));
        assertThat(lastPresetItem.getRelevancy(),
                CoreMatchers.is(MockPresetDataConfig.PRESET_DICTY_BASE.getRelevancy()));
    }

    @Test
    public void loadGeneProductPresets() throws Exception {
        assertThat(presets.getGeneProducts(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(GeneProductPresetsConfig.GENE_PRODUCT_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(presets.getGeneProducts(), hasSize(5));

        PresetItem firstPresetItem = extractFirstPreset(presets.getGeneProducts());
        assertThat(firstPresetItem.getName(), CoreMatchers.is(MockPresetDataConfig.PRESET_BHF_UCL.getName()));
        assertThat(firstPresetItem.getId(), CoreMatchers.is(MockPresetDataConfig.PRESET_BHF_UCL.getId()));
        assertThat(firstPresetItem.getDescription(),
                CoreMatchers.is(MockPresetDataConfig.PRESET_BHF_UCL.getDescription()));
        assertThat(firstPresetItem.getRelevancy(), CoreMatchers.is(MockPresetDataConfig.PRESET_BHF_UCL.getRelevancy()));
    }

    @Test
    public void loadGOSlimSetPresets() throws Exception {
        assertThat(presets.getGoSlimSets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(GOSlimSetPresetsConfig.GO_SLIM_SET_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(presets.getGoSlimSets(), hasSize(3));

        List<PresetItem> presetItems = extractPresets(presets.getGoSlimSets());
        assertThat(presetItems.get(0), is(equalTo(MockPresetDataConfig.PRESET_GO_SLIM_METAGENOMICS)));
        assertThat(presetItems.get(1), is(equalTo(MockPresetDataConfig.PRESET_GO_SLIM_POMBE)));
        assertThat(presetItems.get(2), is(equalTo(MockPresetDataConfig.PRESET_GO_SLIM_SYNAPSE)));
    }

    @Test
    public void loadTaxonPresetsAfterSuccessfulRESTInfoFetching() throws Exception {
        assertThat(presets.getTaxons(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(TaxonPresetsConfig.TAXON_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getTaxons(), PresetItem::getName),
                IsIterableContainingInOrder
                        .contains(MockPresetDataConfig.TAXON_HUMAN, MockPresetDataConfig.TAXON_BACTERIA));
    }

    @Test
    public void loadQualifierPresetsAfterSuccessfulRESTInfoFetching() throws Exception {
        assertThat(presets.getQualifiers(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(QualifierPresetsConfig.QUALIFIER_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getQualifiers(), PresetItem::getName),
                IsIterableContainingInOrder
                        .contains(MockPresetDataConfig.QUALIFIER_ENABLES, MockPresetDataConfig.QUALIFIER_INVOLVED_IN));
    }

    private <T> List<T> extractPresetValues(List<PresetItem> presets, Function<PresetItem, T> extractor) {
        return presets.stream().map(extractor).collect(Collectors.toList());
    }

    private List<PresetItem> extractPresets(List<PresetItem> presets) {
        return presets.stream().collect(Collectors.toList());
    }

    private PresetItem extractFirstPreset(List<PresetItem> presets) {
        return presets.stream().findFirst().orElse(null);
    }

    private PresetItem extractLastPreset(List<PresetItem> presets) {
        return presets.stream().reduce((first, second) -> second).orElse(null);
    }
}