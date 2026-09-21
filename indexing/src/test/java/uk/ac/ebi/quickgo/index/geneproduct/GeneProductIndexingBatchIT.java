package uk.ac.ebi.quickgo.index.geneproduct;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ebi.quickgo.common.store.SolrContainerTestSetup;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.index.common.BatchConfig;

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
@SpringBootTest(classes = {GeneProductConfig.class, DefaultBatchConfiguration.class, BatchConfig.class})
@SpringBatchTest
class GeneProductIndexingBatchIT extends SolrContainerTestSetup {
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

        StepExecution indexingStep = jobExecution.getStepExecutions().stream()
          .filter(se -> se.getStepName().equals(GeneProductConfig.GENE_PRODUCT_INDEXING_STEP_NAME))
          .findAny().orElseThrow();
        assertThat(indexingStep.getReadCount(), is(7L));
        assertThat(indexingStep.getReadSkipCount(), is(1L));
        assertThat(indexingStep.getProcessSkipCount(), is(1L));
        assertThat(indexingStep.getWriteCount(), is(6L));

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

    private Collection<GeneProductDocument> convertToCollection(Iterable<GeneProductDocument> docs) {
        return Lists.newArrayList(docs);
    }

    private Set<String> extractIdsFromGPDocs(Collection<GeneProductDocument> gpDocs) {
        return gpDocs.stream()
                .map(gpDoc -> gpDoc.id)
                .collect(Collectors.toSet());
    }
}
