package uk.ac.ebi.quickgo.ontology.traversal.read;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;

/**
 * Log statistics of a QuickGO job.
 *
 * Created 03/12/15
 * @author Edd
 */
public class LogJobListener implements JobExecutionListener {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(LogJobListener.class);

    @Override public void beforeJob(JobExecution jobExecution) {
        LOGGER.info("Starting QuickGO job '{}'.", jobExecution.getJobInstance().getJobName());
    }

    @Override public void afterJob(JobExecution jobExecution) {
        LOGGER.info("Completed QuickGO job '{}'.\n", jobExecution.getJobInstance().getJobName());

        // compute duration
        var dur = Duration.between(jobExecution.getEndTime(), jobExecution.getStartTime());
        String duration = "%d hrs, %d min, %d sec".formatted(dur.toHours(), dur.toMinutesPart(), dur.toSecondsPart());

        LOGGER.info("=====================================================");
        LOGGER.info("              QuickGO Job Statistics                 ");
        LOGGER.info("Job name      : {}", jobExecution.getJobInstance().getJobName());
        LOGGER.info("Exit status   : {}", jobExecution.getExitStatus().getExitCode());
        LOGGER.info("Start time    : {}", jobExecution.getStartTime());
        LOGGER.info("End time      : {}", jobExecution.getEndTime());
        LOGGER.info("Duration      : {}", duration);

        long skipCount = 0L;
        long readSkips = 0L;
        long writeSkips = 0L;
        long processingSkips = 0L;
        long readCount = 0L;
        long writeCount = 0L;

        for (StepExecution execution : jobExecution.getStepExecutions()) {
            writeSkips += execution.getWriteSkipCount();
            readSkips += execution.getReadSkipCount();
            readCount += execution.getReadCount();
            processingSkips += execution.getProcessSkipCount();
            writeCount += execution.getWriteCount();
            skipCount += execution.getSkipCount();
        }

        LOGGER.info("Read count    : {}", readCount);
        LOGGER.info("Write count   : {}", writeCount);
        LOGGER.info("Skip count    : {} ({} read / {} processing / {} write)", skipCount, readSkips, processingSkips,
                writeSkips);
        LOGGER.info("=====================================================");
        jobExecution.getExitStatus();
    }
}
