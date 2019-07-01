package uk.ac.ebi.quickgo.geneproduct.search;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.geneproduct.GeneProductREST;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.geneproduct.common.common.GeneProductDocMocker.createDocWithId;

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
 *         <li>Exact match: indicates contents of query is matched entirely in a field, e.g., (query's value) "abcde"
 *         vs (field's value) "abcde"</li>
 *         <li>Word match: indicates there is a matching word in both the query and the field value, e.g., "one" vs
 *         "one two"</li>
 *         <li>Partial match: indicates that part of a word from the query/field matches the value of the
 *         field/query, e.g., "import" vs "important"</li>
 *     </ul>
 * </p>
 * <p>
 *     NOTE: the relevancy of search results has been implemented such that a word/exact match on name/synonym
 *     field scores higher than a partial match on the highly boosted symbol field. Refer to the geneproduct
 *     solrconfig.xml's "/search" requestHandler for in-depth details on field boosting.
 * </p>
 *
 * <b>Note: This class should be used solely for functional tests on the user query, and no other section of the user
 * request.</b>
 *
 * Created 04/04/16
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {GeneProductREST.class})
@WebAppConfiguration
public class GeneProductUserQueryScoringIT {
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    /*
     * TODO: fix behaviour documented in https://www.ebi.ac.uk/panda/jira/browse/GOA-2041
     */
    private static final boolean FIXED_GOA_2041 = false;

    private static final String RESOURCE_URL = "/geneproduct/search";
    private static final String QUERY_PARAM = "query";

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
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "sym 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "sym 2", "tax 2", "synonym 2");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "sym 3", "tax 3", "synonym 3");

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
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "sym 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "sym 2", "tax 2", "synonym 2");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "sym 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, VALID_ID_2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2));
    }

    @Test
    public void partialIdMatchInQueryReturnsNoEntries() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "sym 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "sym 2", "tax 2", "synonym 2");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "sym 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, VALID_ID_2.substring(0, VALID_ID_2.length() - 2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(0)));
    }

    @Test
    public void idWithUpperCaseMatchInQueryReturns1Entry() throws Exception {
        populateIndexWithIdenticalFieldsExceptID();

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, VALID_ID_2.toUpperCase()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(1)));
    }

    @Test
    public void idWithLowerCaseMatchInQueryReturns1Entry() throws Exception {
        populateIndexWithIdenticalFieldsExceptID();

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, VALID_ID_2.toLowerCase()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(1)));
    }

    // Exact match against exact match precedence -------------------------------------------------------------------
    @Test
    public void queryExactMatchesSymbolInEntry2ExactMatchesTaxonNameInEntry1AndReturnsEntries21() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "important 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important 1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesSymbolInEntry2ExactMatchesSynonymInEntry1AndReturnsEntries21() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "important 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important 1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void
    queryExactMatchesSymbolInEntry2ExactMatchesNameInEntry3PartialMatchesTaxonInEntry1AndReturnsEntries231() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "important 1", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc3);
        repository.save(doc1);
        repository.save(doc2);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important 1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[2].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    @Test
    public void queryExactMatchesTaxonNameInEntry3ExactMatchesNameInEntry1AndReturnsEntries31() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "important 1", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "important 1", "synonym 3");

        repository.save(doc3);
        repository.save(doc2);
        repository.save(doc1);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important 1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void
    queryExactMatchesTaxonNameInEntry3ExactMatchesSynonymInEntry2PartialMatchesTaxonInEntry1AndReturnsEntries321()
            throws
                                                                                                       Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "tax 2", "important 1");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "important 1", "synonym 3");

        repository.save(doc3);
        repository.save(doc2);
        repository.save(doc1);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important 1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    // Exact match against word match always wins -------------------------------------------------------------------
    @Test
    public void queryExactMatchesSymbolInEntry1WordMatchesSymbolInEntry2AndReturnsEntries12() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "important", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc2);
        repository.save(doc1);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesTaxonNameInEntry1WordMatchesTaxonNameInEntry2AndReturnsEntries12() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "important", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "important 1", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc2);
        repository.save(doc1);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesNameInEntry2WordMatchesNameInEntry1AndReturnsEntries21() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "important 1", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "important", "symbol 2", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc2);
        repository.save(doc1);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesSynonymInEntry2WordMatchesSynonymInEntry1AndReturnsEntries21() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "important 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "tax 2", "important");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc2);
        repository.save(doc1);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesNameInEntry1WordMatchesTaxonNameInEntry2AndReturnsEntries12() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "important", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc3);
        repository.save(doc2);
        repository.save(doc1);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesSynonymInEntry1WordMatchesSymbolInEntry2AndReturnsEntries12() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "important");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc3);
        repository.save(doc2);
        repository.save(doc1);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesNameInEntry1WordMatchesSymbolInEntry2AndReturnsEntries12() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "important", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc2);
        repository.save(doc1);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    // Exact match against partial always wins -------------------------------------------------------------------
    @Test
    public void queryExactMatchesSymbolInEntry2PartialMatchesSymbolInEntry1AndReturnsEntries21() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "important", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "import", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc2);
        repository.save(doc1);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesTaxonNameInEntry2PartialMatchesTaxonNameInEntry1AndReturnsEntries21() throws
                                                                                                       Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "important", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "import", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc2);
        repository.save(doc1);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesNameInEntry1PartialMatchesTaxonInEntry3AndReturnsEntries13() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "import", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "important", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc2);
        repository.save(doc1);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesSynonymInEntry1PartialMatchesSynonymInEntry3AndReturnsEntries13() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "import");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "important");

        repository.save(doc2);
        repository.save(doc1);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesNameInEntry1PartialMatchesSymbolInEntry2AndReturnsEntries12() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "import", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc2);
        repository.save(doc1);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesSynonymInEntry1PartialMatchesSymbolInEntry2AndReturnsEntries12() throws
                                                                                                  Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "import");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc2);
        repository.save(doc1);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryExactMatchesSynonymInEntry1PartialMatchesTaxonNameInEntry2AndReturnsEntries12() throws
                                                                                                     Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "import");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "important", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc2);
        repository.save(doc1);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    // Word match against word match precedence
    // -------------------------------------------------------------------------
    @Test
    public void queryWordMatchesSymbolInEntry2WordMatchesTaxonNameInEntry1AndReturnsEntries21() throws
                                                                                                Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "important 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryWordMatchesSymbolInEntry3WordMatchesSynonymInEntry1AndReturnsEntries31() throws
                                                                                              Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "important 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "important 1", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryWordMatchesSymbolInEntry3WordMatchesNameInEntry1AndReturnsEntries31() throws
                                                                                           Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "important 1", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "important 1", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryWordMatchesTaxonNameInEntry2WordMatchesSynonymInEntry1AndReturnsEntries21() throws
                                                                                                 Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "important 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 1", "important 1", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryWordMatchesTaxonNameInEntry2WordMatchesNameInEntry1AndReturnsEntries21() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "important 1", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 1", "important 1", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    // Word matches win against partial matches ------------------------------------------------------------------------
    @Test
    public void queryWordMatchesNameInEntry3PartialMatchesSymbolInEntry1AndReturnsEntries31() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "important", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "import 1", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryWordMatchesSynonymInEntry3PartialMatchesSymbolInEntry1AndReturnsEntries31() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "important", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "import 1");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryWordMatchesSynonymInEntry3PartialMatchesTaxonNameInEntry2AndReturnsEntries32() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 1", "important", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "import 1");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryWordMatchesNameInEntry3PartialMatchesTaxonNameInEntry2AndReturnsEntries32() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 1", "important", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "import 1", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryWordMatchesTaxonNameInEntry2PartialMatchesSymbolInEntry1AndReturnsEntries21() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "important", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 1", "import 1", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    // Partial match precedence ------------------------------------------------------------------------
    @Test
    public void queryPartiallyMatchesSymbolInEntry2PartiallyMatchesTaxonNameInEntry1AndReturnsEntries21()
            throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "important 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryPartiallyMatchesSymbolInEntry2PartiallyMatchesSynonymInEntry1AndReturnsEntries21()
            throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "important 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryPartiallyMatchesSymbolInEntry2PartiallyMatchesNameInEntry1AndReturnsEntries21()
            throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "important 1", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryPartiallyMatchesTaxonNameInEntry2PartiallyMatchesNameInEntry1AndReturnsEntries21()
            throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "important 1", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "important 1", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    @Test
    public void queryPartiallyMatchesTaxonNameInEntry2PartiallyMatchesSynonymInEntry1AndReturnsEntries21()
            throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "important 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", "important 1", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(2)));
    }

    // Term frequency -------------------------------------------------------------------------
    @Test
    public void termFrequencyDoesNotInfluenceScoring() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "protein 1", "symbol 1", null, "protein 5");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "protein 2 protein 3", "symbol 2", null,
                "protein 4 and protein 6");

        repository.save(doc2);
        repository.save(doc1);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "protein"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(2)))
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2));
    }

    // Basic field matching -------------------------------------------------------------------------
    @Test
    public void nameWordMatchesFindsResult() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", null,
                "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine snazzy process", "symbol 2", null,
                "synonym 2");

        repository.save(doc1);
        repository.save(doc2);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "metabolic"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(1)));
    }

    @Test
    public void symbolWordMatchesFindsResult() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", null,
                "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "cymbal 2", null,
                "synonym 2");

        repository.save(doc1);
        repository.save(doc2);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "symbol"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(1)));
    }

    @Test
    public void synonymWordMatchesFindsResult() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", null,
                "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", null,
                "syn 2");

        repository.save(doc1);
        repository.save(doc2);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "synonym"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(1)));
    }

    @Test
    public void nameMatchGivesResultsInOrderEntriesWereAdded() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", null,
                "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", null,
                "synonym 2");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", null,
                "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "process"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    @Test
    public void synonymMatchGivesResultsInOrderEntriesWereAdded() throws Exception {
        GeneProductDocument doc1 = createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", null,
                "synonym 1");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "glycine metabolic process", "symbol 2", null,
                "synonym 2");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "glycine metabolic process", "symbol 3", null,
                "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "synonym"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    // Phrase matches -------------------------------------------------------------------------
    @Test
    public void phraseMatchesOnNameReturnsShortestMatchFirst() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "a metabolic process is handy", "symbol 1", "tax 1", "synonym 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "metabolic process is handy", "symbol 2", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "ab metabolic process is handy", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "metabolic process"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[2].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    @Test
    public void phraseMatchesOnSynonymReturnsShortestMatchFirst() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "metal 1", "symbol", "tax 1", "a synonym abcdef attention hup sir");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "metal 2", "symbol", "tax 2", "a synonym abcde attention hup");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "metal 3", "symbol", "tax 3", "a synonym abcd attention");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "synonym abcd"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    // Sanity checks on matches that involve synonym, symbol and name
    // -------------------------------------------------------------------------
    @Test
    public void
    queryExactMatchesSynonymInEntry1WordMatchesSymbolInEntry2PartialMatchesNameInEntry3AndReturnsEntries123()
            throws Exception {

        // the query:
        //    exact matches synonym in entry 1
        //    word matches symbol in entry 2
        //    partially matches name in entry 3

        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "important");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "pretend importantify procedure", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc3);
        repository.save(doc2);
        repository.save(doc1);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "important"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    @Test
    public void
    queryWordMatchesNameInEntry3PartiallyMatchesSymbolInEntry2PartiallyMatchesSynonymInEntry1AndReturnsEntries321()
            throws Exception {
        // the query:
        //    word matches name in entry 3
        //    partially matches symbol in entry 2
        //    partially matches name in entry 1

        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "glycine metabolic process", "symbol 1", "tax 1", "importantmuncho 1");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "glycine metabolic process", "important 1", "tax 2", "synonym 2");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "glycine import metabolic process", "symbol 3", "tax 3", "synonym 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "import"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    // Length matches -------------------------------------------------------------------------
    @Test
    public void wordInShortestPhraseMatchesFirst() throws Exception {
        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "metal 1", "symbol", "tax 1", "a synonym is really weird silly");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "metal 2", "symbol", "tax 2", "a synonym is really weird");
        GeneProductDocument
                doc3 = createDoc(VALID_ID_3, "metal 3", "symbol", "tax 3", "a synonym is really");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "synonym"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    @Test
    public void partialInSameWordReturnsShortestMatchesFirst() throws Exception {
        assumeThat(FIXED_GOA_2041, is(true));

        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "metal 1", "symbol", "tax 1", "a synonym one twothree");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "metal 2", "symbol", "tax 2", "a synonym one two");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "metal 3", "symbol", "tax 3", "a synonym one");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "syno"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    @Test
    public void partialInDifferentWordsReturnsShortestMatchesFirst() throws Exception {
        assumeThat(FIXED_GOA_2041, is(true));

        GeneProductDocument doc1 =
                createDoc(VALID_ID_1, "metal 1", "symbol", null, "a synon is like, err, awesome");
        GeneProductDocument doc2 =
                createDoc(VALID_ID_2, "metal 1", "symbol", null, "a synony is like, err, awesome");
        GeneProductDocument doc3 =
                createDoc(VALID_ID_3, "metal 1", "symbol", null, "a synonym is like, err, awesome");

        repository.save(doc2);
        repository.save(doc3);
        repository.save(doc1);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "syno"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(VALID_ID_1))
                .andExpect(jsonPath("$.results[1].id").value(VALID_ID_2))
                .andExpect(jsonPath("$.results[2].id").value(VALID_ID_3))
                .andExpect(jsonPath("$.results.*", hasSize(3)));
    }

    @Test
    public void suiteOfTripletWordTests() {
        assumeThat(FIXED_GOA_2041, is(true));

        StringBuilder report = new StringBuilder("\n\n========== Start of test report ==========").append
                ("\n");
        for (int i = 0; i < 26; i++) {
            String doc1Name = alphabetFieldValue(i + 1);
            String doc2Name = alphabetFieldValue(i);
            GeneProductDocument doc1 = createDoc(VALID_ID_1, doc1Name, "symbol", null, "X");
            GeneProductDocument doc2 = createDoc(VALID_ID_2, doc2Name, "symbol", null, "X");

            repository.save(doc1);
            repository.save(doc2);

            report.append(checkTripletsInDocuments(doc1Name, doc2Name));

            repository.deleteAll();
        }

        report
                .append("\n")
                .append("-- The search query tested was (aaa)").append("\n")
                .append("-- Successful tests returned results in the order, [doc2,doc1], as indicated by, \"PASS\"")
                .append("\n")
                .append("-- Unsuccessful tests returned results in the order, [doc1,doc2], as indicated by, \"FAIL\"")
                .append("\n")
                .append("========== end of test report ==========").append("\n");
        System.out.println(report);

        if (report.indexOf("FAIL") > 0) {
            throw new AssertionError("There were test failures");
        } else {
            System.out.println("Tests passed -- nice one!");
        }
    }

    // Helpers -------------------------------------------------------------------------
    private static GeneProductDocument createDoc(String id, String name, String symbol, String taxonName,
            String... synonyms) {
        GeneProductDocument document = createDocWithId(id);

        document.name = name;
        document.synonyms = Arrays.asList(synonyms);
        document.symbol = symbol;
        document.taxonName = taxonName;
        document.type = "protein";

        return document;
    }

    private void populateIndexWithIdenticalFieldsExceptID() {
        GeneProductDocument
                doc1 = createDoc(VALID_ID_1, "anything", "anything", null, "anything");
        GeneProductDocument doc2 = createDoc(VALID_ID_2, "anything", "anything", null, "anything");
        GeneProductDocument doc3 = createDoc(VALID_ID_3, "anything", "anything", null, "anything");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);
    }

    private StringBuilder checkTripletsInDocuments(String doc1Name, String doc2Name) {
        StringBuilder result = new StringBuilder();
        result
                .append("--------------------------").append("\n")
                .append("Index contents:").append("\n")
                .append("    doc1.name=").append(doc1Name).append("\n")
                .append("    doc2.name=").append(doc2Name).append("\n")
                .append("Test result: \n");
        try {
            mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "aaa"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.results[0].id").value(VALID_ID_2))
                    .andExpect(jsonPath("$.results[1].id").value(VALID_ID_1))
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