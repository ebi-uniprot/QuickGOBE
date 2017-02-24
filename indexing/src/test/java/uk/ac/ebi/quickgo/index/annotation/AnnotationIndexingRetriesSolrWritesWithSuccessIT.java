package uk.ac.ebi.quickgo.index.annotation;

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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.index.annotation.AnnotationDocumentWriteRetryHelper.SolrResponse;
import uk.ac.ebi.quickgo.index.annotation.coterms.CoTermTemporaryDataStore;
import uk.ac.ebi.quickgo.index.common.JobTestRunnerConfig;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationConfig.ANNOTATION_INDEXING_JOB_NAME;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationConfig.ANNOTATION_INDEXING_STEP_NAME;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationDocumentWriteRetryHelper.stubSolrWriteResponses;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationDocumentWriteRetryHelper.validateWriteAttempts;

/**
 * <p>Tests whether Spring Batch's retry + backoff policy works as expected. This class simulates annotation indexing
 * encountering two Solr remote host exceptions occurring (e.g., perhaps Solr becomes overloaded during indexing),
 * subsequently retrying the writes, with success.
 * <p>
 * <p>The mocked behaviour of the "sometimes erroneous" Solr instance has been mocked in {@link RetryConfig}.
 * <p>
 * Created 22/04/16
 *
 * @author Edd
 */
@ActiveProfiles(profiles = {"embeddedServer", "twoSolrRemoteHostErrors"})
//@ActiveProfiles(profiles = {"embeddedServer"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {AnnotationConfig.class, JobTestRunnerConfig.class, CoTermTemporaryDataStore.Config.class,
                AnnotationIndexingRetriesSolrWritesWithSuccessIT.RetryConfig.class},
        loader = SpringApplicationContextLoader.class)
public class AnnotationIndexingRetriesSolrWritesWithSuccessIT {

    @ClassRule
    public static final CoTermTemporaryDataStore coTermsDataStore = new CoTermTemporaryDataStore();

    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ItemWriter<AnnotationDocument> annotationSolrServerWriter;

    @Captor
    private ArgumentCaptor<List<AnnotationDocument>> argumentCaptor;

    private static final List<SolrResponse> SOLR_RESPONSES = asList(
            SolrResponse.OK,                // simulate writing first chunk (size 2: both valid)
            SolrResponse.REMOTE_EXCEPTION,  // error
            SolrResponse.OK,                // simulate writing second chunk (size 2: both valid)
            SolrResponse.REMOTE_EXCEPTION,  // error
            SolrResponse.OK,                // simulate writing third chunk (size 1: only 1 valid document in chunk)
            SolrResponse.OK,                // simulate writing fourth chunk (size 1: only 1 valid document in chunk)
            SolrResponse.OK);               // never called

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void twoRetriesAndSuccessfulIndexingJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getJobInstance().getJobName(), is(ANNOTATION_INDEXING_JOB_NAME));

        List<StepExecution> jobsSingleStepAsList = jobExecution.getStepExecutions()
                .stream()
                .filter(step -> step.getStepName().equals(ANNOTATION_INDEXING_STEP_NAME))
                .collect(Collectors.toList());
        assertThat(jobsSingleStepAsList, hasSize(1));

        StepExecution indexingStep = jobsSingleStepAsList.get(0);

        assertThat(indexingStep.getReadCount(), is(8));
        assertThat(indexingStep.getReadSkipCount(), is(0));
        assertThat(indexingStep.getProcessSkipCount(), is(2));
        assertThat(indexingStep.getWriteSkipCount(), is(0));

        assertThat(indexingStep.getWriteCount(), is(6));

        verify(annotationSolrServerWriter, times(6)).write(argumentCaptor.capture());
        List<List<AnnotationDocument>> docsSentToBeWritten = argumentCaptor.getAllValues();
        validateWriteAttempts(SOLR_RESPONSES, docsSentToBeWritten);

        BatchStatus status = jobExecution.getStatus();
        assertThat(status, is(BatchStatus.COMPLETED));
    }


    @Profile("twoSolrRemoteHostErrors")
    @Configuration
    public static class RetryConfig {

        @Bean
        @Primary
        @SuppressWarnings(value = "unchecked")
        ItemWriter<AnnotationDocument> annotationSolrServerWriter() throws Exception {
            ItemWriter<AnnotationDocument> mockItemWriter = mock(ItemWriter.class);

            stubSolrWriteResponses(SOLR_RESPONSES)
                    .when(mockItemWriter).write(any());

            return mockItemWriter;
        }
    }
}
