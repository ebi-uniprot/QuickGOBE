package uk.ac.ebi.quickgo.index.common;

import uk.ac.ebi.quickgo.common.QuickGODocument;

import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.SolrServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * Created 27/04/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrServerWriterTest {
    @Mock
    private SolrServer solrServer;
    private SolrServerWriter<QuickGODocument> solrServerWriter;

    @Before
    public void setUp() {
        solrServerWriter = new SolrServerWriter<>(solrServer);
    }

    @Test
    public void writerWillSubmitDocumentsForIndexingToSolrServer() throws Exception {
        List<FakeDocument> fakeDocuments = new ArrayList<>();
        solrServerWriter.write(fakeDocuments);

        // ensure solr server has sent the documents off for indexing
        verify(solrServer).addBeans(fakeDocuments);
    }

    private static class FakeDocument implements QuickGODocument {
        @Override public String getUniqueName() {
            return "I'm fake";
        }
    }

}