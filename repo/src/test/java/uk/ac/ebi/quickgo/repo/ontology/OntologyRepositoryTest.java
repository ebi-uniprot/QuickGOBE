package uk.ac.ebi.quickgo.repo.ontology;

import uk.ac.ebi.quickgo.config.RepoConfig;
import uk.ac.ebi.quickgo.repo.TemporarySolrDataStore;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.document.ontology.OntologyDocMocker.createGODoc;

/**
 * Test that the ontology repository can be accessed as expected.
 *
 * Created 11/11/15
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepoConfig.class, loader = SpringApplicationContextLoader.class)
public class OntologyRepositoryTest {

    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Autowired
    private OntologyRepository ontologyRepository;

    @Before
    public void before() {
        ontologyRepository.deleteAll();
    }

    @Test
    public void add1DocumentThenFind1Documents() {
        ontologyRepository.save(createGODoc("A", "Alice Cooper"));

        assertThat(ontologyRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(1L));
    }

    @Test
    public void add3DocumentsThenFind3Documents() {
        ontologyRepository.save(createGODoc("A", "Alice Cooper"));
        ontologyRepository.save(createGODoc("B", "Bob The Builder"));
        ontologyRepository.save(createGODoc("C", "Clint Eastwood"));

        assertThat(ontologyRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(3L));
    }

    // test cannot find document behaviour

}