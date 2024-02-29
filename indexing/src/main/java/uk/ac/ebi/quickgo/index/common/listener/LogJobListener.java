package uk.ac.ebi.quickgo.index.common.listener;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;

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
        Duration.between(jobExecution.getEndTime().toInstant(), jobExecution.getStartTime().toInstant());
        long durationMillis = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();
        String duration = "%d hrs, %d min, %d sec".formatted(
                TimeUnit.MILLISECONDS.toHours(durationMillis),
                TimeUnit.MILLISECONDS.toMinutes(durationMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                        .toHours(durationMillis)),
                TimeUnit.MILLISECONDS.toSeconds(durationMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(durationMillis))
        );

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

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            readSkips += stepExecution.getReadSkipCount();
            writeSkips += stepExecution.getWriteSkipCount();
            processingSkips += stepExecution.getProcessSkipCount();
            readCount += stepExecution.getReadCount();
            writeCount += stepExecution.getWriteCount();
            skipCount += stepExecution.getSkipCount();

        }
        LOGGER.info("Read count    : {}", readCount);
        LOGGER.info("Write count   : {}", writeCount);
        LOGGER.info("Skip count    : {} ({} read / {} processing / {} write)", skipCount, readSkips, processingSkips,
                writeSkips);
        LOGGER.info("=====================================================");
        jobExecution.getExitStatus();
    }
}
