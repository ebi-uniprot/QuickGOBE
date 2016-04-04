package uk.ac.ebi.quickgo.index.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ItemListenerSupport;

/**
 * Logs any failures that occurred during the reading or writing of a record
 *
 * @author Ricardo Antunes
 */
public class ItemFailureLoggerListener extends ItemListenerSupport {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void onReadError(Exception ex) {
        logger.error("Read error",ex);
    }

    public void onWriteError(Exception ex, Object item) {
        logger.error("Write error", ex);
    }
}