package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createGenericDocs;
import static uk.ac.ebi.quickgo.annotation.service.http.GAFHttpMessageConverter.GAF_MEDIA_TYPE;
import static uk.ac.ebi.quickgo.annotation.service.http.GPADHttpMessageConverter.GPAD_MEDIA_TYPE;

/**
 * Tests whether the downloading functionality of the {@link AnnotationController} works as expected.
 * The functional tests relating to the filtering of results are covered by {@link AnnotationControllerIT} since the
 * search results found used by the download functionality is unchanged.
 *
 * Created 24/01/17
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AnnotationREST.class})
@WebAppConfiguration
public class AnnotationControllerDownloadIT {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    private static final int NUMBER_OF_GENERIC_DOCS = 10;
    private static final String DOWNLOAD_SEARCH_URL = "/annotation/downloadSearch";
    private static final String DOWNLOAD_LIMIT_PARAM = "downloadLimit";
    private static final int MIN_DOWNLOAD_NUMBER = 1;
    private static final int MAX_DOWNLOAD_NUMBER = 50000;

    private MockMvc mockMvc;

    private List<AnnotationDocument> genericDocs;

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

        genericDocs = createGenericDocs(NUMBER_OF_GENERIC_DOCS);
        repository.save(genericDocs);
    }

    @Test
    public void canDownloadInGafFormat() throws Exception {
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, GAF_MEDIA_TYPE)
                        .param(DOWNLOAD_LIMIT_PARAM, "10"));

        List<String> storedIds = getFieldValuesFromRepo(doc -> doc.geneProductId);

        response
                .andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andDo(print())
                .andExpect(header().string(CONTENT_DISPOSITION, endsWith(".gaf\"")))
                .andExpect(content().contentType(GAF_MEDIA_TYPE))
                .andExpect(content().string(stringContainsInOrder(storedIds)));
    }

    @Test
    public void canDownloadInGpadFormat() throws Exception {
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, GPAD_MEDIA_TYPE)
                        .param(DOWNLOAD_LIMIT_PARAM, "10"));

        List<String> storedIds = getFieldValuesFromRepo(doc -> doc.geneProductId);

        response
                .andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andDo(print())
                .andExpect(header().string(CONTENT_DISPOSITION, endsWith(".gpad\"")))
                .andExpect(content().contentType(GPAD_MEDIA_TYPE))
                .andExpect(content().string(stringContainsInOrder(storedIds)));
    }

    @Test
    public void downloadLimitTooLargeCausesBadRequest() throws Exception {
        ResultActions response = mockMvc.perform(
                get(DOWNLOAD_SEARCH_URL)
                        .header(ACCEPT, GAF_MEDIA_TYPE)
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

    private <O> List<O> getFieldValuesFromRepo(Function<AnnotationDocument, O> transformation) {
        return genericDocs.stream()
                .map(transformation)
                .collect(Collectors.toList());
    }
}
