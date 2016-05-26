package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig;
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
import static org.hamcrest.Matchers.contains;
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

    private static final int NUMBER_OF_GENERIC_DOCS = 3;

    private static final String ASSIGNED_BY_PARAM = "assignedBy";
    private static final String PAGE_PARAM = "page";
    private static final String LIMIT_PARAM = "limit";
    private static final String TAXON_ID_PARAM = "taxon";

    private static final String UNAVAILABLE_ASSIGNED_BY = "ZZZZZ";

    private static final List<String> VALID_ASSIGNED_BY_PARMS = Arrays.asList("ASPGD", "ASPGD,Agbase", "ASPGD_,Agbase",
            "ASPGD,Agbase_", "ASPGD,Agbase", "BHF-UCL,Agbase", "Roslin_Institute,BHF-UCL,Agbase");

    private static final List<String> INVALID_ASSIGNED_BY_PARMS =
            Arrays.asList("_ASPGD", "ASPGD,_Agbase", "5555,Agbase",
                    "ASPGD,5555,", "4444,5555,");

    private MockMvc mockMvc;

    private List<AnnotationDocument> genericDocs;
    private static final String RESOURCE_URL = "/QuickGO/services/annotation";

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected AnnotationRepository repository;

    @Before
    public void setup() {
        repository.deleteAll();

        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext)
                .build();

        genericDocs = createGenericDocs(NUMBER_OF_GENERIC_DOCS);
        repository.save(genericDocs);
    }

    //ASSIGNED BY
    @Test
    public void lookupAnnotationFilterByAssignedBySuccessfully() throws Exception {
        String geneProductId = "P99999";
        String assignedBy = "ASPGD";

        AnnotationDocument document = createDocWithAssignedBy(geneProductId, assignedBy);
        repository.save(document);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, assignedBy));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results[0].geneProductId").value(geneProductId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void lookupAnnotationFilterByMultipleAssignedBySuccessfully() throws Exception {
        String geneProductId1 = "P99999";
        String assignedBy1 = "ASPGD";

        AnnotationDocument document1 = createDocWithAssignedBy(geneProductId1, assignedBy1);
        repository.save(document1);

        String geneProductId2 = "P99998";
        String assignedBy2 = "BHF-UCL";

        AnnotationDocument document2 = createDocWithAssignedBy(geneProductId2, assignedBy2);
        repository.save(document2);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, assignedBy1 + "," + assignedBy2));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(2))
                .andExpect(jsonPath("$.results[*].geneProductId", contains(geneProductId1, geneProductId2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void lookupAnnotationFilterByRepetitionOfParmsSuccessfully() throws Exception {
        String geneProductId1 = "P99999";
        String assignedBy1 = "ASPGD";

        AnnotationDocument document1 = createDocWithAssignedBy(geneProductId1, assignedBy1);
        repository.save(document1);

        String geneProductId2 = "P99998";
        String assignedBy2 = "BHF-UCL";

        AnnotationDocument document2 = createDocWithAssignedBy(geneProductId2, assignedBy2);
        repository.save(document2);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(ASSIGNED_BY_PARAM, assignedBy1)
                        .param(ASSIGNED_BY_PARAM, assignedBy2));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(2))
                .andExpect(jsonPath("$.results[*].geneProductId", contains(geneProductId1, geneProductId2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void lookupAnnotationFilterByInvalidAssignedBy() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, UNAVAILABLE_ASSIGNED_BY));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void lookupAnnotationFilterByMultipleAssignedByOneCorrectAndOneUnavailable() throws Exception {
        String geneProductId = "P99999";
        String assignedBy = "ASPGD";

        AnnotationDocument document = createDocWithAssignedBy(geneProductId, assignedBy);
        repository.save(document);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, UNAVAILABLE_ASSIGNED_BY + ","
                        + assignedBy));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results[0].geneProductId").value(geneProductId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void invalidAssignedByThrowsAnError() throws Exception {
        String invalidAssignedBy = "_ASPGD";

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, invalidAssignedBy));
        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    //TAXON ID
    @Test
    public void lookupAnnotationFilterByTaxonIdSuccessfully() throws Exception {
        String geneProductId = "P99999";
        int taxonId = 2;

        AnnotationDocument document = createDocWithTaxonId(geneProductId, taxonId);
        repository.save(document);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(TAXON_ID_PARAM, "2"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results[0].geneProductId").value(geneProductId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void lookupAnnotationFilterByMultipleTaxonIdsSuccessfully() throws Exception {
        String geneProductId1 = "P99999";
        int taxonId1 = 2;

        AnnotationDocument document1 = createDocWithTaxonId(geneProductId1, taxonId1);
        repository.save(document1);

        String geneProductId2 = "P99998";
        int taxonId2 = 3;

        AnnotationDocument document2 = createDocWithTaxonId(geneProductId2, taxonId2);
        repository.save(document2);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(TAXON_ID_PARAM, String.valueOf(taxonId1))
                        .param(TAXON_ID_PARAM, String.valueOf(taxonId2)));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(2))
                .andExpect(jsonPath("$.results.[*].geneProductId", contains(geneProductId1, geneProductId2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void invalidTaxIdThrowsError() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(TAXON_ID_PARAM, "-2"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    //---------- Page related tests.

    @Test
    public void retrievesSecondPageOfAllEntriesRequest() throws Exception {
        int totalEntries = 60;

        repository.deleteAll();
        repository.save(createGenericDocs(totalEntries));

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(PAGE_PARAM, "2"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(totalEntries))
                .andExpect(jsonPath("$.results", hasSize(AnnotationRequest.DEFAULT_ENTRIES_PER_PAGE)));
    }

    @Test
    public void pageRequestEqualToAvailablePagesReturns200() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(PAGE_PARAM, "1"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(NUMBER_OF_GENERIC_DOCS))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void pageRequestOfZeroAndResultsAvailableReturns400() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(PAGE_PARAM, "0"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void pageRequestHigherThanAvailablePagesReturns400() throws Exception {

        repository.deleteAll();

        int existingPages = 4;
        createGenericDocs(SearchServiceConfig.MAX_PAGE_RESULTS * existingPages);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(PAGE_PARAM, String.valueOf(existingPages + 1)));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    //---------- Limit related tests.

    @Test
    public void limitForPageExceedsMaximumAllowed() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search")
                        .param(LIMIT_PARAM, "101"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void limitForPageWithinMaximumAllowed() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(LIMIT_PARAM, "100"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(NUMBER_OF_GENERIC_DOCS))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void limitForPageThrowsErrorWhenNegative() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(LIMIT_PARAM, "-20"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

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

        for (int i = 0; i > genericDocs.size(); i++) {
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
                .andExpect(jsonPath(path + "extensions").exists());
    }

    private AnnotationDocument createDocWithAssignedBy(String geneProductId, String assignedBy) {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        doc.assignedBy = assignedBy;

        return doc;
    }

    private AnnotationDocument createDocWithTaxonId(String geneProductId, int taxonId) {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        doc.taxonId = taxonId;

        return doc;
    }

    private List<AnnotationDocument> createGenericDocs(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDoc(createId(i))).collect
                        (Collectors.toList());
    }

    private String createId(int idNum) {
        return String.format("A0A%03d", idNum);
    }
}
