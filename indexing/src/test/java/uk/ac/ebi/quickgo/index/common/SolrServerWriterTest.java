package uk.ac.ebi.quickgo.index.common;

import uk.ac.ebi.quickgo.common.QuickGODocument;

import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.SolrClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.ac.ebi.quickgo.common.SolrCollectionName;

import static org.mockito.Mockito.verify;

/**
 * Created 27/04/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrServerWriterTest {
    private static final String COLLECTION = SolrCollectionName.FAKE;
    @Mock
    private SolrClient solrServer;
    private SolrServerWriter<QuickGODocument> solrServerWriter;

    @Before
    public void setUp() {
        solrServerWriter = new SolrServerWriter<>(solrServer, COLLECTION);
    }

    @Test
    public void writerWillSubmitDocumentsForIndexingToSolrServer() throws Exception {
        List<FakeDocument> fakeDocuments = new ArrayList<>();
        solrServerWriter.write(fakeDocuments);

        // ensure solr server has sent the documents off for indexing
        verify(solrServer).addBeans(COLLECTION, fakeDocuments);
    }

    private static class FakeDocument implements QuickGODocument {
        @Override public String getUniqueName() {
            return "I'm fake";
        }
    }

}