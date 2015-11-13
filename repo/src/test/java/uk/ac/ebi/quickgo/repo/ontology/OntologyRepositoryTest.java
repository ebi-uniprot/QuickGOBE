package uk.ac.ebi.quickgo.repo.ontology;

import uk.ac.ebi.quickgo.config.AppContext;
import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.repo.TemporarySolrDataStore;

import java.util.List;
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
import static uk.ac.ebi.quickgo.repo.ontology.OntologyDocumentMocker.Term.createGOTerm;
import static uk.ac.ebi.quickgo.repo.ontology.OntologyDocumentMocker.createSimpleOntologyDocument;

/**
 * Test that the ontology repository can be accessed as expected.
 * Created 11/11/15
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppContext.class, loader = SpringApplicationContextLoader.class)
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
        OntologyDocument od = new OntologyDocument();
        od.id = "hello";

        ontologyRepository.save(od);

        assertThat(ontologyRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(1L));
    }

    @Test
    public void add3DocumentsThenFind3Documents() {
        ontologyRepository.save(createSimpleOntologyDocument("A", "Alice Cooper"));
        ontologyRepository.save(createSimpleOntologyDocument("B", "Bob The Builder"));
        ontologyRepository.save(createSimpleOntologyDocument("C", "Clint Eastwood"));

        assertThat(ontologyRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(3L));
    }

    @Test
    public void findByDocTypeAndIdTypeAndId() {
        OntologyDocument goTerm = createGOTerm();
        goTerm.id = "0000001";

        ontologyRepository.save(goTerm);

        List<OntologyDocument> results =
                ontologyRepository.findByTermId("term", "go", "0000001", new PageRequest(0, 1));

        assertThat(results.size(), is(1));
        assertThat(results.get(0).id, is("0000001"));
        assertThat(results.get(0).idType, is("go"));
    }

    @Test
    public void doNotFindByDocTypeAndIdTypeAndId() {
        OntologyDocument goTerm = createGOTerm();
        goTerm.id = "0000001";

        ontologyRepository.save(goTerm);

        List<OntologyDocument> results =
                ontologyRepository.findByTermId("term", "go", "0000002", new PageRequest(0, 1));

        assertThat(results.size(), is(0));
    }
}