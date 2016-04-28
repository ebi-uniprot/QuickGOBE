package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
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

    private static final String ASSIGNEDBY_PARAM = "assignedby";
    protected MockMvc mockMvc;

    private final static String COMMA = ",";
    private String resourceUrl;
    private String validId;
    private String validAssignedBy;
    private String validIdsCSV;
    private List<String> validIdList;
    private static final String RESOURCE_URL = "/QuickGO/services/annotation";

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected AnnotationRepository annotationRepository;

    @Autowired
    private SolrTemplate annotationProductTemplate; //todo required?

    @Before
    public void setup() {
        annotationRepository.deleteAll();

        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();

        List<AnnotationDocument> basicDocs = createBasicDocs();
        assertThat(basicDocs.size(), is(greaterThan(1)));

        validId = basicDocs.get(0).id;
        validAssignedBy = basicDocs.get(0).assignedBy;
        validIdsCSV = basicDocs.stream().map(doc -> doc.id).collect(Collectors.joining(","));
        validIdList = Arrays.asList(validIdsCSV.split(COMMA));

        annotationRepository.save(basicDocs);
    }



    @Test
    public void lookupAnnotationFilterByAssignedBySuccessfullyAndReceivesValidResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL+"/search").param(ASSIGNEDBY_PARAM, validAssignedBy));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results.*").exists());
    }

    protected List<AnnotationDocument> createBasicDocs() {
        return Arrays.asList(
                AnnotationDocMocker.createAnnotationDoc("A0A000"),
                AnnotationDocMocker.createAnnotationDoc("A0A001","ASPGD"));
    }


    protected ResultActions expectResultsInfoExists(ResultActions result) throws Exception {
        return result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo").exists())
                .andExpect(jsonPath("$.pageInfo.resultsPerPage").exists())
                .andExpect(jsonPath("$.pageInfo.total").exists())
                .andExpect(jsonPath("$.pageInfo.current").exists());
    }

}
