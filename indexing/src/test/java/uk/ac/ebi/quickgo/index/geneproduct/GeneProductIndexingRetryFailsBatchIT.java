package uk.ac.ebi.quickgo.index.geneproduct;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.index.DocumentWriteRetryHelper;
import uk.ac.ebi.quickgo.index.common.JobTestRunnerConfig;

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
@ExtendWith(TemporarySolrDataStore.class)
@ActiveProfiles(profiles = {"embeddedServer", "tooManySolrRemoteHostErrors"})
@SpringBootTest(classes = {GeneProductConfig.class, JobTestRunnerConfig.class, GeneProductIndexingRetryFailsBatchIT
                .RetryConfig.class})
class GeneProductIndexingRetryFailsBatchIT {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ItemWriter<GeneProductDocument> geneProductRepositoryWriter;

    @Captor
    private ArgumentCaptor<List<GeneProductDocument>> argumentCaptor;

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

        StepExecution indexingStep = getStepByName(GeneProductConfig.GENE_PRODUCT_INDEXING_STEP_NAME, jobExecution);
        assertThat(indexingStep.getReadCount(), is(2));
        assertThat(indexingStep.getReadSkipCount(), is(0));
        assertThat(indexingStep.getProcessSkipCount(), is(0));
        assertThat(indexingStep.getWriteCount(), is(0));

        verify(geneProductRepositoryWriter, times(2)).write(new Chunk<>(argumentCaptor.capture()));
        List<List<GeneProductDocument>> docsSentToBeWritten = argumentCaptor.getAllValues();
        validateWriteAttempts(SOLR_RESPONSES, docsSentToBeWritten, d -> d.id);

        BatchStatus status = jobExecution.getStatus();
        assertThat(status, is(BatchStatus.FAILED));
    }

    private StepExecution getStepByName(String stepName, JobExecution jobExecution) {
        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            if (stepExecution.getStepName().equals(stepName)) {
                return stepExecution;
            }
        }

        throw new IllegalArgumentException("Step name not recognized: " + stepName);
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
