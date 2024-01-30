package uk.ac.ebi.quickgo.client.service.search.ontology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.client.QuickGOREST;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
@ExtendWith(TemporarySolrDataStore.class)
@SpringBootTest(classes = {QuickGOREST.class})
@WebAppConfiguration
class OntologyUserQueryScoringIT {
    private static final String RESOURCE_URL = "/internal/search/ontology";
    private static final String QUERY_PARAM = "query";

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    private OntologyRepository repository;

    protected MockMvc mockMvc;

    @BeforeEach
    void setup() {
        repository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void nonMatchingIdInQueryReturnsNoEntries() throws Exception {
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
    void idInQueryMatchesExactlyOneEntryReturnsThatEntry() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1");
        OntologyDocument doc2 = createDoc("GO:0000002", "go2");
        OntologyDocument doc3 = createDoc("GO:0000003", "go3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "GO:0000002"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000002"));
    }

    @Test
    void partiallyMatchingIdInQueryReturnsNoEntries() throws Exception {
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
    void nonMatchingNameInQueryReturnsNoEntries() throws Exception {
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
    void substringQueryPartiallyMatchesNameInEntry2ReturnsEntry2() throws Exception {
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
    void substringQueryPartiallyMatchesSynonymInEntry2ReturnsEntry2() throws Exception {
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
    void queryPartiallyMatchesNameInEntry1AndPartiallyMatchesSynonymInEntry2ReturnsEntry1FirstAndEntry2Second()
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
    void queryPartiallyMatchesNameInEntry1AndExactMatchesNameInEntry2ReturnsEntry2FirstAndEntry1Second()
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
    void queryPartiallyMatchesNameInEntry1AndExactMatchesSynonymInEntry2ReturnsEntry2FirstAndEntry1Second()
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
    void namesInEntriesDoNotMatchMultiWordQueryReturnsZeroEntries() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 with something");
        OntologyDocument doc2 = createDoc("GO:0000002", "go1 do something else");
        OntologyDocument doc3 = createDoc("GO:0000003", "go1 up then down");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go2 or"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(0)));
    }

    @Test
    void namesInEntryMatchAllWordsInMultiWordQueryReturnsMatchingEntry() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 up and down");

        repository.save(doc1);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go1 and"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000001"));
    }

    @Test
    void multiWordQueryMatchesPartiallyNameInEntry1AndExactMatchesNameInEntry2ReturnsEntry2ThenEntry1()
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
    void multiWordQueryMatchesPartiallyNameInEntry1AndExactMatchesSynonymInEntry2ReturnsEntry2ThenEntry1()
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
    void termFrequencyDoesNotInfluenceScoring() throws Exception {
        OntologyDocument docContainingTwoGo1s = createDoc("GO:0000001", "go1 go2", "go1");
        OntologyDocument docContaining6Go1s = createDoc(
                "GO:0000002",
                "go1 go2",
                "go1 and go1 is not go2",
                "go1 go2 synonym",
                "go1 or go1");

        repository.save(docContainingTwoGo1s);
        repository.save(docContaining6Go1s);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "go1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(2)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000001"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000002"));
    }

    @Test
    void whenQueryMatchesDocumentsEquallyResultsAreOrderedByShortestToLongest() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 has a particularly long function");
        OntologyDocument doc2 = createDoc("GO:0000002", "go1 has a long function");
        OntologyDocument doc3 = createDoc("GO:0000003", "go1 has a function");

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
    void whenQueryMatches2FieldsInFirstDocAnd1FieldInSecondDocResultsAreOrderedByNumOfMatches() throws
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

    @Test
    void hyphenatedQueryMatchesHyphenatedName() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 a function", "synonym one");
        OntologyDocument doc2 = createDoc("GO:0000002", "name-2-two", "some synonym");
        OntologyDocument doc3 = createDoc("GO:0000003", "go3 a function");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "name-2-two"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000002"));
    }

    @Test
    void nonHyphenatedQueryMatchesHyphenatedName() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "go1 a function", "synonym one");
        OntologyDocument doc2 = createDoc("GO:0000002", "name-2-two", "some synonym");
        OntologyDocument doc3 = createDoc("GO:0000003", "go3 a function");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "name 2 two"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000002"));
    }

    @Test
    void searchForASecondaryIdIsSuccessful() throws Exception {
        OntologyDocument doc1 = createDoc("GO:0000001", "something");
        OntologyDocument doc2 = createDoc("GO:0000002", "something");
        OntologyDocument doc3 = createDoc("GO:0000003", "something");

        doc1.secondaryIds = Collections.singletonList("GO:0000008");
        doc2.secondaryIds = Collections.singletonList("GO:0000009");
        doc3.secondaryIds = Collections.singletonList("GO:0000008");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "GO:0000009"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(1)))
                .andExpect(jsonPath("$.results[0].id").value("GO:0000002"));
    }

    @Test
    void scoringWorks() throws Exception {
        OntologyDocument idDoc = createDoc("GO:0000001", "something");
        OntologyDocument nameExactDoc = createDoc("GO:0000002", "GO:0000001");
        OntologyDocument nameEdgeDoc = createDoc("GO:0000003", "GO:000000123456");
        OntologyDocument synonymExactDoc = createDoc("GO:0000004", "something", "GO:0000001");
        OntologyDocument secondaryIdDoc = createDoc("GO:0000005", "something");
        OntologyDocument synonymEdgeDoc = createDoc("GO:0000006", "something", "GO:0000001ABCD");
        OntologyDocument noMatchDoc = createDoc("GO:0000007", "something");

        secondaryIdDoc.secondaryIds = Collections.singletonList("GO:0000001");

        repository.save(idDoc);
        repository.save(nameExactDoc);
        repository.save(nameEdgeDoc);
        repository.save(synonymExactDoc);
        repository.save(secondaryIdDoc);
        repository.save(synonymEdgeDoc);
        repository.save(noMatchDoc);

        mockMvc.perform(get(RESOURCE_URL).param(QUERY_PARAM, "GO:0000001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.*", hasSize(6)))
                .andExpect(jsonPath("$.results[0].id").value(idDoc.id))
                .andExpect(jsonPath("$.results[1].id").value(nameExactDoc.id))
                .andExpect(jsonPath("$.results[2].id").value(synonymExactDoc.id))
                .andExpect(jsonPath("$.results[3].id").value(secondaryIdDoc.id))
                .andExpect(jsonPath("$.results[4].id").value(nameEdgeDoc.id))
                .andExpect(jsonPath("$.results[5].id").value(synonymEdgeDoc.id));

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
