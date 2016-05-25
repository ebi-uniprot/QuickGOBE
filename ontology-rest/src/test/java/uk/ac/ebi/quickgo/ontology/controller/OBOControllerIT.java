package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.OntologyREST;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Performs common tests on REST controllers that derive from {@link OBOController}.
 * Uses an embedded Solr server that is cleaned up automatically after tests complete.
 *
 * Created by edd on 14/01/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OntologyREST.class})
@WebAppConfiguration
public abstract class OBOControllerIT {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    static final String COMMA = ",";

    private static final String SEARCH_ENDPOINT = "search";
    private static final String TERMS_ENDPOINT = "terms";
    private static final String ANCESTORS_ENDPOINT = "ancestors";
    private static final String DESCENDANTS_ENDPOINT = "descendants";
    private static final String PATHS_ENDPOINT = "paths";
    private static final String QUERY_PARAM = "query";
    private static final String PAGE_PARAM = "page";
    private static final String RELATIONS_PARAM = "relations";

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected OntologyRepository ontologyRepository;

    @Autowired
    protected OntologyGraph ontologyGraph;

    MockMvc mockMvc;

    private String resourceUrl;
    private String validId;
    private String validIdsCSV;
    private List<String> validIdList;
    private List<OntologyRelationship> relationships;
    private int relationshipChainLength;
    private String validRelation;
    private String invalidRelation;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();

        resourceUrl = getResourceURL();

        List<OntologyDocument> basicDocs = createBasicDocs();
        assertThat(basicDocs.size(), is(greaterThan(1)));

        validId = basicDocs.get(0).id;
        validIdsCSV = basicDocs.stream().map(doc -> doc.id).collect(Collectors.joining(","));
        validIdList = Arrays.asList(validIdsCSV.split(COMMA));

        ontologyRepository.deleteAll();
        ontologyRepository.save(basicDocs);

        setupSimpleRelationshipChain();
    }

    @Test
    public void canRetrieveCoreAttrByOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validId)));

        expectCoreFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.id", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCoreAttrByTwoIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV)));

        expectCoreFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.id", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCompletebyOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validId) + "/complete"));

        expectCompleteFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.history", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCompletebyTwoIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV) + "/complete"));

        expectCompleteFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.history", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveHistorybyOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validId) + "/history"));

        expectBasicFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.history", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveHistorybyTwoIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV) + "/history"));

        expectBasicFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.history", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXRefsbyOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validId) + "/xrefs"));

        expectBasicFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.xRefs", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXRefsbyTwoIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV) + "/xrefs"));

        expectBasicFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.xRefs", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveTaxonConstraintsbyOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validId) + "/constraints"));

        expectBasicFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.taxonConstraints", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveTaxonConstraintsbyTwoIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV) + "/constraints"));

        expectBasicFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.taxonConstraints", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveAnnotationGuideLinesbyOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validId) + "/guidelines"));

        expectBasicFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.annotationGuidelines", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveAnnotationGuideLinesbyTwoIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV) + "/guidelines"));

        expectBasicFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.annotationGuidelines", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXORelsbyOneId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validId) + "/xontologyrelations"));

        expectBasicFieldsInResults(response, singletonList(validId))
                .andExpect(jsonPath("$.results.*.xRelations", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXORelsbyTwoIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV) + "/xontologyrelations"));

        expectBasicFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.xRelations", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void finds400IfUrlIsEmpty() throws Exception {
        mockMvc.perform(get(resourceUrl + "/"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void finds400IfUrlIsJustWrong() throws Exception {
        mockMvc.perform(get(resourceUrl + "/thisIsAnEndPointThatDoesNotExist"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void finds200IfNoResultsBecauseIdsDoNotExist() throws Exception {
        mockMvc.perform(get(buildTermsURL(idMissingInRepository())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void finds400OnInvalidId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(invalidId())))
                .andDo(print())
                .andExpect(status().isBadRequest());

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void searchesForTermSuccessfullyAndReceivesValidResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(resourceUrl + "/" + SEARCH_ENDPOINT)
                        .param(QUERY_PARAM, validId));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results.*").exists());
    }

    @Test
    public void searchesForInvalidIdAndReceivesZeroValidResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(resourceUrl + "/" + SEARCH_ENDPOINT)
                        .param(QUERY_PARAM, invalidId()));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void searchesForMissingIdAndReceivesZeroValidResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(resourceUrl + "/" + SEARCH_ENDPOINT)
                        .param(QUERY_PARAM, idMissingInRepository()));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void searchesForFieldThatDoesNotExistAndReceivesZeroValidResults() throws Exception {
        ResultActions response = mockMvc.perform(
                get(resourceUrl + "/" + SEARCH_ENDPOINT)
                        .param(QUERY_PARAM, "fieldDoesNotExist:sandwiches"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.numberOfHits").value(0))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void negativePageRequestOfAllEntriesRequestReturns400() throws Exception {
        ontologyRepository.deleteAll();

        int existingPages = 4;
        createAndSaveDocs(OBOController.MAX_PAGE_RESULTS * existingPages);

        mockMvc.perform(
                get(resourceUrl + "/" + TERMS_ENDPOINT)
                        .param(PAGE_PARAM, "-1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void pageRequestHigherThanAvailablePagesForAllEntriesRequestReturns400() throws Exception {
        ontologyRepository.deleteAll();

        int existingPages = 4;
        createAndSaveDocs(OBOController.MAX_PAGE_RESULTS * existingPages);

        mockMvc.perform(
                get(resourceUrl + "/" + TERMS_ENDPOINT)
                        .param(PAGE_PARAM, String.valueOf(existingPages + 1)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void retrievesFirstPageOfAllEntriesRequest() throws Exception {
        ontologyRepository.deleteAll();

        int existingPages = 4;
        createAndSaveDocs(OBOController.MAX_PAGE_RESULTS * existingPages);

        ResultActions response = mockMvc.perform(
                get(resourceUrl + "/" + TERMS_ENDPOINT)
                        .param(PAGE_PARAM, "1"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results", hasSize(OBOController.MAX_PAGE_RESULTS)));
    }

    @Test
    public void retrievesSecondPageOfAllEntriesRequest() throws Exception {
        ontologyRepository.deleteAll();

        int existingPages = 4;
        createAndSaveDocs(OBOController.MAX_PAGE_RESULTS * existingPages);

        ResultActions response = mockMvc.perform(
                get(resourceUrl + "/" + TERMS_ENDPOINT)
                        .param(PAGE_PARAM, "2"));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results", hasSize(OBOController.MAX_PAGE_RESULTS)));
    }

    @Test
    public void retrievesLastPageOfAllEntriesRequest() throws Exception {
        ontologyRepository.deleteAll();

        int existingPages = 4;
        createAndSaveDocs(OBOController.MAX_PAGE_RESULTS * existingPages);

        ResultActions response = mockMvc.perform(
                get(resourceUrl + "/" + TERMS_ENDPOINT)
                        .param(PAGE_PARAM, String.valueOf(existingPages - 1)));

        expectResultsInfoExists(response)
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results", hasSize(OBOController.MAX_PAGE_RESULTS)));
    }

    @Test
    public void canRetrieveCompleteByFindAll() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL()));

        expectCompleteFieldsInResults(response, validIdList)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canFetchAllAncestorsFrom1Term() throws Exception {
        String lowestChild = relationships.get(0).child;
        ResultActions response = mockMvc.perform(
                get(buildTermsURL(lowestChild) + "/" + ANCESTORS_ENDPOINT));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(relationshipChainLength))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void canFetchAllAncestorsFrom2Terms() throws Exception {
        String bottom = relationships.get(0).child;
        String secondBottom = relationships.get(1).child;

        ResultActions response = mockMvc.perform(
                get(buildTermsURL(asCSV(bottom, secondBottom)) + "/" + ANCESTORS_ENDPOINT));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(relationshipChainLength))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void canFetchAllAncestorsFromRelation() throws Exception {
        String bottom = relationships.get(0).child;

        ResultActions response = mockMvc.perform(
                get(buildTermsURL(bottom) + "/" + ANCESTORS_ENDPOINT)
                        .param(RELATIONS_PARAM, validRelation));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(relationshipChainLength))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void invalidAncestorsProduces400AndErrorMessage() throws Exception {
        ResultActions response = mockMvc.perform(
                get(buildTermsURL(invalidId()) + "/" + ANCESTORS_ENDPOINT));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void invalidAncestorsRelationProduces400AndErrorMessage() throws Exception {
        String bottom = relationships.get(0).child;

        ResultActions response = mockMvc.perform(
                get(buildTermsURL(bottom) + "/" + ANCESTORS_ENDPOINT)
                        .param(RELATIONS_PARAM, invalidRelation));

        expectInvalidRelationError(response, invalidRelation);
    }

    @Test
    public void canFetchAllDescendantsFrom1Term() throws Exception {
        String highestParent = relationships.get(relationships.size() - 1).parent;
        ResultActions response = mockMvc.perform(
                get(buildTermsURL(highestParent) + "/" + DESCENDANTS_ENDPOINT));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(relationshipChainLength))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void canFetchAllDescendantsFrom2Terms() throws Exception {
        String top = relationships.get(relationships.size() - 1).parent;
        String secondTop = relationships.get(relationships.size() - 2).parent;

        ResultActions response = mockMvc.perform(
                get(buildTermsURL(asCSV(top, secondTop)) + "/" + DESCENDANTS_ENDPOINT));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(relationshipChainLength))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void canFetchAllDescendantsFromRelation() throws Exception {
        String highestParent = relationships.get(relationships.size() - 1).parent;
        ResultActions response = mockMvc.perform(
                get(buildTermsURL(highestParent) + "/" + DESCENDANTS_ENDPOINT)
                        .param(RELATIONS_PARAM, validRelation));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(relationshipChainLength))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void invalidDescendantsProduces400AndErrorMessage() throws Exception {
        ResultActions response = mockMvc.perform(
                get(buildTermsURL(invalidId()) + "/" + DESCENDANTS_ENDPOINT));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void invalidDescendantsRelationProduces400AndErrorMessage() throws Exception {
        String highestParent = relationships.get(relationships.size() - 1).parent;

        ResultActions response = mockMvc.perform(
                get(buildTermsURL(highestParent) + "/" + DESCENDANTS_ENDPOINT)
                        .param(RELATIONS_PARAM, invalidRelation));

        expectInvalidRelationError(response, invalidRelation);
    }

    @Test
    public void canFetchAllPathsFrom1Term() throws Exception {
        String bottomChild = relationships.get(0).child;
        String highestParent = relationships.get(relationships.size() - 1).parent;

        ResultActions response = mockMvc.perform(
                get(buildTermsURL(bottomChild) + "/" + PATHS_ENDPOINT + "/" + highestParent));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void canFetchAllPathsFrom2Terms() throws Exception {
        String bottom = relationships.get(0).child;
        String secondBottom = relationships.get(1).child;
        String highest = relationships.get(relationships.size() - 1).parent;

        ResultActions response = mockMvc.perform(
                get(buildTermsURL(asCSV(bottom, secondBottom) + "/" + PATHS_ENDPOINT + "/" + highest)));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(2))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void canFetchAllPathsTo2Terms() throws Exception {
        String bottom = relationships.get(0).child;

        String top = relationships.get(relationships.size() - 1).parent;
        String secondTop = relationships.get(relationships.size() - 2).parent;

        ResultActions response = mockMvc.perform(
                get(buildTermsURL(bottom) + "/" + PATHS_ENDPOINT + "/" + asCSV(top, secondTop)));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(2))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void canFetchAllPathsFrom1TermWithRelation() throws Exception {
        String bottomChild = relationships.get(0).child;
        String highestParent = relationships.get(relationships.size() - 1).parent;

        ResultActions response = mockMvc.perform(
                get(buildTermsURL(bottomChild) + "/" + PATHS_ENDPOINT + "/" + highestParent)
                        .param(RELATIONS_PARAM, validRelation));

        response.andDo(print())
                .andExpect(jsonPath("$.numberOfHits").value(1))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    public void invalidStartPathsProduces400AndErrorMessage() throws Exception {
        String highest = relationships.get(relationships.size() - 1).parent;
        ResultActions response = mockMvc.perform(
                get(buildTermsURL(invalidId()) + "/" + PATHS_ENDPOINT + "/" + highest));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void invalidEndPathsProduces400AndErrorMessage() throws Exception {
        String bottom = relationships.get(0).child;
        ResultActions response = mockMvc.perform(
                get(buildTermsURL(bottom) + "/" + PATHS_ENDPOINT + "/" + invalidId()));

        expectInvalidIdError(response, invalidId());
    }

    @Test
    public void invalidPathsRelationProduces400AndErrorMessage() throws Exception {
        String bottomChild = relationships.get(0).child;
        String highestParent = relationships.get(relationships.size() - 1).parent;

        ResultActions response = mockMvc.perform(
                get(buildTermsURL(bottomChild) + "/" + PATHS_ENDPOINT + "/" + highestParent)
                        .param(RELATIONS_PARAM, invalidRelation));

        expectInvalidRelationError(response, invalidRelation);
    }

    protected abstract String getResourceURL();

    /**
     * Create a basic document to be stored in the repository.
     * It must be a valid document, with a valid document ID.
     * This document will serve as the basis for numerous common
     * tests, relevant to all OBO controllers.
     *
     * @return a valid document with a valid ID
     */
    protected abstract List<OntologyDocument> createBasicDocs();

    protected abstract List<OntologyDocument> createNDocs(int n);

    protected abstract String idMissingInRepository();

    protected abstract String invalidId();

    protected ResultActions expectCoreFields(ResultActions result, String id) throws Exception {
        return expectCoreFields(result, id, "$.");
    }

    protected ResultActions expectCoreFields(ResultActions result, String id, String path) throws Exception {
        return expectBasicFields(result, id, path)
                .andExpect(jsonPath(path + "synonyms").exists())
                .andExpect(jsonPath(path + "ancestors").exists());
    }

    protected ResultActions expectBasicFields(ResultActions result, String id, String path) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath(path + "id").value(id))
                .andExpect(jsonPath(path + "name").exists())
                .andExpect(jsonPath(path + "isObsolete").exists())
                .andExpect(jsonPath(path + "comment").exists())
                .andExpect(jsonPath(path + "definition").exists());
    }

    protected String buildTermsURL() {
        return getResourceURL() + "/" + TERMS_ENDPOINT;
    }

    protected String buildTermsURL(String id) {
        return getResourceURL() + "/" + TERMS_ENDPOINT + "/" + id;
    }

    protected ResultActions expectCoreFieldsInResults(ResultActions result, List<String> ids) throws Exception {
        int index = 0;

        for (String id : ids) {
            expectCoreFields(result, id, "$.results[" + index++ + "].");
        }

        return result;
    }

    protected ResultActions expectCompleteFieldsInResults(ResultActions result, List<String> ids) throws Exception {
        int index = 0;

        for (String id : ids) {
            expectCompleteFields(result, id, "$.results[" + index++ + "].");
        }

        return result;
    }

    protected ResultActions expectCompleteFields(ResultActions result, String id, String path) throws Exception {
        return expectCoreFields(result, id, path)
                .andExpect(jsonPath(path + "children").exists())
                .andExpect(jsonPath(path + "secondaryIds").exists())
                .andExpect(jsonPath(path + "history").exists())
                .andExpect(jsonPath(path + "xRefs").exists())
                .andExpect(jsonPath(path + "xRelations").exists())
                .andExpect(jsonPath(path + "annotationGuidelines").exists())
                .andExpect(jsonPath(path + "taxonConstraints").exists())
                .andExpect(jsonPath(path + "consider").exists())
                .andExpect(jsonPath(path + "subsets").exists())
                .andExpect(jsonPath(path + "replacedBy").exists());
    }

    protected ResultActions expectInvalidIdError(ResultActions result, String id) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.url", is(requestUrl(result))))
                .andExpect(jsonPath("$.message", containsString("Provided ID: '" + id + "'")));
    }

    protected ResultActions expectInvalidRelationError(ResultActions result, String relation) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.url", is(requestUrl(result))))
                .andExpect(jsonPath("$.message", containsString("Unknown relationship requested: '" + relation + "'")));
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

    protected ResultActions expectBasicFieldsInResults(ResultActions result, List<String> ids) throws Exception {
        int index = 0;

        for (String id : ids) {
            expectBasicFields(result, id, "$.results[" + index++ + "].");
        }

        return result;
    }

    private static String asCSV(String... values) {
        return Arrays.asList(values).stream().collect(Collectors.joining(","));
    }

    private void setupSimpleRelationshipChain() {
        List<OntologyRelationship> simpleRelationships = new ArrayList<>();

        int idCount = 10;
        List<OntologyDocument> fakeDocuments = createNDocs(idCount);
        OntologyRelationType validRelationType = OntologyRelationType.IS_A;
        for (int i = 0; i < fakeDocuments.size() - 1; i++) {
            simpleRelationships.add(new OntologyRelationship(
                    fakeDocuments.get(i).id,                        // child
                    fakeDocuments.get(i + 1).id,                    // parent
                    validRelationType));                            // relationship
        }

        this.relationships = simpleRelationships;
        relationshipChainLength = idCount;
        validRelation = OntologyRelationType.IS_A.getLongName();
        invalidRelation = "this-does-not-exist";
        ontologyGraph.addRelationships(simpleRelationships);
    }

    private String requestUrl(ResultActions resultActions) {
        return resultActions.andReturn().getRequest().getRequestURL().toString();
    }

    private void createAndSaveDocs(int n) {
        ontologyRepository.save(createNDocs(n));
    }
}