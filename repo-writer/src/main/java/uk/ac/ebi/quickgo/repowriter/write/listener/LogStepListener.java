package uk.ac.ebi.quickgo.repowriter.write.listener;

import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

/**
 * Created 02/12/15
 * @author Edd
 */
public class LogStepListener implements StepExecutionListener {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(LogStepListener.class);
    private final static AtomicLong readCount = new AtomicLong();
    private final static AtomicLong writeCount = new AtomicLong();


    @Override public void beforeStep(StepExecution stepExecution) {
        LOGGER.info("QuickGO indexing STEP '{}' starting.", stepExecution.getStepName());
    }

    @Override public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.info("QuickGO indexing STEP '{}' finished.", stepExecution.getStepName());
        LOGGER.info("QuickGO: read count: {}", stepExecution.getReadCount());
        LOGGER.info("QuickGO: write count: {}", stepExecution.getWriteCount());
        return stepExecution.getExitStatus();
    }
}
