package uk.ac.ebi.quickgo.index.common.listener;

import java.time.Instant;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.index.common.listener.ItemRateWriterListener.WRITE_RATE_DOCUMENT_INTERVAL;

/**
 * Created 27/04/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemRateWriterListenerTest {

    private Instant start;
    private ItemRateWriterListener<Object> itemRateWriterListener;

    @Mock
    private List<Object> mockedWrittenDocList;

    @Before
    public void setUp() {
        start = Instant.now();
        itemRateWriterListener = new ItemRateWriterListener<>(start);
    }

    @Test
    public void computesRateAfterOneWrite() throws Exception {
        int numDocs = 40;
        Instant fiveSecsAfterStart = start.plusSeconds(5);

        when(mockedWrittenDocList.size()).thenReturn(numDocs);

        itemRateWriterListener.afterWrite(mockedWrittenDocList);
        ItemRateWriterListener.StatsInfo statsInfo = itemRateWriterListener.computeWriteRateStats(fiveSecsAfterStart);

        System.out.println(statsInfo.toString());
        assertThat(statsInfo.totalSeconds, is(5L));
        assertThat(statsInfo.totalWriteCount, is(numDocs));
    }

    @Test
    public void computesRateAfterMultipleWrites() throws Exception {
        int tenDocs = 10;
        long twoSeconds = 2L;
        Instant twoSecsAfterStart = start.plusSeconds(twoSeconds);

        when(mockedWrittenDocList.size()).thenReturn(tenDocs);
        itemRateWriterListener.afterWrite(mockedWrittenDocList);

        // add lots of docs to trigger a new delta
        when(mockedWrittenDocList.size()).thenReturn(WRITE_RATE_DOCUMENT_INTERVAL);
        itemRateWriterListener.afterWrite(mockedWrittenDocList);

        when(mockedWrittenDocList.size()).thenReturn(tenDocs);
        itemRateWriterListener.afterWrite(mockedWrittenDocList);

        ItemRateWriterListener.StatsInfo statsInfo = itemRateWriterListener.computeWriteRateStats(twoSecsAfterStart);

        System.out.println(statsInfo);
        assertThat(statsInfo.deltaWriteCount, is(tenDocs));
        // do not test delta time, because it internally uses
        assertThat(statsInfo.totalSeconds, is(twoSeconds));
        assertThat(statsInfo.totalWriteCount, is(tenDocs + tenDocs + WRITE_RATE_DOCUMENT_INTERVAL));
    }
}