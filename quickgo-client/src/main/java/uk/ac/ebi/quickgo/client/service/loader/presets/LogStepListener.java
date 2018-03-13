package uk.ac.ebi.quickgo.client.service.loader.presets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

/**
 * Created 31/08/16
 * @author Edd
 */
public class LogStepListener implements StepExecutionListener {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(LogStepListener.class);

    @Override public void beforeStep(StepExecution stepExecution) {
        LOGGER.info("Starting QuickGO STEP '{}'.", stepExecution.getStepName());
    }

    @Override public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.info("=====================================================");
        LOGGER.info("              QuickGO Step Statistics      asdfsadfsadf           ");
        LOGGER.info("Step name     : {}", stepExecution.getStepName());
        LOGGER.info("Read count    : {}", stepExecution.getReadCount());
        LOGGER.info("Write count   : {}", stepExecution.getWriteCount());
        LOGGER.info("Skip count    : {} ({} read / {} processing /{} write)", stepExecution.getSkipCount(),
                stepExecution.getReadSkipCount(), stepExecution.getProcessSkipCount(),
                stepExecution.getWriteSkipCount());
        LOGGER.info("Exit status   : {}", stepExecution.getExitStatus().getExitCode());
        LOGGER.info("=====================================================");
        return stepExecution.getExitStatus();
    }
}
