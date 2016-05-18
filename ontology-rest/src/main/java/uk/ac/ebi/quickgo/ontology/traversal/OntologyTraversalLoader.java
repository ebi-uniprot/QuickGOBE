package uk.ac.ebi.quickgo.ontology.traversal;

import org.slf4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created 18/05/16
 * @author Edd
 */
public class OntologyTraversalLoader {
    private static final Logger LOGGER = getLogger(OntologyTraversalLoader.class);
    private final JobLauncher launcher;
    private final Job job;

    public OntologyTraversalLoader(JobLauncher launcher, Job job) {
        this.launcher = launcher;
        this.job = job;
    }

    public void load()
            throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException,
                   JobInstanceAlreadyCompleteException {
        JobExecution jobExecution = launcher.run(this.job, null);
        LOGGER.info("Job exit status: " + jobExecution.getExitStatus().getExitCode());
    }
}
