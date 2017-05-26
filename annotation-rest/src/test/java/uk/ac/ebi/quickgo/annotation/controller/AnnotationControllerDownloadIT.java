package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationParameters;
import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.IdGeneratorUtil;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.INCLUDE_FIELD_PARAM;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.SELECTED_FIELD_PARAM;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createGenericDocs;
import static uk.ac.ebi.quickgo.annotation.controller.DownloadResponseVerifier.nonNullMandatoryFieldsExist;
import static uk.ac.ebi.quickgo.annotation.controller.DownloadResponseVerifier.selectedFieldsExist;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.*;

/**
 * Tests whether the downloading functionality of the {@link AnnotationController} works as expected.
 * The functional tests relating to the filtering of results are covered by {@link AnnotationControllerIT} since the
 * search results found used by the download functionality is unchanged.
 * <p>
 * Created 24/01/17
 *
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AnnotationREST.class})
@WebAppConfiguration
public class AnnotationControllerDownloadIT {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();
    private static final int NUMBER_OF_GENERIC_DOCS = 200;
    private static final String DOWNLOAD_SEARCH_URL = "/annotation/downloadSearch";
    private static final String DOWNLOAD_LIMIT_PARAM = "downloadLimit";
    private static final int MIN_DOWNLOAD_NUMBER = 1;
    private static final int MAX_DOWNLOAD_NUMBER = 50000;
    private static final String EXACT = "exact";
    private static final String GO_NAME_FIELD = "goName";
    private static final String GO_TERM_RESOURCE_FORMAT = "/ontology/go/terms/%s";
    private static final String BASE_URL = "http://localhost";
    private static final String GENE_PRODUCT_ID_FIELD_NAME_MIXED_CASE = "geneproDuctid";
    private static final String SYMBOL_FIELD_NAME_MIXED_CASE = "sYmbol";
    private static final String WITH_FROM_FIELD_NAME_MIXED_CASE = "withfrOm";
    private MockMvc mockMvc;

    private List<AnnotationDocument> genericDocs;
    private List<AnnotationDocument> savedDocs;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AnnotationRepository repository;

    @Autowired
    private RestOperations restOperations;

    private MockRestServiceServer mockRestServiceServer;
    private ObjectMapper dtoMapper;

    @Before
    public void setup() {
        repository.deleteAll();

        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();

        genericDocs = createDocs(NUMBER_OF_GENERIC_DOCS);
        savedDocs = new ArrayList<>();

        saveToRepo(genericDocs);

        mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
        dtoMapper = new ObjectMapper();
    }

    @Test
    public void canDownloadAnAnnotationAmountFewerThanPageSize() throws Exception {
        int expectedDownloadCount = 1;
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, GAF_MEDIA_TYPE)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(expectedDownloadCount)));

        List<String> storedIds = getFieldValuesFromRepo(doc -> idFrom(doc.geneProductId), expectedDownloadCount);

        checkResponse(GAF_MEDIA_TYPE, response, storedIds);
    }

    @Test
    public void canDownloadInGafFormat() throws Exception {
        canDownload(GAF_MEDIA_TYPE);
    }

    @Test
    public void canDownloadWithFilterInGafFormat() throws Exception {
        canDownloadWithFilter(GAF_MEDIA_TYPE);
    }

    @Test
    public void canDownloadWithFilterAllAvailableItemsInGafFormat() throws Exception {
        canDownloadWithFilterAllAvailableItems(GAF_MEDIA_TYPE);
    }

    @Test
    public void canDownloadInGpadFormat() throws Exception {
        canDownload(GPAD_MEDIA_TYPE);
    }

    @Test
    public void canDownloadWithFilterInGpadFormat() throws Exception {
        canDownloadWithFilter(GPAD_MEDIA_TYPE);
    }

    @Test
    public void canDownloadWithFilterAllAvailableItemsInGpadFormat() throws Exception {
        canDownloadWithFilterAllAvailableItems(GPAD_MEDIA_TYPE);
    }

    @Test
    public void downloadLimitTooLargeCausesBadRequest() throws Exception {
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, GPAD_MEDIA_TYPE)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(MAX_DOWNLOAD_NUMBER + 1)));

        response
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void downloadLimitTooSmallCausesBadRequest() throws Exception {
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, GPAD_MEDIA_TYPE)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(MIN_DOWNLOAD_NUMBER - 1)));

        response
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void canDownloadInTSVFormat() throws Exception {
        for(int i=1; i<=97; i++) {
            expectGoTermsHaveGoNamesViaRest(singletonList(IdGeneratorUtil.createGoId(3824)), singletonList
                    ("catalytic activity"));
        }
        canDownloadWithOptionalFields(TSV_MEDIA_TYPE);
    }

    @Test
    public void canDownloadInTSVFormatWithSelectedFieldsCaseInsensitive() throws Exception {
        canDownloadWithSelectedFields(TSV_MEDIA_TYPE);
    }

    private List<AnnotationDocument> createDocs(int number) {
        return createGenericDocs(number, AnnotationDocMocker::createUniProtGPID);
    }

    private void saveToRepo(List<AnnotationDocument> docsToSave) {
        repository.save(docsToSave);
        savedDocs.addAll(docsToSave);
    }

    private void saveToRepo(AnnotationDocument docToSave) {
        repository.save(docToSave);
        savedDocs.add(docToSave);
    }

    private void canDownload(MediaType mediaType) throws Exception {
        int expectedDownloadCount = 97;
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, mediaType)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(expectedDownloadCount)));

        List<String> storedIds = getFieldValuesFromRepo(doc -> idFrom(doc.geneProductId), expectedDownloadCount);

        checkResponse(mediaType, response, storedIds);
    }

    private void canDownloadWithOptionalFields(MediaType mediaType) throws Exception {
        int expectedDownloadCount = 97;
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, mediaType)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(expectedDownloadCount))
                        .param(INCLUDE_FIELD_PARAM.getName(), GO_NAME_FIELD));

        List<String> storedIds = getFieldValuesFromRepo(doc -> idFrom(doc.geneProductId), expectedDownloadCount);

        checkResponse(mediaType, response, storedIds);
    }

    private void canDownloadWithSelectedFields(MediaType mediaType) throws Exception {
        int expectedDownloadCount = 97;
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, mediaType)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(expectedDownloadCount))
                        .param(SELECTED_FIELD_PARAM.getName(), GENE_PRODUCT_ID_FIELD_NAME_MIXED_CASE,
                               SYMBOL_FIELD_NAME_MIXED_CASE, WITH_FROM_FIELD_NAME_MIXED_CASE));

        List<String> storedIds = getFieldValuesFromRepo(doc -> idFrom(doc.geneProductId), expectedDownloadCount);

        checkResponseForSelectedFields(response, storedIds);
    }


    private void checkResponse(MediaType mediaType, ResultActions response, List<String> storedIds) throws Exception {
        response.andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andDo(print())
                .andExpect(header().string(CONTENT_DISPOSITION, endsWith(getFileNameEndingFor(mediaType))))
                .andExpect(content().contentType(mediaType))
                .andExpect(nonNullMandatoryFieldsExist(mediaType))
                .andExpect(content().string(stringContainsInOrder(storedIds)));
    }

    private void checkResponseForSelectedFields(ResultActions response, List<String> storedIds) throws Exception {
        response.andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andDo(print())
                .andExpect(selectedFieldsExist());
    }

    private void canDownloadWithFilter(MediaType mediaType) throws Exception {
        int expectedDownloadCount = 31;
        int moreThanExpectedDownloadCount = expectedDownloadCount + 5;
        int expectedTaxonId = 1066;

        createDocs(moreThanExpectedDownloadCount)
                .stream()
                .map(doc -> {
                    doc.taxonId = expectedTaxonId;
                    return doc;
                })
                .forEach(this::saveToRepo);

        // we expect to receive fewer ids in the response than those we saved, because we will request fewer
        List<String> expectedIds = savedDocs.stream()
                .filter(doc -> doc.taxonId == expectedTaxonId)
                .map(doc -> idFrom(doc.geneProductId))
                .collect(Collectors.toList())
                .subList(0, expectedDownloadCount);

        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, mediaType)
                        .param(AnnotationParameters.TAXON_ID_PARAM.getName(), Integer.toString(expectedTaxonId))
                        .param(AnnotationParameters.TAXON_USAGE_PARAM.getName(), EXACT)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(expectedDownloadCount)));

        checkResponse(mediaType, response, expectedIds);
    }

    private String getFileNameEndingFor(MediaType mediaType) {
        switch (mediaType.getSubtype()) {
            case MediaTypeFactory.GAF_SUB_TYPE:
            case MediaTypeFactory.GPAD_SUB_TYPE:
            case MediaTypeFactory.TSV_SUB_TYPE:
                return "." + mediaType.getSubtype() + "\"";
            default:
                throw new IllegalArgumentException("Unknown media type: " + mediaType);
        }
    }

    private void canDownloadWithFilterAllAvailableItems(MediaType mediaType) throws Exception {
        int requestedDownloadCount = 31;
        int actualAvailableDownloadCount = requestedDownloadCount - 5;
        int expectedTaxonId = 1066;

        createDocs(actualAvailableDownloadCount)
                .stream()
                .map(doc -> {
                    doc.taxonId = expectedTaxonId;
                    return doc;
                })
                .forEach(this::saveToRepo);

        // we expect to receive fewer ids in the response than those we saved, because we will request fewer
        List<String> expectedIds = savedDocs.stream()
                .filter(doc -> doc.taxonId == expectedTaxonId)
                .map(doc -> idFrom(doc.geneProductId))
                .collect(Collectors.toList());

        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, mediaType)
                        .param(AnnotationParameters.TAXON_ID_PARAM.getName(), Integer.toString(expectedTaxonId))
                        .param(AnnotationParameters.TAXON_USAGE_PARAM.getName(), EXACT)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(requestedDownloadCount)));

        checkResponse(mediaType, response, expectedIds);
    }

    private String idFrom(String geneProductId) {
        return geneProductId.substring(10);
    }

    private <O> List<O> getFieldValuesFromRepo(Function<AnnotationDocument, O> transformation, int subListSize) {
        return getFieldValuesFromRepo(transformation)
                .subList(0, subListSize);
    }

    private <O> List<O> getFieldValuesFromRepo(Function<AnnotationDocument, O> transformation) {
        return genericDocs.stream()
                .map(transformation)
                .collect(Collectors.toList());
    }

    private void expectRestCallSuccess(HttpMethod method, String url, String response) {
        mockRestServiceServer.expect(requestTo(BASE_URL + url))
                             .andExpect(method(method))
                             .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
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
                    buildResource(
                            GO_TERM_RESOURCE_FORMAT,
                            termId),
                    constructGoTermsResponseObject(singletonList(termId), singletonList(termName)));
        }
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
}
