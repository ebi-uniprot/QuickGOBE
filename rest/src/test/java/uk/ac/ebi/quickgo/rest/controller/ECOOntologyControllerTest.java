package uk.ac.ebi.quickgo.rest.controller;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.document.ontology.OntologyType;
import uk.ac.ebi.quickgo.repo.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.repo.ontology.OntologyRepository;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the {@link ECOOntologyController} class using an embedded server.
 *
 * Created 24/11/15
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {QuickGOREST.class})
@WebAppConfiguration
public class ECOOntologyControllerTest {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    private static final String RESOURCE_URL = "/QuickGO/services/eco";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OntologyRepository ontologyRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        ontologyRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void canRetrieveById() throws Exception {
        saveTerm("ECO:0000001");

        mockMvc.perform(get(RESOURCE_URL + "/ECO:0000001"))
                .andDo(print())
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        jsonPath("$.id").value("ECO:0000001")
                )
                .andExpect(status().isOk());
    }

    @Test
    public void finds404IfIdDoesNotExist() throws Exception {
        saveTerm("ECO:0000001");

        mockMvc.perform(get(RESOURCE_URL + "/ECO:0000002"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void finds400IfIdIsEmpty() throws Exception {
        mockMvc.perform(get(RESOURCE_URL + "/"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void finds400OnInvalidGOId() throws Exception {
        saveTerm("ECO:0000001");

        mockMvc.perform(get(RESOURCE_URL + "/ECO;0000002"))
                .andExpect(status().isBadRequest());
    }

    private void saveTerm(String ecoId) {
        OntologyDocument ecoTerm = new OntologyDocument();
        ecoTerm.id = ecoId;
        ecoTerm.ontologyType = OntologyType.ECO.name();
        ontologyRepository.save(ecoTerm);
    }
}