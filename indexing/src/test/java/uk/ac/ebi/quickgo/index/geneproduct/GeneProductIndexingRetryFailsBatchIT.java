package uk.ac.ebi.quickgo.index.geneproduct;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.ebi.quickgo.common.store.SolrContainerTestSetup;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.index.DocumentWriteRetryHelper;
import uk.ac.ebi.quickgo.index.common.BatchConfig;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static uk.ac.ebi.quickgo.index.DocumentWriteRetryHelper.stubSolrWriteResponses;
import static uk.ac.ebi.quickgo.index.DocumentWriteRetryHelper.validateWriteAttempts;

/**
 * Tests whether Spring Batch is correctly wired up to run the Gene product indexing.
 */
@ActiveProfiles(profiles = {"embeddedServer", "tooManySolrRemoteHostErrors"})
@SpringBootTest(classes = {GeneProductConfig.class, GeneProductIndexingRetryFailsBatchIT
                .RetryConfig.class, DefaultBatchConfiguration.class, BatchConfig.class})
@SpringBatchTest
class GeneProductIndexingRetryFailsBatchIT extends SolrContainerTestSetup {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ItemWriter<GeneProductDocument> geneProductRepositoryWriter;

    @Captor
    private ArgumentCaptor<Chunk<GeneProductDocument>> argumentCaptor;

    private static final List<DocumentWriteRetryHelper.SolrResponse> SOLR_RESPONSES = asList(
            DocumentWriteRetryHelper.SolrResponse.REMOTE_EXCEPTION,// error
            DocumentWriteRetryHelper.SolrResponse.REMOTE_EXCEPTION,// error
            DocumentWriteRetryHelper.SolrResponse.REMOTE_EXCEPTION,// too many errors -- indexing fails
            DocumentWriteRetryHelper.SolrResponse.OK);               // never called

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void successfulJobRun() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        StepExecution indexingStep = jobExecution.getStepExecutions().stream()
          .filter(se -> se.getStepName().equals(GeneProductConfig.GENE_PRODUCT_INDEXING_STEP_NAME))
          .findAny().orElseThrow();
        assertThat(indexingStep.getReadCount(), is(2L));
        assertThat(indexingStep.getReadSkipCount(), is(0L));
        assertThat(indexingStep.getProcessSkipCount(), is(0L));
        assertThat(indexingStep.getWriteCount(), is(0L));

        verify(geneProductRepositoryWriter, times(2)).write(argumentCaptor.capture());
        List<List<GeneProductDocument>> docsSentToBeWritten = argumentCaptor.getAllValues().stream().map(Chunk::getItems).toList();
        validateWriteAttempts(SOLR_RESPONSES, docsSentToBeWritten, d -> d.id);

        BatchStatus status = jobExecution.getStatus();
        assertThat(status, is(BatchStatus.FAILED));
    }

    @Profile("tooManySolrRemoteHostErrors")
    public static class RetryConfig {

        private static final String HOST = "http://www.myhost.com";
        private static final String MESSAGE = "Looks like the host is not reachable?!";
        private static final int CODE = 1;

        @Bean
        @Primary
        @SuppressWarnings(value = "unchecked")
        ItemWriter<GeneProductDocument> geneProductRepositoryWriter() throws Exception {
            ItemWriter<GeneProductDocument> mockItemWriter = mock(ItemWriter.class);

            stubSolrWriteResponses(SOLR_RESPONSES)
                    .when(mockItemWriter).write(any());

            return mockItemWriter;
        }
    }

}
