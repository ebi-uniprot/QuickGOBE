package uk.ac.ebi.quickgo.geneproduct.common;

import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;

import java.util.Optional;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static uk.ac.ebi.quickgo.geneproduct.common.common.GeneProductDocMocker.*;

/**
 * Tests the behaviour of the {@link GeneProductRepository}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepoConfig.class, loader = SpringApplicationContextLoader.class)
public class GeneProductRepositoryIT {
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Autowired
    private GeneProductRepository geneProductRepository;

    @Before
    public void before() {
        geneProductRepository.deleteAll();
    }

    @Test
    public void addGeneProductToRepository() throws Exception {
        String id = "geneProduct1";

        GeneProductDocument doc = createDocWithId(id);

        GeneProductDocument savedDoc = geneProductRepository.save(doc);

        assertThat(savedDoc, is(doc));
    }

    @Test
    public void add3GeneProductTosRepository() throws Exception {
        String id1 = "geneProduct1";
        String id2 = "geneProduct2";
        String id3 = "geneProduct3";

        GeneProductDocument doc1 = createDocWithId(id1);
        GeneProductDocument doc2 = createDocWithId(id2);
        GeneProductDocument doc3 = createDocWithId(id3);

        geneProductRepository.save(doc1);
        geneProductRepository.save(doc2);
        geneProductRepository.save(doc3);

        assertThat(geneProductRepository.findAll(), containsInAnyOrder(doc1, doc2, doc3));
    }

    @Test
    public void removeGeneProductFromRepository() {
        String id = "geneProduct1";

        GeneProductDocument doc = createDocWithId(id);

        geneProductRepository.save(doc);

        geneProductRepository.delete(id);

        Optional<GeneProductDocument> retrievedDoc = geneProductRepository.findById(id);

        assertThat(retrievedDoc.isPresent(), is(false));
    }

    @Test
    public void lookupGeneProductById() {
        String id = "geneProduct1";

        GeneProductDocument doc = createDocWithId(id);

        geneProductRepository.save(doc);

        Optional<GeneProductDocument> retrievedDoc = geneProductRepository.findById(id);

        assertThat(retrievedDoc.isPresent(), is(true));
        assertThat(retrievedDoc.get(), is(doc));

    }
}
