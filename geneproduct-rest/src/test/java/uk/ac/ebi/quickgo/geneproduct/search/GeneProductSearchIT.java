package uk.ac.ebi.quickgo.geneproduct.search;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ebi.quickgo.geneproduct.GeneProductREST;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductType;
import uk.ac.ebi.quickgo.rest.search.SearchControllerSetup;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static uk.ac.ebi.quickgo.geneproduct.GeneProductParameters.*;
import static uk.ac.ebi.quickgo.geneproduct.common.common.GeneProductDocMocker.createDocWithId;
import static uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl.MAX_PAGE_NUMBER;

@SpringBootTest(classes = {GeneProductREST.class})
class GeneProductSearchIT extends SearchControllerSetup {
    @Autowired
    private GeneProductRepository repository;

    private static final String GENE_PRODUCT_RESOURCE_URL = "/geneproduct/search";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        resourceUrl = GENE_PRODUCT_RESOURCE_URL;
    }

    // response format ---------------------------------------------------------
    @Test
    void requestWhichFindsNothingReturnsValidResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process");

        saveToRepository(doc1);

        checkValidEmptyResultsResponse("doesn't exist");
    }

    @Test
    void pageRequestHigherThanPaginationLimitReturns400() throws Exception {
        int totalEntries = MAX_PAGE_NUMBER + 1;
        int pageSize = 1;

        checkInvalidPageInfoInResponse("aaaa", totalEntries, pageSize, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void requestWhichAsksForPage0WithLimit0Returns400Response() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process");

        saveToRepository(doc1);

        checkInvalidPageInfoInResponse("aaaa", 0, 0, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void requestWithNegativePageNumberReturns400Response() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = -1;
        int entriesPerPage = 10;

        checkInvalidPageInfoInResponse("glycine", pageNum, entriesPerPage, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void requestWithNegativeLimitNumberReturns400Response() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = 1;
        int entriesPerPage = -1;

        checkInvalidPageInfoInResponse("process 3", pageNum, entriesPerPage, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void requestForFirstPageWithLimitOf10ReturnsAllResults() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = 1;
        int entriesPerPage = 3;

        checkValidPageInfoInResponse("glycine", pageNum, entriesPerPage);
    }

    @Test
    void requestForSecondPageWithLimitOf2ReturnsLastEntry() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = 2;
        int entriesPerPage = 2;

        checkValidPageInfoInResponse("glycine", pageNum, entriesPerPage);
    }

    @Test
    void requestForPageThatIsLargerThanTotalNumberOfPagesInResponseReturns400Response() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = 3;
        int entriesPerPage = 2;

        checkInvalidPageInfoInResponse("metabolic", pageNum, entriesPerPage, HttpStatus.SC_BAD_REQUEST);
    }

    // facets ---------------------------------------------------------
    @Test
    void requestWithInValidFacetFieldReturns400Response() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        checkInvalidFacetResponse("glycine", "incorrect_field");
    }

    @Test
    void requestWithValidFacetFieldReturnsResponseWithFacetInResult() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse("glycine", TYPE_PARAM.getName());
    }

    @Test
    void requestWithMultipleValidFacetFieldsReturnsResponseWithMultipleFacetsInResult() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse("glycine", TYPE_PARAM.getName(), TAXON_ID_PARAM.getName());
    }

    @Test
    void requestWithTypeFacetFieldReturnsResponseWithFacetInResult() throws Exception {
        GeneProductType type = GeneProductType.PROTEIN;
        String name = "name";

        GeneProductDocument doc1 = createGeneProductDocWithNameAndType("A0A0F8CSS1", name, type);
        GeneProductDocument doc2 = createGeneProductDocWithNameAndType("A0A0F8CSS2", name, type);
        GeneProductDocument doc3 = createGeneProductDocWithNameAndType("A0A0F8CSS3", name, type);

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse(name, TYPE_PARAM.getName());
    }

    @Test
    void requestWithTaxonIdFacetFieldReturnsResponseWithFacetInResult() throws Exception {
        int taxonId = 99;
        String name = "name";

        GeneProductDocument doc1 = createGeneProductDocWithNameAndTaxonId("A0A0F8CSS1", name, taxonId);
        GeneProductDocument doc2 = createGeneProductDocWithNameAndTaxonId("A0A0F8CSS2", name, taxonId);
        GeneProductDocument doc3 = createGeneProductDocWithNameAndTaxonId("A0A0F8CSS3", name, taxonId);

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse(name, TAXON_ID_PARAM.getName());
    }

    @Test
    void requestWithDbSubsetFacetFieldReturnsResponseWithFacetInResult() throws Exception {
        String dbSubset = "TrEMBL";
        String name = "name";

        GeneProductDocument doc1 = createGeneProductDocWithNameAndDbSubset("A0A0F8CSS1", name, dbSubset);
        GeneProductDocument doc2 = createGeneProductDocWithNameAndDbSubset("A0A0F8CSS2", name, dbSubset);
        GeneProductDocument doc3 = createGeneProductDocWithNameAndDbSubset("A0A0F8CSS3", name, dbSubset);

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse(name, DB_SUBSET_PARAM.getName());
    }

    @Test
    void requestWithProteomeFacetFieldReturnsResponseWithFacetInResult() throws Exception {
        String proteome = "Complete";
        String name = "name";

        GeneProductDocument doc1 = createGeneProductDocWithNameAndProteome("A0A0F8CSS1", name, proteome);
        GeneProductDocument doc2 = createGeneProductDocWithNameAndProteome("A0A0F8CSS2", name, proteome);
        GeneProductDocument doc3 = createGeneProductDocWithNameAndProteome("A0A0F8CSS3", name, proteome);

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse(name, PROTEOME_PARAM.getName());
    }

    // filter queries ---------------------------------------------------------
    @Test
    void requestWithInvalidFilterQueryIgnoresTheFilter() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        Param fq = new Param("thisFieldDoesNotExist", "Process");

        checkValidFilterQueryResponse("glycine", 3, fq);
    }

    @Test
    void requestWithATypeFilterQueryReturnsFilteredResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        doc1.type = "protein";
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        doc2.type = "protein";
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");
        doc3.type = "RNA";

        saveToRepository(doc1, doc2, doc3);

        Param fq = new Param(TYPE_PARAM.getName(), "protein");

        checkValidFilterQueryResponse("metabolic", 2, fq);
    }

    @Test
    void requestWithADbSubsetFilterQueryReturnsFilteredResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        doc1.databaseSubset = "Swiss-Prot";
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        doc2.databaseSubset = "TrEMBL";
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");
        doc3.databaseSubset = "Swiss-Prot";

        saveToRepository(doc1, doc2, doc3);

        Param fq = new Param(DB_SUBSET_PARAM.getName(), "Swiss-Prot");

        checkValidFilterQueryResponse("metabolic", 2, fq);
    }

    @Test
    void requestWith2FilterQueriesThatFilterOutAllResults() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        doc1.type = "protein";
        doc1.taxonId = 2;
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        doc2.type = "RNA";
        doc2.taxonId = 1;
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");
        doc3.type = "protein";
        doc3.taxonId = 2;

        saveToRepository(doc1, doc2, doc3);

        Param fq1 = new Param(TYPE_PARAM.getName(), "RNA");
        Param fq2 = new Param(TAXON_ID_PARAM.getName(), "2");

        checkValidFilterQueryResponse("process", 0, fq1, fq2);
    }

    @Test
    void requestWithFilterQueryThatDoesNotFilterOutAnyEntryReturnsAllResults() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        doc1.type = "RNA";
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        doc2.type = "RNA";
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");
        doc3.type = "RNA";

        saveToRepository(doc1, doc2, doc3);

        Param fq = new Param(TYPE_PARAM.getName(), "RNA");

        checkValidFilterQueryResponse("glycine", 3, fq);
    }

    @Test
    void requestWithAProteomeFilterQueryReturnsFilteredResponse() throws Exception {
        String proteome = "Complete";
        String name = "glycine metabolic process";
        GeneProductDocument doc1 = createGeneProductDocWithNameAndProteome("A0A0F8CSS1", name, proteome);
        GeneProductDocument doc2 = createGeneProductDocWithNameAndProteome("A0A0F8CSS2", name, proteome);
        GeneProductDocument doc3 = createGeneProductDocWithNameAndProteome("A0A0F8CSS3", name, "Reference");
        saveToRepository(doc1, doc2, doc3);
        Param fq = new Param(PROTEOME_PARAM.getName(), proteome);

        checkValidFilterQueryResponse("metabolic", 2, fq);
    }

    // highlighting ------------------------------------------------
    @Test
    void requestWithHighlightingOnAndOneHitReturnsValidResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process one");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process two");

        saveToRepository(doc1, doc2);

        checkValidHighlightOnQueryResponse("two", "A0A0F8CSS2");
    }

    @Test
    void requestWithHighlightingOnAndTwoHitsReturnsValidResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic smurf 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic smurf 3");

        saveToRepository(doc1, doc2, doc3);

        checkValidHighlightOnQueryResponse("smurf", "A0A0F8CSS2", "A0A0F8CSS3");
    }

    @Test
    void requestWithHighlightingOnAndZeroHitsReturnsValidResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");

        saveToRepository(doc1, doc2);

        checkValidHighlightOnQueryResponse("Southampton");
    }

    @Test
    void requestWithHighlightingOffAndOneHitReturnsValidResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic sausage 2");

        saveToRepository(doc1, doc2);

        checkValidHighlightOffQueryResponse("process", 1);
    }

    @Test
    void requestWithHighlightingOffAndOnZeroHitsReturnsValidResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");

        saveToRepository(doc1, doc2);

        checkValidHighlightOffQueryResponse("Southampton", 0);
    }

    @Test
    void requestWithHighlightingOnReturnsTwoHighlightedValuesInResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic Slider 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic Slider 3");

        saveToRepository(doc1, doc2, doc3);

        checkValidHighlightOnQueryResponse("Slider", "A0A0F8CSS2", "A0A0F8CSS3")
                .andExpect(jsonPath("$.results.*.id", containsInAnyOrder("A0A0F8CSS2", "A0A0F8CSS3")))
                .andExpect(jsonPath("$.highlighting.*.id", containsInAnyOrder("A0A0F8CSS2", "A0A0F8CSS3")))
                .andExpect(jsonPath("$.highlighting.*.matches.*.field", containsInAnyOrder("name", "name", "name", "name")))
                .andExpect(jsonPath("$.highlighting[0].matches[0].values[0]", containsString("Slider")))
                .andExpect(jsonPath("$.highlighting[1].matches[0].values[0]", containsString("Slider")));
    }

    private void saveToRepository(GeneProductDocument... documents) {
        for (GeneProductDocument doc : documents) {
            repository.save(doc);
        }
    }

    private GeneProductDocument createGeneProductDocWithName(String id, String name) {
        GeneProductDocument geneProductDocument = createDocWithId(id);
        geneProductDocument.name = name;

        return geneProductDocument;
    }

    private GeneProductDocument createGeneProductDocWithNameAndType(String id, String name, GeneProductType type) {
        GeneProductDocument geneProductDocument = createDocWithId(id);
        geneProductDocument.type = type.getName();
        geneProductDocument.name = name;

        return geneProductDocument;
    }

    private GeneProductDocument createGeneProductDocWithNameAndTaxonId(String id, String name, int taxonId) {
        GeneProductDocument geneProductDocument = createDocWithId(id);
        geneProductDocument.taxonId = taxonId;
        geneProductDocument.name = name;

        return geneProductDocument;
    }

    private GeneProductDocument createGeneProductDocWithNameAndDbSubset(String id, String name, String dbSubset) {
        GeneProductDocument geneProductDocument = createDocWithId(id);
        geneProductDocument.name = name;
        geneProductDocument.databaseSubset = dbSubset;

        return geneProductDocument;
    }

    private GeneProductDocument createGeneProductDocWithNameAndProteome(String id, String name, String proteome) {
        GeneProductDocument geneProductDocument = createDocWithId(id);
        geneProductDocument.name = name;
        geneProductDocument.proteome = proteome;

        return geneProductDocument;
    }
}
