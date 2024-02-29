package uk.ac.ebi.quickgo.index.common.listener;

import uk.ac.ebi.quickgo.index.common.SolrServerWriter;

import java.time.Duration;
import java.time.Instant;
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
    public static final int WRITE_RATE_DOCUMENT_INTERVAL = 100000;
    int writeRateDocumentInterval;
    private final Instant startOfWriting;
    private AtomicInteger totalWriteCount = new AtomicInteger(0);
    private AtomicInteger deltaWriteCount = new AtomicInteger(0);
    private Instant startOfDelta;

    public ItemRateWriterListener(Instant now) {
        startOfWriting = startOfDelta = now;
        writeRateDocumentInterval = WRITE_RATE_DOCUMENT_INTERVAL;
    }

    public ItemRateWriterListener(Instant now, final int writeInterval) {
        startOfWriting = startOfDelta = now;
        writeRateDocumentInterval = writeInterval;
    }

    @Override public void beforeWrite(List<? extends O> list) {

    }

    @Override public void afterWrite(List<? extends O> list) {
        deltaWriteCount.addAndGet(list.size());

        if (deltaWriteCount.get() >= writeRateDocumentInterval) {
            LOGGER.info(computeWriteRateStats(Instant.now()).toString());
            resetDelta();
        }
    }

    @Override public void onWriteError(Exception e, List<? extends O> list) {

    }

    /**
     * Compute writing rate statistics and return a formatted {@link StatsInfo} instance,
     * ready for printing.
     *
     * @param now the time point at which the statistics should be computed
     * @return a {@link StatsInfo} instance representing the write rate statistics
     */
    StatsInfo computeWriteRateStats(Instant now) {
        totalWriteCount.addAndGet(deltaWriteCount.get());

        StatsInfo statsInfo = new StatsInfo();
        statsInfo.totalWriteCount = totalWriteCount.get();
        statsInfo.totalSeconds = Duration.between(startOfWriting, now).getSeconds();
        statsInfo.deltaWriteCount = deltaWriteCount.get();
        statsInfo.deltaSeconds = Duration.between(startOfDelta, now).getSeconds();

        return statsInfo;
    }

    private void resetDelta() {
        deltaWriteCount.set(0);
        startOfDelta = Instant.now();
    }

    static class StatsInfo {
        private static final int SECONDS_IN_AN_HOUR = 3600;

        int deltaWriteCount;
        long deltaSeconds;

        int totalWriteCount;
        long totalSeconds;

        @Override public String toString() {
            float deltaDocsPerSecond = (float) deltaWriteCount / deltaSeconds;
            float totalDocsPerSecond = (float) totalWriteCount / totalSeconds;
            return
                            "\n\tWrite statistics {\n" +
                            "\t\tLatest delta:\n" +
                                    "\t\t\t# docs\t\t:\t%d\n".formatted(deltaWriteCount) +
                                    "\t\t\ttime (sec)\t:\t%d\n".formatted(deltaSeconds) +
                                    "\t\t\tdocs/sec\t:\t%.2f\n".formatted(deltaDocsPerSecond) +
                                    "\t\t\tdocs/hour\t:\t%.0f\t(projected from docs/sec)\n".formatted(deltaDocsPerSecond
                                            * SECONDS_IN_AN_HOUR) +
                            "\t\tOverall:\n" +
                                    "\t\t\t# docs\t\t:\t%d\n".formatted(totalWriteCount) +
                                    "\t\t\ttime (sec)\t:\t%d\n".formatted(totalSeconds) +
                                    "\t\t\tdocs/sec\t:\t%.2f\n".formatted(totalDocsPerSecond) +
                                    "\t\t\tdocs/hour\t:\t%.0f\t(projected from docs/sec)\n".formatted(totalDocsPerSecond *
                                            SECONDS_IN_AN_HOUR) +
                            "\t}\n";
        }
    }


}
