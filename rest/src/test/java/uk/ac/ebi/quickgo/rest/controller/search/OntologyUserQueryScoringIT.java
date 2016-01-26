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
 * Runs several functional tests to check if the user query, expressed in the client request, obtains a correct
 * response from the server.
 *
 * The tests check if the query should return something, and if so in which order.
 *
 * The search order requirements are expressed in: https://www.ebi.ac.uk/panda/jira/browse/GOA-1708
 *
 * <b>Note: This class should be used solely for functional tests on the user query, and no other section of the user
 * request.</b>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {QuickGOREST.class})
@WebAppConfiguration
public class OntologyUserQueryScoringIT {
    private static final String RESOURCE_URL = "/QuickGO/internal/search/ontology";
    private static final String QUERY_PARAM = "query";
    private static final String FILTER_QUERY_PARAM = "filterQuery";

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

    @Test
    public void termFrequencyDoesNotInfluenceScoring() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1");
        OntologyDocument doc2 = createDoc("GO:0000002", "go1", "go1 and go1 is not go2", "go1 synonym", "go1 or go1");

        repository.save(doc1);
        repository.save(doc2);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(2)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000001"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000002"));
    }

    @Test
    public void whenQueryMatchesDocumentsEquallyResultsAreOrderedByShortestToLongest() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 has a particularly long function");
        OntologyDocument doc2 = createDoc("GO:0000002", "go1 has a long function");
        OntologyDocument doc3 = createDoc("GO:0000003", "go1 a function");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go1 function"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(3)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000003"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000002"))
                .andExpect(jsonPath("$.results[2].id").value("GO:0000001"));
    }

    @Test
    public void whenQueryMatches2FieldsInFirstDocAnd1FieldInSecondDocResultsAreOrderedByNumOfMatches() throws
                                                                                                       Exception {
        OntologyDocument doc2 = createDoc("GO:0000001", "name one", "some synonym");
        OntologyDocument doc1 = createDoc("GO:0000002", "name one", "synonym one");
        OntologyDocument doc3 = createDoc("GO:0000003", "go1 a function");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "name one"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(2)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000002"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000001"));
    }

    // filter queries ------------------------------------------------
    @Test
    public void requestWith1ValidFilterQueryReturnsFilteredResponse() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go function 1");
        doc1.aspect = "Process";
        OntologyDocument doc2 = createDoc("GO:0000002", "go function 2");
        doc2.aspect = "Function";
        OntologyDocument doc3 = createDoc("GO:0000003", "go function 3");
        doc3.aspect = "Process";

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL)
                .param(QUERY_PARAM, "go function")
                .param(FILTER_QUERY_PARAM, OntologyFieldSpec.Search.aspect.name() + ":Process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(2)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000001"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000003"));
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