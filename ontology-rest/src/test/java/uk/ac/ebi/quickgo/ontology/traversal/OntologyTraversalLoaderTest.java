package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.common.batch.JobTestRunnerConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
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

    @Test
    public void runManuallyOntologyGraphLoading()
            throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException,
                   JobInstanceAlreadyCompleteException {
        launcher.run(ontologyGraphBuildJob, new JobParameters());
    }
}