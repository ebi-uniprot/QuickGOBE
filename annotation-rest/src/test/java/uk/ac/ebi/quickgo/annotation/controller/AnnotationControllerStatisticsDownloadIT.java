package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import java.util.List;
import java.util.stream.IntStream;
import net.sf.ehcache.CacheManager;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
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

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.VARY;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.ECO_ID;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createGenericDocsChangingGoId;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.numOfResults;
import static uk.ac.ebi.quickgo.annotation.controller.StatsResponseVerifier.namesInTypeWithinGroup;
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
    private static final String ECO_TERM_NAME = "match to sequence model evidence used in automatic assertion";
    private static final int NO_OF_STATISTICS_GROUPS = 2;
    private static final String ANNOTATION_GROUP = "annotation";
    private static final String GENE_PRODUCT_GROUP = "geneProduct";
    private static final String TAXON_ID_STATS_FIELD = "taxonId";
    private static final String EVIDENCE_CODE_STATS_FIELD = "evidenceCode";
    private static final String GO_ID_STATS_FIELD = "goId";
    private static final String TAXON_NAME = "taxon name: " + 12345;
    private static final String TAXON_ID = "12345";

    @Autowired
    CacheManager cacheManager;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AnnotationRepository annotationRepository;
    @Autowired
    private RestOperations restOperations;
    private MockRestServiceServer mockRestServiceServer;
    private String[] goNames;
    private StatsSetupHelper setupHelper;

    @Before
    public void setup() {
        annotationRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
        List<AnnotationDocument> genericDocs = createGenericDocsChangingGoId(NUMBER_OF_GENERIC_DOCS);
        annotationRepository.save(genericDocs);
        setupHelper = new StatsSetupHelper(mockRestServiceServer);
        goNames = new String[NUMBER_OF_GENERIC_DOCS];
        IntStream.range(0, goNames.length)
                .forEach(i -> goNames[i] = setupHelper.goName(i));
        cacheManager.clearAll();
    }

    @Test
    public void canDownloadInExcelFormat() throws Exception {
        setupHelper.expectGoTermHasNameViaRest(NUMBER_OF_GENERIC_DOCS);
        setupHelper.expectTaxonIdHasNameViaRest(TAXON_ID, TAXON_NAME);
        setupHelper.expectEcoCodeHasNameViaRest(ECO_ID, ECO_TERM_NAME, NUMBER_OF_GENERIC_DOCS);

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL).header(ACCEPT, EXCEL_MEDIA_TYPE));

        checkResponse(EXCEL_MEDIA_TYPE, response);
    }

    @Test
    public void canDownloadInJsonFormat() throws Exception {
        setupHelper.expectGoTermHasNameViaRest(NUMBER_OF_GENERIC_DOCS);
        setupHelper.expectTaxonIdHasNameViaRest(TAXON_ID, TAXON_NAME);
        setupHelper.expectEcoCodeHasNameViaRest(ECO_ID, ECO_TERM_NAME, NUMBER_OF_GENERIC_DOCS);

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL).header(ACCEPT, JSON_MEDIA_TYPE));

        checkResponse(response, NUMBER_OF_GENERIC_DOCS);
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
    public void downloadStatisticsSuccessfulAfterFailedToRetrieveGONames() throws Exception {
        setExpectationsForUnsuccessfulOntologyServiceRestResponse();
        setupHelper.expectTaxonIdHasNameViaRest(TAXON_ID, TAXON_NAME);
        setupHelper.expectEcoCodeHasNameViaRest(ECO_ID, ECO_TERM_NAME, NUMBER_OF_GENERIC_DOCS);

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL).header(ACCEPT, JSON_MEDIA_TYPE));

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
    public void downloadStatisticsSuccessfulAfterFailedToRetrieveTaxonNames() throws Exception {
        setupHelper.expectGoTermHasNameViaRest(NUMBER_OF_GENERIC_DOCS);
        setExpectationsForUnsuccessfulTaxonomyServiceRestResponse();
        setupHelper.expectEcoCodeHasNameViaRest(ECO_ID, ECO_TERM_NAME, NUMBER_OF_GENERIC_DOCS);

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL).header(ACCEPT, JSON_MEDIA_TYPE));

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
    public void downloadStatisticsSuccessfulAfterFailedToRetrieveECONames() throws Exception {
        setupHelper.expectGoTermHasNameViaRest(NUMBER_OF_GENERIC_DOCS);
        setupHelper.expectTaxonIdHasNameViaRest(TAXON_ID, TAXON_NAME);
        setExpectationsForUnsuccessfulOntologyServiceRestResponseForEcoCodes();

        ResultActions response = mockMvc.perform(get(DOWNLOAD_STATISTICS_SEARCH_URL).header(ACCEPT, JSON_MEDIA_TYPE));

        checkResponse(JSON_MEDIA_TYPE, response);
        response.andDo(print())
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, GO_ID_STATS_FIELD, goNames))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, GO_ID_STATS_FIELD, goNames))
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, TAXON_ID_STATS_FIELD, new String[]{TAXON_NAME}))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, TAXON_ID_STATS_FIELD, new String[]{TAXON_NAME}))
                .andExpect(namesInTypeWithinGroup(ANNOTATION_GROUP, EVIDENCE_CODE_STATS_FIELD, new String[]{null}))
                .andExpect(namesInTypeWithinGroup(GENE_PRODUCT_GROUP, EVIDENCE_CODE_STATS_FIELD, new String[]{null}));
    }

    private void setExpectationsForUnsuccessfulOntologyServiceRestResponse() {
        for (int k = 0; k < NO_OF_STATISTICS_GROUPS; k++) {
            for (int j = 0; j < NUMBER_OF_GENERIC_DOCS; j++) {
                setupHelper.expectGORestCallResponse(setupHelper.goId(j), withStatus(HttpStatus.NOT_FOUND));
            }
        }
    }

    private void setExpectationsForUnsuccessfulTaxonomyServiceRestResponse() {
        for (int i = 0; i < NO_OF_STATISTICS_GROUPS; i++) {
            setupHelper.expectTaxonomyRestCallResponse(String.valueOf(TAXON_ID), withStatus(HttpStatus.NOT_FOUND));
        }
    }

    private void setExpectationsForUnsuccessfulOntologyServiceRestResponseForEcoCodes() {
        for (int i = 0; i < NO_OF_STATISTICS_GROUPS; i++) {
            setupHelper.expectEcoRestCallResponse(ECO_ID, withStatus(HttpStatus.NOT_FOUND));
        }
    }

    private void checkResponse(ResultActions response, int expectedSize) throws Exception {
        checkResponse(JSON_MEDIA_TYPE, response);
        response.andExpect(numOfResults(NUMBER_OF_GO_ID_RESULTS_FOR_ANNOTATIONS, expectedSize));
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
