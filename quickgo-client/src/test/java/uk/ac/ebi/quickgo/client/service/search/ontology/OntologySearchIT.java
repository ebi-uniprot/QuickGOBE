package uk.ac.ebi.quickgo.client.service.search.ontology;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ebi.quickgo.client.QuickGOREST;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.rest.search.SearchControllerSetup;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ebi.quickgo.ontology.common.OntologyFields.Facetable;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MAX_PAGE_NUMBER;

@SpringBootTest(classes = {QuickGOREST.class})
public class OntologySearchIT extends SearchControllerSetup {
    private static final String ONTOLOGY_RESOURCE_URL = "/internal/search/ontology";

    private static final String ASPECT_PARAM = "aspect";
    private static final String TYPE_PARAM = "ontologyType";
    private static final String BIOLOGICAL_PROCESS = "Process";
    private static final String MOLECULAR_FUNCTION = "Function";
    private static final String COMPONENT = "Component";

    @Autowired
    private OntologyRepository repository;

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
    public void pageRequestHigherThanPaginationLimitReturns400() throws Exception {
        int totalEntries = MAX_PAGE_NUMBER + 1;
        int pageSize = 1;
        int pageNumWhichIsTooHigh = totalEntries;

        saveNDocs(totalEntries);

        checkInvalidPageInfoInResponse("bbbb", pageNumWhichIsTooHigh, pageSize, HttpStatus.SC_BAD_REQUEST);
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

        checkValidFacetResponse("go", ASPECT_PARAM);
    }

    @Test
    public void requestWithMultipleValidFacetFieldsReturnsResponseWithMultipleFacetsInResult() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse("go", Facetable.ASPECT, Facetable.ONTOLOGY_TYPE, Facetable.IS_OBSOLETE);
    }

    // filter queries ---------------------------------------------------------
    @Test
    public void requestWithInvalidFilterQueryIgnoresFilter() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go2");
        OntologyDocument doc3 = createGODoc("GO:0000003", "go3");

        saveToRepository(doc1, doc2, doc3);

        Param filterParam = new Param("thisFieldDoesNotExist", BIOLOGICAL_PROCESS);

        checkValidFilterQueryResponse("go", 3, filterParam);
    }

    @Test
    public void requestWithAFilterQueryReturnsFilteredResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        doc1.aspect = BIOLOGICAL_PROCESS;
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");
        doc2.aspect = MOLECULAR_FUNCTION;
        OntologyDocument doc3 = createGODoc("GO:0000003", "go function 3");
        doc3.aspect = BIOLOGICAL_PROCESS;

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        Param filterParam = new Param(ASPECT_PARAM, BIOLOGICAL_PROCESS);

        checkValidFilterQueryResponse("go function", 2, filterParam);
    }

    @Test
    public void requestWith2FilterQueriesThatFilterOutAllResults() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        doc1.aspect = BIOLOGICAL_PROCESS;
        doc1.ontologyType = "GO";
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");
        doc2.aspect = MOLECULAR_FUNCTION;
        doc2.ontologyType = "GO";
        OntologyDocument doc3 = createGODoc("GO:0000003", "go function 3");
        doc3.aspect = BIOLOGICAL_PROCESS;
        doc3.ontologyType = "GO";

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        Param fq1 = new Param(ASPECT_PARAM, BIOLOGICAL_PROCESS);
        Param fq2 = new Param(TYPE_PARAM, "eco");

        checkValidFilterQueryResponse("go function", 0, fq1, fq2);
    }

    @Test
    public void requestWithFilterQueryThatDoesNotFilterOutAnyEntryReturnsAllResults() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        doc1.aspect = BIOLOGICAL_PROCESS;
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");
        doc2.aspect = BIOLOGICAL_PROCESS;
        OntologyDocument doc3 = createGODoc("GO:0000003", "go function 3");
        doc3.aspect = BIOLOGICAL_PROCESS;

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        Param fq = new Param(ASPECT_PARAM, BIOLOGICAL_PROCESS);

        checkValidFilterQueryResponse("go function", 3, fq);
    }

    @Test
    public void requestWith1ValidFilterQueryReturnsFilteredResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        doc1.aspect = BIOLOGICAL_PROCESS;
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");
        doc2.aspect = MOLECULAR_FUNCTION;
        OntologyDocument doc3 = createGODoc("GO:0000003", "go function 3");
        doc3.aspect = BIOLOGICAL_PROCESS;

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        Param fq = new Param(ASPECT_PARAM, BIOLOGICAL_PROCESS);

        checkValidFilterQueryResponse("go function", 2, fq)
                .andExpect(jsonPath("$.results[0].id").value("GO:0000001"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000003"));
    }

    @Test
    public void requestWithValidAspectComponentFilterQueryReturnsFilteredResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        doc1.aspect = COMPONENT;
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 1");
        doc2.aspect = MOLECULAR_FUNCTION;
        OntologyDocument doc3 = createGODoc("GO:0000003", "go function 2");
        doc3.aspect = COMPONENT;

        repository.save(doc1);
        repository.save(doc2);
        repository.save(doc3);

        Param fq = new Param(ASPECT_PARAM, COMPONENT);

        checkValidFilterQueryResponse("go function", 2, fq)
                .andExpect(jsonPath("$.results[0].id").value("GO:0000001"))
                .andExpect(jsonPath("$.results[1].id").value("GO:0000003"));
    }

    @Test
    public void requestWithInvalidAspectFilterQueryReturns500() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        doc1.aspect = COMPONENT;
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 1");
        doc2.aspect = MOLECULAR_FUNCTION;

        repository.save(doc1);
        repository.save(doc2);

        String invalidAspect = "biological_process";

        Param fq = new Param(ASPECT_PARAM, invalidAspect);

        checkInvalidFilterQueryResponse("go function", fq)
                .andExpect(status().is(HttpStatus.SC_BAD_REQUEST));
    }

    // highlighting ------------------------------------------------
    @Test
    public void requestWithHighlightingOnAndOneHitReturnsValidResponse() throws Exception {
        OntologyDocument doc1 = createGODoc("GO:0000001", "go function 1");
        OntologyDocument doc2 = createGODoc("GO:0000002", "go function 2");

        repository.save(doc1);
        repository.save(doc2);

        checkValidHighlightOnQueryResponse("function 2", "GO:0000002", "GO:0000001");
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

        checkValidHighlightOffQueryResponse("go function 2", 2);
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
        repository.saveAll(asList(documents));
    }

    private void saveNDocs(int n) {
        List<OntologyDocument> documents = IntStream.range(1, n + 1)
                .mapToObj(i -> createGODoc(String.valueOf(i), "name " + n))
                .collect(Collectors.toList());
        repository.saveAll(documents);
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