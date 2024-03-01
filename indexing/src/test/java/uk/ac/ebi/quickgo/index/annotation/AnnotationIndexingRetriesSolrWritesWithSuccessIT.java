package uk.ac.ebi.quickgo.index.annotation;

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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.index.common.JobTestRunnerConfig;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static uk.ac.ebi.quickgo.index.DocumentWriteRetryHelper.*;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationConfig.ANNOTATION_INDEXING_JOB_NAME;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationConfig.ANNOTATION_INDEXING_STEP_NAME;


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
@ExtendWith(TemporarySolrDataStore.class)
@ActiveProfiles(profiles = {"embeddedServer", "twoSolrRemoteHostErrors"})
@SpringBootTest(classes = {AnnotationIndexingConfig.class, JobTestRunnerConfig.class,
                AnnotationIndexingRetriesSolrWritesWithSuccessIT.RetryConfig.class})
class AnnotationIndexingRetriesSolrWritesWithSuccessIT {
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

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void twoRetriesAndSuccessfulIndexingJob() throws Exception {
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

        verify(annotationSolrServerWriter, times(6)).write(new Chunk<>(argumentCaptor.capture()));
        List<List<AnnotationDocument>> docsSentToBeWritten = argumentCaptor.getAllValues();
        validateWriteAttempts(SOLR_RESPONSES, docsSentToBeWritten, d -> d.geneProductId);

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
