package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.IdGeneratorUtil;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepoConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
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

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.INCLUDE_FIELD_PARAM;
import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createGPId;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createAnnotationDoc;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.contentTypeToBeJson;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.pageInfoExists;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.totalNumOfResults;

/**
 * The purpose of this class is to integration test the annotation result transformation components
 * of the annotation RESTful application.
 *
 * Created 07/04/17
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AnnotationREST.class, OntologyRepoConfig.class})
@WebAppConfiguration
public class AnnotationTransformerControllerIT {
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    private static final String SEARCH_RESOURCE = "/annotation/search";
    private static final String BASE_URL = "https://localhost";
    private static final String GO_NAME_FIELD = "goName";
    private static final String TAXON_NAME_FIELD = "taxonName";
    private static final String GO_TERM_RESOURCE_FORMAT = "/ontology/go/terms/%s";
    private static final String TAXONOMY_ID_NODE_RESOURCE_FORMAT = "/proteins/api/taxonomy/id/%s/node";

    @Autowired
    private AnnotationRepository annotationRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
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

        annotationRepository.deleteAll();
    }

    // ----------------- gene ontology name values -----------------
    @Test
    public void requestOmittingGoNameProducesResultWhereGoNameIsNull() throws Exception {
        annotationRepository.save(createAnnotationDoc(createGPId(1), goId(1)));

        expectGoTermsHaveGoNamesViaRest(singletonList(goId(1)), singletonList(goName(1)));

        ResultActions response = mockMvc.perform(get(SEARCH_RESOURCE));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(jsonPath("$.results[0].goName", is(nullValue())))
                .andExpect(totalNumOfResults(1));
    }

    @Test
    public void includeGoNameForOneTermFetchesNameFromExternalServiceSuccessfully() throws Exception {
        annotationRepository.save(createAnnotationDoc(createGPId(0), goId(0)));

        expectGoTermsHaveGoNamesViaRest(singletonList(goId(0)), singletonList(goName(0)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(INCLUDE_FIELD_PARAM.getName(), GO_NAME_FIELD));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(jsonPath("$.results[0].goId", is(goId(0))))
                .andExpect(jsonPath("$.results[0].goName", is(goName(0))))
                .andExpect(totalNumOfResults(1));
    }

    @Test
    public void includeGoNameForMultipleTermsFetchesNameFromExternalServiceSuccessfully() throws Exception {
        annotationRepository.save(createAnnotationDoc(createGPId(0), goId(0)));
        annotationRepository.save(createAnnotationDoc(createGPId(1), goId(1)));

        expectGoTermsHaveGoNamesViaRest(asList(goId(0), goId(1)), asList(goName(0), goName(1)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(INCLUDE_FIELD_PARAM.getName(), GO_NAME_FIELD));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(jsonPath("$.results[0].goId", is(goId(0))))
                .andExpect(jsonPath("$.results[0].goName", is(goName(0))))
                .andExpect(jsonPath("$.results[1].goId", is(goId(1))))
                .andExpect(jsonPath("$.results[1].goName", is(goName(1))))
                .andExpect(totalNumOfResults(2));
    }

    // ----------------- taxonomy name values -----------------
    @Test
    public void requestOmittingTaxonNameProducesResultWhereTaxonNameIsNull() throws Exception {
        annotationRepository.save(annotationDocWithTaxon(goId(1), taxonId(1)));

        expectTaxonIdHasGivenTaxonNameViaRest(taxonId(1), taxonName(1));

        ResultActions response = mockMvc.perform(get(SEARCH_RESOURCE));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(jsonPath("$.results[0].taxonName", is(nullValue())))
                .andExpect(totalNumOfResults(1));
    }

    @Test
    public void includeTaxonNameForOneTermFetchesNameFromExternalServiceSuccessfully() throws Exception {
        annotationRepository.save(annotationDocWithTaxon(goId(1), taxonId(1)));

        expectTaxonIdHasGivenTaxonNameViaRest(taxonId(1), taxonName(1));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(INCLUDE_FIELD_PARAM.getName(), TAXON_NAME_FIELD));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(jsonPath("$.results[0].goId", is(goId(1))))
                .andExpect(jsonPath("$.results[0].taxonId", is(taxonId(1))))
                .andExpect(jsonPath("$.results[0].taxonName", is(taxonName(1))))
                .andExpect(totalNumOfResults(1));
    }

    @Test
    public void includeTaxonNameForMultipleTermsFetchesNameFromExternalServiceSuccessfully() throws Exception {
        annotationRepository.save(annotationDocWithTaxon(goId(1), taxonId(1)));
        annotationRepository.save(annotationDocWithTaxon(goId(2), taxonId(2)));

        expectTaxonIdHasGivenTaxonNameViaRest(taxonId(1), taxonName(1));
        expectTaxonIdHasGivenTaxonNameViaRest(taxonId(2), taxonName(2));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(INCLUDE_FIELD_PARAM.getName(), TAXON_NAME_FIELD));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(jsonPath("$.results[0].goId", is(goId(1))))
                .andExpect(jsonPath("$.results[0].taxonId", is(taxonId(1))))
                .andExpect(jsonPath("$.results[0].taxonName", is(taxonName(1))))
                .andExpect(jsonPath("$.results[1].goId", is(goId(2))))
                .andExpect(jsonPath("$.results[1].taxonId", is(taxonId(2))))
                .andExpect(jsonPath("$.results[1].taxonName", is(taxonName(2))))
                .andExpect(totalNumOfResults(2));
    }

    @Test
    public void includeGoAndTaxonNameFetchesNamesFromTwoExternalServicesSuccessfully() throws Exception {
        AnnotationDocument doc = createAnnotationDoc(createGPId(1), goId(1));
        doc.taxonId = taxonId(1);
        annotationRepository.save(doc);

        expectGoTermsHaveGoNamesViaRest(singletonList(goId(1)), singletonList(goName(1)));
        expectTaxonIdHasGivenTaxonNameViaRest(taxonId(1), taxonName(1));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(INCLUDE_FIELD_PARAM.getName(), GO_NAME_FIELD + "," + TAXON_NAME_FIELD));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(jsonPath("$.results[0].goId", is(goId(1))))
                .andExpect(jsonPath("$.results[0].goName", is(goName(1))))
                .andExpect(jsonPath("$.results[0].taxonId", is(taxonId(1))))
                .andExpect(jsonPath("$.results[0].taxonName", is(taxonName(1))))
                .andExpect(totalNumOfResults(1));
    }

    // ----------------- external service error handling -----------------
    // the follow tests are relevant for all instances of ResponseValueInjector, and
    // not only OntologyNameInjector -- which is used to show the behaviour the results transformer
    @Test
    public void doNotPopulateValueWhenExternalServiceProduces404() throws Exception {
        annotationRepository.save(createAnnotationDoc(createGPId(0), goId(0)));
        annotationRepository.save(createAnnotationDoc(createGPId(1), goId(1)));

        expectRestCallResponse(GET, buildResource(GO_TERM_RESOURCE_FORMAT, goId(0)), withStatus(HttpStatus.NOT_FOUND));
        expectGoTermsHaveGoNamesViaRest(singletonList(goId(1)), singletonList(goName(1)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(INCLUDE_FIELD_PARAM.getName(), GO_NAME_FIELD));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(jsonPath("$.results[0].goId", is(goId(0))))
                .andExpect(jsonPath("$.results[0].goName", is(nullValue())))
                .andExpect(jsonPath("$.results[1].goId", is(goId(1))))
                .andExpect(jsonPath("$.results[1].goName", is(goName(1))))
                .andExpect(totalNumOfResults(2));
    }

    @Test
    public void injectingValueProducesErrorWhenExternalServiceProducesTimeoutError() throws Exception {
        annotationRepository.save(createAnnotationDoc(createGPId(0), goId(0)));
        annotationRepository.save(createAnnotationDoc(createGPId(1), goId(1)));

        expectRestCallResponse(GET, buildResource(GO_TERM_RESOURCE_FORMAT, goId(0)),
                withStatus(HttpStatus.REQUEST_TIMEOUT));
        expectGoTermsHaveGoNamesViaRest(singletonList(goId(1)), singletonList(goName(1)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(INCLUDE_FIELD_PARAM.getName(), GO_NAME_FIELD));

        response.andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void injectingValueProducesErrorWhenExternalServiceProducesBadGatewayError() throws Exception {
        annotationRepository.save(createAnnotationDoc(createGPId(0), goId(0)));
        annotationRepository.save(createAnnotationDoc(createGPId(1), goId(1)));

        expectRestCallResponse(GET, buildResource(GO_TERM_RESOURCE_FORMAT, goId(0)),
                withStatus(HttpStatus.BAD_GATEWAY));
        expectGoTermsHaveGoNamesViaRest(singletonList(goId(1)), singletonList(goName(1)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(INCLUDE_FIELD_PARAM.getName(), GO_NAME_FIELD));

        response.andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void injectingValueProducesErrorWhenExternalServiceProducesBadRequestError() throws Exception {
        annotationRepository.save(createAnnotationDoc(createGPId(0), goId(0)));
        annotationRepository.save(createAnnotationDoc(createGPId(1), goId(1)));

        expectRestCallResponse(GET, buildResource(GO_TERM_RESOURCE_FORMAT, goId(0)),
                withStatus(HttpStatus.BAD_REQUEST));
        expectGoTermsHaveGoNamesViaRest(singletonList(goId(1)), singletonList(goName(1)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(INCLUDE_FIELD_PARAM.getName(), GO_NAME_FIELD));

        response.andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void injectingValueProducesErrorWhenExternalServiceProducesError500() throws Exception {
        annotationRepository.save(createAnnotationDoc(createGPId(0), goId(0)));
        annotationRepository.save(createAnnotationDoc(createGPId(1), goId(1)));

        expectRestCallResponse(GET, buildResource(GO_TERM_RESOURCE_FORMAT, goId(0)),
                withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        expectGoTermsHaveGoNamesViaRest(singletonList(goId(1)), singletonList(goName(1)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(INCLUDE_FIELD_PARAM.getName(), GO_NAME_FIELD));

        response.andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void invalidIncludeFieldCausesBadRequest() throws Exception {
        annotationRepository.save(createAnnotationDoc(createGPId(0), goId(0)));

        expectGoTermsHaveGoNamesViaRest(singletonList(goId(0)), singletonList(goName(0)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(INCLUDE_FIELD_PARAM.getName(), "XXXXXX"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ----------------- helpers -----------------
    private AnnotationDocument annotationDocWithTaxon(String goId, int taxonId) {
        AnnotationDocument annotationDocument = createAnnotationDoc(createGPId(taxonId), goId);
        annotationDocument.taxonId = taxonId;
        return annotationDocument;
    }

    private void expectTaxonIdHasGivenTaxonNameViaRest(int taxonId, String taxonName) {
        checkArgument(taxonName != null && !taxonName.isEmpty(), "taxonName cannot be null or empty");

        BasicTaxonomyNode expectedResponse = new BasicTaxonomyNode();
        expectedResponse.setScientificName(taxonName);
        String responseAsString = getResponseAsString(expectedResponse);

        expectRestCallSuccess(
                GET,
                buildResource(
                        TAXONOMY_ID_NODE_RESOURCE_FORMAT,
                        String.valueOf(taxonId)),
                responseAsString);
    }

    private void expectGoTermsHaveGoNamesViaRest(
            List<String> termIds,
            List<String> termNames) {
        checkArgument(termIds != null, "termIds cannot be null");
        checkArgument(termNames != null, "termIds cannot be null");
        checkArgument(termIds.size() == termNames.size(),
                "termIds and termNames lists must be the same size");

        for (int i = 0; i < termIds.size(); i++) {
            String termId = termIds.get(i);
            String termName = termNames.get(i);
            expectRestCallSuccess(
                    GET,
                    buildResource(GO_TERM_RESOURCE_FORMAT, termId),
                    constructGoTermsResponseObject(singletonList(termId), singletonList(termName)));
        }
    }

    private String constructGoTermsResponseObject(List<String> termIds, List<String> termNames) {
        checkArgument(termIds != null, "termIds cannot be null");
        checkArgument(termNames != null, "termIds cannot be null");
        checkArgument(termIds.size() == termNames.size(),
                "termIds and termNames lists must be the same size");

        BasicOntology response = new BasicOntology();
        List<BasicOntology.Result> results = new ArrayList<>();

        for (int i = 0; i < termIds.size(); i++) {
            BasicOntology.Result result = new BasicOntology.Result();
            result.setId(termIds.get(i));
            result.setName(termNames.get(i));
            results.add(result);
        }

        response.setResults(results);
        return getResponseAsString(response);
    }

    private <T> String getResponseAsString(T response) {
        try {
            return dtoMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Problem constructing mocked GO term REST response:", e);
        }
    }

    private void expectRestCallResponse(HttpMethod method, String url, ResponseCreator response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + url))
                .andExpect(method(method))
                .andRespond(response);
    }

    private void expectRestCallSuccess(HttpMethod method, String url, String response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + url))
                .andExpect(method(method))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
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

    private String goId(int id) {
        return IdGeneratorUtil.createGoId(id);
    }

    private String goName(int id) {
        return IdGeneratorUtil.createGoId(id) + " name";
    }

    private int taxonId(int id) {
        return id;
    }

    private String taxonName(int id) {
        return "taxon name: " + id;
    }
}
