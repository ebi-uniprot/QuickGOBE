package uk.ac.ebi.quickgo.rest.controller;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocMocker;
import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
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
 * Tests the {@link GOController} class using an embedded server.
 *
 * Created 16/11/15
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {QuickGOREST.class})
@WebAppConfiguration
public class GOControllerIT {
    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    private static final String RESOURCE_URL = "/QuickGO/services/go";

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
    public void canRetrieveCoreById() throws Exception {
        saveTerm("GO:0000001");

        mockMvc.perform(get(RESOURCE_URL + "/GO:0000001"))
                .andDo(print())
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        jsonPath("$.id").value("GO:0000001")
                )
                .andExpect(
                        jsonPath("$.history").doesNotExist()
                )
                .andExpect(
                        jsonPath("$.aspect").value("Biological Process")
                )
                .andExpect(
                        jsonPath("$.usage").value("Unrestricted")
                )
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveCompleteById() throws Exception {
        saveTerm("GO:0000001");

        mockMvc.perform(get(RESOURCE_URL + "/GO:0000001/complete"))
                .andDo(print())
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        jsonPath("$.id").value("GO:0000001")
                )
                .andExpect(
                        jsonPath("$.history").isArray()
                )
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveHistoryById() throws Exception {
        saveTerm("GO:0000001");

        mockMvc.perform(get(RESOURCE_URL + "/GO:0000001/history"))
                .andDo(print())
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        jsonPath("$.id").value("GO:0000001")
                )
                .andExpect(
                        jsonPath("$.history").isArray()
                )
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXRefsById() throws Exception {
        saveTerm("GO:0000001");

        mockMvc.perform(get(RESOURCE_URL + "/GO:0000001/xrefs"))
                .andDo(print())
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        jsonPath("$.id").value("GO:0000001")
                )
                .andExpect(
                        jsonPath("$.xrefs").isArray()
                )
                .andExpect(status().isOk());
    }

    /**
     * Shows taxon constraints and blacklist for term
     * @throws Exception
     */
    @Test
    public void canRetrieveTaxonConstraintsById() throws Exception {
        saveTerm("GO:0000001");

        mockMvc.perform(get(RESOURCE_URL + "/GO:0000001/constraints"))
                .andDo(print())
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        jsonPath("$.id").value("GO:0000001")
                )
                .andExpect(
                        jsonPath("$.taxonConstraints").isArray()
                )
                .andExpect(
                        jsonPath("$.blacklist").isArray()
                )
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveAnnotationGuideLinesById() throws Exception {
        saveTerm("GO:0000001");

        mockMvc.perform(get(RESOURCE_URL + "/GO:0000001/guidelines"))
                .andDo(print())
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        jsonPath("$.id").value("GO:0000001")
                )
                .andExpect(
                        jsonPath("$.annotationGuidelines").isArray()
                )
                .andExpect(status().isOk());
    }

    @Test
    public void canRetrieveXORelsById() throws Exception {
        saveTerm("GO:0000001");

        mockMvc.perform(get(RESOURCE_URL + "/GO:0000001/xontologyrelations"))
                .andDo(print())
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        jsonPath("$.id").value("GO:0000001")
                )
                .andExpect(
                        jsonPath("$.xRelations").isArray()
                )
                .andExpect(status().isOk());
    }

    @Test
    public void finds404IfIdDoesNotExist() throws Exception {
        saveTerm("GO:0000001");

        mockMvc.perform(get(RESOURCE_URL + "/GO:0000002"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void finds400IfIdIsEmpty() throws Exception {
        mockMvc.perform(get(RESOURCE_URL + "/"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void finds400OnInvalidGOId() throws Exception {
        saveTerm("GO:0000001");

        mockMvc.perform(get(RESOURCE_URL + "/GO;0000002"))
                .andExpect(status().isBadRequest());
    }

    private void saveTerm(String goId) {
        OntologyDocument goTerm = OntologyDocMocker.createGODoc(goId, "go name");
        ontologyRepository.save(goTerm);
    }
}