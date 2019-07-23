package uk.ac.ebi.quickgo.geneproduct.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.GeneProductService;
import uk.ac.ebi.quickgo.geneproduct.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 30/03/2016
 * Time: 16:33
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneProductControllerTest {

    private static final String GENE_PRODUCT_ID1 = "A0A001";
    private static final String GENE_PRODUCT_ID2 = "A0A002";
    private static final String GENE_PRODUCT_ID3 = "A0A003";
    private static final String SINGLE_CSV = GENE_PRODUCT_ID1;
    private static final List<String> SINGLE_CSV_LIST = singletonList(GENE_PRODUCT_ID1);
    private static final String MULTI_CSV = GENE_PRODUCT_ID1 + "," + GENE_PRODUCT_ID2 + "," + GENE_PRODUCT_ID3;
    private static final List<String> MULTI_CSV_LIST = asList(GENE_PRODUCT_ID1, GENE_PRODUCT_ID2, GENE_PRODUCT_ID3);
    private static String multiCSVTooBig;
    private GeneProductController controller;
    private GeneProduct geneProduct1;
    private GeneProduct geneProduct2;
    private GeneProduct geneProduct3;
    private SearchServiceConfig.GeneProductCompositeRetrievalConfig geneProductRetrievalConfig;

    @Mock
    private GeneProductService geneProductService;

    @Mock
    private ControllerValidationHelper validationHelper;

    @Mock
    private SearchService<GeneProduct> geneProductSearchService;

    @Mock
    private FilterConverterFactory converterFactory;

    @Before
    public void setUp() {
        geneProduct1 = createGeneProduct(GENE_PRODUCT_ID1);
        geneProduct2 = createGeneProduct(GENE_PRODUCT_ID2);
        geneProduct3 = createGeneProduct(GENE_PRODUCT_ID3);
        geneProductRetrievalConfig = createStubGeneProductCompositeRetrievalConfig();

        this.controller = new GeneProductController(
                geneProductService,
                geneProductSearchService,
                geneProductRetrievalConfig,
                validationHelper,
                converterFactory);

        final List<GeneProduct> singleGP = singletonList(geneProduct1);
        when(geneProductService.findById(SINGLE_CSV_LIST)).thenReturn(singleGP);

        final List<GeneProduct> multiGP = asList(geneProduct1, geneProduct2, geneProduct3);

        multiCSVTooBig = createOversizedCSVRequest();

        when(validationHelper.validateCSVIds(SINGLE_CSV)).thenReturn(SINGLE_CSV_LIST);
        doThrow(new IllegalArgumentException()).when(validationHelper).validateCSVIds(multiCSVTooBig);

    }

    @Test
    public void retrieveEmptyList() {
        ResponseEntity<QueryResult<GeneProduct>> response = controller.findById("");
        assertThat(response.getHeaders().isEmpty(), is(true));
        assertThat(response.getBody().getResults(), is(empty()));
    }

    @Test
    public void retrieveSingleGeneProduct() {
        ResponseEntity<QueryResult<GeneProduct>> response = controller.findById(GENE_PRODUCT_ID1);
        assertThat(response.getBody().getResults(), contains(geneProduct1));
        assertThat(response.getBody().getResults(), hasSize(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void retrieveMultipleGeneProductOverLimit() {
        controller.findById(multiCSVTooBig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullGeneProductService() {
        new GeneProductController(
                null,
                geneProductSearchService,
                geneProductRetrievalConfig,
                validationHelper,
                converterFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullSearchService() {
        new GeneProductController(
                geneProductService,
                null,
                geneProductRetrievalConfig,
                validationHelper,
                converterFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullRetrievalConfig() {
        new GeneProductController(
                geneProductService,
                geneProductSearchService,
                null,
                validationHelper,
                converterFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullValidationHelper() {
        new GeneProductController(
                geneProductService,
                geneProductSearchService,
                geneProductRetrievalConfig,
                null,
                converterFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullFilterConverterHelper() {
        new GeneProductController(
                geneProductService,
                geneProductSearchService,
                geneProductRetrievalConfig,
                validationHelper,
                null);
    }

    private String createOversizedCSVRequest() {
        String delim = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ControllerValidationHelperImpl.MAX_PAGE_RESULTS + 1; i++) {
            sb.append(delim).append("A0A").append(i);
            delim = ",";
        }
        return sb.toString();
    }

    private SearchServiceConfig.GeneProductCompositeRetrievalConfig createStubGeneProductCompositeRetrievalConfig() {
        return new SearchServiceConfig.GeneProductCompositeRetrievalConfig() {
            @Override public Map<String, String> repo2DomainFieldMap() {
                return Collections.emptyMap();
            }

            @Override public List<String> getSearchReturnedFields() {
                return Collections.emptyList();
            }

            @Override public String getHighlightStartDelim() {
                return "<hl>";
            }

            @Override public String getHighlightEndDelim() {
                return "</hl>";
            }
        };
    }

    private GeneProduct createGeneProduct(String id) {
        GeneProduct geneProduct = new GeneProduct();
        geneProduct.id = id;
        return geneProduct;
    }
}
