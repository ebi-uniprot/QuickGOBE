package uk.ac.ebi.quickgo.annotation.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.cache.CacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.IdGeneratorUtil;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.ECO_ID;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createGenericDocsChangingGoId;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.numOfResults;
import static uk.ac.ebi.quickgo.annotation.controller.StatsResponseVerifier.namesInTypeWithinGroup;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.*;

/**
 * @author Tony Wardell
 * Date: 10/10/2017
 * Time: 13:41
 * Created with IntelliJ IDEA.
 */
@ExtendWith(TemporarySolrDataStore.class)
@SpringBootTest(classes = {AnnotationREST.class})
@WebAppConfiguration
class AnnotationControllerStatisticsDownloadIT {
    private static final int NUMBER_OF_GENERIC_DOCS = 5;
    private static final String DOWNLOAD_STATISTICS_SEARCH_URL = "/annotation/downloadStats";
    private static final String NUMBER_OF_GO_ID_RESULTS_FOR_ANNOTATIONS =
            "$.results[0].types.[?(@.type == 'goId')].values.length()";
    private static final String ECO_TERM_NAME = "match to sequence model evidence used in automatic assertion";
    private static final int NO_OF_STATISTICS_GROUPS = 2;
    private static final String ANNOTATION_GROUP = "annotation";
    private static final String GENE_PRODUCT_GROUP = "geneProduct";
    private static final String TAXON_ID_STATS_FIELD = "taxonId";
    private static final String EVIDENCE_CODE_STATS_FIELD = "evidenceCode";
    private static final String GO_ID_STATS_FIELD = "goId";
    private static final String TAXON_NAME = "taxon name: " + 12345;
    private static final String TAXON_ID = "12345";
    private static final Function<Integer, String> toId = IdGeneratorUtil::createGoId;
    private static final Function<Integer, String> toName = i -> IdGeneratorUtil.createGoId(i) + " name";
    private static final String TAXON_ID_PARAMETER_NAME = "taxonId";

    @Autowired
    CacheManager cacheManager;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AnnotationRepository annotationRepository;
    @Autowired
    private RestOperations restOperations;
    private String[] goNames;
    private StatsSetupHelper setupHelper;

    @BeforeEach
    void setup() {
        annotationRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockRestServiceServer mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
        List<AnnotationDocument> genericDocs = createGenericDocsChangingGoId(NUMBER_OF_GENERIC_DOCS);
        annotationRepository.saveAll(genericDocs);
        setupHelper = new StatsSetupHelper(mockRestServiceServer);
        goNames = new String[NUMBER_OF_GENERIC_DOCS];
        IntStream.range(0, goNames.length)
                .forEach(i -> goNames[i] = toName.apply(i));
        cacheManager.getCache("names").clear();
    }

    @Test
    void canDownloadInExcelFormat() throws Exception {
        setupSuccessfullyReceivingRestNames();

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL)
                .param(TAXON_ID_PARAMETER_NAME, AnnotationDocMocker.TAXON_ID)
                .header(ACCEPT, EXCEL_MEDIA_TYPE));

        checkResponse(EXCEL_MEDIA_TYPE, response);
    }

    @Test
    void canDownloadInJsonFormat() throws Exception {
        setupSuccessfullyReceivingRestNames();

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL)
                .param(TAXON_ID_PARAMETER_NAME, AnnotationDocMocker.TAXON_ID)
                .header(ACCEPT, JSON_MEDIA_TYPE));

        checkResponse(response);
        response.andDo(print())
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, GO_ID_STATS_FIELD, goNames))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, GO_ID_STATS_FIELD, goNames))
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, TAXON_ID_STATS_FIELD, new String[]{TAXON_NAME}))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, TAXON_ID_STATS_FIELD, new String[]{TAXON_NAME}))
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, EVIDENCE_CODE_STATS_FIELD,
                        new String[]{ECO_TERM_NAME}))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, EVIDENCE_CODE_STATS_FIELD,
                        new String[]{ECO_TERM_NAME}));
    }

    @Test
    void downloadStatisticsSuccessfulAfterFailedToRetrieveGONames() throws Exception {
        setExpectationsForUnsuccessfulOntologyServiceRestResponse();
        setupHelper.expectTaxonIdHasNameViaRest(TAXON_ID, TAXON_NAME);
        setupHelper.expectEcoCodeHasNameViaRest(ECO_ID, ECO_TERM_NAME, NUMBER_OF_GENERIC_DOCS);

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL)
                .param(TAXON_ID_PARAMETER_NAME, AnnotationDocMocker.TAXON_ID)
                .header(ACCEPT, JSON_MEDIA_TYPE));

        checkResponse(JSON_MEDIA_TYPE, response);
        response.andDo(print())
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, GO_ID_STATS_FIELD,
                        new String[]{null, null, null, null, null}))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, GO_ID_STATS_FIELD,
                        new String[]{null, null, null, null, null}))
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, TAXON_ID_STATS_FIELD, new String[]{TAXON_NAME}))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, TAXON_ID_STATS_FIELD, new String[]{TAXON_NAME}))
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, EVIDENCE_CODE_STATS_FIELD,
                        new String[]{ECO_TERM_NAME}))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, EVIDENCE_CODE_STATS_FIELD,
                        new String[]{ECO_TERM_NAME}));
    }

    @Test
    void downloadStatisticsSuccessfulAfterFailedToRetrieveTaxonNames() throws Exception {
        setupHelper.expectGoTermHasNameViaRest(NUMBER_OF_GENERIC_DOCS, toId, toName);
        setExpectationsForUnsuccessfulTaxonomyServiceRestResponse();
        setupHelper.expectEcoCodeHasNameViaRest(ECO_ID, ECO_TERM_NAME, NUMBER_OF_GENERIC_DOCS);

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL)
                .param(TAXON_ID_PARAMETER_NAME, AnnotationDocMocker.TAXON_ID)
                .header(ACCEPT, JSON_MEDIA_TYPE));

        checkResponse(JSON_MEDIA_TYPE, response);
        response.andDo(print())
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, GO_ID_STATS_FIELD, goNames))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, GO_ID_STATS_FIELD, goNames))
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, TAXON_ID_STATS_FIELD, new String[]{null}))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, TAXON_ID_STATS_FIELD, new String[]{null}))
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, EVIDENCE_CODE_STATS_FIELD,
                        new String[]{ECO_TERM_NAME}))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, EVIDENCE_CODE_STATS_FIELD,
                        new String[]{ECO_TERM_NAME}));
    }

    @Test
    void downloadStatisticsSuccessfulAfterFailedToRetrieveECONames() throws Exception {
        setupHelper.expectGoTermHasNameViaRest(NUMBER_OF_GENERIC_DOCS, toId, toName);
        setupHelper.expectTaxonIdHasNameViaRest(TAXON_ID, TAXON_NAME);
        setExpectationsForUnsuccessfulOntologyServiceRestResponseForEcoCodes();

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL)
                .param(TAXON_ID_PARAMETER_NAME, AnnotationDocMocker.TAXON_ID)
                .header(ACCEPT, JSON_MEDIA_TYPE));

        checkResponse(JSON_MEDIA_TYPE, response);
        response.andDo(print())
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, GO_ID_STATS_FIELD, goNames))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, GO_ID_STATS_FIELD, goNames))
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, TAXON_ID_STATS_FIELD, new String[]{TAXON_NAME}))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, TAXON_ID_STATS_FIELD, new String[]{TAXON_NAME}))
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, EVIDENCE_CODE_STATS_FIELD, new String[]{null}))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, EVIDENCE_CODE_STATS_FIELD, new String[]{null}));
    }

    private void setupSuccessfullyReceivingRestNames() {
        setupHelper.expectGoTermHasNameViaRest(NUMBER_OF_GENERIC_DOCS, toId, toName);
        setupHelper.expectTaxonIdHasNameViaRest(TAXON_ID, TAXON_NAME);
        setupHelper.expectEcoCodeHasNameViaRest(ECO_ID, ECO_TERM_NAME, NUMBER_OF_GENERIC_DOCS);
    }

    private void setExpectationsForUnsuccessfulOntologyServiceRestResponse() {
        for (int k = 0; k < NO_OF_STATISTICS_GROUPS; k++) {
            setupHelper.expectFailureToGetNameForGoTermViaRest(NUMBER_OF_GENERIC_DOCS, toId);
        }
    }

    private void setExpectationsForUnsuccessfulTaxonomyServiceRestResponse() {
        setupHelper.expectFailureToGetTaxonomyNameViaRest(TAXON_ID, NO_OF_STATISTICS_GROUPS);
    }

    private void setExpectationsForUnsuccessfulOntologyServiceRestResponseForEcoCodes() {
        setupHelper.expectFailureToGetEcoNameViaRest(ECO_ID, NO_OF_STATISTICS_GROUPS);
    }

    private void checkResponse(ResultActions response) throws Exception {
        checkResponse(JSON_MEDIA_TYPE, response);
        response.andExpect(numOfResults(NUMBER_OF_GO_ID_RESULTS_FOR_ANNOTATIONS,
                AnnotationControllerStatisticsDownloadIT.NUMBER_OF_GENERIC_DOCS));
    }

    private void checkResponse(MediaType mediaType, ResultActions response) throws Exception {
        response.andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andDo(print())
                .andExpect(header().string(VARY, is(ACCEPT)))
                .andExpect(header().string(CONTENT_DISPOSITION, endsWith("." + fileExtension(mediaType) + "\"")))
                .andExpect(content().contentType(mediaType));
    }
}
