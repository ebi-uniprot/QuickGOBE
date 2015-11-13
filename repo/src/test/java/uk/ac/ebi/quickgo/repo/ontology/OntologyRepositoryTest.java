package uk.ac.ebi.quickgo.repo.ontology;

import uk.ac.ebi.quickgo.config.AppContext;
import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;

import java.util.Collections;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

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

    @After
    public void after() {
        ontologyRepository.deleteAll();
    }

    @Test
    public void add1DocumentThenFind1Documents() {
        OntologyDocument od = new OntologyDocument();
        od.id = "hello";
        ontologyRepository.save(od);
        int size = 0;
        for (OntologyDocument doc : ontologyRepository.findAll()) {
            size++;
        }
        assertThat(size, is(1));
        //        ontologyRepository.findAll().forEach(doc -> System.out.println("[DOC INFO] "+doc.id+", "+doc.name));
    }

    @Test
    public void add3DocumentsThenFind3Documents() {
        ontologyRepository.save(createOntologyDocument("A", "Alice Cooper"));
        ontologyRepository.save(createOntologyDocument("B", "Bob The Builder"));
        ontologyRepository.save(createOntologyDocument("C", "Clint Eastwood"));
        int size = 0;
        for (OntologyDocument od : ontologyRepository.findAll()) {
            size++;
        }
        assertThat(size, is(3));
    }

    @Test
    public void findOneDocByName() {
        OntologyDocument ontologyDocument = new OntologyDocument();
        ontologyDocument.id = "1";
        ontologyDocument.name = "Bill";
        ontologyRepository.save(ontologyDocument);
        Page<OntologyDocument> results =
                ontologyRepository.findByNameIn(Collections.singletonList("Bill"), new PageRequest(0, 1));
        assertThat(results.getTotalPages(), is(1));
        assertThat(results.getTotalElements(), is(1L));
    }

    @Test
    public void doNotFindOneDocByWrongName() {
        OntologyDocument ontologyDocument = new OntologyDocument();
        ontologyDocument.id = "1";
        ontologyDocument.name = "Bill";
        ontologyRepository.save(ontologyDocument);
        Page<OntologyDocument> results =
                ontologyRepository.findByNameIn(Collections.singletonList("Bil"), new PageRequest(0, 1));
        assertThat(results.getTotalPages(), is(0));
        assertThat(results.getTotalElements(), is(0L));
    }

    private static OntologyDocument createOntologyDocument(String id, String name) {
        OntologyDocument ontologyDocument = new OntologyDocument();
        ontologyDocument.id = id;
        ontologyDocument.name = name;
        return ontologyDocument;
    }

}