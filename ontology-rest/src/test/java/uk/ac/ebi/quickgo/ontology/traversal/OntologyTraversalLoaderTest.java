package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.common.batch.JobTestRunnerConfig;
import uk.ac.ebi.quickgo.ontology.traversal.read.OntologyTraversalConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

/**
 * Created 18/05/16
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {OntologyTraversalConfig.class, JobTestRunnerConfig.class},
        loader = SpringApplicationContextLoader.class)
public class OntologyTraversalLoaderTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OntologyGraph ontologyGraph;

    @Autowired
    private JobLauncher launcher;

    @Autowired
    private Job ontologyGraphBuildJob;

    @Test
    public void runOntologyGraphLoading() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        BatchStatus status = jobExecution.getStatus();
        assertThat(status, is(BatchStatus.COMPLETED));

        System.out.println(ontologyGraph);
    }
}