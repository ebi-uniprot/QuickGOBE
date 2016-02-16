package uk.ac.ebi.quickgo.geneproduct.common;

import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;

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
}
