package uk.ac.ebi.quickgo.index.annotation;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.quickgo.common.store.BasicTemporaryFolder;
import uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfigProperties;
import uk.ac.ebi.quickgo.index.common.JobTestRunnerConfig;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.quickgo.index.annotation.CoTermIndexingConfig.ANNOTATION_READING_STEP_NAME;
import static uk.ac.ebi.quickgo.index.annotation.CoTermIndexingConfig.COTERM_INDEXING_JOB_NAME;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfig.CO_TERM_ALL_SUMMARIZATION_STEP;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfig.CO_TERM_MANUAL_SUMMARIZATION_STEP;

/**
 * Tests whether Spring Batch is correctly wired up to run the indexing for CoTerms only.
 *
 * Created 22/04/16
 * @author Edd
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CoTermIndexingBatchIT.TestConfig.class, CoTermIndexingConfig.class,
  JobTestRunnerConfig.class})
public class CoTermIndexingBatchIT {

    @ClassRule
    public static BasicTemporaryFolder basicTemporaryFolder = new BasicTemporaryFolder();

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void successfulCoTermsOnlyJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getJobInstance().getJobName(), is(COTERM_INDEXING_JOB_NAME));

        List<StepExecution> jobsSingleStepAsList = jobExecution.getStepExecutions()
                .stream()
                .filter(step -> step.getStepName()
                        .equals(ANNOTATION_READING_STEP_NAME))
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
                .filter(step -> step.getStepName()
                        .equals(CO_TERM_MANUAL_SUMMARIZATION_STEP))
                .collect(Collectors.toList());
        assertThat(summarizeCoTermManualSteps, hasSize(1));
        StepExecution coTermsManualStep = summarizeCoTermManualSteps.get(0);
        assertThat(coTermsManualStep.getReadCount(), is(4));
        assertThat(coTermsManualStep.getReadSkipCount(), is(0));
        assertThat(coTermsManualStep.getProcessSkipCount(), is(0));
        assertThat(coTermsManualStep.getWriteCount(), is(4));

        List<StepExecution> summarizeCoTermAllSteps = jobExecution.getStepExecutions()
                .stream()
                .filter(step -> step.getStepName()
                        .equals(CO_TERM_ALL_SUMMARIZATION_STEP))
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

    /**
     * Configure properties used by co-term generation, using test values.
     */
    @Configuration
    public static class TestConfig {
        @Primary
        @Bean
        public CoTermsConfigProperties primaryCoTermsConfigProperties() {
            CoTermsConfigProperties properties = new CoTermsConfigProperties();
            properties.setChunkSize(1);
            properties.setLoginterval(1000);
            properties.setManual(basicTemporaryFolder.getRoot().getAbsolutePath() + "/CoTermsManual");
            properties.setAll(basicTemporaryFolder.getRoot().getAbsolutePath() + "/CoTermsAll");
            return properties;
        }
    }
}
