package uk.ac.ebi.quickgo.geneproduct.search;

import uk.ac.ebi.quickgo.geneproduct.GeneProductREST;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductFields;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductType;
import uk.ac.ebi.quickgo.rest.search.SearchControllerSetup;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static uk.ac.ebi.quickgo.geneproduct.common.common.GeneProductDocMocker.createDocWithId;

@SpringApplicationConfiguration(classes = {GeneProductREST.class})
public class GeneProductSearchIT extends SearchControllerSetup {
    @Autowired
    private GeneProductRepository repository;

    private static final String ONTOLOGY_RESOURCE_URL = "/geneproduct/search";

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();
        resourceUrl = ONTOLOGY_RESOURCE_URL;
    }

    // response format ---------------------------------------------------------
    @Test
    public void requestWhichFindsNothingReturnsValidResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process");

        saveToRepository(doc1);

        checkValidEmptyResultsResponse("doesn't exist");
    }

    @Test
    public void requestWhichAsksForPage0WithLimit0Returns400Response() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process");

        saveToRepository(doc1);

        checkInvalidPageInfoInResponse("aaaa", 0, 0, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void requestWithNegativePageNumberReturns400Response() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = -1;
        int entriesPerPage = 10;

        checkInvalidPageInfoInResponse("glycine", pageNum, entriesPerPage, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void requestWithNegativeLimitNumberReturns400Response() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = 1;
        int entriesPerPage = -1;

        checkInvalidPageInfoInResponse("process 3", pageNum, entriesPerPage, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void requestForFirstPageWithLimitOf10ReturnsAllResults() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = 1;
        int entriesPerPage = 3;

        checkValidPageInfoInResponse("glycine", pageNum, entriesPerPage);
    }

    @Test
    public void requestForSecondPageWithLimitOf2ReturnsLastEntry() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        int pageNum = 2;
        int entriesPerPage = 2;

        checkValidPageInfoInResponse("glycine", pageNum, entriesPerPage);
    }

    @Test
    public void requestForPageThatIsLargerThanTotalNumberOfPagesInResponseReturns400Response() throws Exception {
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
    public void requestWithInValidFacetFieldReturns400Response() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        checkInvalidFacetResponse("glycine", "incorrect_field");
    }

    @Test
    public void requestWithValidFacetFieldReturnsResponseWithFacetInResult() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse("glycine", GeneProductFields.Searchable.TYPE);
    }

    @Test
    public void requestWithMultipleValidFacetFieldsReturnsResponseWithMultipleFacetsInResult() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse("glycine", GeneProductFields.Searchable.TYPE,
                GeneProductFields.Searchable.TAXON_ID);
    }

    @Test
    public void requestWithTypeFacetFieldReturnsResponseWithFacetInResult() throws Exception {
        GeneProductType type = GeneProductType.PROTEIN;
        String name = "name";

        GeneProductDocument doc1 = createGeneProductDocWithNameAndType("A0A0F8CSS1", name, type);
        GeneProductDocument doc2 = createGeneProductDocWithNameAndType("A0A0F8CSS2", name, type);
        GeneProductDocument doc3 = createGeneProductDocWithNameAndType("A0A0F8CSS3", name, type);

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse(name, GeneProductFields.Searchable.TYPE);
    }

    @Test
    public void requestWithTaxonIdFacetFieldReturnsResponseWithFacetInResult() throws Exception {
        int taxonId = 99;
        String name = "name";

        GeneProductDocument doc1 = createGeneProductDocWithNameAndTaxonId("A0A0F8CSS1", name, taxonId);
        GeneProductDocument doc2 = createGeneProductDocWithNameAndTaxonId("A0A0F8CSS2", name, taxonId);
        GeneProductDocument doc3 = createGeneProductDocWithNameAndTaxonId("A0A0F8CSS3", name, taxonId);

        saveToRepository(doc1, doc2, doc3);

        checkValidFacetResponse(name, GeneProductFields.Searchable.TAXON_ID);
    }

    // filter queries ---------------------------------------------------------
    @Test
    public void requestWithInvalidFilterQueryReturns400Response() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");

        saveToRepository(doc1, doc2, doc3);

        String fq = buildFilterQuery("thisFieldDoesNotExist", "Process");

        checkInvalidFilterQueryResponse("glycine", fq);
    }

    @Test
    public void requestWithAFilterQueryReturnsFilteredResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        doc1.taxonId = 1;
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        doc2.taxonId = 1;
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");
        doc3.taxonId = 2;

        saveToRepository(doc1, doc2, doc3);

        String fq = buildFilterQuery(GeneProductFields.Searchable.TAXON_ID, "1");

        checkValidFilterQueryResponse("metabolic", 2, fq);
    }

    @Test
    public void requestWith3FilterQueriesThatFilterOutAllResults() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        doc1.type = GeneProductType.PROTEIN.getName();
        doc1.taxonId = 1;
        doc1.databaseSubset = "TrEMBL";
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        doc2.type = GeneProductType.PROTEIN.getName();
        doc2.taxonId = 2;
        doc1.databaseSubset = "Swiss-Prot";
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");
        doc3.type = GeneProductType.RNA.getName();
        doc3.taxonId = 3;
        doc1.databaseSubset = null;

        saveToRepository(doc1, doc2, doc3);

        String fq1 = buildFilterQuery(GeneProductFields.Searchable.TYPE, GeneProductType.PROTEIN.getName());
        String fq2 = buildFilterQuery(GeneProductFields.Searchable.TAXON_ID, "1");
        String fq3 = buildFilterQuery(GeneProductFields.Searchable.DATABASE_SUBSET, "Swiss-Prot");

        checkValidFilterQueryResponse("process", 0, fq1, fq2, fq3);
    }

    @Test
    public void requestWithFilterQueryThatDoesNotFilterOutAnyEntryReturnsAllResults() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        doc1.taxonId = 1;
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        doc2.taxonId = 1;
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");
        doc3.taxonId = 1;

        saveToRepository(doc1, doc2, doc3);

        String fq = buildFilterQuery(GeneProductFields.Searchable.TAXON_ID, "1");

        checkValidFilterQueryResponse("glycine", 3, fq);
    }

    @Test
    public void requestWith1ValidFilterQueryReturnsFilteredResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        doc1.taxonId = 1;
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");
        doc2.taxonId = 2;
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic process 3");
        doc3.taxonId = 1;

        saveToRepository(doc1, doc2, doc3);

        checkValidFilterQueryResponse("process", 2, GeneProductFields.Searchable.TAXON_ID + ":1")
                .andExpect(jsonPath("$.results[0].id").value("A0A0F8CSS1"))
                .andExpect(jsonPath("$.results[1].id").value("A0A0F8CSS3"));
    }

    // highlighting ------------------------------------------------
    @Test
    public void requestWithHighlightingOnAndOneHitReturnsValidResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process one");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process two");

        saveToRepository(doc1, doc2);

        checkValidHighlightOnQueryResponse("process two", "A0A0F8CSS2");
    }

    @Test
    public void requestWithHighlightingOnAndTwoHitsReturnsValidResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic smurf 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic smurf 3");

        saveToRepository(doc1, doc2, doc3);

        checkValidHighlightOnQueryResponse("smurf", "A0A0F8CSS2", "A0A0F8CSS3");
    }

    @Test
    public void requestWithHighlightingOnAndZeroHitsReturnsValidResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");

        saveToRepository(doc1, doc2);

        checkValidHighlightOnQueryResponse("Southampton");
    }

    @Test
    public void requestWithHighlightingOffAndOneHitReturnsValidResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic sausage 2");

        saveToRepository(doc1, doc2);

        checkValidHighlightOffQueryResponse("metabolic process", 1);
    }

    @Test
    public void requestWithHighlightingOffAndOnZeroHitsReturnsValidResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic process 2");

        saveToRepository(doc1, doc2);

        checkValidHighlightOffQueryResponse("Southampton", 0);
    }

    @Test
    public void requestWithHighlightingOnReturnsTwoHighlightedValuesInResponse() throws Exception {
        GeneProductDocument doc1 = createGeneProductDocWithName("A0A0F8CSS1", "glycine metabolic process 1");
        GeneProductDocument doc2 = createGeneProductDocWithName("A0A0F8CSS2", "glycine metabolic Slider 2");
        GeneProductDocument doc3 = createGeneProductDocWithName("A0A0F8CSS3", "glycine metabolic Slider 3");

        saveToRepository(doc1, doc2, doc3);

        checkValidHighlightOnQueryResponse("Slider", "A0A0F8CSS2", "A0A0F8CSS3")
                .andExpect(jsonPath("$.results.*.id", containsInAnyOrder("A0A0F8CSS2", "A0A0F8CSS3")))
                .andExpect(jsonPath("$.highlighting.*.id", containsInAnyOrder("A0A0F8CSS2", "A0A0F8CSS3")))
                .andExpect(jsonPath("$.highlighting.*.matches.*.field", containsInAnyOrder("name", "name")))
                .andExpect(jsonPath("$.highlighting[0].matches[0].values[0]", containsString("Slider")))
                .andExpect(jsonPath("$.highlighting[1].matches[0].values[0]", containsString("Slider")));
    }

    private void saveToRepository(GeneProductDocument... documents) {
        for (GeneProductDocument doc : documents) {
            repository.save(doc);
        }
    }

    private String buildFilterQuery(String field, String value) {
        return field + ":" + value;
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
}