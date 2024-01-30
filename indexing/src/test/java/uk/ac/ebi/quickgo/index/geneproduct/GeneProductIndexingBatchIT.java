package uk.ac.ebi.quickgo.index.geneproduct;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.index.common.JobTestRunnerConfig;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * Tests whether Spring Batch is correctly wired up to run the Gene product indexing.
 */
@ExtendWith(TemporarySolrDataStore.class)
@ActiveProfiles(profiles = {"embeddedServer"})
@SpringBootTest(classes = {GeneProductConfig.class, JobTestRunnerConfig.class})
class GeneProductIndexingBatchIT {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private GeneProductRepository geneProductRepository;

    @BeforeEach
    void setUp() {
        geneProductRepository.deleteAll();
    }

    @Test
    void successfulJobRun() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        BatchStatus status = jobExecution.getStatus();
        assertThat(status, is(BatchStatus.COMPLETED));

        StepExecution indexingStep = getStepByName(GeneProductConfig.GENE_PRODUCT_INDEXING_STEP_NAME, jobExecution);
        assertThat(indexingStep.getReadCount(), is(7));
        assertThat(indexingStep.getReadSkipCount(), is(1));
        assertThat(indexingStep.getProcessSkipCount(), is(1));
        assertThat(indexingStep.getWriteCount(), is(6));

        Collection<GeneProductDocument> gpDocs = convertToCollection(geneProductRepository.findAll());

        assertThat(gpDocs, hasSize(6));
        assertThat(extractIdsFromGPDocs(gpDocs),
                containsInAnyOrder("A0A001",
                        "A0A009EQL3",
                        "EBI-10043123",
                        "EBI-10043549",
                        "URS0000000005_77133",
                        "URS0000000017_77133"));
    }

    private StepExecution getStepByName(String stepName, JobExecution jobExecution) {
        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            if (stepExecution.getStepName().equals(stepName)) {
                return stepExecution;
            }
        }

        throw new IllegalArgumentException("Step name not recognized: " + stepName);
    }

    private Collection<GeneProductDocument> convertToCollection(Iterable<GeneProductDocument> docs) {
        return Lists.newArrayList(docs);
    }

    private Set<String> extractIdsFromGPDocs(Collection<GeneProductDocument> gpDocs) {
        return gpDocs.stream()
                .map(gpDoc -> gpDoc.id)
                .collect(Collectors.toSet());
    }
}