package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.index.IndexingJobConfig;
import uk.ac.ebi.quickgo.index.JobTestRunnerConfig;
import uk.ac.ebi.quickgo.index.QuickGOIndexOntologyMainITConfig;

import java.util.Collections;
import java.util.Iterator;
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
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

/**
 * Tests whether Spring Batch is correctly wired up to run the Gene product indexing.
 */
@ActiveProfiles(profiles = {"embeddedServer"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {GeneProductConfig.class, JobTestRunnerConfig.class},
        loader = SpringApplicationContextLoader.class)
public class GeneProductIndexingBatchIT {
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void successfulJobRun() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        StepExecution indexingStep = getStepByName(GeneProductConfig.GENE_PRODUCT_INDEXING_STEP_NAME, jobExecution);
        assertThat(indexingStep.getReadCount(), is(6));
        assertThat(indexingStep.getProcessSkipCount(), is(0));
        assertThat(indexingStep.getWriteCount(), is(6));

        BatchStatus status = jobExecution.getStatus();
        assertThat(status, is(BatchStatus.COMPLETED));
    }

    private StepExecution getStepByName(String stepName, JobExecution jobExecution) {

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            if (stepExecution.getStepName().equals(stepName)) {
                return stepExecution;
            }
        }

        throw new IllegalArgumentException("Step name not recognized: " + stepName);
    }
}