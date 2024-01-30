package uk.ac.ebi.quickgo.index.common;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.common.QuickGODocument;

import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.SolrClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import uk.ac.ebi.quickgo.common.SolrCollectionName;

import static org.mockito.Mockito.verify;

/**
 * Created 27/04/16
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class SolrServerWriterTest {
    private static final String COLLECTION = SolrCollectionName.FAKE;
    @Mock
    private SolrClient solrServer;
    private SolrServerWriter<QuickGODocument> solrServerWriter;

    @BeforeEach
    void setUp() {
        solrServerWriter = new SolrServerWriter<>(solrServer, COLLECTION);
    }

    @Test
    void writerWillSubmitDocumentsForIndexingToSolrServer() throws Exception {
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