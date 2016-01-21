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
        repository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void nonMatchingIdInQueryReturnsNoEntries() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1");
        OntologyDocument doc2 = createDoc("GO:0000002", "go2");
        OntologyDocument doc3 = createDoc("GO:0000003", "go3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "GO:0000004"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(0)));
    }

    @Test
    public void idInQueryMatchesExactlyOneEntryReturnsThatEntry() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1");
        OntologyDocument doc2 = createDoc("GO:0000002", "go2");
        OntologyDocument doc3 = createDoc("GO:0000003", "go3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "GO:0000002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000002"));
    }

    @Test
    public void partiallyMatchingIdInQueryReturnsNoEntries() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1");
        OntologyDocument doc2 = createDoc("GO:0000002", "go2");
        OntologyDocument doc3 = createDoc("GO:0000003", "go3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "GO:000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(0)));
    }

    @Test
    public void nonMatchingNameInQueryReturnsNoEntries() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1");
        OntologyDocument doc2 = createDoc("GO:0000002", "go2");
        OntologyDocument doc3 = createDoc("GO:0000003", "go3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(0)));
    }

    @Test
    public void substringQueryPartiallyMatchesNameInEntry2ReturnsEntry2() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "substring1");
        OntologyDocument doc2 = createDoc("GO:0000002", "substitute2");
        OntologyDocument doc3 = createDoc("GO:0000003", "subsistence3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "substi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000002"));
    }

    @Test
    public void substringQueryPartiallyMatchesSynonymInEntry2ReturnsEntry2() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1", "substring1");
        OntologyDocument doc2 = createDoc("GO:0000002", "go2", "substitute2");
        OntologyDocument doc3 = createDoc("GO:0000003", "go3", "subsistence3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "substi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000002"));
    }

    @Test
    public void queryPartiallyMatchesNameInEntry1AndPartiallyMatchesSynonymInEntry2ReturnsEntry1FirstAndEntry2Second()
            throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 with something");
        OntologyDocument doc2 = createDoc("GO:0000002", "go2", "synonym of go1");
        OntologyDocument doc3 = createDoc("GO:0000003", "go3", "another synonym");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(2)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000001"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000002"));
    }

    @Test
    public void queryPartiallyMatchesNameInEntry1AndExactMatchesNameInEntry2ReturnsEntry2FirstAndEntry1Second()
            throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 with something");
        OntologyDocument doc2 = createDoc("GO:0000002", "go1");
        OntologyDocument doc3 = createDoc("GO:0000003", "go3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(2)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000002"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000001"));
    }

    @Test
    public void queryPartiallyMatchesNameInEntry1AndExactMatchesSynonymInEntry2ReturnsEntry2FirstAndEntry1Second()
            throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 with something");
        OntologyDocument doc2 = createDoc("GO:0000002", "go2", "go1");
        OntologyDocument doc3 = createDoc("GO:0000003", "go3", "another synonym");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(2)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000002"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000001"));
    }

    @Test
    public void namesInEntriesDoNotCompletelyMatchMultiWordQueryReturnsNoEntries() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 with something");
        OntologyDocument doc2 = createDoc("GO:0000002", "go1 do something else");
        OntologyDocument doc3 = createDoc("GO:0000003", "go1 up then down");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go1 and"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(0)));
    }

    @Test
    public void namesInEntryMatchAllWordsInMultiWordQueryReturnsMatchingEntry() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 up and down");

        repository.save(doc1);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go1 and"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000001"));
    }

    @Test
    public void multiWordQueryMatchesPartiallyNameInEntry1AndExactMatchesNameInEntry2ReturnsEntry2ThenEntry1()
            throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 up and down");
        OntologyDocument doc2 = createDoc("GO:0000002", "go1 and ");

        repository.save(doc1);
        repository.save(doc2);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go1 and"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(2)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000002"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000001"));
    }

    @Test
    public void multiWordQueryMatchesPartiallyNameInEntry1AndExactMatchesSynonymInEntry2ReturnsEntry2ThenEntry1()
            throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 up and down");
        OntologyDocument doc2 = createDoc("GO:0000002", "go2", "go1 and");

        repository.save(doc1);
        repository.save(doc2);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go1 and"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(2)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000002"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000001"));
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