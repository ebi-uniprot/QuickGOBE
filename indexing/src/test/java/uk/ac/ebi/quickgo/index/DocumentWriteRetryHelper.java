package uk.ac.ebi.quickgo.index;

import org.apache.solr.client.solrj.SolrServerException;
import org.mockito.stubbing.Stubber;
import org.springframework.batch.item.ItemWriter;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

/**
 * Utility methods to help the testing of the retry logic associated with writing
 * gene product documents to an index, in the presence of an possibly busy
 * Solr instance, which may occasionally not be responsive due to other tasks, e.g.,
 * dedicated to Zookeeper.
 *
 * Created by edd on 23/02/2017.
 */
public class DocumentWriteRetryHelper {
    private static final String HOST = "http://www.myhost.com";
    private static final String MESSAGE = "Looks like the host is not reachable?!";
    private static final int CODE = 1;

    /**
     * Stubs successive Solrmethod call actions based on a given list of {@link SolrResponse}
     * values. {@link SolrResponse#OK} simulates that Solr was able to write the documents it
     * received; {@link SolrResponse#REMOTE_EXCEPTION} simulates Solr being busy and responding
     * with a {@link SolrServerException}, meaning the documents could not be
     * written
     * @param responses represents a list of behavioural responses from Solr
     * @return a {@link Stubber} which can be associated with a method call
     */
    public static Stubber stubSolrWriteResponses(List<SolrResponse> responses) {
        Stubber stubber = null;
        for (SolrResponse response : responses) {
          stubber = switch (response) {
            case OK -> (stubber == null) ? doNothing() : stubber.doNothing();
            case REMOTE_EXCEPTION -> {
              String msg = "Host: " + HOST + ", Code: " + CODE + ", Message: " + MESSAGE;
              yield (stubber == null) ? doThrow(new SolrServerException(msg))
                : stubber.doThrow(new SolrServerException(msg));
            }
          };
        }
        return stubber;
    }

    /**
     * Validates that the documents sent to be written to Solr, constitute the correct
     * documents that *should* be written; even when Solr occasionally is not able to write
     * @param docsSentToBeWritten a list of document lists, each of which was sent as an
     *                            argument to an {@link ItemWriter}, for writing to Solr.
     */
    public static <T,I> void validateWriteAttempts(List<SolrResponse> responses, List<List<T>>
            docsSentToBeWritten, Function<T, I> transformation) {
        int counter = 0;
        List<T> docsToWrite;
        List<T> docsToRetryWriting = Collections.emptyList();

        Iterator<SolrResponse> responsesIt = responses.iterator();
        for(int i = 0; i < docsSentToBeWritten.size() && responsesIt.hasNext(); i++) {
            SolrResponse response = responsesIt.next();

            docsToWrite = docsSentToBeWritten.get(counter++);
            switch (response) {
                case OK:
                    // documents could not be written last time, but can this time
                    if (!docsToRetryWriting.isEmpty()) {
                        assertThat(extractDocAttribute(docsToWrite, transformation),
                                is(extractDocAttribute(docsToRetryWriting, transformation)));
                        docsToRetryWriting = Collections.emptyList();
                    }
                    break;
                case REMOTE_EXCEPTION:
                    docsToRetryWriting = docsToWrite;
                    break;
                default:
                    throw new IllegalStateException("Unknown SolrResponse");
            }
        }
    }

    private static <T,I> List<I> extractDocAttribute( List<T> docs,Function<T, I> transformation) {
        return docs.stream().map(transformation).collect(Collectors.toList());
    }

    /**
     * Represents possible responses from Solr -- extend if needed
     */
    public enum SolrResponse {
        OK, REMOTE_EXCEPTION
    }
}
