package uk.ac.ebi.quickgo.repowriter.main;

import uk.ac.ebi.quickgo.repo.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.repowriter.JobTestRunnerConfig;
import uk.ac.ebi.quickgo.repowriter.reader.DocumentReaderException;
import uk.ac.ebi.quickgo.repowriter.reader.ODocReader;
import uk.ac.ebi.quickgo.repowriter.write.IndexerProperties;
import uk.ac.ebi.quickgo.repowriter.write.job.IndexingJobConfig;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.document.ontology.OntologyDocMocker.createECODoc;
import static uk.ac.ebi.quickgo.document.ontology.OntologyDocMocker.createGODoc;
import static uk.ac.ebi.quickgo.repowriter.main.QuickGOIndexOntologyMainITConfig.STEP_SKIP_LIMIT;

/**
 * Test specific behaviour of the job executed by {@link QuickGOIndexOntologyMain}.
 * <p>
 * To see how to steps are configured, refer to:
 * <ul>
 *     <li>https://docs.spring.io/spring-batch/reference/html/configureStep.html</li>
 * </ul>
 *
 * Created 18/12/15
 * @author Edd
 */
@ActiveProfiles(profiles = {"QuickGOIndexOntologyMainIT", "dev"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {IndexingJobConfig.class, JobTestRunnerConfig.class, QuickGOIndexOntologyMainITConfig.class},
        loader = SpringApplicationContextLoader.class)
public class QuickGOIndexOntologyMainIT {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ODocReader reader;

    @Autowired
    private IndexerProperties indexerProperties;

    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Test
    public void stepSucceedsWhenNoSkips() throws Exception {
        int docCount = 5;

        when(reader.read())
                .thenReturn(createGODoc("go1", "go1-name"))
                .thenReturn(createGODoc("go2", "go2-name"))
                .thenReturn(createECODoc("eco1", "eco1-name"))
                .thenReturn(createECODoc("eco2", "eco2-name"))
                .thenReturn(createECODoc("eco3", "eco3-name"))
                .thenReturn(null);

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("readThenWriteToRepoStep");
        assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));

        StepExecution step = jobExecution.getStepExecutions().iterator().next();
        assertThat(step.getReadCount(), is(docCount));
        assertThat(step.getWriteCount(), is(docCount));
        assertThat(step.getSkipCount(), is(0));
    }

    @Test
    public void stepSkipsOnceWhenReaderFindsOneEmptyOptionalDocument() throws Exception {
        int docCount = 2;

        when(reader.read())
                .thenReturn(createGODoc("go1", "go1-name"))
                .thenThrow(new DocumentReaderException("Error!"))
                .thenReturn(createECODoc("eco1", "eco1-name"))
                .thenReturn(null);

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("readThenWriteToRepoStep");
        assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));

        StepExecution step = jobExecution.getStepExecutions().iterator().next();
        assertThat(step.getReadCount(), is(docCount));
        assertThat(step.getWriteCount(), is(docCount));
        assertThat(step.getSkipCount(), is(1));
    }

    @Test
    public void tooManySkipsCausesStepToFail() throws Exception {
        when(reader.read())
                .thenReturn(createGODoc("go1", "go1-name"))
                .thenThrow(new DocumentReaderException("Error!"))
                .thenReturn(createGODoc("go2", "go2-name"))
                .thenThrow(new DocumentReaderException("Error!"))
                .thenReturn(createECODoc("eco1", "eco1-name"))
                .thenReturn(createECODoc("eco2", "eco2-name"))
                // full chunk of documents now created, and should
                // be written

                .thenThrow(new DocumentReaderException("Error!"))
                .thenReturn(createECODoc("eco3", "eco3-name"))
                .thenReturn(null);

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("readThenWriteToRepoStep");
        assertThat(jobExecution.getStatus(), is(BatchStatus.FAILED));

        StepExecution step = jobExecution.getStepExecutions().iterator().next();
        assertThat(step.getReadCount(), is(4));
        assertThat(step.getWriteCount(), is(4));
        assertThat(step.getSkipCount(), is(STEP_SKIP_LIMIT));
    }

    @Test
    public void succeedsOn2ChunksButSkips1ChunkWhenSkipLimitExceeded() throws Exception {
        int goDocIdHelper = 1;
        int ecoDocIdHelper = 1;
        when(reader.read())
                .thenReturn(createGODoc("go" + goDocIdHelper, "go" + goDocIdHelper++ + "-name"))
                .thenReturn(createGODoc("go" + goDocIdHelper, "go" + goDocIdHelper++ + "-name"))
                .thenThrow(new DocumentReaderException("Error!"))
                .thenReturn(createECODoc("eco" + ecoDocIdHelper, "eco" + ecoDocIdHelper++ + "-name"))
                .thenReturn(createECODoc("eco" + ecoDocIdHelper, "eco" + ecoDocIdHelper++ + "-name"))
                // a full chunk of documents to write has now been created, so
                // chunk 1 should be written

                .thenThrow(new DocumentReaderException("Error!"))
                .thenReturn(createECODoc("eco" + ecoDocIdHelper, "eco" + ecoDocIdHelper++ + "-name"))
                .thenReturn(createECODoc("eco" + ecoDocIdHelper, "eco" + ecoDocIdHelper++ + "-name"))
                .thenReturn(createGODoc("go" + goDocIdHelper, "go" + goDocIdHelper++ + "-name"))
                .thenReturn(createGODoc("go" + goDocIdHelper, "go" + goDocIdHelper++ + "-name"))
                // now chunk 2 should be written

                .thenReturn(createECODoc("eco" + ecoDocIdHelper, "eco" + ecoDocIdHelper++ + "-name"))
                .thenReturn(createGODoc("go" + goDocIdHelper, "go" + goDocIdHelper + "-name"))
                .thenThrow(new DocumentReaderException("Error!"))
                // ------ BOOM! Skip Limit exceeded now

                .thenReturn(createECODoc("eco" + ecoDocIdHelper, "eco" + ecoDocIdHelper + "-name"))

                // null indicates reading is done
                .thenReturn(null);

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("readThenWriteToRepoStep");
        assertThat(jobExecution.getStatus(), is(BatchStatus.FAILED));

        StepExecution step = jobExecution.getStepExecutions().iterator().next();
        assertThat(step.getReadCount(), is(10));
        assertThat(step.getWriteCount(), is(8));
        assertThat(step.getSkipCount(), is(STEP_SKIP_LIMIT));
    }
}