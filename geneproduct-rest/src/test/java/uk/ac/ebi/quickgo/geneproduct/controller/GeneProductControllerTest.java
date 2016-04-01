package uk.ac.ebi.quickgo.geneproduct.controller;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.GeneProductService;
import uk.ac.ebi.quickgo.rest.search.ControllerHelper;
import uk.ac.ebi.quickgo.rest.search.ControllerHelperImpl;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

/**
 * @Author Tony Wardell
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
	private ControllerHelper controllerHelper;

	@Mock
	private GeneProduct geneProduct;

	@Mock
	private GeneProduct geneProduct2;

	@Mock
	private GeneProduct geneProduct3;

	static final String SINGLE_CSV = "A0A000";
	static final String MULTI_CSV = "A0A000,A0A001,A0A002";
	static final String NOTFOUND_CSV = "Butter";


	@Before
	public void setUp() {
		this.controller = new GeneProductController(geneProductService, controllerHelper);

		//Lookup for single Id
		final List<String> singleId = Arrays.asList(SINGLE_CSV);
		final List<GeneProduct> singleGP = Arrays.asList(geneProduct);
		when(geneProductService.findById(singleId)).thenReturn(singleGP);

		//Lookup for multi Id
		final List<String> multiIds = Arrays.asList(MULTI_CSV);
		final List<GeneProduct> multiGP = Arrays.asList(geneProduct, geneProduct2, geneProduct3);
		when(geneProductService.findById(multiIds)).thenReturn(multiGP);


		//stub behaviour for controller helper
		when(controllerHelper.csvToList(SINGLE_CSV)).thenReturn(singleId);
		when(controllerHelper.csvToList(MULTI_CSV)).thenReturn(multiIds);
	}

	@Test
	public void checkValidId() {
		controller.checkValidId("id0");
	}

	@Test
	public void validatesValidRequestedResults() {
		controller.validateRequestedResults(GeneProductController.MAX_PAGE_RESULTS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void validatesInvalidRequestedResults() {
		controller.validateRequestedResults(GeneProductController.MAX_PAGE_RESULTS + 1);
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
		assertThat(geneProduct , sameInstance(response.getBody().getResults().get(0)));
	}

	@Test
	public void retrieveMultipleGeneProduct() {
		ResponseEntity<QueryResult<GeneProduct>> response = controller.findById(MULTI_CSV);
		assertThat(response.getBody().getResults().size(), is(3));
		assertThat(response.getBody().getResults(), contains(geneProduct, geneProduct2, geneProduct3));
	}

}
