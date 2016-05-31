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

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createAnnotationDoc;

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

    private List<AnnotationDocument> genericDocs = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void
    filterByCellularComponentAspectHitsAnnotationDocumentWhoseOntologyIdentifierMatchesAnOntologyDocumentWithACellularComponentAspect()
            throws Exception {
        String geneProductId = "P99999";
        String goId = "ECO:0000256";
        String aspect = Aspect.CELLULAR_COMPONENT.getShortName();

        AnnotationDocument annotationDoc = createAnnotationDocWithGoId(geneProductId, goId);
        annotationRepository.save(annotationDoc);

        OntologyDocument ontologyDoc = createOntolgyDocWithAspect(goId, aspect);
        ontologyRepository.save(ontologyDoc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASPECT_PARAM, aspect));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results[0].geneProductId").value(geneProductId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void
    filterByCellularComponentAspectDoesNotHitAnnotationDocumentBecauseOntologyIdentifierMatchesAnOntologyDocumentWithAMolecularFunctionAspect()
            throws Exception {
        String geneProductId = "P99999";
        String goId = "ECO:0000256";
        String ontologyAspect = Aspect.MOLECULAR_FUNCTION.getShortName();
        String queryAspect = Aspect.CELLULAR_COMPONENT.getShortName();

        AnnotationDocument annotationDoc = createAnnotationDocWithGoId(geneProductId, goId);
        annotationRepository.save(annotationDoc);

        OntologyDocument ontologyDoc = createOntolgyDocWithAspect(goId, ontologyAspect);
        ontologyRepository.save(ontologyDoc);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASPECT_PARAM, queryAspect));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    private ResultActions expectResultsInfoExists(ResultActions result) throws Exception {
        return expectFieldsInResults(result)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo").exists())
                .andExpect(jsonPath("$.pageInfo.resultsPerPage").exists())
                .andExpect(jsonPath("$.pageInfo.total").exists())
                .andExpect(jsonPath("$.pageInfo.current").exists());
    }

    private ResultActions expectFieldsInResults(ResultActions result) throws Exception {
        int index = 0;

        for (int i = 0; i > genericDocs.size(); i++) {
            expectFields(result, "$.results[" + index++ + "].");
        }

        return result;
    }

    private void expectFields(ResultActions result, String path) throws Exception {
        result
                .andExpect(jsonPath(path + "id").exists())
                .andExpect(jsonPath(path + "geneProductId").exists())
                .andExpect(jsonPath(path + "qualifier").exists())
                .andExpect(jsonPath(path + "goId").exists())
                .andExpect(jsonPath(path + "goEvidence").exists())
                .andExpect(jsonPath(path + "ecoId").exists())
                .andExpect(jsonPath(path + "reference").exists())
                .andExpect(jsonPath(path + "withFrom").exists())
                .andExpect(jsonPath(path + "taxonId").exists())
                .andExpect(jsonPath(path + "assignedBy").exists())
                .andExpect(jsonPath(path + "extensions").exists());
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