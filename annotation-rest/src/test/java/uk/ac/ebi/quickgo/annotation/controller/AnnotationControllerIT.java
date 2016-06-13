package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.common.AnnotationRepository;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.annotation.controller.ResponseVerifier.*;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.DEFAULT_ENTRIES_PER_PAGE;

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
    private static final String REF_PARAM = "reference";
    private static final String PAGE_PARAM = "page";
    private static final String LIMIT_PARAM = "limit";

    private static final String UNAVAILABLE_ASSIGNED_BY = "ZZZZZ";

    private MockMvc mockMvc;

    private List<AnnotationDocument> genericDocs;
    private static final String RESOURCE_URL = "/QuickGO/services/annotation";

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

    //ASSIGNED BY
    @Test
    public void lookupAnnotationFilterByAssignedBySuccessfully() throws Exception {
        String geneProductId = "P99999";
        String assignedBy = "ASPGD";

        AnnotationDocument document = createDocWithAssignedBy(geneProductId, assignedBy);
        repository.save(document);

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, assignedBy));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId));
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

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId1, geneProductId2));
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

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(2))
                .andExpect(fieldsInAllResultsExist(2))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId1, geneProductId2));
    }

    @Test
    public void lookupAnnotationFilterByInvalidAssignedBy() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, UNAVAILABLE_ASSIGNED_BY));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(0));
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

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(1))
                .andExpect(fieldsInAllResultsExist(1))
                .andExpect(valuesOccurInField(GENEPRODUCT_ID_FIELD, geneProductId));
    }

    @Test
    public void invalidAssignedByThrowsAnError() throws Exception {
        String invalidAssignedBy = "_ASPGD";

        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(ASSIGNED_BY_PARAM, invalidAssignedBy));
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

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(totalEntries))
                .andExpect(resultsInPage(DEFAULT_ENTRIES_PER_PAGE))
                .andExpect(
                        pageInfoMatches(
                                2,
                                totalPages(totalEntries, DEFAULT_ENTRIES_PER_PAGE),
                                DEFAULT_ENTRIES_PER_PAGE)
                );
    }

    @Test
    public void pageRequestEqualToAvailablePagesReturns200() throws Exception {
        ResultActions response = mockMvc.perform(
                get(RESOURCE_URL + "/search").param(PAGE_PARAM, "1"));

        response.andExpect(status().isOk())
                .andExpect(contentTypeToBeJson())
                .andExpect(totalNumOfResults(genericDocs.size()))
                .andExpect(
                        pageInfoMatches(
                                1,
                                totalPages(genericDocs.size(), DEFAULT_ENTRIES_PER_PAGE),
                                DEFAULT_ENTRIES_PER_PAGE)
                );
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

        response.andExpect(status().isOk())
                .andExpect(totalNumOfResults(genericDocs.size()))
                .andExpect(pageInfoMatches(1, 1, 100));
    }

    @Test
    public void limitForPageThrowsErrorWhenNegative() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search")
                .param(LIMIT_PARAM, "-20"));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    private AnnotationDocument createDocWithAssignedBy(String geneProductId, String assignedBy) {
        AnnotationDocument doc = AnnotationDocMocker.createAnnotationDoc(geneProductId);
        doc.assignedBy = assignedBy;

    //----- Tests for reference ---------------------//

    @Test
    public void singleReferenceIsSuccessful() throws Exception{

        AnnotationDocument a = AnnotationDocMocker.createAnnotationDoc("A0A123");
        a.reference = "PMID:0000002";
        annotationRepository.save(a);
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM, AnnotationDocMocker.GO_REF_0000002));

        expectResultsInfoExists(response, basicDocs.size())
                .andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(basicDocs.size()))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void threeReferencesAreSuccessful() throws Exception{

        AnnotationDocument a = AnnotationDocMocker.createAnnotationDoc("A0A123");
        a.reference = "PMID:0000002";
        annotationRepository.save(a);

        AnnotationDocument b = AnnotationDocMocker.createAnnotationDoc("A0A124");
        b.reference = "PMID:0000003";
        annotationRepository.save(b);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM,
                AnnotationDocMocker.GO_REF_0000002 + "," + a.reference + "," + b.reference));

        expectResultsInfoExists(response, basicDocs.size())
                .andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(basicDocs.size() + 2))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(jsonPath("$.results[0].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[1].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[2].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[3].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[4].reference").value(a.reference))
                .andExpect(jsonPath("$.results[5].reference").value(b.reference))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

    }


    @Test
    public void threeIndependentReferencesAreSuccessful() throws Exception{

        AnnotationDocument a = AnnotationDocMocker.createAnnotationDoc("A0A123");
        a.reference = "PMID:0000002";
        annotationRepository.save(a);

        AnnotationDocument b = AnnotationDocMocker.createAnnotationDoc("A0A124");
        b.reference = "PMID:0000003";
        annotationRepository.save(b);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM,
                AnnotationDocMocker.GO_REF_0000002).param(REF_PARAM, a.reference).param(REF_PARAM, b.reference));

        expectResultsInfoExists(response, basicDocs.size())
                .andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(basicDocs.size() + 2))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(jsonPath("$.results[0].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[1].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[2].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[3].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[4].reference").value(a.reference))
                .andExpect(jsonPath("$.results[5].reference").value(b.reference))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }



    @Test
    public void dbNameIsSuccessful() throws Exception{

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM, "GO_REF"));

        //This one shouldn't be found
        AnnotationDocument a = AnnotationDocMocker.createAnnotationDoc("A0A123");
        a.reference = "PMID:0000002";
        annotationRepository.save(a);

        expectResultsInfoExists(response, basicDocs.size())
                .andExpect(jsonPath("$.numberOfHits").value(basicDocs.size()))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void unknownDbNameIsUnsuccessful() throws Exception{

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM, "GO_LEFT"));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }


    @Test
    public void singleDbIdIsSuccessful() throws Exception{
        //Don't find this one.
        AnnotationDocument a = AnnotationDocMocker.createAnnotationDoc("A0A123");
        a.reference = "PMID:0000002";
        annotationRepository.save(a);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM, "0000002"));

        expectResultsInfoExists(response, basicDocs.size())
                .andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(basicDocs.size()+1))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(jsonPath("$.results[0].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[1].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[2].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[3].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[4].reference").value(a.reference))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }


    @Test
    public void multipleDbIdIsSuccessful() throws Exception{
        AnnotationDocument a = AnnotationDocMocker.createAnnotationDoc("A0A123");
        a.reference = "PMID:0000002";
        annotationRepository.save(a);

        AnnotationDocument b = AnnotationDocMocker.createAnnotationDoc("A0A124");
        b.reference = "PMID:0000003";
        annotationRepository.save(b);

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM, "0000002")
                .param(REF_PARAM, "0000003"));

        expectResultsInfoExists(response, basicDocs.size())
                .andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(basicDocs.size()+2))
                .andExpect(jsonPath("$.results.*").exists())
                .andExpect(jsonPath("$.results[0].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[1].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[2].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[3].reference").value(AnnotationDocMocker.GO_REF_0000002))
                .andExpect(jsonPath("$.results[4].reference").value(a.reference))
                .andExpect(jsonPath("$.results[5].reference").value(b.reference))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }



    @Test
    public void unknownDbIdIsUnsuccessful() throws Exception{

        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/search").param(REF_PARAM, "999999"));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    /**
     *      TESTING RESULTS
     */

        return doc;
    private ResultActions expectResultsInfoExists(ResultActions result, int expectedHits) throws Exception {
        return expectFieldsInResults(result, expectedHits)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo").exists())
                .andExpect(jsonPath("$.pageInfo.resultsPerPage").exists())
                .andExpect(jsonPath("$.pageInfo.total").exists())
                .andExpect(jsonPath("$.pageInfo.current").exists());
    }

    private List<AnnotationDocument> createGenericDocs(int n) {
    private ResultActions expectFieldsInResults(ResultActions result, int expectedHits) throws Exception {
        int index = 0;

        for (int i = 0; i < expectedHits; i++) {
            expectFields(result, "$.results[" + index++ + "].");
        }

        return result;
    }

    private void expectFields(ResultActions result, String path) throws Exception {
        result
                .andExpect(jsonPath(path + "reference").exists())
                .andExpect(jsonPath(path + "assignedBy").exists());

//        .andExpect(jsonPath(path + "id").exists())
//                .andExpect(jsonPath(path + "geneProductId").exists())
//                .andExpect(jsonPath(path + "qualifier").exists())
//                .andExpect(jsonPath(path + "goId").exists())
//                .andExpect(jsonPath(path + "goEvidence").exists())
//                .andExpect(jsonPath(path + "ecoId").exists())
//                .andExpect(jsonPath(path + "withFrom").exists())
//                .andExpect(jsonPath(path + "taxonId").exists())
//                .andExpect(jsonPath(path + "extension").exists());

    }

    /**
     * Create some
     */
    private List<AnnotationDocument> createBasicDocs() {
        return Arrays.asList(
                AnnotationDocMocker.createAnnotationDoc("A0A000"),
                AnnotationDocMocker.createAnnotationDoc("A0A001", "ASPGD"),
                AnnotationDocMocker.createAnnotationDoc("A0A001", "BHF-UCL"),
                AnnotationDocMocker.createAnnotationDoc("A0A002", "AGPRD"));
    }

    private List<AnnotationDocument> createAndSaveDocs(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> AnnotationDocMocker.createAnnotationDoc(createId(i))).collect
                        (Collectors.toList());
    }

    private String createId(int idNum) {
        return String.format("A0A%03d", idNum);
    }

    private int totalPages(int totalEntries, int resultsPerPage) {
        return (int) Math.ceil(totalEntries / resultsPerPage) + 1;
    }
}
