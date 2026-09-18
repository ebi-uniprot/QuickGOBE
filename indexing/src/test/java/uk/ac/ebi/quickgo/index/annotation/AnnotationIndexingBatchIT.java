package uk.ac.ebi.quickgo.index.annotation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.common.store.SolrContainerTestSetup;
import uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsConfigProperties;
import uk.ac.ebi.quickgo.index.common.BatchConfig;

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
@ActiveProfiles(profiles = {"embeddedServer"})
@SpringBootTest(classes = {AnnotationIndexingBatchIT.TestConfig.class,
                AnnotationIndexingConfig.class,  DefaultBatchConfiguration.class, BatchConfig.class})
@SpringBatchTest
class AnnotationIndexingBatchIT extends SolrContainerTestSetup {
    @TempDir
    private static Path basicTemporaryFolder;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private AnnotationRepository annotationRepository;

    @BeforeEach
    void setUp() {
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

        StepExecution indexingStep = jobsSingleStepAsList.getFirst();

        assertThat(indexingStep.getReadCount(), is(8L));
        assertThat(indexingStep.getReadSkipCount(), is(0L));
        assertThat(indexingStep.getProcessSkipCount(), is(2L));
        assertThat(indexingStep.getWriteCount(), is(6L));

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
        StepExecution coTermsManualStep = summarizeCoTermManualSteps.getFirst();
        assertThat(coTermsManualStep.getReadCount(), is(4L));
        assertThat(coTermsManualStep.getReadSkipCount(), is(0L));
        assertThat(coTermsManualStep.getProcessSkipCount(), is(0L));
        assertThat(coTermsManualStep.getWriteCount(), is(4L));

        List<StepExecution> summarizeCoTermAllSteps = jobExecution.getStepExecutions()
                .stream()
                .filter(step -> step.getStepName().equals(CO_TERM_ALL_SUMMARIZATION_STEP))
                .collect(Collectors.toList());
        assertThat(summarizeCoTermAllSteps, hasSize(1));
        StepExecution coTermsAllStep = summarizeCoTermAllSteps.getFirst();
        assertThat(coTermsAllStep.getReadCount(), is(5L));
        assertThat(coTermsAllStep.getReadSkipCount(), is(0L));
        assertThat(coTermsAllStep.getProcessSkipCount(), is(0L));
        assertThat(coTermsAllStep.getWriteCount(), is(5L));
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
