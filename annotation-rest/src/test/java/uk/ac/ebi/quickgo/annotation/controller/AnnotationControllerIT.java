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

    private static final String ASSIGNED_BY_PARAM = "assignedby";
    private static final String PAGE = "page";


    protected MockMvc mockMvc;

    private String validAssignedBy;
    private static final String INVALID_ASSIGNED_BY = "ZZZZZ";
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
        validAssignedBy = basicDocs.get(0).assignedBy;
        annotationRepository.save(basicDocs);
    }



    @Test
    public void lookupAnnotationFilterByAssignedBySuccessfully() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, validAssignedBy));

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
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, INVALID_ASSIGNED_BY));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }


    @Test
    public void lookupAnnotationFilterByMultipleAssignedByOneCorrectAndOneInvalid() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, INVALID_ASSIGNED_BY + ","
                        + basicDocs.get(1).assignedBy));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }


    @Test
    public void retrievesSecondPageOfAllEntriesRequest() throws Exception {
        annotationRepository.deleteAll();
        annotationRepository.save(createNDocs(60));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, validAssignedBy)
                        .param(PAGE, "2"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results", hasSize(Integer.parseInt(AnnotationFilter.DEFAULT_ENTRIES_PER_PAGE))));
    }


    @Test
    public void cannotRetrievesPageOfEntriesPassedWhatsAvailable() throws Exception {
        annotationRepository.deleteAll();
        annotationRepository.save(createNDocs(60));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNED_BY_PARAM, validAssignedBy)
                        .param(PAGE, "4"));

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

    protected ResultActions expectFields(ResultActions result, String path) throws Exception {
        return result
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


    private List<AnnotationDocument> createNDocs(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDoc(createId(i))).collect
                        (Collectors.toList());
    }

    private String createId(int idNum) {
        return String.format("A0A%03d", idNum);
    }
}
