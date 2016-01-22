package uk.ac.ebi.quickgo.rest.controller.search;

import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocMocker;
import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.repo.solr.io.ontology.OntologyRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OntologySearchIT extends SearchControllerSetup {
    @Autowired
    private OntologyRepository repository;

    private static final String ONTOLOGY_RESOURCE_URL = SEARCH_RESOURCE_URL + "/ontology";

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();
        resourceUrl = ONTOLOGY_RESOURCE_URL;
    }

    @Test
    public void requestWithInValidFacetFieldReturns400Response() throws Exception {
        OntologyDocument doc1 = OntologyDocMocker.createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = OntologyDocMocker.createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = OntologyDocMocker.createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        checkInvalidFacetResponse("go", "incorrect_field");
    }

    @Test
    public void requestWithValidFacetFieldReturnsResponseWithFacetInResult() throws Exception {
        OntologyDocument doc1 = OntologyDocMocker.createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = OntologyDocMocker.createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = OntologyDocMocker.createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse("go", "aspect");
    }

    @Test
    public void requestWithMultipleValidFacetFieldsReturnsResponseWithMultipleFacetsInResult() throws Exception {
        OntologyDocument doc1 = OntologyDocMocker.createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = OntologyDocMocker.createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = OntologyDocMocker.createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse("go", "aspect", "ontologyType");
    }

    private void saveToRepository(OntologyDocument... documents) {
        for(OntologyDocument doc : documents) {
            repository.save(doc);
        }
    }
}