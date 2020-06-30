package uk.ac.ebi.quickgo.annotation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.annotation.AnnotationParameters;
import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.IdGeneratorUtil;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory;
import uk.ac.ebi.quickgo.annotation.model.AnnotationMocker;
import uk.ac.ebi.quickgo.annotation.model.AnnotationRequestBody;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.model.BasicGeneProduct;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.INCLUDE_FIELD_PARAM;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.SELECTED_FIELD_PARAM;
import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createGoId;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createGenericDocs;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createGenericDocsChangingGoId;
import static uk.ac.ebi.quickgo.annotation.controller.DownloadResponseVerifier.nonNullMandatoryFieldsExist;
import static uk.ac.ebi.quickgo.annotation.controller.DownloadResponseVerifierSelectedFields.selectedFieldsExist;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.GAF_MEDIA_TYPE;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.GPAD_MEDIA_TYPE;

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
@SpringBootTest(classes = {AnnotationREST.class})
@WebAppConfiguration
public class AnnotationControllerDownloadIT {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();
    private static final int NUMBER_OF_GENERIC_DOCS = 200;
    private static final String DOWNLOAD_SEARCH_URL = "/annotation/downloadSearch";
    private static final String DOWNLOAD_LIMIT_PARAM = "downloadLimit";
    private static final int MIN_DOWNLOAD_NUMBER = 1;
    private static final int MAX_DOWNLOAD_NUMBER = 2000000;
    private static final String EXACT = "exact";
    private static final String GO_NAME_FIELD = "goName";
    private static final String GO_TERM_RESOURCE_FORMAT = "/ontology/go/terms/%s";
    private static final String BASE_URL = "https://localhost";
    private static final String GENE_PRODUCT_ID_FIELD_NAME_MIXED_CASE = "geneproDuctid";
    private static final String SYMBOL_FIELD_NAME_MIXED_CASE = "sYmbol";
    private static final String WITH_FROM_FIELD_NAME_MIXED_CASE = "withfrOm";
    private MockMvc mockMvc;

    //GeneProductResource
    private static final String GENE_PRODUCT_RESOURCE_FORMAT = "/QuickGO/services/geneproduct/%s";

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
        genericDocs.forEach(e -> {
            expectToLoadGeneProductValuesViaRest(singletonList(e.geneProductId),
                    singletonList(AnnotationMocker.SYNONYMS));
            expectToLoadGeneProductValuesViaRest(singletonList(e.geneProductId), singletonList(AnnotationMocker.NAME));
        });
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, GAF_MEDIA_TYPE)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(expectedDownloadCount)));

        List<String> storedIds = getFieldValuesFromRepo(doc -> idFrom(doc.geneProductId), expectedDownloadCount);

        checkResponse(GAF_MEDIA_TYPE, response, storedIds);
    }

    @Test
    public void canDownloadInGafFormat() throws Exception {
        genericDocs.forEach(e -> {
            expectToLoadGeneProductValuesViaRest(singletonList(e.geneProductId),
                    singletonList(AnnotationMocker.SYNONYMS));
            expectToLoadGeneProductValuesViaRest(singletonList(e.geneProductId), singletonList(AnnotationMocker.NAME));
        });
        canDownload(GAF_MEDIA_TYPE);

    }

    @Test
    public void canDownloadWithFilterInGafFormat() throws Exception {
        genericDocs.forEach(e -> {
            expectToLoadGeneProductValuesViaRest(singletonList(e.geneProductId),
                    singletonList(AnnotationMocker.SYNONYMS));
            expectToLoadGeneProductValuesViaRest(singletonList(e.geneProductId), singletonList(AnnotationMocker.NAME));
        });
        canDownloadWithFilter(GAF_MEDIA_TYPE);
    }

    @Test
    public void canDownloadWithFilterAllAvailableItemsInGafFormat() throws Exception {
        genericDocs.forEach(e -> {
            expectToLoadGeneProductValuesViaRest(singletonList(e.geneProductId),
                    singletonList(AnnotationMocker.SYNONYMS));
            expectToLoadGeneProductValuesViaRest(singletonList(e.geneProductId), singletonList(AnnotationMocker.NAME));
        });
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
    public void canDownloadWhenBodyIsProvidedAsPost() throws Exception {
        int expectedDownloadCount = 5;

        createGenericDocsChangingGoId(expectedDownloadCount).forEach(this::saveToRepo);

        List<String> expectedIds = Arrays.asList(createGoId(2));

        AnnotationRequestBody.GoDescription description = AnnotationRequestBody.GoDescription.builder()
          .goTerms(new String[]{createGoId(2)})
          .goUsage("exact")
          .build();
        AnnotationRequestBody body = AnnotationRequestBody.builder().and(description).build();


        ResultActions response = mockMvc.perform(
          post(DOWNLOAD_SEARCH_URL)
            .header(ACCEPT, GPAD_MEDIA_TYPE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(body))
            .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(expectedDownloadCount)));

        checkResponse(GPAD_MEDIA_TYPE, response, expectedIds);
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
        int downloadCount = 97;
        for(int i=1; i<=downloadCount; i++) {
            expectGoTermsHaveGoNamesViaRest(singletonList(IdGeneratorUtil.createGoId(3824)), singletonList
                    ("catalytic activity"));
        }
        canDownloadWithOptionalFields();
    }

    @Test
    public void canDownloadInTSVFormatWithSelectedFieldsCaseInsensitive() throws Exception {
        canDownloadWithSelectedFields();
    }

    private List<AnnotationDocument> createDocs(int number) {
        return createGenericDocs(number, AnnotationDocMocker::createUniProtGPID);
    }

    private void saveToRepo(List<AnnotationDocument> docsToSave) {
        repository.saveAll(docsToSave);
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

    private void canDownloadWithOptionalFields() throws Exception {
        int expectedDownloadCount = 97;
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, MediaTypeFactory.TSV_MEDIA_TYPE)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(expectedDownloadCount))
                        .param(INCLUDE_FIELD_PARAM.getName(), GO_NAME_FIELD));

        List<String> storedIds = getFieldValuesFromRepo(doc -> idFrom(doc.geneProductId), expectedDownloadCount);
        List<String> perLine = new ArrayList<>();
        perLine.add("GENE");
        //StringContainsInOrder.matchesSafely not working as expected, not finding A0A009, so cut down list.
        perLine.addAll(storedIds.subList(0,8));
        checkResponse(MediaTypeFactory.TSV_MEDIA_TYPE, response, perLine);
    }

    private void canDownloadWithSelectedFields() throws Exception {
        int expectedDownloadCount = 97;
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, MediaTypeFactory.TSV_MEDIA_TYPE)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(expectedDownloadCount))
                        .param(SELECTED_FIELD_PARAM.getName(), SYMBOL_FIELD_NAME_MIXED_CASE,
                               WITH_FROM_FIELD_NAME_MIXED_CASE));

        getFieldValuesFromRepo(doc -> idFrom(doc.geneProductId), expectedDownloadCount);
        checkResponseForSelectedFields(response, SYMBOL_FIELD_NAME_MIXED_CASE, WITH_FROM_FIELD_NAME_MIXED_CASE);
    }

    private void checkResponse(MediaType mediaType, ResultActions response, List<String> storedIds) throws Exception {
        response.andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andDo(print())
                .andExpect(header().string(VARY, is(ACCEPT)))
                .andExpect(header().string(CONTENT_DISPOSITION, endsWith(getFileNameEndingFor(mediaType))))
                .andExpect(content().contentType(mediaType))
                .andExpect(nonNullMandatoryFieldsExist(mediaType))
                .andExpect(content().string(stringContainsInOrder(storedIds)));
    }

    private void checkResponseForSelectedFields(ResultActions response, String... expectedFields) throws Exception {
        response.andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andDo(print())
                .andExpect(selectedFieldsExist(expectedFields));
    }

    private void canDownloadWithFilter(MediaType mediaType) throws Exception {
        int expectedDownloadCount = 31;
        int moreThanExpectedDownloadCount = expectedDownloadCount + 5;
        int expectedTaxonId = 1066;

        createDocs(moreThanExpectedDownloadCount)
                .stream()
                .peek(doc -> doc.taxonId = expectedTaxonId)
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
                .peek(doc -> doc.taxonId = expectedTaxonId)
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

    private void expectRestCallSuccess(String url, String response) {
        mockRestServiceServer.expect(requestTo(BASE_URL + url))
                             .andExpect(method(HttpMethod.GET))
                             .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    }

    private void expectGoTermsHaveGoNamesViaRest(List<String> termIds, List<String> termNames) {
        checkArgument(termIds != null, "termIds cannot be null");
        checkArgument(termNames != null, "termIds cannot be null");
        checkArgument(termIds.size() == termNames.size(), "termIds and termNames lists must be the same size");

        for (int i = 0; i < termIds.size(); i++) {
            String termId = termIds.get(i);
            String termName = termNames.get(i);
            expectRestCallSuccess(buildGeneOntologyResource(termId),
                    constructGoTermsResponseObject(singletonList(termId), singletonList(termName)));
        }
    }

    private void expectToLoadGeneProductValuesViaRest(List<String> geneProductIds, List<String> valueList) {
        for (int i = 0; i < geneProductIds.size(); i++) {
            String geneProductId = geneProductIds.get(i);
            String values = valueList.get(i);
            String withoutDB = geneProductId.substring(geneProductId.indexOf(":") + 1);
            expectRestCallSuccess(buildGeneProductResource(withoutDB),
                    constructGeneProductResponseObject(geneProductId, values));
        }
    }

    private String constructGeneProductResponseObject(String geneProductId, String synonyms) {
        BasicGeneProduct response = new BasicGeneProduct();
        BasicGeneProduct.Result result = new BasicGeneProduct.Result();
        String withoutDB = geneProductId.substring(geneProductId.indexOf(":") + 1);
        result.setId(withoutDB);
        result.setSynonyms(singletonList(synonyms));
        response.setResults(singletonList(result));
        return getResponseAsString(response);
    }

    private String buildGeneOntologyResource(String... arguments) {
        int requiredArgsCount = AnnotationControllerDownloadIT.GO_TERM_RESOURCE_FORMAT.length() - AnnotationControllerDownloadIT.GO_TERM_RESOURCE_FORMAT

                .replace("%", "").length();
        List<String> args = new ArrayList<>();
        for (int i = 0; i < requiredArgsCount; i++) {
            if (i < arguments.length) {
                args.add(arguments[i]);
            } else {
                args.add("");
            }
        }
        return String.format(AnnotationControllerDownloadIT.GO_TERM_RESOURCE_FORMAT, args.toArray());
    }

    private String buildGeneProductResource(String... arguments) {
        int requiredArgsCount = AnnotationControllerDownloadIT.GENE_PRODUCT_RESOURCE_FORMAT.length() -
                AnnotationControllerDownloadIT.GENE_PRODUCT_RESOURCE_FORMAT

                        .replace("%", "").length();
        List<String> args = new ArrayList<>();
        for (int i = 0; i < requiredArgsCount; i++) {
            if (i < arguments.length) {
                args.add(arguments[i]);
            } else {
                args.add("");
            }
        }
        return String.format(AnnotationControllerDownloadIT.GENE_PRODUCT_RESOURCE_FORMAT, args.toArray());
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
