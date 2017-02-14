package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.index.annotation.coterms.CoTermTemporaryDataStore;
import uk.ac.ebi.quickgo.index.common.JobTestRunnerConfig;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationConfig.ANNOTATION_INDEXING_JOB_NAME;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationConfig.ANNOTATION_INDEXING_STEP_NAME;

/**
 * <p>Tests whether Spring Batch's retry + backoff policy works as expected. This class simulates annotation indexing
 * encountering two Solr remote host exceptions occurring (e.g., perhaps Solr becomes overloaded during indexing),
 * subsequently retrying the writes, with success.
 *
 * <p>The mocked behaviour of the "sometimes erroneous" Solr instance has been mocked in {@link RetryConfig}.
 *
 * Created 22/04/16
 * @author Edd
 */
@ActiveProfiles(profiles = {"embeddedServer", "twoSolrRemoteHostErrors"})
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
    private AnnotationRepository annotationRepository;

    @Before
    public void setUp() {
        annotationRepository.deleteAll();
    }

    @Autowired
    private ItemWriter<AnnotationDocument> annotationSolrServerWriter;

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
        verify(annotationSolrServerWriter, times(6)).write(any());

        BatchStatus status = jobExecution.getStatus();
        assertThat(status, is(BatchStatus.COMPLETED));
    }

    @Profile("twoSolrRemoteHostErrors")
    @Configuration
    public static class RetryConfig {

        private static final String HOST = "http://www.myhost.com";
        private static final String MESSAGE = "Looks like the host is not reachable?!";
        private static final int CODE = 1;

        @Bean @Primary
        @SuppressWarnings(value = "unchecked")
        ItemWriter<AnnotationDocument> annotationSolrServerWriter() throws Exception {
            ItemWriter<AnnotationDocument> mockItemWriter = mock(ItemWriter.class);

            doNothing()             // simulate writing first chunk
                    .doThrow(new HttpSolrClient.RemoteSolrException(HOST, CODE, MESSAGE, null))    // error
                    .doNothing()    // simulate writing second chunk
                    .doNothing()    // simulate writing third chunk
                    .doThrow(new HttpSolrClient.RemoteSolrException(HOST, CODE, MESSAGE, null))    // error
                    .doNothing()    // simulate writing fourth chunk
                    .when(mockItemWriter).write(any());

            return mockItemWriter;
        }
    }
}
