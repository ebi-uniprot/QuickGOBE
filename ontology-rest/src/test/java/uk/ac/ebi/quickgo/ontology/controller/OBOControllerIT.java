package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.OntologyREST;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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

    private static final String SEARCH_ENDPOINT = "search";
    private static final String QUERY_PARAM = "query";
    protected static final String COMMA = ",";
    private static final String TERM = "term";
    private static final String TERMS = "terms";

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected OntologyRepository ontologyRepository;

    protected MockMvc mockMvc;

    private String resourceUrl;
    private String validId;
    private String validIdsCSV;
    private List<String> validIdList;

    @Before
    public void setup() {
        ontologyRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();

        resourceUrl = getResourceURL();

        List<OntologyDocument> basicDocs = createBasicDocs();
        assertThat(basicDocs.size(), is(greaterThan(1)));

        validId = basicDocs.get(0).id;
        validIdsCSV = basicDocs.stream().map(doc -> doc.id).collect(Collectors.joining(","));
        validIdList = Arrays.asList(validIdsCSV.split(COMMA));

        ontologyRepository.save(basicDocs);
    }

    @Test
    public void canRetrieveCoreById() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermURL(validId)));

        expectCoreFields(response, validId)
                .andExpect(jsonPath("$.history").doesNotExist())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCoreByIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV)));

        expectCoreFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.history").doesNotExist())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCompleteById() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermURL(validId) + "/complete"));

        expectCompleteFields(response, validId)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCompleteByIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV) + "/complete"));

        expectCompleteFieldsInResults(response, validIdList)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveHistoryById() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermURL(validId) + "/history"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.history").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveHistoryByIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV) + "/history"));

        expectBasicFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.history", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXRefsById() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermURL(validId) + "/xrefs"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.xRefs").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXRefsByIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV) + "/xrefs"));

        expectBasicFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.xRefs", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    /**
     * Shows taxon constraints and blacklist for term
     *
     * @throws Exception
     */
    @Test
    public void canRetrieveTaxonConstraintsById() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermURL(validId) + "/constraints"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.taxonConstraints").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveTaxonConstraintsByIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV) + "/constraints"));

        expectBasicFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.taxonConstraints", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveAnnotationGuideLinesById() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermURL(validId) + "/guidelines"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.annotationGuidelines").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveAnnotationGuideLinesByIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV) + "/guidelines"));

        expectBasicFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.annotationGuidelines", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXORelsById() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermURL(validId) + "/xontologyrelations"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.xRelations").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXORelsByIds() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermsURL(validIdsCSV) + "/xontologyrelations"));

        expectBasicFieldsInResults(response, validIdList)
                .andExpect(jsonPath("$.results.*.xRelations", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void finds400IfIdIsEmpty() throws Exception {
        mockMvc.perform(get(resourceUrl + "/"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void finds404IfIdDoesNotExist() throws Exception {
        mockMvc.perform(get(buildTermURL(idMissingInRepository())))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void finds400OnInvalidId() throws Exception {
        ResultActions response = mockMvc.perform(get(buildTermURL(invalidId())))
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

    protected String buildTermURL(String id) {
        return getResourceURL() + "/" + TERM + "/" + id;
    }

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

    protected String buildTermsURL(String id) {
        return getResourceURL() + "/" + TERMS + "/" + id;
    }

    protected ResultActions expectCoreFieldsInResults(ResultActions result, List<String> ids) throws Exception {
        AtomicInteger index = new AtomicInteger(0);

        for (String id : ids) {
            expectCoreFields(result, id, "$.results[" + index.getAndIncrement() + "].");
        }

        return result;
    }

    protected ResultActions expectCompleteFields(ResultActions result, String id) throws Exception {
        return expectCompleteFields(result, id, "$.");
    }

    protected ResultActions expectCompleteFieldsInResults(ResultActions result, List<String> ids) throws Exception {
        AtomicInteger index = new AtomicInteger(0);

        for (String id : ids) {
            expectCompleteFields(result, id, "$.results[" + index.getAndIncrement() + "].");
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

    protected ResultActions expectBasicFields(ResultActions result, String id) throws Exception {
        return expectBasicFields(result, id, "$.");
    }

    protected abstract String idMissingInRepository();

    protected abstract String invalidId();

    protected ResultActions expectInvalidIdError(ResultActions result, String id) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.url", is(requestUrl(result))))
                .andExpect(jsonPath("$.message", containsString("Provided id: " + id)));
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
        AtomicInteger index = new AtomicInteger(0);

        for (String id : ids) {
            expectBasicFields(result, id, "$.results[" + index.getAndIncrement() + "].");
        }

        return result;
    }

    private String requestUrl(ResultActions resultActions) {
        return resultActions.andReturn().getRequest().getRequestURL().toString();
    }
}