package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.IdGeneratorUtil;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.sf.ehcache.CacheManager;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.VARY;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createGenericDocsChangingGoId;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.expectedValues;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.numOfResults;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.EXCEL_MEDIA_TYPE;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.JSON_MEDIA_TYPE;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.fileExtension;

/**
 * @author Tony Wardell
 * Date: 10/10/2017
 * Time: 13:41
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AnnotationREST.class})
@WebAppConfiguration
public class AnnotationControllerStatisticsDownloadIT {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();
    private static final int NUMBER_OF_GENERIC_DOCS = 5;
    private static final String DOWNLOAD_STATISTICS_SEARCH_URL = "/annotation/downloadStats";
    private static final String NUMBER_OF_GO_ID_RESULTS_FOR_ANNOTATIONS =
            "$.results[0].types.[?(@.type == 'goId')].values.length()";
    private static final String ALL_GO_TERM_NAMES = "$.results[0].types.[?(@.type == 'goId')].values.*.name";
    private static final String ALL_TAXON_NAMES = "$.results[0].types.[?(@.type == 'taxonId')].values.*.name";
    private static final String BASE_URL = "https://localhost";
    private static final String GO_TERM_RESOURCE_FORMAT = "/ontology/go/terms/%s";
    private static final String TAXONOMY_ID_NODE_RESOURCE_FORMAT = "/proteins/api/taxonomy/id/%s/node";
    private static final int NO_OF_STATISTICS_GROUPS = 2;
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AnnotationRepository annotationRepository;
    @Autowired
    private RestOperations restOperations;
    private MockRestServiceServer mockRestServiceServer;
    private ObjectMapper dtoMapper;
    private List<String> populatedGoNames;

    @Autowired
    CacheManager cacheManager;

    @Before
    public void setup() {
        annotationRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
        dtoMapper = new ObjectMapper();
        List<AnnotationDocument> genericDocs = createGenericDocsChangingGoId(NUMBER_OF_GENERIC_DOCS);
        annotationRepository.save(genericDocs);
        populatedGoNames = new ArrayList<>(NUMBER_OF_GENERIC_DOCS);
        for (int j = 0; j < NUMBER_OF_GENERIC_DOCS; j++) {
            populatedGoNames.add(goName(j));
        }
    }

    private String goName(int id) {
        return IdGeneratorUtil.createGoId(id) + " name";
    }

    @Test
    public void canDownloadInExcelFormat() throws Exception {
        cacheManager.clearAll();
        setExpectationsForSuccessfulOntologyServiceRestResponse();
        setExpectationsForSuccessfulTaxonomyServiceRestResponse();

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL).header(ACCEPT, EXCEL_MEDIA_TYPE));

        checkResponse(EXCEL_MEDIA_TYPE, response);
    }

    private void setExpectationsForSuccessfulOntologyServiceRestResponse() {
        //Hitting the cache means that the call to the rest service will occur only once for each term to get term name
        for (int j = 0; j < NUMBER_OF_GENERIC_DOCS; j++) {
            expectGoTermsHaveGoNamesViaRest(singletonList(goId(j)), singletonList(goName(j)));
        }
    }

    private void setExpectationsForSuccessfulTaxonomyServiceRestResponse() {
        //We are only going to hit the restful service once due to caching the result
        expectTaxonIdHasGivenTaxonNameViaRest(taxonId(), taxonName());
    }

    private void checkResponse(MediaType mediaType, ResultActions response) throws Exception {
        response.andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andDo(print())
                .andExpect(header().string(VARY, is(ACCEPT)))
                .andExpect(header().string(CONTENT_DISPOSITION, endsWith("." + fileExtension(mediaType) + "\"")))
                .andExpect(content().contentType(mediaType));
    }

    private void expectGoTermsHaveGoNamesViaRest(List<String> termIds, List<String> termNames) {
        checkArgument(termIds != null, "termIds cannot be null");
        checkArgument(termNames != null, "termIds cannot be null");
        checkArgument(termIds.size() == termNames.size(),
                "termIds and termNames lists must be the same size");

        for (int i = 0; i < termIds.size(); i++) {
            String termId = termIds.get(i);
            String termName = termNames.get(i);
            expectRestCallSuccess(
                    buildResource(GO_TERM_RESOURCE_FORMAT, termId),
                    constructGoTermsResponseObject(singletonList(termId), singletonList(termName)));
        }
    }

    private String goId(int id) {
        return IdGeneratorUtil.createGoId(id);
    }

    private void expectTaxonIdHasGivenTaxonNameViaRest(int taxonId, String taxonName) {
        checkArgument(taxonName != null && !taxonName.isEmpty(), "taxonName cannot be null or empty");

        BasicTaxonomyNode expectedResponse = new BasicTaxonomyNode();
        expectedResponse.setScientificName(taxonName);
        String responseAsString = getResponseAsString(expectedResponse);

        expectRestCallSuccess(
                buildResource(
                        TAXONOMY_ID_NODE_RESOURCE_FORMAT,
                        String.valueOf(taxonId)),
                responseAsString);
    }

    private int taxonId() {
        return 12345;
    }

    private String taxonName() {
        return "taxon name: " + 12345;
    }

    private void expectRestCallSuccess(String url, String response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + url))
                .andExpect(method(HttpMethod.GET))
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

    @Test
    public void canDownloadInJsonFormat() throws Exception {
        cacheManager.clearAll();
        setExpectationsForSuccessfulOntologyServiceRestResponse();
        setExpectationsForSuccessfulTaxonomyServiceRestResponse();
        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL).header(ACCEPT, JSON_MEDIA_TYPE));

        checkResponse(response, NUMBER_OF_GENERIC_DOCS);

        response.andExpect(expectedValues(ALL_GO_TERM_NAMES, populatedGoNames));
        response.andExpect(expectedValues(ALL_TAXON_NAMES, singletonList(String.valueOf(taxonName()))));
    }

    private void checkResponse(ResultActions response, int expectedSize) throws Exception {
        checkResponse(JSON_MEDIA_TYPE, response);
        response.andExpect(numOfResults(NUMBER_OF_GO_ID_RESULTS_FOR_ANNOTATIONS, expectedSize));
    }

    @Test
    public void downloadStatisticsSuccessfulAfterFailedToRetrieveGONames() throws Exception {
        cacheManager.clearAll();
        setExpectationsForUnsuccessfulOntologyServiceRestResponse();
        setExpectationsForSuccessfulTaxonomyServiceRestResponse();

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL).header(ACCEPT, JSON_MEDIA_TYPE));

        checkResponse(JSON_MEDIA_TYPE, response);

        response.andExpect(expectedValues(ALL_GO_TERM_NAMES, Arrays.asList(null, null, null, null, null)));
        response.andExpect(expectedValues(ALL_TAXON_NAMES, singletonList(String.valueOf(taxonName()))));
    }

    private void setExpectationsForUnsuccessfulOntologyServiceRestResponse() {
        for (int k = 0; k < NO_OF_STATISTICS_GROUPS; k++) {
            for (int j = 0; j < NUMBER_OF_GENERIC_DOCS; j++) {
                expectRestCallResponse(buildResource(GO_TERM_RESOURCE_FORMAT, goId(j)), withStatus(HttpStatus
                        .NOT_FOUND));
            }
        }
    }

    private void expectRestCallResponse(String url, ResponseCreator response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(response);
    }

    @Test
    public void downloadStatisticsSuccessfulAfterFailedToRetrieveTaxonNames() throws Exception {
        cacheManager.clearAll();
        setExpectationsForSuccessfulOntologyServiceRestResponse();
        setExpectationsForUnsuccessfulTaxonomyServiceRestResponse();

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL).header(ACCEPT, JSON_MEDIA_TYPE));

        checkResponse(JSON_MEDIA_TYPE, response);
        response.andExpect(expectedValues(ALL_GO_TERM_NAMES, populatedGoNames));
        response.andExpect(expectedValues(ALL_TAXON_NAMES, singletonList(null)));
    }

    private void setExpectationsForUnsuccessfulTaxonomyServiceRestResponse() {
        for (int i = 0; i < NO_OF_STATISTICS_GROUPS; i++) {
            expectRestCallResponse(buildResource(TAXONOMY_ID_NODE_RESOURCE_FORMAT, String.valueOf(taxonId()
            )), withStatus(HttpStatus.NOT_FOUND));
        }
    }
}
