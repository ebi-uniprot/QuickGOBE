package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.index.common.JobTestRunnerConfig;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationConfig.ANNOTATION_INDEXING_JOB_NAME;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationConfig.ANNOTATION_INDEXING_STEP_NAME;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermConfiguration.COSTATS_ALL_COMPLETION_STEP_NAME;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermConfiguration.COSTATS_MANUAL_COMPLETION_STEP_NAME;

/**
 * Tests whether Spring Batch is correctly wired up to run the annotation indexing.
 *
 * Created 22/04/16
 * @author Edd
 */
@ActiveProfiles(profiles = {"embeddedServer"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {AnnotationConfig.class, JobTestRunnerConfig.class},
        loader = SpringApplicationContextLoader.class)
public class AnnotationIndexingBatchIT {
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private AnnotationRepository annotationRepository;

    @Test
    public void successfulIndexingJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getJobInstance().getJobName(), is(ANNOTATION_INDEXING_JOB_NAME));

        List<StepExecution> jobsSingleStepAsList = jobExecution.getStepExecutions()
                .stream()
                .filter(step -> step.getStepName().equals (ANNOTATION_INDEXING_STEP_NAME))
                .collect(Collectors.toList());
        assertThat(jobsSingleStepAsList, hasSize(1));

        StepExecution indexingStep = jobsSingleStepAsList.get(0);

        assertThat(indexingStep.getReadCount(), is(6));
        assertThat(indexingStep.getReadSkipCount(), is(0));
        assertThat(indexingStep.getProcessSkipCount(), is(2));
        assertThat(indexingStep.getWriteCount(), is(4));

        List<String> writtenAnnotationDocGeneProductIds =
                getGeneProductIdsFromAnnotationDocuments(annotationRepository.findAll());

        assertThat(writtenAnnotationDocGeneProductIds, containsInAnyOrder(
                "IntAct:EBI-10043081",
                "IntAct:EBI-10205244",
                "IntAct:EBI-8801830",
                "IntAct:EBI-10043082"
        ));


        List<StepExecution> jobsSingleStepAsList2 = jobExecution.getStepExecutions()
                .stream()
                .filter(step -> step.getStepName().equals (COSTATS_MANUAL_COMPLETION_STEP_NAME))
                .collect(Collectors.toList());
        assertThat(jobsSingleStepAsList2, hasSize(1));


        StepExecution cotermsManualStep = jobsSingleStepAsList2.get(0);

        assertThat(cotermsManualStep.getReadCount(), is(4));
        assertThat(cotermsManualStep.getReadSkipCount(), is(0));
        assertThat(cotermsManualStep.getProcessSkipCount(), is(0));
        assertThat(cotermsManualStep.getWriteCount(), is(4));

//        List<String> writtenAnnotationDocGeneProductIds =
//                getGeneProductIdsFromAnnotationDocuments(annotationRepository.findAll());
//
//        assertThat(writtenAnnotationDocGeneProductIds, containsInAnyOrder(
//                "IntAct:EBI-10043081",
//                "IntAct:EBI-10205244",
//                "IntAct:EBI-8801830",
//                "IntAct:EBI-10043082"
//        ));

        List<StepExecution> jobsSingleStepAsList3 = jobExecution.getStepExecutions()
                .stream()
                .filter(step -> step.getStepName().equals (COSTATS_ALL_COMPLETION_STEP_NAME))
                .collect(Collectors.toList());
        assertThat(jobsSingleStepAsList3, hasSize(1));


        BatchStatus status = jobExecution.getStatus();
        assertThat(status, is(BatchStatus.COMPLETED));
    }

    private List<String> getGeneProductIdsFromAnnotationDocuments(Iterable<AnnotationDocument> repoDocsWritten) {
        return StreamSupport.stream(repoDocsWritten.spliterator(), false).map(i -> i.geneProductId).collect(Collectors
                .toList());
    }
}
