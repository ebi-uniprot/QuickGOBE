package uk.ac.ebi.quickgo.rest.controller.search;

import uk.ac.ebi.quickgo.repo.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyType;
import uk.ac.ebi.quickgo.repo.solr.io.ontology.OntologyRepository;
import uk.ac.ebi.quickgo.rest.QuickGOREST;

import java.util.Arrays;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Does several functional tests to verify if the user queries sent to the server return the expected results.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {QuickGOREST.class})
@WebAppConfiguration
public class OntologySearchResultsIT {
    private static final String RESOURCE_URL = "/QuickGO/internal/search/ontology";
    private static final String QUERY_PARAM = "query";

    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    private OntologyRepository repository;

    protected MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    //goIdMatches exactly
    //partial id match returns some results

    @Test
    public void idThatMatchesNoStoredEntriesReturnsEmptyResult() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "mitochondrion inheritance");
        OntologyDocument doc2 = createDoc("GO:0000002", "mitochondrial genome maintenance");
        OntologyDocument doc3 = createDoc("GO:0000003", "reproduction");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "GO:0000004"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(0)));
    }



//    @Test
//    public void partialMatchInNAmeTakesPrecedenceOverPartialMatchInName() throws Exception {
//        OntologyDocument doc1 = createDoc("GO:1900119", "positive regulation of execution phase of apoptosis",
//                "upregulation of execution phase of apoptosis");
//        OntologyDocument doc2 = createDoc("GO:0097194", "execution phase of apoptosis",
//                "execution phase of apoptotic process");
//        OntologyDocument doc3 = createDoc("GO:0006915", "apoptotic process", "apoptosis");
//
//        repository.save(doc1);
//        repository.save(doc2);
//        repository.save(doc3);
//
//        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "apoptosis"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.results[0].id").value("GO:0006915"));
//    }

    @Test
    public void exactMatchInSynonymTakesPrecedenceOverPartialMatchInName() throws Exception {
        OntologyDocument doc1 = createDoc("GO:1900119", "positive regulation of execution phase of apoptosis",
                "upregulation of execution phase of apoptosis");
        OntologyDocument doc2 = createDoc("GO:0097194", "execution phase of apoptosis",
                "execution phase of apoptotic process");
        OntologyDocument doc3 = createDoc("GO:0006915", "apoptotic process", "apoptosis");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "apoptosis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value("GO:0006915"));
    }

    private static OntologyDocument createDoc(String id, String name, String... synonyms) {
        OntologyDocument document = new OntologyDocument();
        document.id = id;
        document.name = name;
        document.synonymNames = Arrays.asList(synonyms);
        document.ontologyType = OntologyType.GO.name();

        return document;
    }
}