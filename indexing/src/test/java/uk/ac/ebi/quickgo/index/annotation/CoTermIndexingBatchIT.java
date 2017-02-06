package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.index.annotation.coterms.CoTermTemporaryDataStore;
import uk.ac.ebi.quickgo.index.common.JobTestRunnerConfig;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.ac.ebi.quickgo.index.annotation.CoTermIndexingConfig.ANNOTATION_READING_STEP_NAME;
import static uk.ac.ebi.quickgo.index.annotation.CoTermIndexingConfig.COTERM_INDEXING_JOB_NAME;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfig.CO_TERM_ALL_SUMMARIZATION_STEP;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfig.CO_TERM_MANUAL_SUMMARIZATION_STEP;

/**
 * Tests whether Spring Batch is correctly wired up to run the annotation indexing.
 *
 * Created 22/04/16
 * @author Edd
 */
@ActiveProfiles(profiles = {"embeddedServer"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {CoTermIndexingConfig.class, JobTestRunnerConfig.class, CoTermTemporaryDataStore.Config.class},
        loader = SpringApplicationContextLoader.class)
public class CoTermIndexingBatchIT {

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


    @Test
    public void successfulCoTermsOnlyJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getJobInstance().getJobName(), is(COTERM_INDEXING_JOB_NAME));

        List<StepExecution> jobsSingleStepAsList = jobExecution.getStepExecutions()
                                                               .stream()
                                                               .filter(step -> step.getStepName().equals(ANNOTATION_READING_STEP_NAME))
                                                               .collect(Collectors.toList());
        assertThat(jobsSingleStepAsList, hasSize(1));

        StepExecution readingStep = jobsSingleStepAsList.get(0);

        assertThat(readingStep.getReadCount(), is(8));
        assertThat(readingStep.getReadSkipCount(), is(0));
        assertThat(readingStep.getProcessSkipCount(), is(2));
        assertThat(readingStep.getWriteCount(), is(6));

        //Manual
        List<StepExecution> summarizeCoTermManualSteps = jobExecution.getStepExecutions()
                                                                     .stream()
                                                                     .filter(step -> step.getStepName().equals(CO_TERM_MANUAL_SUMMARIZATION_STEP))
                                                                     .collect(Collectors.toList());
        assertThat(summarizeCoTermManualSteps, hasSize(1));
        StepExecution coTermsManualStep = summarizeCoTermManualSteps.get(0);
        assertThat(coTermsManualStep.getReadCount(), is(4));
        assertThat(coTermsManualStep.getReadSkipCount(), is(0));
        assertThat(coTermsManualStep.getProcessSkipCount(), is(0));
        assertThat(coTermsManualStep.getWriteCount(), is(4));

        List<StepExecution> summarizeCoTermAllSteps = jobExecution.getStepExecutions()
                                                                  .stream()
                                                                  .filter(step -> step.getStepName().equals(CO_TERM_ALL_SUMMARIZATION_STEP))
                                                                  .collect(Collectors.toList());
        assertThat(summarizeCoTermAllSteps, hasSize(1));
        StepExecution coTermsAllStep = summarizeCoTermAllSteps.get(0);
        assertThat(coTermsAllStep.getReadCount(), is(5));
        assertThat(coTermsAllStep.getReadSkipCount(), is(0));
        assertThat(coTermsAllStep.getProcessSkipCount(), is(0));
        assertThat(coTermsAllStep.getWriteCount(), is(5));
        assertThat(coTermsAllStep.getExecutionContext().get("FlatFileItemWriter.written"), is(7L));

        //Has finished
        BatchStatus status = jobExecution.getStatus();
        assertThat(status, is(BatchStatus.COMPLETED));

    }
}
