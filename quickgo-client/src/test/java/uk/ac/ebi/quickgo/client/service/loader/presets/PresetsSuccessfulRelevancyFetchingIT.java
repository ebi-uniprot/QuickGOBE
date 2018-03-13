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
import static uk.ac.ebi.quickgo.client.model.presets.PresetItem.Property.*;

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
@ActiveProfiles(profiles = {MockPresetDataConfig.SUCCESSFUL_FETCHING, MockPresetDataConfig.NO_SEARCH_ATTRIBUTES})
public class PresetsSuccessfulRelevancyFetchingIT {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private CompositePreset presets;

    @Test
    public void loadAssignedByPresetsAfterSuccessfulRESTInfoFetching() {
        assertThat(presets.getAssignedBy(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(AssignedByPresetsConfig.ASSIGNED_BY_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getAssignedBy(), p -> p.getProperty(NAME)),
                IsIterableContainingInOrder.contains(MockPresetDataConfig.UNIPROT_KB, MockPresetDataConfig.ENSEMBL));
    }

    @Test
    @DirtiesContext
    public void loadGenericReferencePresetsAfterSuccessfulRESTInfoFetching() {
        assertThat(presets.getReferences(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(ReferencePresetsConfig.CORE_REFERENCE_DB_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getReferences(), p -> p.getProperty(NAME)),
                IsIterableContainingInOrder.contains(MockPresetDataConfig.REACTOME, MockPresetDataConfig.DOI));
    }

    @Test
    @DirtiesContext
    public void loadSpecificReferencePresetsAfterSuccessfulRESTInfoFetching() {
        assertThat(presets.getReferences(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(ReferencePresetsConfig.SPECIFIC_REFERENCE_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getReferences(), p -> p.getProperty(NAME)),
                CoreMatchers.is(MockPresetDataConfig.GO_REFS_FROM_RESOURCE));
    }

    @Test
    public void loadEvidencesPresets() {
        assertThat(presets.getEvidences(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(EvidencePresetsConfig.EVIDENCE_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(presets.getEvidences(), hasSize(22));

        PresetItem firstPresetItem = extractFirstPreset(presets.getEvidences());
        assertThat(firstPresetItem.getProperty(NAME), is(MockPresetDataConfig.PRESET_ECO_32.getProperty(NAME)));
        assertThat(firstPresetItem.getProperty(ID), is(MockPresetDataConfig.PRESET_ECO_32.getProperty(ID)));
        assertThat(firstPresetItem.getProperty(DESCRIPTION),
                CoreMatchers.is(MockPresetDataConfig.PRESET_ECO_32.getProperty(DESCRIPTION)));
        assertThat(firstPresetItem.getRelevancy(), is(MockPresetDataConfig.PRESET_ECO_32.getRelevancy()));
    }

    @Test
    public void loadWithFromPresetsAfterSuccessfulRESTInfoFetching() {
        assertThat(presets.getWithFrom(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(WithFromPresetsConfig.WITH_FROM_DB_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getWithFrom(), p -> p.getProperty(NAME)),
                IsIterableContainingInOrder
                        .contains(MockPresetDataConfig.UNIPROT_KB, MockPresetDataConfig.PANTHER));
    }

    @Test
    public void loadGeneProductPresets() {
        assertThat(presets.getGeneProducts(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(GeneProductPresetsConfig.GENE_PRODUCT_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(presets.getGeneProducts(), hasSize(5));

        PresetItem firstPresetItem = extractFirstPreset(presets.getGeneProducts());
        assertThat(firstPresetItem.getProperty(NAME), is(MockPresetDataConfig.PRESET_BHF_UCL.getProperty(NAME)));
        assertThat(firstPresetItem.getProperty(ID), is(MockPresetDataConfig.PRESET_BHF_UCL.getProperty(ID)));
        assertThat(firstPresetItem.getProperty(DESCRIPTION),
                is(MockPresetDataConfig.PRESET_BHF_UCL.getProperty(DESCRIPTION)));
        assertThat(firstPresetItem.getRelevancy(), is(MockPresetDataConfig.PRESET_BHF_UCL.getRelevancy()));
    }

    @Test
    public void loadGOSlimSetPresets() {
        assertThat(presets.getGoSlimSets(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(GOSlimSetPresetsConfig.GO_SLIM_SET_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(presets.getGoSlimSets(), hasSize(4));

        List<PresetItem> presetItems = presets.getGoSlimSets();
        assertThat(presetItems.get(0), is(equalTo(MockPresetDataConfig.PRESET_GO_SLIM_ASPERGILLUS)));
        assertThat(presetItems.get(1), is(equalTo(MockPresetDataConfig.PRESET_GO_SLIM_METAGENOMICS)));
        assertThat(presetItems.get(2), is(equalTo(MockPresetDataConfig.PRESET_GO_SLIM_POMBE)));
        assertThat(presetItems.get(3), is(equalTo(MockPresetDataConfig.PRESET_GO_SLIM_SYNAPSE)));
    }

    @Test
    public void loadTaxonPresets() {
        assertThat(presets.getTaxons(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(TaxonPresetsConfig.TAXON_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        final List<PresetItem> presetItems = presets.getTaxons();
        assertThat(presetItems.get(0), is(equalTo(MockPresetDataConfig.PRESET_TAXON_ARABIDOPSIS)));
        assertThat(presetItems.get(1), is(equalTo(MockPresetDataConfig.PRESET_TAXON_DROSOPHILA)));
    }

    @Test
    public void loadQualifierPresetsAfterSuccessfulRESTInfoFetching() {
        assertThat(presets.getQualifiers(), hasSize(0));

        JobExecution jobExecution =
                jobLauncherTestUtils.launchStep(QualifierPresetsConfig.QUALIFIER_LOADING_STEP_NAME);
        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));
        assertThat(
                extractPresetValues(presets.getQualifiers(), p -> p.getProperty(NAME)),
                IsIterableContainingInOrder
                        .contains(MockPresetDataConfig.QUALIFIER_ENABLES, MockPresetDataConfig.QUALIFIER_INVOLVED_IN));
    }

    private <T> List<T> extractPresetValues(List<PresetItem> presets, Function<PresetItem, T> extractor) {
        return presets.stream().map(extractor).collect(Collectors.toList());
    }

    private PresetItem extractFirstPreset(List<PresetItem> presets) {
        return presets.stream().findFirst().orElse(null);
    }
}
