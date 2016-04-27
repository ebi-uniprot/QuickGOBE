package uk.ac.ebi.quickgo.index.common.listener;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.when;

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
    public void computesZeroDocsPerSecond() {
        float docsPerSecond = itemRateWriterListener.getItemsPerSecond(start.plusSeconds(10), new AtomicInteger(0));
        assertThat(docsPerSecond, is(0.0F));
    }

    @Test
    public void computes1DocPerSecond() {
        float docsPerSecond = itemRateWriterListener.getItemsPerSecond(start.plusSeconds(44), new AtomicInteger(44));
        assertThat(docsPerSecond, is(1.0F));
    }

    @Test
    public void computes8DocPerSecond() {
        float docsPerSecond = itemRateWriterListener.getItemsPerSecond(start.plusSeconds(5), new AtomicInteger(40));
        assertThat(docsPerSecond, is(8.0F));
    }

    @Test
    public void computesWriteRate() throws Exception {
        int numDocs = 40;
        Instant fiveSecsAfterStart = start.plusSeconds(5);

        when(mockedWrittenDocList.size()).thenReturn(numDocs);

        itemRateWriterListener.afterWrite(mockedWrittenDocList);
        String writeRateStats = itemRateWriterListener.computeWriteRateStats(fiveSecsAfterStart);

        assertThat(writeRateStats, is(not(isEmptyString())));
        System.out.println(writeRateStats);
    }
}