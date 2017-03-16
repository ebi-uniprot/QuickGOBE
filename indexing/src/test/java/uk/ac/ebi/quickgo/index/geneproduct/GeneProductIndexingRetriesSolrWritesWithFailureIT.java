package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.index.annotation.AnnotationDocumentWriteRetryHelper.SolrResponse;
import uk.ac.ebi.quickgo.index.annotation.AnnotationIndexingConfig;
import uk.ac.ebi.quickgo.index.common.JobTestRunnerConfig;
import uk.ac.ebi.quickgo.index.common.SolrServerWriter;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationConfig.ANNOTATION_INDEXING_JOB_NAME;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationConfig.ANNOTATION_INDEXING_STEP_NAME;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationDocumentWriteRetryHelper.stubSolrWriteResponses;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationDocumentWriteRetryHelper.validateWriteAttempts;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductConfig.GENE_PRODUCT_INDEXING_JOB_NAME;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductConfig.GENE_PRODUCT_INDEXING_STEP_NAME;

/**
 * <p>Tests whether Spring Batch's retry + backoff policy works as expected. This class simulates gene product indexing
 * encountering two Solr remote host exceptions occurring (e.g., perhaps Solr becomes overloaded during indexing),
 * subsequently retrying the writes. However, since the maximum number of retries (see application.properties)
 * is exhausted, retrying eventually fails.
 *
 * <p>The mocked behaviour of the "sometimes erroneous" Solr instance has been mocked in {@link RetryConfig}.
 *
 * Created 22/04/16
 * @author Edd
 */
@ActiveProfiles(profiles = {"embeddedServer", "tooManySolrRemoteHostErrors"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {GeneProductConfig.class, JobTestRunnerConfig.class,
                GeneProductIndexingRetriesSolrWritesWithFailureIT.RetryConfig.class},
        loader = SpringApplicationContextLoader.class)
public class GeneProductIndexingRetriesSolrWritesWithFailureIT {

    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ItemWriter<GeneProductDocument> geneProductRepositoryWriter;

    @Captor
    private ArgumentCaptor<List<AnnotationDocument>> argumentCaptor;

    private static final List<SolrResponse> SOLR_RESPONSES = asList(
            SolrResponse.OK,                // simulate writing first chunk (size 2: both valid)
            SolrResponse.REMOTE_EXCEPTION,  // error
            SolrResponse.OK,                // simulate writing second chunk (size 2: both valid)
            SolrResponse.OK,                // simulate writing second chunk (size 1: only 1 valid document in chunk)
            SolrResponse.REMOTE_EXCEPTION,  // error
            SolrResponse.REMOTE_EXCEPTION,  // too many errors -- indexing fails
            SolrResponse.OK);               // never called

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void tooManyRetriesAndFailedIndexingJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getJobInstance().getJobName(), is(GENE_PRODUCT_INDEXING_JOB_NAME));

        List<StepExecution> jobsSingleStepAsList = jobExecution.getStepExecutions()
                .stream()
                .filter(step -> step.getStepName().equals(GENE_PRODUCT_INDEXING_STEP_NAME))
                .collect(Collectors.toList());
        assertThat(jobsSingleStepAsList, hasSize(1));

        StepExecution indexingStep = jobsSingleStepAsList.get(0);

        assertThat(indexingStep.getReadCount(), is(8));
        assertThat(indexingStep.getReadSkipCount(), is(0));
        assertThat(indexingStep.getProcessSkipCount(), is(2));
        assertThat(indexingStep.getWriteSkipCount(), is(0));
        assertThat(indexingStep.getWriteCount(), is(5));

        verify(geneProductRepositoryWriter, times(6)).write(argumentCaptor.capture());
        List<List<AnnotationDocument>> docsSentToBeWritten = argumentCaptor.getAllValues();
        validateWriteAttempts(SOLR_RESPONSES, docsSentToBeWritten);

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
        ItemWriter<AnnotationDocument> geneProductSolrServerWriter() throws Exception {
            ItemWriter<AnnotationDocument> mockItemWriter = mock(ItemWriter.class);

            stubSolrWriteResponses(SOLR_RESPONSES)
                    .when(mockItemWriter).write(any());

            return mockItemWriter;
        }
    }

    @Bean
    ItemWriter<AnnotationDocument> geneProductSolrServerWriter() {
        return new SolrServerWriter<>(annotationTemplate.getSolrClient());
    }
}
