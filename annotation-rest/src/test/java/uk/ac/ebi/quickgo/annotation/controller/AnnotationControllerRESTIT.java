package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepoConfig;

import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
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

    private static final String FAILED_REST_FETCH_PREFIX = "Failed to fetch REST response";
    private static final String RESOURCE_URL = "/QuickGO/services/annotation";
    private static final String BASE_URL = "http://localhost";
    private static final String COMMA = ",";
    private static final String DESCENDANTS_RESOURCE_FORMAT = "/QuickGO/services/go/terms/%s/descendants";

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

    @Test
    public void filterFor1GOTermBy0ValidDescendantMeansFilterEverything()
            throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        expectGoIdsHaveDescendants(goId(1));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param("usage", "descendants")
                        .param("usageIds", goId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterFor1GOTermBy1ValidDescendant()
            throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));

        expectGoIdsHaveDescendants(goId(1), goId(3));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param("usage", "descendants")
                        .param("usageIds", goId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GO_ID_FIELD, goId(3)));
    }

    @Test
    public void filterFor1GOTermBy2ValidDescendants()
            throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));

        expectGoIdsHaveDescendants(goId(1), goId(2), goId(3));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param("usage", "descendants")
                        .param("usageIds", goId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccurInField(GO_ID_FIELD, goId(2), goId(3)));
    }

    @Test
    public void filterFor2GOTermsBy0ValidDescendantsMeansFilterEverything() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));

        String goIdsCSV = goId(1) + "," + goId(2);
        expectGoIdsHaveDescendants(goIdsCSV);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param("usage", "descendants")
                        .param("usageIds", goIdsCSV));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterFor2GOTermsBy1ValidDescendant() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));

        String goIdsCSV = goId(1) + "," + goId(2);
        expectGoIdsHaveDescendants(goIdsCSV, goId(3));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param("usage", "descendants")
                        .param("usageIds", goIdsCSV));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GO_ID_FIELD, goId(3)));
    }

    @Test
    public void filterFor2GOTermsBy2ValidDescendants() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(4), goId(4)));

        String goIdsCSV = goId(1) + "," + goId(2);
        expectGoIdsHaveDescendants(goIdsCSV, goId(3), goId(4));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param("usage", "descendants")
                        .param("usageIds", goIdsCSV));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccurInField(GO_ID_FIELD, goId(3), goId(4)));
    }

    @Test
    public void wrongResourcePathForDescendantsCausesErrorMessage() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param("usage", "descendants")
                        .param("usageIds", goId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valuesOccurInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void fetchTimeoutForDescendantFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        expectRestCallResponse(GET, String.format(DESCENDANTS_RESOURCE_FORMAT, goId(1)), withTimeout());

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param("usage", "descendants")
                        .param("usageIds", goId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valuesOccurInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void serverErrorForDescendantFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        expectRestCallResponse(GET, String.format(DESCENDANTS_RESOURCE_FORMAT, goId(1)), withServerError());

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param("usage", "descendants")
                        .param("usageIds", goId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valuesOccurInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void badRequestForDescendantFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        expectRestCallResponse(GET, String.format(DESCENDANTS_RESOURCE_FORMAT, goId(1)), withBadRequest());

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param("usage", "descendants")
                        .param("usageIds", goId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valuesOccurInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @After
    public void deleteCoreContents() {
        annotationRepository.deleteAll();
    }

    private String gpId(int id) {
        return String.format("P00000%d", id);
    }

    private String goId(int id) {
        return String.format("GO:000000%d", id);
    }

    private void expectGoIdsHaveDescendants(String termId, String... descendantIds) {
        String descendantsCSV = Stream.of(descendantIds).collect(Collectors.joining(COMMA));
        expectRestCallSuccess(
                GET,
                String.format(DESCENDANTS_RESOURCE_FORMAT, termId),
                "{ results : [ { descendants : [" + descendantsCSV + "] } ]}");
    }

    private void expectRestCallSuccess(HttpMethod method, String url, String response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + url))
                .andExpect(method(method))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    }

    private void expectRestCallResponse(HttpMethod method, String url, ResponseCreator response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + url))
                .andExpect(method(method))
                .andRespond(response);
    }

    private AnnotationDocument createAnnotationDocWithGoId(String geneProductId, String goId) {
        AnnotationDocument doc = createAnnotationDoc(geneProductId);
        doc.goId = goId;

        return doc;
    }
}