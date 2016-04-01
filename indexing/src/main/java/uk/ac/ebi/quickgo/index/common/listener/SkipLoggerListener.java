package uk.ac.ebi.quickgo.index.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

/**
 * Logs any entries that have been skipped during reading, processing or writing of a record
 *
 * @author Ricardo Antunes
 */
public class SkipLoggerListener implements SkipListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override public void onSkipInRead(Throwable t) {
        logger.error("Error whilst reading: ", t);
    }

    @Override public void onSkipInWrite(Object item, Throwable t) {
        logger.error("Error whilst writing: ", t);
    }

    @Override public void onSkipInProcess(Object item, Throwable t) {
        logger.error("Error whilst processing: ", t);
    }
}
