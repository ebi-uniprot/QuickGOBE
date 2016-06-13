package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepoConfig;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.document.Aspect;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createAnnotationDoc;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.*;

/**
 * Tests filter parameters in the {@link AnnotationController} that require joins between different collections/tables.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AnnotationREST.class, OntologyRepoConfig.class})
@WebAppConfiguration
public class AnnotationControllerJoinsIT {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    private static final String RESOURCE_URL = "/QuickGO/services/annotation";

    private static final String ASPECT_PARAM = "aspect";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AnnotationRepository annotationRepository;

    @Autowired
    private OntologyRepository ontologyRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test public void
    filterByCellularComponentAspectHitsAnnotationDocumentWhoseOntologyIdentifierMatchesAnOntologyDocumentWithACellularComponentAspect()
            throws Exception {
        String geneProductId = "P99999";
        String goId = "GO:0003870";
        String aspect = "cellular_component";

        AnnotationDocument annotationDoc = createAnnotationDocWithGoId(geneProductId, goId);
        annotationRepository.save(annotationDoc);

        OntologyDocument ontologyDoc = createOntolgyDocWithAspect(goId, aspect);
        ontologyRepository.save(ontologyDoc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASPECT_PARAM, aspect));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId));
    }

    @Test
    public void
    filterByCellularComponentAspectDoesNotHitAnnotationDocumentBecauseOntologyIdentifierMatchesAnOntologyDocumentWithAMolecularFunctionAspect()
            throws Exception {
        String geneProductId = "P99999";
        String goId = "GO:0003870";
        String ontologyAspect = "molecular_function";
        String queryAspect = "cellular_component";

        AnnotationDocument annotationDoc = createAnnotationDocWithGoId(geneProductId, goId);
        annotationRepository.save(annotationDoc);

        OntologyDocument ontologyDoc = createOntolgyDocWithAspect(goId, ontologyAspect);
        ontologyRepository.save(ontologyDoc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASPECT_PARAM, queryAspect));

        response.andExpect(status().isOk())
                .andExpect(totalNumOfResults(0))
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists());
    }

    @After
    public void deleteCores() {
        annotationRepository.deleteAll();
        ontologyRepository.deleteAll();
    }

    private AnnotationDocument createAnnotationDocWithGoId(String geneProductId, String goId) {
        AnnotationDocument doc = createAnnotationDoc(geneProductId);
        doc.goId = goId;

        return doc;
    }

    private OntologyDocument createOntolgyDocWithAspect(String goId, String aspect) {
        OntologyDocument doc = OntologyDocMocker.createECODoc(goId, goId);
        doc.aspect = aspect;

        return doc;
    }
}