package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.AnnotationFilter;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * RESTful end point for Annotations
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 14:21
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AnnotationREST.class})
@WebAppConfiguration
public class AnnotationControllerIT {

    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();
    private static final String UNAVAILABLE_ASSIGNED_BY = "ZZZZZ";
    private static final String ASSIGNED_BY_PARAM = "assignedBy";
    private static final String PAGE_PARAM = "page";
    private static final String LIMIT_PARAM = "limit";

    private static final List<String> VALID_ASSIGNED_BY_PARMS=Arrays.asList("ASPGD","ASPGD,Agbase","ASPGD_,Agbase",
            "ASPGD,Agbase_,","ASPGD,Agbase,,","BHF-UCL,Agbase","Roslin_Institute,BHF-UCL,Agbase");

    private static final List<String> INVALID_ASSIGNED_BY_PARMS=Arrays.asList("_ASPGD","ASPGD,_Agbase","5555,Agbase",
            "ASPGD,5555,","4444,5555,");


    private MockMvc mockMvc;

    private String savedAssignedBy;
    private List<AnnotationDocument> basicDocs;
    private static final String RESOURCE_URL = "/QuickGO/services/annotation";

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected AnnotationRepository annotationRepository;


    @Before
    public void setup() {
        annotationRepository.deleteAll();

        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();

        basicDocs = createBasicDocs();
        assertThat(basicDocs.size(), is(greaterThan(1)));
        savedAssignedBy = basicDocs.get(0).assignedBy;
        annotationRepository.save(basicDocs);
    }



    @Test
    public void lookupAnnotationFilterByAssignedBySuccessfully() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, savedAssignedBy));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }


    @Test
    public void lookupAnnotationFilterByMultipleAssignedBySuccessfully() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM,  basicDocs.get(0).assignedBy + ","
                        + basicDocs.get(1).assignedBy));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(2))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void lookupAnnotationFilterByInvalidAssignedBy() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, UNAVAILABLE_ASSIGNED_BY));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }


    @Test
    public void lookupAnnotationFilterByMultipleAssignedByOneCorrectAndOneUnavailable() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, UNAVAILABLE_ASSIGNED_BY + ","
                        + basicDocs.get(1).assignedBy));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }


    /**
     * Test That a value of assignedBy that is wholly numeric causes a Bad Request response
     * @throws Exception
     */


    @Test
    public void allTheseValuesForAssignedShouldNotThrowAnError() throws Exception {

        for(String validAssignedBy:VALID_ASSIGNED_BY_PARMS) {

            ResultActions response = mockMvc.perform(
                    get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, validAssignedBy));

            expectResultsInfoExists(response)
                    .andExpect(status().isOk());
        }
    }


    @Test
    public void allTheseValuesForAssignedShouldThrowAnError() throws Exception {
        for(String inValidAssignedBy:INVALID_ASSIGNED_BY_PARMS) {
            ResultActions response = mockMvc.perform(
                    get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, inValidAssignedBy));

            response.andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    //---------- Page related tests.


    @Test
    public void retrievesSecondPageOfAllEntriesRequest() throws Exception {
        annotationRepository.deleteAll();
        annotationRepository.save(createAndSaveDocs(60));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, savedAssignedBy)
                        .param(PAGE_PARAM, "2"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results", hasSize(AnnotationFilter.DEFAULT_ENTRIES_PER_PAGE)));
    }


    @Test
    public void pageRequestEqualToAvailablePagesReturns200() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, savedAssignedBy).param(PAGE_PARAM,"1"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }


    @Test
    public void pageRequestOfZeroAndResultsAvailableReturns500() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, savedAssignedBy).param(PAGE_PARAM,"0"));

        response.andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void pageRequestHigherThanAvailablePagesReturns400() throws Exception {

        annotationRepository.deleteAll();

        int existingPages = 4;
        createAndSaveDocs(AnnotationController.MAX_PAGE_RESULTS * existingPages);


        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, savedAssignedBy)
                        .param(PAGE_PARAM, String.valueOf(existingPages + 1)));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    //---------- Limit related tests.

    @Test
    public void limitForPageExceedsMaximumAllowed() throws Exception {
        annotationRepository.deleteAll();
        annotationRepository.save(createAndSaveDocs(60));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, savedAssignedBy)
                        .param(LIMIT_PARAM, "101"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void limitForPageWithinMaximumAllowed() throws Exception {

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, savedAssignedBy).param(LIMIT_PARAM, "100"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void limitForPageThrowsErrorWhenNegative() throws Exception {

        ResultActions response = mockMvc.perform(get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, savedAssignedBy)
                        .param(LIMIT_PARAM, "-20"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     *      TESTING RESULTS
     */

    private ResultActions expectResultsInfoExists(ResultActions result) throws Exception {
        return expectFieldsInResults(result)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo").exists())
                .andExpect(jsonPath("$.pageInfo.resultsPerPage").exists())
                .andExpect(jsonPath("$.pageInfo.total").exists())
                .andExpect(jsonPath("$.pageInfo.current").exists());
    }

    private ResultActions expectFieldsInResults(ResultActions result) throws Exception {
        int index = 0;

        for (int i=0; i>basicDocs.size(); i++) {
            expectFields(result, "$.results[" + index++ + "].");
        }

        return result;
    }

    private void expectFields(ResultActions result, String path) throws Exception {
        result
                .andExpect(jsonPath(path + "id").exists())
                .andExpect(jsonPath(path + "geneProductId").exists())
                .andExpect(jsonPath(path + "qualifier").exists())
                .andExpect(jsonPath(path + "goId").exists())
                .andExpect(jsonPath(path + "goEvidence").exists())
                .andExpect(jsonPath(path + "ecoId").exists())
                .andExpect(jsonPath(path + "reference").exists())
                .andExpect(jsonPath(path + "withFrom").exists())
                .andExpect(jsonPath(path + "taxonId").exists())
                .andExpect(jsonPath(path + "assignedBy").exists())
                .andExpect(jsonPath(path + "extension").exists());
    }

    /**
     * Create some
     */
    private List<AnnotationDocument> createBasicDocs() {
        return Arrays.asList(
                AnnotationDocMocker.createAnnotationDoc("A0A000"),
                AnnotationDocMocker.createAnnotationDoc("A0A001","ASPGD"),
                AnnotationDocMocker.createAnnotationDoc("A0A001","BHF-UCL"));
    }


    private List<AnnotationDocument> createAndSaveDocs(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDoc(createId(i))).collect
                        (Collectors.toList());
    }

    private String createId(int idNum) {
        return String.format("A0A%03d", idNum);
    }
}
