package uk.ac.ebi.quickgo.geneproduct.controller;

import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.GeneProductService;
import uk.ac.ebi.quickgo.geneproduct.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.SearchableField;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 30/03/2016
 * Time: 16:33
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneProductControllerTest {

    private GeneProductController controller;

    @Mock
    private GeneProductService geneProductService;

    @Mock
    private GeneProduct geneProduct;

    @Mock
    private GeneProduct geneProduct2;

    @Mock
    private GeneProduct geneProduct3;

    @Mock
    private SearchService<GeneProduct> geneProductSearchService;
    private static final String SINGLE_CSV = "A0A000";
    private static final List<String> SINGLE_CSV_LIST = singletonList("A0A000");

    @Mock
    private SearchableField geneProductSearchableField;

    @Mock
    private SearchServiceConfig.GeneProductCompositeRetrievalConfig geneProductRetrievalConfig;

    private static final String MULTI_CSV = "A0A000,A0A001,A0A002";
    private static final List<String> MULTI_CSV_LIST = asList("A0A000", "A0A001", "A0A002");

    private static String multiCSVTooBig;

    @Before
    public void setUp() {
        this.controller = new GeneProductController(
                geneProductService,
                geneProductSearchService,
                geneProductSearchableField,
                geneProductRetrievalConfig);

        //Lookup for single Id
        final List<GeneProduct> singleGP = singletonList(geneProduct);
        when(geneProductService.findById(SINGLE_CSV_LIST)).thenReturn(singleGP);

        //Lookup for multi Id
        final List<GeneProduct> multiGP = asList(geneProduct, geneProduct2, geneProduct3);
        when(geneProductService.findById(MULTI_CSV_LIST)).thenReturn(multiGP);

        // too big CSV
        String delim = "";
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < ControllerValidationHelperImpl.MAX_PAGE_RESULTS + 1; i++) {
            sb.append(delim).append("A0A").append(i);
            delim = ",";
        }
        multiCSVTooBig = sb.toString();
    }

    @Test
    public void retrieveEmptyList() {
        ResponseEntity<QueryResult<GeneProduct>> response = controller.findById("");
        assertThat(response.getHeaders().isEmpty(), is(true));
        assertThat(response.getBody().getResults(), is(empty()));
    }

    @Test
    public void retrieveSingleGeneProduct() {
        ResponseEntity<QueryResult<GeneProduct>> response = controller.findById(SINGLE_CSV);
        assertThat(response.getBody().getResults(), contains(geneProduct));
        assertThat(response.getBody().getResults(), hasSize(1));
    }

    @Test
    public void retrieveMultipleGeneProduct() {
        ResponseEntity<QueryResult<GeneProduct>> response = controller.findById(MULTI_CSV);
        assertThat(response.getBody().getResults(), hasSize(3));
        assertThat(response.getBody().getResults(), contains(geneProduct, geneProduct2, geneProduct3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void retrieveMultipleGeneProductOverLimit() {
        controller.findById(multiCSVTooBig);
    }
}
