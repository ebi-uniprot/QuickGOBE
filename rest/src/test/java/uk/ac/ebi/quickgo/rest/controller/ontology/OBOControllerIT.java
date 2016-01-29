package uk.ac.ebi.quickgo.rest.controller.ontology;

import uk.ac.ebi.quickgo.repo.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.repo.solr.io.ontology.OntologyRepository;
import uk.ac.ebi.quickgo.rest.QuickGOREST;

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
@SpringApplicationConfiguration(classes = {QuickGOREST.class})
@WebAppConfiguration
public abstract class OBOControllerIT {

    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected OntologyRepository ontologyRepository;

    protected MockMvc mockMvc;

    private String RESOURCE_URL;
    protected String validId;


    @Before
    public void setup() {
        ontologyRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();

        RESOURCE_URL = getResourceURL();

        OntologyDocument basicDoc = createBasicDoc();
        validId = basicDoc.id;
        ontologyRepository.save(basicDoc);

    }

    @Test
    public void canRetrieveCoreById() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/" + validId));

        expectCoreFields(response, validId)
                .andExpect(jsonPath("$.history").doesNotExist())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCompleteById() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/" + validId + "/complete"));

        expectCompleteFields(response, validId)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveHistoryById() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/" + validId + "/history"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.history").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXRefsById() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/" + validId + "/xrefs"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.xRefs").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveTaxonConstraintsById() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/" + validId + "/constraints"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.taxonConstraints").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveAnnotationGuideLinesById() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/" + validId + "/guidelines"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.annotationGuidelines").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXORelsById() throws Exception {
        ResultActions response = mockMvc.perform(get(RESOURCE_URL + "/" + validId + "/xontologyrelations"));

        expectBasicFields(response, validId)
                .andExpect(jsonPath("$.xRelations").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void finds400IfIdIsEmpty() throws Exception {
        mockMvc.perform(get(RESOURCE_URL + "/"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void finds404IfIdDoesNotExist() throws Exception {
        mockMvc.perform(get(RESOURCE_URL + "/" + idMissingInRepository()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void finds400OnInvalidGOId() throws Exception {
        mockMvc.perform(get(RESOURCE_URL + "/" + invalidId()))
                .andExpect(status().isBadRequest());
    }

    protected ResultActions expectCompleteFields(ResultActions result, String id) throws Exception {
        return expectCoreFields(result, id)
                .andExpect(jsonPath("$.children").exists())
                .andExpect(jsonPath("$.secondaryIds").exists())
                .andExpect(jsonPath("$.history").exists())
                .andExpect(jsonPath("$.xRefs").exists())
                .andExpect(jsonPath("$.xRelations").exists())
                .andExpect(jsonPath("$.annotationGuidelines").exists())
                .andExpect(jsonPath("$.taxonConstraints").exists())
                .andExpect(jsonPath("$.consider").exists())
                .andExpect(jsonPath("$.subsets").exists())
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
}
