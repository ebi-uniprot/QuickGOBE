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
 * <p>
 *     Terminology used in test names and comments:
 *     <ul>
 *         <li>Exact match: indicates contents of query is matched entirely in a field, e.g., "contents"
 *         vs "contents"</li>
 *         <li>Word match: indicates there is a matching word in both the query and the field value, e.g., "one" vs
 *         "one two"</li>
 *         <li>Partial match: indicates that part of a word from the query/field matches the value of the
 *         field/query, e.g., "import" vs "important"</li>
 *     </ul>
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

    // ID tests -------------------------------------------------------------------------
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
    public void partialIdMatchInQueryReturnsNoEntries() throws Exception {
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

    // Exact matches win -------------------------------------------------------------------------
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
    public void queryExactMatchesSymbolInEntry2ExactMatchesNameInEntry1AndReturnsEntry2Entry1() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "synonym X");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "synonym X");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "important 1", "symbol 3", "synonym X");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important 1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesSynonymInEntry1WordMatchesSymbolInEntry2AndReturnsEntry1Entry2() throws
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
    public void queryExactMatchesNameInEntry1WordMatchesSymbolInEntry2AndReturnsEntry1Entry2() throws
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
    public void queryExactMatchesSymbolInEntry1WordMatchesSymbolInEntry2AndReturnsEntry1Entry2() throws
                                                                                                 Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "important", "synonym X");
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
    public void queryExactMatchesNameInEntry1PartialMatchesSymbolInEntry2AndReturnsEntry1Entry2() throws
                                                                                                  Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "import", "symbol 1", "synonym X");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "synonym X");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "synonym X");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesSynonymInEntry1PartialMatchesSymbolInEntry2AndReturnsEntry1Entry2() throws
                                                                                                     Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "import");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "synonym X");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "synonym X");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesSymbolInEntry1PartialMatchesSymbolInEntry2AndReturnsEntry1Entry2() throws
                                                                                                    Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "import", "important");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "synonym X");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "synonym X");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    // Word matches win -------------------------------------------------------------------------
    @Test
    public void queryWordMatchesSymbolInEntry2WordMatchesSynonymInEntry1AndReturnsEntry2Entry1() throws
                                                                                                 Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "important 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "synonym X");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "synonym X");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryWordMatchesSymbolInEntry2WordMatchesNameInEntry1AndReturnsEntry2Entry1() throws
                                                                                              Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "synonym X");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "synonym X");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "important 1", "symbol 3", "synonym X");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    // Partial matches win -------------------------------------------------------------------------
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
    public void queryPartiallyMatchesSymbolInEntry2PartiallyMatchesNameInEntry1AndReturnsEntry2Entry1() throws
                                                                                                        Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "synonym X");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "synonym X");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "important 1", "symbol 3", "synonym X");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    // Term frequency -------------------------------------------------------------------------
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

    // Basic field matching -------------------------------------------------------------------------
    @Test
    public void nameWordMatchFindsResult() throws Exception {
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
    public void symbolWordMatchFindsResult() throws Exception {
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
    public void synonymWordMatchFindsResult() throws Exception {
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
    public void nameMatchGivesResultsInOrderEntriesWereAdded() throws Exception {
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
    public void synonymMatchGivesResultsInOrderEntriesWereAdded() throws Exception {
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

    // Phrase matches -------------------------------------------------------------------------
    @Test
    public void phraseMatchOnNameReturnsShortestMatchFirst() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "a metabolic process is handy", "symbol 1", "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "metabolic process is handy", "symbol 2", "synonym 2");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "ab metabolic process is handy", "symbol 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "metabolic process"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[2].identifier").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    @Test
    public void phraseMatchOnSynonymReturnsShortestMatchFirst() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "metal 1", "symbol", "a synonym abcdef attention hup sir");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "metal 2", "symbol", "a synonym abcde attention hup");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "metal 3", "symbol", "a synonym abcd attention");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "synonym abcd"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    // Length matches -------------------------------------------------------------------------
    @Test
    public void wordInShortestPhraseMatchesFirst() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "metal 1", "symbol", "a synonym is really weird silly");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "metal 2", "symbol", "a synonym is really weird");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "metal 3", "symbol", "a synonym is really");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "synonym"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    @Test
    public void partialInSameWordReturnsShortestMatchesFirst() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "metal 1", "symbol", "a synonym one two three");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "metal 2", "symbol", "a synonym one two");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "metal 3", "symbol", "a synonym one");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "syno"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    @Test
    public void partialInDifferentWordsReturnsShortestMatchesFirst() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "metal 1", "symbol", "a synon is like, err, awesome");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "metal 1", "symbol", "a synony is like, err, awesome");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "metal 1", "symbol", "a synonym is like, err, awesome");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "syno"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].identifier").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    // Helpers -------------------------------------------------------------------------
    private static GeneProductDocument createDoc(String id, String name, String symbol, String... synonyms) {
        GeneProductDocument document = new GeneProductDocument();

        document.id = id;
        document.name = name;
        document.synonyms = Arrays.asList(synonyms);
        document.symbol = symbol;
        document.type = "protein";

        return document;
    }

    @Test
    public void suiteOfTripletWordTests() {
        StringBuilder report = new StringBuilder("\n\n========== Start of test report ==========").append("\n");
        for (int i = 0; i < 26; i++) {
            String doc1Name = alphabetFieldValue(i + 1);
            String doc2Name = alphabetFieldValue(i);
            GeneProductDocument doc1 = createDoc(VALID_ID_1, doc1Name, "symbol", "X");
            GeneProductDocument doc2 = createDoc(VALID_ID_2, doc2Name, "symbol", "X");

            repository.save(doc1);
            repository.save(doc2);

            report.append(checkTripletsInDocuments(doc1Name, doc2Name));

            repository.deleteAll();
        }

        report
                .append("\n")
                .append("-- The search query tested was (aaa)").append("\n")
                .append("-- Successful tests returned results in the order, [doc2,doc1], as indicated by, \"PASS\"").append("\n")
                .append("-- Unsuccessful tests returned results in the order, [doc1,doc2], as indicated by, \"FAIL\"").append("\n")
                .append("========== end of test report ==========").append("\n");
        System.out.println(report);

        if (report.indexOf("FAIL") > 0) {
            throw new AssertionError("There were test failures");
        } else {
            System.out.println("Tests passed -- nice one!");
        }
    }

    private StringBuilder checkTripletsInDocuments(String doc1Name, String doc2Name) {
        StringBuilder result = new StringBuilder();
        result
                .append("--------------------------").append("\n")
                .append("Index contents:").append("\n")
                .append("    doc1.name=").append(doc1Name).append("\n")
                .append("    doc2.name=").append(doc2Name).append("\n")
                .append("Test result: \n");
        try{
            mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "aaa"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.results[0].identifier").value(VALID_ID_2))
                    .andExpect(jsonPath("$.results[1].identifier").value(VALID_ID_1))
                    .andExpect(jsonPath("$.results.*", hasSize(2)));
            result.append("    PASS:").append("\n");
        } catch (AssertionError | Exception e) {
            result.append("    FAIL:").append("\n");
        }
        return result;
    }

    private String alphabetFieldValue(int letterNumber) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder doc = new StringBuilder();
        for (int i = 0; i < alphabet.length() && i <= letterNumber; i++) {
            String letter = alphabet.substring(i, i + 1);
            doc.append(letter).append(letter).append(letter).append(" ");
        }
        return doc.toString().trim();
    }

}