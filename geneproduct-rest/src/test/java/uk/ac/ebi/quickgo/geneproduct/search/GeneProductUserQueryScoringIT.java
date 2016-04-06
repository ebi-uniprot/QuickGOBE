package uk.ac.ebi.quickgo.geneproduct.search;

import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.geneproduct.GeneProductREST;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 *     Functional tests to check if a user query, expressed in the client request, obtains a correct
 *     response from the server.
 * </p>
 * <p>
 *     The tests check if the query should return something, and if so in which order.
 * </p>
 * <p>
 *     Search order requirements are specified in: https://www.ebi.ac.uk/panda/jira/browse/GOA-1840
 * </p>
 *
 * <b>Note: This class should be used solely for functional tests on the user query, and no other section of the user
 * request.</b>
 *
 * Created 04/04/16
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {GeneProductREST.class})
@WebAppConfiguration
public class GeneProductUserQueryScoringIT {
    private static final String RESOURCE_URL = "/QuickGO/services/geneproduct/search";
    private static final String QUERY_PARAM = "query";

    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    private static final String VALID_ID_1 = "A0A0F8CSS1";
    private static final String VALID_ID_2 = "A0A0F8CSS2";
    private static final String VALID_ID_3 = "A0A0F8CSS3";
    private static final String MISSING_ID = "A0A0F8CSS4";

    @Autowired
    protected WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private GeneProductRepository repository;

    @Before
    public void setup() {
        repository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void nonMatchingIdInQueryReturnsNoEntries() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "sym 1", "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "sym 2", "synonym 2");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "sym 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, MISSING_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(0)));
    }

    @Test
    public void matchingIdInQueryReturnsCorrectEntry() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "sym 1", "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "sym 2", "synonym 2");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "sym 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, VALID_ID_2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_2));
    }

    @Test
    public void partiallyMatchingIdInQueryReturnsNoEntries() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "sym 1", "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "sym 2", "synonym 2");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "sym 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, VALID_ID_2.substring(0, VALID_ID_2.length() - 2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(0)));
    }

    @Test
    public void queryExactMatchesSymbolInEntry2ExactMatchesSynonymInEntry1AndReturnsEntry2Entry1() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "important 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "synonym X");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "synonym X");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important 1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryPartiallyMatchesSymbolInEntry2PartiallyMatchesSynonymInEntry1AndReturnsEntry2Entry1() throws
                                                                                                       Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "important 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "synonym X");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "synonym X");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesSynonymInEntry1PartiallyMatchesSymbolInEntry2AndReturnsEntry1Entry2() throws
                                                                                                           Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "important");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "synonym X");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "synonym X");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesNameInEntry1PartiallyMatchesSymbolInEntry2AndReturnsEntry1Entry2() throws
                                                                                                       Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "important", "symbol 1", "synonym X");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "synonym X");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "synonym X");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void termFrequencyDoesNotInfluenceScoring() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "protein 1", "symbol 1", "protein 5");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "protein 2 protein 3", "symbol 2", "protein 4 and protein 6");

        repository.save(doc1);
        repository.save(doc2);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "protein"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(2)))
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_2));
    }

    @Test
    public void tokenisedNameFieldHasMatch() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine snazzy process", "symbol 2", "synonym 2");

        repository.save(doc1);
        repository.save(doc2);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "metabolic"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(1)));
    }

    @Test
    public void tokenisedSymbolFieldHasMatch() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "cymbal 2", "synonym 2");

        repository.save(doc1);
        repository.save(doc2);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "symbol"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(1)));
    }

    @Test
    public void tokenisedSynonymFieldHasMatch() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "syn 2");

        repository.save(doc1);
        repository.save(doc2);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "synonym"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(1)));
    }

    @Test
    public void nameFieldMatchGivesResultsInOrderEntriesWereAdded() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "synonym 2");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "process"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].identifier").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    @Test
    public void synonymFieldMatchGivesResultsInOrderEntriesWereAdded() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "synonym 2");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "synonym"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].identifier").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }



    private static GeneProductDocument createDoc(String id, String name, String symbol, String... synonyms) {
        GeneProductDocument document = new GeneProductDocument();

        document.id = id;
        document.name = name;
        document.synonyms = Arrays.asList(synonyms);
        document.symbol = symbol;
        document.type = "protein";

        return document;
    }
}