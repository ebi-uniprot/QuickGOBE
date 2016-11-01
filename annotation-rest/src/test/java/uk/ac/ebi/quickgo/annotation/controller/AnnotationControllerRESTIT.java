package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.ConvertedOntologyFilter;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepoConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.GO_ID;
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
    private static final String RESOURCE_URL = "/annotation";
    private static final String BASE_URL = "http://localhost";
    private static final String COMMA = ",";
    private static final String DESCENDANTS_RESOURCE_FORMAT = "/ontology/go/terms/%s/descendants?relations=%s";
    private static final String IS_A = "is_a";
    private static final String DESCENDANTS_USAGE = "descendants";
    private static final String SLIM_USAGE = "slim";
    private static final String GO_USAGE = "goUsage";
    private static final String GO_USAGE_RELATIONS = "goUsageRelationships";
    private static final String SEARCH_RESOURCE = RESOURCE_URL + "/search";

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AnnotationRepository annotationRepository;
    @Autowired
    private RestOperations restOperations;

    private MockMvc mockMvc;
    private MockRestServiceServer mockRestServiceServer;
    private ObjectMapper dtoMapper;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();
        mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
        dtoMapper = new ObjectMapper();
    }

    @Test
    public void filterFor1GOTermBy0ValidDescendantMeansFilterEverything()
            throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        expectRestCallHasDescendants(singletonList(goId(1)), emptyList(), emptyList());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, DESCENDANTS_USAGE)
                        .param(GO_ID, goId(1)));

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

        expectRestCallHasDescendants(singletonList(goId(1)), emptyList(), singletonList(goId(3)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, DESCENDANTS_USAGE)
                        .param(GO_ID, goId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(GO_ID_FIELD, goId(3)));
    }

    @Test
    public void filterFor1GOTermBy2ValidDescendants()
            throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));

        expectRestCallHasDescendants(
                singletonList(goId(1)),
                singletonList(IS_A),
                asList(goId(2), goId(3)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, DESCENDANTS_USAGE)
                        .param(GO_ID, goId(1))
                        .param(GO_USAGE_RELATIONS, IS_A));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(GO_ID_FIELD, goId(2), goId(3)));
    }

    @Test
    public void filterFor2GOTermsBy0ValidDescendantsMeansFilterEverything() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));

        expectRestCallHasDescendants(asList(goId(1), goId(2)), singletonList(IS_A), emptyList());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, DESCENDANTS_USAGE)
                        .param(GO_ID, goId(1) + "," + goId(2))
                        .param(GO_USAGE_RELATIONS, IS_A));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterFor2GOTermsBy1ValidDescendant() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));

        String goIdsCSV = goId(1) + "," + goId(2);
        expectRestCallHasDescendants(asList(goId(1), goId(2)), emptyList(), singletonList(goId(3)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, DESCENDANTS_USAGE)
                        .param(GO_ID, goIdsCSV));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(GO_ID_FIELD, goId(3)));
    }

    @Test
    public void filterFor2GOTermsBy2ValidDescendants() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(4), goId(4)));

        String goIdsCSV = goId(1) + "," + goId(2);
        expectRestCallHasDescendants(asList(goId(1), goId(2)), emptyList(), asList(goId(3), goId(4)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, DESCENDANTS_USAGE)
                        .param(GO_ID, goIdsCSV));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(GO_ID_FIELD, goId(3), goId(4)));
    }

    @Test
    public void wrongResourcePathForDescendantsCausesErrorMessage() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, DESCENDANTS_USAGE)
                        .param(GO_ID, goId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void fetchTimeoutForDescendantFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        expectRestCallResponse(GET, buildResource(DESCENDANTS_RESOURCE_FORMAT, goId(1)), withTimeout());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, DESCENDANTS_USAGE)
                        .param(GO_ID, goId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void serverErrorForDescendantFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        expectRestCallResponse(GET, buildResource(DESCENDANTS_RESOURCE_FORMAT, goId(1)), withServerError());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, DESCENDANTS_USAGE)
                        .param(GO_ID, goId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void badRequestForDescendantFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        expectRestCallResponse(GET, buildResource(DESCENDANTS_RESOURCE_FORMAT, goId(1)), withBadRequest());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, DESCENDANTS_USAGE)
                        .param(GO_ID, goId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    // slim
    @Test
    public void slimFilterWhenThereAreNoDescendantsMeansFilterEverything() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        expectRestCallHasDescendants(singletonList(goId(1)), emptyList(), emptyList());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, SLIM_USAGE)
                        .param(GO_ID, goId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void slimFilterFor1GOTermWith1Descendant() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));

        expectRestCallHasDescendants(singletonList(goId(1)), emptyList(), singletonList(goId(3)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, SLIM_USAGE)
                        .param(GO_ID, goId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GO_ID_FIELD, goId(3)))
                .andExpect(valuesOccurInField(SLIMMED_ID_FIELD, singletonList(singletonList(goId(1)))));
    }

    @Test
    public void slimFilterFor1GOTermWith2Descendants() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));

        expectRestCallHasDescendants(
                singletonList(goId(1)),
                singletonList(IS_A),
                asList(goId(2), goId(3)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, SLIM_USAGE)
                        .param(GO_USAGE_RELATIONS, IS_A)
                        .param(GO_ID, goId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccurInField(GO_ID_FIELD, goId(2), goId(3)))
                .andExpect(valuesOccurInField(SLIMMED_ID_FIELD,
                        asList(
                                singletonList(goId(1)),
                                singletonList(goId(1)))));
    }

    @Test
    public void slimFilterFor2GOTermsWith0ValidDescendantsMeansFilterEverything() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));

        expectRestCallHasDescendants(asList(goId(1), goId(2)), singletonList(IS_A), emptyList());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, SLIM_USAGE)
                        .param(GO_ID, goId(1) + "," + goId(2))
                        .param(GO_USAGE_RELATIONS, IS_A));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void slimFilterFor2GOTermsWith1Descendant() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));

        String goIdsCSV = goId(1) + "," + goId(2);
        expectRestCallHasDescendants(asList(goId(1), goId(2)), emptyList(), singletonList(goId(3)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, SLIM_USAGE)
                        .param(GO_ID, goIdsCSV));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GO_ID_FIELD, goId(3)))
                .andExpect(valuesOccurInField(SLIMMED_ID_FIELD, singletonList(asList(goId(1), goId(2)))));
    }

    @Test
    public void slimFilterFor2GOTermsWith2Descendants() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(3), goId(3)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(4), goId(4)));

        String goIdsCSV = goId(1) + "," + goId(2);
        expectRestCallHasDescendants(asList(goId(1), goId(2)), emptyList(), asList(goId(3), goId(4)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, SLIM_USAGE)
                        .param(GO_ID, goIdsCSV));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccurInField(GO_ID_FIELD, goId(3), goId(4)))
                .andExpect(valuesOccurInField(SLIMMED_ID_FIELD,
                        asList(
                                asList(goId(1), goId(2)),
                                asList(goId(1), goId(2)))));
    }

    @Test
    public void wrongResourcePathForSlimCausesErrorMessage() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, SLIM_USAGE)
                        .param(GO_ID, goId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void fetchTimeoutForSlimFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        expectRestCallResponse(GET, buildResource(DESCENDANTS_RESOURCE_FORMAT, goId(1)), withTimeout());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, SLIM_USAGE)
                        .param(GO_ID, goId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void serverErrorForSlimFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        expectRestCallResponse(GET, buildResource(DESCENDANTS_RESOURCE_FORMAT, goId(1)), withServerError());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, SLIM_USAGE)
                        .param(GO_ID, goId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void badRequestForSlimFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithGoId(gpId(1), goId(1)));
        annotationRepository.save(createAnnotationDocWithGoId(gpId(2), goId(2)));

        expectRestCallResponse(GET, buildResource(DESCENDANTS_RESOURCE_FORMAT, goId(1)), withBadRequest());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(GO_USAGE, SLIM_USAGE)
                        .param(GO_ID, goId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
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

    private String buildResource(String format, String... arguments) {
        int requiredArgsCount = format.length() - format.replace("%", "").length();
        List<String> args = new ArrayList<>();
        for (int i = 0; i < requiredArgsCount; i++) {
            if (i < arguments.length) {
                args.add(arguments[i]);
            } else {
                args.add("");
            }
        }
        return String.format(format, args.toArray());
    }

    private void expectRestCallHasDescendants(
            List<String> termIds,
            List<String> usageRelations,
            List<String> descendants) {

        String termIdsCSV = termIds.stream().collect(Collectors.joining(COMMA));
        String relationsCSV = usageRelations.stream().collect(Collectors.joining(COMMA));

        expectRestCallSuccess(
                GET,
                buildResource(
                        DESCENDANTS_RESOURCE_FORMAT,
                        termIdsCSV,
                        relationsCSV),
                constructResponseObject(termIds, descendants));
    }

    private String constructResponseObject(List<String> termIds, List<String> descendants) {
        ConvertedOntologyFilter response = new ConvertedOntologyFilter();
        List<ConvertedOntologyFilter.Result> results = new ArrayList<>();

        termIds.forEach(t -> {
            ConvertedOntologyFilter.Result result = new ConvertedOntologyFilter.Result();
            result.setId(t);
            result.setDescendants(descendants);
            results.add(result);
        });

        response.setResults(results);
        try {
            return dtoMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Problem constructing mocked REST response:", e);
        }
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