package uk.ac.ebi.quickgo.index.common.listener;

import uk.ac.ebi.quickgo.index.common.SolrServerWriter;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.springframework.batch.core.ItemWriteListener;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Class used to log statistics of the rate of writing. The primary purpose is to provide
 * a basis for reviewing, comparing and tuning batch run performances.
 *
 * Created 27/04/16
 * @author Edd
 */
public class ItemRateWriterListener<O> implements ItemWriteListener<O> {
    private static final Logger LOGGER = getLogger(SolrServerWriter.class);
    private static final int WRITE_RATE_DOCUMENT_INTERVAL = 10000;
    private final Instant startOfWriting;
    private AtomicInteger writeCount = new AtomicInteger(0);

    public ItemRateWriterListener(Instant now) {
        startOfWriting = now;
    }

    @Override public void beforeWrite(List<? extends O> list) {

    }

    @Override public void afterWrite(List<? extends O> list) {
        writeCount.addAndGet(list.size());

        if (writeCount.get() % WRITE_RATE_DOCUMENT_INTERVAL == 0) {
            LOGGER.info(computeWriteRateStats(Instant.now()));
        }
    }

    @Override public void onWriteError(Exception e, List<? extends O> list) {

    }

    /**
     * Computes the rate of items written per second, from instance creation time
     * to a specified time-point, {@code now}.
     *
     * @param now the time-point upper bound of the time points for which to compute the rate
     * @param writeCount the number of items written
     * @return a floating point number representing the rate of writing
     */
    float getItemsPerSecond(Instant now, AtomicInteger writeCount) {
        Duration duration = Duration.between(startOfWriting, now);
        return (float) writeCount.get() / duration.get(ChronoUnit.SECONDS);
    }

    /**
     * Compute writing rate statistics and return a formatted {@link String},
     * ready for printing.
     *
     * @param now the time point at which the statistics should be computed
     * @return a formatted {@link String} representing the write rate statistics
     */
    String computeWriteRateStats(Instant now) {
        float docsPerSecond = getItemsPerSecond(now, writeCount);
        float docsPerHour = docsPerSecond * 3600;

        return "\t---- Write statistics ----\n" +
                String.format("\t\t# docs\t\t:\t%d\n", writeCount.get()) +
                String.format("\t\tdocs/sec\t:\t%.2f\n", docsPerSecond) +
                String.format("\t\tdocs/hour\t:\t%.0f\n", docsPerHour) +
                "\t---- End of statistics ----\n";
    }

}
