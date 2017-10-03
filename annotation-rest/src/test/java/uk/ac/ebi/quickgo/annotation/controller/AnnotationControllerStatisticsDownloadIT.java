package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import java.util.List;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.VARY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createGenericDocsChangingGoId;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.numOfResults;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.EXCEL_MEDIA_TYPE;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.JSON_MEDIA_TYPE;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.fileExtension;

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
public class AnnotationControllerStatisticsDownloadIT {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();
    private static final int NUMBER_OF_GENERIC_DOCS = 50;
    private static final String DOWNLOAD_STATISTICS_SEARCH_URL = "/annotation/downloadStats";
    private static final String DOWNLOAD_LIMIT_PARAM = "downloadLimit";
    private static final int MIN_DOWNLOAD_NUMBER = 1;
    private static final int MAX_DOWNLOAD_NUMBER = 50000;
    public static final String NUMBER_OF_GO_ID_RESULTS_FOR_ANNOTATIONS =
            "$.results[0].types.[?(@.type == 'goId')].values.length()";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AnnotationRepository repository;

    @Before
    public void setup() {
        repository.deleteAll();

        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();

        List<AnnotationDocument> genericDocs = createDocs(NUMBER_OF_GENERIC_DOCS);
        saveToRepo(genericDocs);
    }

    @Test
    public void canDownloadInExcelFormat() throws Exception {
        canDownload(EXCEL_MEDIA_TYPE);
    }

    @Test
    public void canDownloadWithInJsonFormat() throws Exception {
        canDownload(JSON_MEDIA_TYPE,50);
    }

    @Test
    public void downloadLimitIsObeyed() throws Exception {
        canDownloadWithLimit(JSON_MEDIA_TYPE, 11);
    }

    @Test
    public void downloadLimitTooLargeCausesBadRequest() throws Exception {
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_STATISTICS_SEARCH_URL)
                        .header(ACCEPT, EXCEL_MEDIA_TYPE)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(MAX_DOWNLOAD_NUMBER + 1)));

        response
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void downloadLimitTooSmallCausesBadRequest() throws Exception {
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_STATISTICS_SEARCH_URL)
                        .header(ACCEPT, EXCEL_MEDIA_TYPE)
                        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(MIN_DOWNLOAD_NUMBER - 1)));

        response
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private List<AnnotationDocument> createDocs(int number) {
        return createGenericDocsChangingGoId(number);
    }

    private void saveToRepo(List<AnnotationDocument> docsToSave) {
        repository.save(docsToSave);
    }

    private void canDownload(MediaType mediaType) throws Exception {
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_STATISTICS_SEARCH_URL)
                        .header(ACCEPT, mediaType));
        checkResponse(mediaType, response);
    }

    private void canDownload(MediaType mediaType, int expectedSize) throws Exception {
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_STATISTICS_SEARCH_URL)
                        .header(ACCEPT, mediaType));
        checkResponse(mediaType, response, expectedSize);
    }

    private void canDownloadWithLimit(MediaType mediaType, int limit) throws Exception {
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_STATISTICS_SEARCH_URL)
                        .header(ACCEPT, mediaType)
        .param(DOWNLOAD_LIMIT_PARAM, Integer.toString(limit)));
        checkResponse(mediaType, response, limit);
    }

    private void checkResponse(MediaType mediaType, ResultActions response) throws Exception {
        response.andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andDo(print())
                .andExpect(header().string(VARY, is(ACCEPT)))
                .andExpect(header().string(CONTENT_DISPOSITION, endsWith("." + fileExtension(mediaType) + "\"")))
                .andExpect(content().contentType(mediaType));
    }

    private void checkResponse(MediaType mediaType, ResultActions response, int expectedSize) throws Exception {
        checkResponse(mediaType, response);
        response.andExpect(numOfResults(NUMBER_OF_GO_ID_RESULTS_FOR_ANNOTATIONS, expectedSize));
    }

}
