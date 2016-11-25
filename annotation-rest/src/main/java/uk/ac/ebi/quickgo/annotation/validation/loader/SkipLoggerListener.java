package uk.ac.ebi.quickgo.annotation.validation.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

/**
 * Logs any entries that have been skipped during reading, processing or writing of a record
 *
 * @author Ricardo Antunes
 */
class SkipLoggerListener<T, S> implements SkipListener<T, S> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override public void onSkipInWrite(S item, Throwable t) {
        logger.error("Error whilst writing: {}", item, t);
    }

    @Override public void onSkipInProcess(T item, Throwable t) {
        logger.error("Error whilst processing: {}", item, t);
    }

    @Override public void onSkipInRead(Throwable t) {
        logger.error("Error whilst reading", t);
    }
}
