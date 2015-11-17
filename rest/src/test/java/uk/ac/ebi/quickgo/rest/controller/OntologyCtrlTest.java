package uk.ac.ebi.quickgo.rest.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.document.ontology.OntologyDocumentMocker.Term.createGOTerm;

/**
 * Tests the {@link OntologyCtrl} class using an embedded server.
 *
 * Created 16/11/15
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = QuickGOREST.class)
@WebAppConfiguration
public class OntologyCtrlTest {

    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

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
    public void getWillFindExistingGoId() throws Exception {
        OntologyDocument goTerm = createGOTerm();
        goTerm.id = "0000001";
        goTerm.name = "apoptosis";
        ontologyRepository.save(goTerm);

        mockMvc.perform(get("/go/term/0000001"))
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        jsonPath("$.id").value("0000001")
                )
                .andExpect(
                        jsonPath("$.name").value("apoptosis")
                );
    }

    @Test
    public void getWillNotFindMissingGoId() throws Exception {
        OntologyDocument goTerm = createGOTerm();
        goTerm.id = "0000001";
        goTerm.name = "apoptosis";
        ontologyRepository.save(goTerm);

        mockMvc.perform(get("/go/term/0000002")).andExpect(status().is4xxClientError());
    }

}