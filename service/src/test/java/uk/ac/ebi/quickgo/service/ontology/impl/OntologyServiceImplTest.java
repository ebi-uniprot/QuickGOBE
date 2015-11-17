package uk.ac.ebi.quickgo.service.ontology.impl;

import uk.ac.ebi.quickgo.config.AppContext;
import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.repo.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.repo.ontology.OntologyRepository;
import uk.ac.ebi.quickgo.service.ontology.OntologyService;

import java.util.List;
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
import static uk.ac.ebi.quickgo.document.ontology.OntologyDocumentMocker.Term.createECOTerm;
import static uk.ac.ebi.quickgo.document.ontology.OntologyDocumentMocker.Term.createGOTerm;

/**
 * Testing the {@link OntologyServiceImpl} class.
 *
 * Created 11/11/15
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppContext.class, loader = SpringApplicationContextLoader.class)
public class OntologyServiceImplTest {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Autowired
    private OntologyRepository ontologyRepository;

    @Autowired
    private OntologyService ontologyService;

    @Test
    public void findByGoId() {
        OntologyDocument goTerm = createGOTerm();
        goTerm.id = "0000001";

        ontologyRepository.save(goTerm);

        List<OntologyDocument> results = ontologyService.findByGoId("0000001", new PageRequest(0, 1));
        assertThat(results.size(), is(1));
        assertThat(results.get(0).id, is("0000001"));
        assertThat(results.get(0).idType, is("go"));
    }

    @Test
    public void doNotfindByWrongGoId() {
        OntologyDocument goTerm = createGOTerm();
        goTerm.id = "0000001";

        ontologyRepository.save(goTerm);

        List<OntologyDocument> results = ontologyService.findByGoId("0000002", new PageRequest(0, 1));
        assertThat(results.size(), is(0));
    }

    @Test
    public void findByEcoId() {
        OntologyDocument ecoTerm = createECOTerm();
        ecoTerm.id = "0000001";

        ontologyRepository.save(ecoTerm);

        List<OntologyDocument> results = ontologyService.findByEcoId("0000001", new PageRequest(0, 1));
        assertThat(results.size(), is(1));
        assertThat(results.get(0).id, is("0000001"));
        assertThat(results.get(0).idType, is("eco"));
    }

    @Test
    public void doNotfindByWrongEcoId() {
        OntologyDocument ecoTerm = createECOTerm();
        ecoTerm.id = "0000001";

        ontologyRepository.save(ecoTerm);

        List<OntologyDocument> results = ontologyService.findByEcoId("0000002", new PageRequest(0, 1));
        assertThat(results.size(), is(0));
    }

}