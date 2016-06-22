package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepoConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createAnnotationDoc;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.*;

/**
 * Tests filter parameters in the {@link AnnotationController} that require joins between different collections/tables.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AnnotationREST.class, OntologyRepoConfig.class})
@WebAppConfiguration
public class AnnotationControllerRESTIT {
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
    private RestOperations restOperations;

    private MockMvc mockMvc;
    private MockRestServiceServer mockRestServiceServer;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();
        mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
    }

    @Test public void
    filterByCellularComponentAspectHitsAnnotationDocumentWhoseOntologyIdentifierMatchesAnOntologyDocumentWithACellularComponentAspect()
            throws Exception {
        String geneProductId1 = "P0000001";
        String goId1 = "GO:0000001";

        String goId2 = "GO:0000002";
        String geneProductId2 = "P0000002";

        String goId3 = "GO:0000003";
        String geneProductId3 = "P0000003";

        String descendantId = goId3;

        annotationRepository.save(createAnnotationDocWithGoId(geneProductId1, goId1));
        annotationRepository.save(createAnnotationDocWithGoId(geneProductId2, goId2));
        annotationRepository.save(createAnnotationDocWithGoId(geneProductId3, goId3));

        expectRestCallSuccess(GET,
                "http://localhost/QuickGO/services/go/terms/" + goId1 + "/descendants",
                "{somekey:\"" + descendantId + "\"}");

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param("usage", "descendants")
                        .param("usageIds", goId1));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId1));
    }

    private void expectRestCallSuccess(HttpMethod method, String url, String response) {
        mockRestServiceServer.expect(
                requestTo(url))
                .andExpect(method(method))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    }

    @After
    public void deleteCores() {
        annotationRepository.deleteAll();
    }

    private AnnotationDocument createAnnotationDocWithGoId(String geneProductId, String goId) {
        AnnotationDocument doc = createAnnotationDoc(geneProductId);
        doc.goId = goId;

        return doc;
    }
}