package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyDescendants;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepoConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
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

import static com.google.common.base.Preconditions.checkArgument;
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
import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createGPId;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.*;

/**
 * Tests filter parameters in the {@link AnnotationController} that require REST requests to perform
 * joins on annotation data.
 *
 * Created 02/11/16
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AnnotationREST.class, OntologyRepoConfig.class})
@WebAppConfiguration
public abstract class AbstractFilterAnnotationByOntologyRESTIT {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    static final String FAILED_REST_FETCH_PREFIX = "Failed to fetch REST response due to: ";
    static final String IS_A = "is_a";
    static final String SLIM_USAGE = "slim";
    static final String NO_DESCENDANTS_PREFIX = "No descendants found for IDs, ";
    static final String SEARCH_RESOURCE = "/annotation" + "/search";

    private static final String BASE_URL = "http://localhost";
    private static final String DELIMITER = ", ";
    private static final String COMMA = ",";
    private static final String DESCENDANTS_USAGE = "descendants";

    MockMvc mockMvc;
    String resourceFormat;
    String usageParam;
    String idParam;
    String usageRelations;

    @Autowired
    protected AnnotationRepository annotationRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private RestOperations restOperations;

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
    public void filterFor1TermBy0ValidDescendantMeansFilterEverything() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));

        expectRestCallHasDescendants(singletonList(ontologyId(1)), emptyList(), singletonList(emptyList()));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void defaultFilterFor1TermBy1ValidDescendant() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(3), ontologyId(3)));

        expectRestCallHasDescendants(singletonList(ontologyId(1)), emptyList(),
                singletonList(singletonList(ontologyId(3))));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(idParam, ontologyId(3)));
    }

    @Test
    public void filterFor1TermBy1ValidDescendant() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(3), ontologyId(3)));

        expectRestCallHasDescendants(singletonList(ontologyId(1)), emptyList(),
                singletonList(singletonList(ontologyId(3))));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(idParam, ontologyId(3)));
    }

    @Test
    public void filterFor1TermBy2ValidDescendants() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(3), ontologyId(3)));

        expectRestCallHasDescendants(
                singletonList(ontologyId(1)),
                singletonList(IS_A),
                singletonList(asList(ontologyId(2), ontologyId(3))));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1))
                        .param(usageRelations, IS_A));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(idParam, ontologyId(2), ontologyId(3)));
    }

    @Test
    public void filterFor2TermsBy0ValidDescendantsMeansFilterEverything() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(3), ontologyId(3)));

        expectRestCallHasDescendants(asList(ontologyId(1), ontologyId(2)), singletonList(IS_A),
                asList(emptyList(), emptyList()));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1) + "," + ontologyId(2))
                        .param(usageRelations, IS_A));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(totalNumOfResults(0));
    }

    @Test
    public void filterFor2TermsBy1ValidDescendant() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(3), ontologyId(3)));

        expectRestCallHasDescendants(
                asList(ontologyId(1), ontologyId(2)),
                emptyList(),
                asList(singletonList(ontologyId(3)), singletonList(ontologyId(3))));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, csv(ontologyId(1), ontologyId(2))));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(idParam, ontologyId(3)));
    }

    @Test
    public void filterFor2TermsBy2ValidDescendants() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(3), ontologyId(3)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(4), ontologyId(4)));

        expectRestCallHasDescendants(
                asList(ontologyId(1), ontologyId(2)),
                emptyList(),
                asList(
                        singletonList(ontologyId(3)),
                        singletonList(ontologyId(4))));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, csv(ontologyId(1), ontologyId(2))));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(pageInfoExists())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(fieldDoesNotExist(SLIMMED_ID_FIELD))
                .andExpect(valuesOccurInField(idParam, ontologyId(3), ontologyId(4)));
    }

    @Test
    public void termWithNullDescendantsProducesErrorMessage() throws Exception {
        expectRestCallHasDescendants(singletonList(ontologyId(1)), emptyList(), singletonList(null));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valuesOccurInErrorMessage(
                        failedRESTResponseErrorMessage(NO_DESCENDANTS_PREFIX + ontologyId(1))));
    }

    @Test
    public void termWithOneNullDescendantsListAndOneValidDescendantsProducesError() throws Exception {
        expectRestCallHasDescendants(
                asList(ontologyId(1), ontologyId(2)),
                emptyList(),
                asList(
                        singletonList(ontologyId(3)),
                        null));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, csv(ontologyId(1), ontologyId(2))));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valuesOccurInErrorMessage(
                        failedRESTResponseErrorMessage(NO_DESCENDANTS_PREFIX + ontologyId(2))));
    }

    @Test
    public void termWithTwoNullDescendantsListAndOneValidDescendantsProducesErrorShowingBothIds() throws Exception {
        expectRestCallHasDescendants(
                asList(ontologyId(1), ontologyId(2), ontologyId(3)),
                emptyList(),
                asList(
                        singletonList(ontologyId(4)),
                        null,
                        null));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, csv(ontologyId(1), ontologyId(2), ontologyId(3))));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valuesOccurInErrorMessage(
                        failedRESTResponseErrorMessage(NO_DESCENDANTS_PREFIX + csv(ontologyId(2),
                                ontologyId(3)))));
    }

    @Test
    public void oneTermWithOneDescendantIdThatIsNullAndOneNonNullSucceeds() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));

        expectRestCallHasDescendants(
                singletonList(ontologyId(1)),
                emptyList(),
                singletonList(
                        asList(ontologyId(2), null)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(valuesOccurInField(idParam, ontologyId(2)));
    }

    @Test
    public void wrongResourcePathForDescendantsCausesErrorMessage() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void fetchTimeoutForDescendantFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));

        expectRestCallResponse(GET, buildResource(resourceFormat, ontologyId(1)), withTimeout());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void serverErrorForDescendantFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));

        expectRestCallResponse(GET, buildResource(resourceFormat, ontologyId(1)), withServerError());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @Test
    public void badRequestForDescendantFilteringCauses500() throws Exception {
        annotationRepository.save(createAnnotationDocWithId(createGPId(1), ontologyId(1)));
        annotationRepository.save(createAnnotationDocWithId(createGPId(2), ontologyId(2)));

        expectRestCallResponse(GET, buildResource(resourceFormat, ontologyId(1)), withBadRequest());

        ResultActions response = mockMvc.perform(
                get(SEARCH_RESOURCE)
                        .param(usageParam, DESCENDANTS_USAGE)
                        .param(idParam, ontologyId(1)));

        response.andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(contentTypeToBeJson())
                .andExpect(valueStartingWithOccursInErrorMessage(FAILED_REST_FETCH_PREFIX));
    }

    @After
    public void deleteCoreContents() {
        annotationRepository.deleteAll();
    }

    protected abstract AnnotationDocument createAnnotationDocWithId(String geneProductId, String id);

    protected abstract String ontologyId(int id);

    static String csv(String... values) {
        StringJoiner joiner = new StringJoiner(DELIMITER);
        for (String value : values) {
            joiner.add(value);
        }
        return joiner.toString();
    }

    String failedRESTResponseErrorMessage(String suffix) {
        return FAILED_REST_FETCH_PREFIX + suffix;
    }

    void expectRestCallHasDescendants(
            List<String> termIds,
            List<String> usageRelations,
            List<List<String>> descendants) {
        String termIdsCSV = termIds.stream().collect(Collectors.joining(COMMA));
        String relationsCSV = usageRelations.stream().collect(Collectors.joining(COMMA));

        expectRestCallSuccess(
                GET,
                buildResource(
                        resourceFormat,
                        termIdsCSV,
                        relationsCSV),
                constructResponseObject(termIds, descendants));
    }

    String buildResource(String format, String... arguments) {
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

    void expectRestCallResponse(HttpMethod method, String url, ResponseCreator response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + url))
                .andExpect(method(method))
                .andRespond(response);
    }

    private String constructResponseObject(List<String> termIds, List<List<String>> descendants) {
        checkArgument(termIds != null, "termIds cannot be null");
        checkArgument(descendants != null, "descendants cannot be null");
        checkArgument(termIds.size() == descendants.size(), "Term ID list and the (list of lists) of their " +
                "descendants should be the same size");

        OntologyDescendants response = new OntologyDescendants();
        List<OntologyDescendants.Result> results = new ArrayList<>();

        Iterator<List<String>> descendantListsIterator = descendants.iterator();
        termIds.forEach(t -> {
            OntologyDescendants.Result result = new OntologyDescendants.Result();
            result.setId(t);
            result.setDescendants(descendantListsIterator.next());
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
}
