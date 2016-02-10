package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.OntologyREST;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

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
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected OntologyRepository ontologyRepository;

    protected MockMvc mockMvc;

    private String resourceUrl;
    private String validId;

    @Before
    public void setup() {
        ontologyRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();

        resourceUrl = getResourceURL();

        OntologyDocument basicDoc = createBasicDoc();
        validId = basicDoc.id;
        ontologyRepository.save(basicDoc);
    }

    @Test
    public void canRetrieveCoreById() throws Exception {
        ResultActions response = mockMvc.perform(get(resourceUrl + "/" + validId));

        expectCoreFields(response, validId)
                .andExpect(jsonPath("$.history").doesNotExist())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCompleteById() throws Exception {
        ResultActions response = mockMvc.perform(get(resourceUrl + "/" + validId +
                "/complete"));

        expectCompleteFields(response, validId)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveHistoryById() throws Exception {
        ResultActions response = mockMvc.perform(get(resourceUrl + "/" + validId + "/history"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.history").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXRefsById() throws Exception {
        ResultActions response = mockMvc.perform(get(resourceUrl + "/" + validId + "/xrefs"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.xRefs").isArray())
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
        ResultActions response = mockMvc.perform(get(resourceUrl + "/" + validId + "/constraints"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.taxonConstraints").isArray())
                .andExpect(jsonPath("$.blacklist").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveAnnotationGuideLinesById() throws Exception {
        ResultActions response = mockMvc.perform(get(resourceUrl + "/" + validId + "/guidelines"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.annotationGuidelines").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXORelsById() throws Exception {
        ResultActions response = mockMvc.perform(get(resourceUrl + "/" + validId + "/xontologyrelations"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.xRelations").isArray())
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
        mockMvc.perform(get(resourceUrl + "/" + idMissingInRepository()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void finds400OnInvalidGOId() throws Exception {
        ResultActions response = mockMvc.perform(get(resourceUrl + "/" + invalidId()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        expectInvalidIdError(response, "GO");
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

    protected ResultActions expectResultsInfoExists(ResultActions result) throws Exception {
        return result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo").exists())
                .andExpect(jsonPath("$.pageInfo.resultsPerPage").exists())
                .andExpect(jsonPath("$.pageInfo.total").exists())
                .andExpect(jsonPath("$.pageInfo.current").exists());
    }

    protected ResultActions expectCompleteFields(ResultActions result, String id) throws Exception {
        return expectCoreFields(result, id)
                .andExpect(jsonPath("$.children").exists())
                .andExpect(jsonPath("$.secondaryIds").exists())
                .andExpect(jsonPath("$.subsets").exists())
                .andExpect(jsonPath("$.history").exists())
                .andExpect(jsonPath("$.xRefs").exists())
                .andExpect(jsonPath("$.xRelations").exists())
                .andExpect(jsonPath("$.annotationGuidelines").exists())
                .andExpect(jsonPath("$.taxonConstraints").exists())
                .andExpect(jsonPath("$.blacklist").exists())
                .andExpect(jsonPath("$.consider").exists())
                .andExpect(jsonPath("$.replacedBy").exists());
    }

    protected ResultActions expectCoreFields(ResultActions result, String id) throws Exception {
        return expectBasicFields(result, id)
                .andExpect(jsonPath("$.synonyms").exists())
                .andExpect(jsonPath("$.ancestors").exists());
    }

    protected ResultActions expectBasicFields(ResultActions result, String id) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.isObsolete").exists())
                .andExpect(jsonPath("$.comment").exists())
                .andExpect(jsonPath("$.definition").exists());
    }

    protected ResultActions expectInvalidIdError(ResultActions result, String id) throws Exception {
        return result
                .andDo(print())
                .andExpect(jsonPath("$.url", is(requestUrl(result))))
                .andExpect(jsonPath("$.message", containsString("Provided id: " + id)));
    }

    /**
     * Create a basic document to be stored in the repository.
     * It must be a valid document, with a valid document ID.
     * This document will serve as the basis for numerous common
     * tests, relevant to all OBO controllers.
     *
     * @return a valid document with a valid ID
     */
    protected abstract OntologyDocument createBasicDoc();

    protected abstract String idMissingInRepository();

    protected abstract String invalidId();

    protected abstract String getResourceURL();

    private String requestUrl(ResultActions resultActions) {
        return resultActions.andReturn().getRequest().getRequestURL().toString();
    }
}