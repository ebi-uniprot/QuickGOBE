package uk.ac.ebi.quickgo.index.annotation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfigProperties;
import uk.ac.ebi.quickgo.index.common.JobTestRunnerConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationConfig.ANNOTATION_INDEXING_JOB_NAME;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationConfig.ANNOTATION_INDEXING_STEP_NAME;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfig.CO_TERM_ALL_SUMMARIZATION_STEP;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfig.CO_TERM_MANUAL_SUMMARIZATION_STEP;

/**
 * Tests whether Spring Batch is correctly wired up to run the annotation indexing.
 *
 * Created 22/04/16
 * @author Edd
 */
@ExtendWith(TemporarySolrDataStore.class)
@ActiveProfiles(profiles = {"embeddedServer"})
@SpringBootTest(classes = {AnnotationIndexingBatchIT.TestConfig.class,
                AnnotationIndexingConfig.class, JobTestRunnerConfig.class})
class AnnotationIndexingBatchIT {
    @TempDir
    private static Path basicTemporaryFolder;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private AnnotationRepository annotationRepository;

    @BeforeEach
    void setUp() throws IOException {
        annotationRepository.deleteAll();
    }

    @Test
    void successfulIndexingJob() throws Exception {
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
        assertThat(indexingStep.getWriteCount(), is(6));

        List<String> writtenAnnotationDocGeneProductIds =
                getGeneProductIdsFromAnnotationDocuments(annotationRepository.findAll());

        assertThat(writtenAnnotationDocGeneProductIds, containsInAnyOrder(
                "IntAct:EBI-10043081",
                "IntAct:EBI-10043081",
                "IntAct:EBI-10043081",
                "IntAct:EBI-10205244",
                "IntAct:EBI-8801830",
                "IntAct:EBI-10043089"
        ));

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

    private List<String> getGeneProductIdsFromAnnotationDocuments(Iterable<AnnotationDocument> repoDocsWritten) {
        return StreamSupport.stream(repoDocsWritten.spliterator(), false).map(i -> i.geneProductId).collect(Collectors
                .toList());
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
            properties.setLoginterval(100);
            properties.setManual(basicTemporaryFolder + "/CoTermsManual");
            properties.setAll(basicTemporaryFolder + "/CoTermsAll");
            return properties;
        }
    }
}
