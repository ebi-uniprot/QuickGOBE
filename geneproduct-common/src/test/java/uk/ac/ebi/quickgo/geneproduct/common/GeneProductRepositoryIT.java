package uk.ac.ebi.quickgo.geneproduct.common;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.solr.core.SolrTemplate;
import uk.ac.ebi.quickgo.common.SolrCollectionName;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static uk.ac.ebi.quickgo.geneproduct.common.common.GeneProductDocMocker.createDocWithId;

/**
 * Tests the behaviour of the {@link GeneProductRepository}
 */
@ExtendWith(TemporarySolrDataStore.class)
@SpringBootTest(classes = GeneProductRepoConfig.class)
class GeneProductRepositoryIT {
    private static final String COLLECTION = SolrCollectionName.GENE_PRODUCT;

    @Autowired
    private GeneProductRepository geneProductRepository;

    @Autowired
    private SolrTemplate geneProductTemplate;

    @BeforeEach
    void before() {
        geneProductRepository.deleteAll();
    }

    @Test
    void addGeneProductToRepository() throws Exception {
        String id = "geneProduct1";

        GeneProductDocument doc = createDocWithId(id);

        GeneProductDocument savedDoc = geneProductRepository.save(doc);

        assertThat(savedDoc, is(doc));
    }

    @Test
    void add3GeneProductTosRepository() throws Exception {
        String id1 = "geneProduct1";
        String id2 = "geneProduct2";
        String id3 = "geneProduct3";

        GeneProductDocument doc1 = createDocWithId(id1);
        GeneProductDocument doc2 = createDocWithId(id2);
        GeneProductDocument doc3 = createDocWithId(id3);

        geneProductRepository.save(doc1);
        geneProductRepository.save(doc2);
        geneProductRepository.save(doc3);

        Collection<String> extractedIds = extractIds(geneProductRepository.findAll());

        assertThat(extractedIds, containsInAnyOrder(id1, id2, id3));
    }

    private Collection<String> extractIds(Iterable<GeneProductDocument> gpDocuments) {
        List<String> ids = new ArrayList<>();

        gpDocuments.forEach(gpDoc -> ids.add(gpDoc.id));

        return ids;
    }

    @Test
    void removeGeneProductFromRepository() throws IOException, SolrServerException {
        String id = "geneProduct1";

        GeneProductDocument doc = createDocWithId(id);

        geneProductRepository.save(doc);

        deleteFromRepositoryByIds(doc);

        List<GeneProductDocument> retrievedDoc = geneProductRepository.findById(Collections.singletonList(id));

        assertThat(retrievedDoc.isEmpty(), is(true));
    }

    @Test
    void lookupGeneProductById() {
        String id = "geneProduct1";

        GeneProductDocument doc = createDocWithId(id);

        geneProductRepository.save(doc);

        List<GeneProductDocument> retrievedDoc = geneProductRepository.findById(Collections.singletonList(id));

        assertThat(retrievedDoc.isEmpty(), is(false));
        assertThat(retrievedDoc.get(0).id, is(id));

    }

    /**
     * Deleting from a repository is a special case when the schema.xml defines a non-"string"
     * analyzer on the field used to identify the documents to delete. We use a lower-casing analyser
     * for IDs, which means the geneProductTemplate.deleteById fails.
     * <p>
     * To get the desired behaviour, delete by accessing the solr server instance directly, so that
     * the request goes through a query, which is subject to the same analyser used for indexing.
     *
     * @param docs the documents to delete
     * @throws SolrServerException
     * @throws IOException
     */
    private void deleteFromRepositoryByIds(GeneProductDocument... docs) throws SolrServerException, IOException {
        for (GeneProductDocument doc : docs) {
            geneProductTemplate.getSolrClient().deleteByQuery(COLLECTION,GeneProductFields.Searchable.ID + ":" + doc.id);
        }
        geneProductTemplate.getSolrClient().commit(COLLECTION);
    }
}
