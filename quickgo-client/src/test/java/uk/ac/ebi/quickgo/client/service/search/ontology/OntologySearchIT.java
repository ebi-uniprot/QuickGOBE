package uk.ac.ebi.quickgo.client.service.search.ontology;

import uk.ac.ebi.quickgo.client.QuickGOREST;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyFields;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.rest.search.SearchControllerSetup;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringApplicationConfiguration(classes = {QuickGOREST.class})
public class OntologySearchIT extends SearchControllerSetup {
    @Autowired
    private OntologyRepository repository;

    private static final String ONTOLOGY_RESOURCE_URL = "/internal/search/ontology";

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();
        resourceUrl = ONTOLOGY_RESOURCE_URL;
    }

    // response format ---------------------------------------------------------
    @Test
    public void requestWhichFindsNothingReturnsValidResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        saveToRepository(doc1);

        checkValidEmptyResultsResponse("doesn't exist");
    }

    @Test
    public void requestWhichAsksForPage0WithLimit0Returns400Response() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        saveToRepository(doc1);

        checkInvalidPageInfoInResponse("aaaa", 0, 0, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void requestWithNegativePageNumberReturns400Response() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = -1;
        int entriesPerPage = 10;

        checkInvalidPageInfoInResponse("go", pageNum, entriesPerPage, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void requestWithNegativeLimitNumberReturns400Response() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = 1;
        int entriesPerPage = -1;

        checkInvalidPageInfoInResponse("go", pageNum, entriesPerPage, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void requestForOntologyFindsAllFieldsInModelPopulated() throws Exception {
        String id = "GO:0000001";
        String name = "go1";
        boolean isObsolete = true;

        OntologyDocument doc1 = createGODocWithObsolete(id, name, isObsolete);

        saveToRepository(doc1);

        checkResultsBodyResponse("go")
                .andExpect(jsonPath("$.results[0].id", is(id)))
                .andExpect(jsonPath("$.results[0].name", is(name)))
                .andExpect(jsonPath("$.results[0].isObsolete", is(isObsolete)));
    }

    @Test
    public void requestForFirstPageWithLimitOf10ReturnsAllResults() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = 1;
        int entriesPerPage = 3;

        checkValidPageInfoInResponse("go", pageNum, entriesPerPage);
    }

    @Test
    public void requestForSecondPageWithLimitOf2ReturnsLastEntry() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = 2;
        int entriesPerPage = 2;

        checkValidPageInfoInResponse("go", pageNum, entriesPerPage);
    }

    @Test
    public void requestForPageThatIsLargerThanTotalNumberOfPagesInResponseReturns400Response() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = 3;
        int entriesPerPage = 2;

        checkInvalidPageInfoInResponse("go", pageNum, entriesPerPage, HttpStatus.SC_BAD_REQUEST);
    }

    // facets ---------------------------------------------------------
    @Test
    public void requestWithInValidFacetFieldReturns400Response() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        checkInvalidFacetResponse("go", "incorrect_field");
    }

    @Test
    public void requestWithValidFacetFieldReturnsResponseWithFacetInResult() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse("go", OntologyFields.Searchable.ASPECT);
    }

    @Test
    public void requestWithMultipleValidFacetFieldsReturnsResponseWithMultipleFacetsInResult() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse("go", OntologyFields.Searchable.ASPECT,
                OntologyFields.Searchable.NAME);
    }

    // filter queries ---------------------------------------------------------
    @Test
    public void requestWithInvalidFilterQueryReturns400Response() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        String fq = buildFilterQuery("thisFieldDoesNotExist", "Process");

        checkInvalidFilterQueryResponse("go", fq);
    }

    @Test
    public void requestWithAFilterQueryReturnsFilteredResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        doc1.aspect = "Process";
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");
        doc2.aspect = "Function";
        OntologyDocument doc3 = createGODoc("GO:0000003", "go function 3");
        doc3.aspect = "Process";

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        String fq = buildFilterQuery(OntologyFields.Searchable.ASPECT, "Process");

        checkValidFilterQueryResponse("go function", 2, fq);
    }

    @Test
    public void requestWith3FilterQueriesThatFilterOutAllResults() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        doc1.aspect = "Process";
        doc1.definition = "definition Klose";
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");
        doc2.aspect = "Function";
        doc2.definition = "definition Jerome";
        OntologyDocument doc3 = createGODoc("GO:0000003", "go function 3");
        doc3.aspect = "Process";
        doc3.definition = "definition Jerome";

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        String fq1 = buildFilterQuery(OntologyFields.Searchable.ASPECT, "Process");
        String fq2 = buildFilterQuery(OntologyFields.Searchable.DEFINITION, "Klose");
        String fq3 = buildFilterQuery(OntologyFields.Searchable.DEFINITION, "Ibrahimovic");

        checkValidFilterQueryResponse("go function", 0, fq1, fq2, fq3);
    }

    @Test
    public void requestWithFilterQueryThatDoesNotFilterOutAnyEntryReturnsAllResults() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        doc1.aspect = "Process";
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");
        doc2.aspect = "Process";
        OntologyDocument doc3 = createGODoc("GO:0000003", "go function 3");
        doc3.aspect = "Process";

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        String fq = buildFilterQuery(OntologyFields.Searchable.ASPECT, "Process");

        checkValidFilterQueryResponse("go function", 3, fq);
    }

    @Test
    public void requestWith1ValidFilterQueryReturnsFilteredResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        doc1.aspect = "Process";
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");
        doc2.aspect = "Function";
        OntologyDocument doc3 = createGODoc("GO:0000003", "go function 3");
        doc3.aspect = "Process";

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        checkValidFilterQueryResponse("go function", 2, OntologyFields.Searchable.ASPECT + ":Process")
                .andExpect(jsonPath("$.results[0].id").value("GO:0000001"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000003"));
    }

    // highlighting ------------------------------------------------
    @Test
    public void requestWithHighlightingOnAndOneHitReturnsValidResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");

        repository.save(doc1);
        repository.save(doc2);

        checkValidHighlightOnQueryResponse("function 2", "GO:0000002");
    }

    @Test
    public void requestWithHighlightingOnAndTwoHitsReturnsValidResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go anotherFunction 2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go anotherFunction 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        checkValidHighlightOnQueryResponse("anotherFunction", "GO:0000002", "GO:0000003");
    }

    @Test
    public void requestWithHighlightingOnAndZeroHitsReturnsValidResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");

        repository.save(doc1);
        repository.save(doc2);

        checkValidHighlightOnQueryResponse("Southampton");
    }

    @Test
    public void requestWithHighlightingOffAndOneHitReturnsValidResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");

        repository.save(doc1);
        repository.save(doc2);

        checkValidHighlightOffQueryResponse("function 2", 1);
    }

    @Test
    public void requestWithHighlightingOffAndOnZeroHitsReturnsValidResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");

        repository.save(doc1);
        repository.save(doc2);

        checkValidHighlightOffQueryResponse("Southampton", 0);
    }

    @Test
    public void requestWithHighlightingOnReturnsTwoHighlightedValuesInResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go anotherFunction 2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go anotherFunction 3");

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        checkValidHighlightOnQueryResponse("anotherFunction", "GO:0000002", "GO:0000003")
                .andExpect(jsonPath("$.results.*.id", containsInAnyOrder("GO:0000002", "GO:0000003")))
                .andExpect(jsonPath("$.highlighting.*.id", containsInAnyOrder("GO:0000002", "GO:0000003")))
                .andExpect(jsonPath("$.highlighting.*.matches.*.field", containsInAnyOrder("name", "name")))
                .andExpect(jsonPath("$.highlighting[0].matches[0].values[0]", containsString("anotherFunction")))
                .andExpect(jsonPath("$.highlighting[1].matches[0].values[0]", containsString("anotherFunction")));
    }

    private void saveToRepository(OntologyDocument... documents) {
        for (OntologyDocument doc : documents) {
            repository.save(doc);
        }
    }

    private String buildFilterQuery(String field, String value) {
        return field + ":" + value;
    }

    private OntologyDocument createGODoc(String id, String name) {
        OntologyDocument od = new OntologyDocument();
        od.id = id;
        od.name = name;
        od.ontologyType = OntologyType.GO.name();

        return od;
    }

    private OntologyDocument createGODocWithObsolete(String id, String name, boolean isObsolete) {
        OntologyDocument doc = createGODoc(id, name);
        doc.isObsolete = isObsolete;

        return doc;
    }
}