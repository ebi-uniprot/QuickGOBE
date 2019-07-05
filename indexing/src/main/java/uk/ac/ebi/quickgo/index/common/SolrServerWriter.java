package uk.ac.ebi.quickgo.index.common;

import uk.ac.ebi.quickgo.common.QuickGODocument;

import java.util.List;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.batch.item.ItemWriter;

/**
 * A simple {@link ItemWriter} implementation used for indexing
 * documents directly to a {@link SolrClient} instance.
 *
 * Created 26/04/16
 * @author Edd
 */
public class SolrServerWriter<D extends QuickGODocument> implements
                                                         ItemWriter<D> {
    private final SolrClient server;
    private final String collection;

    public SolrServerWriter(SolrClient server, String collectionName) {
        this.server = server;
        this.collection = collectionName;
    }

    @Override public void write(List<? extends D> list) throws Exception {
        server.addBeans(collection, list);
    }
}
