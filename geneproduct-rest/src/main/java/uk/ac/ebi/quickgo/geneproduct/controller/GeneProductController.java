package uk.ac.ebi.quickgo.geneproduct.controller;

import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.GeneProductService;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import io.swagger.annotations.ApiOperation;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tony Wardell
 * Date: 29/03/2016
 * Time: 10:09
 *
 * Provides RESTful endpoints for retrieving Gene Product Information
 *
 * Created with IntelliJ IDEA.
 */
@RestController
@RequestMapping(value = "/QuickGO/services/geneproduct")
public class GeneProductController {
	private static final int MAX_PAGE_RESULTS = 100;

	private final Logger LOGGER = LoggerFactory.getLogger(GeneProductController.class);

	private final GeneProductService geneProductService;
	private final ControllerValidationHelper controllerValidationHelper;

	@Autowired
	public GeneProductController(GeneProductService gpService) {
		Objects.requireNonNull(gpService, "The GeneProductService instance passed to the constructor of " +
				"GeneProductController should not be null.");
		this.geneProductService = gpService;
		this.controllerValidationHelper = new ControllerValidationHelperImpl(MAX_PAGE_RESULTS);
	}

	/**
	 * An empty or unknown path should result in a bad request
	 *
	 * @return a 400 response
	 */
	@ApiOperation(value = "Catches any bad requests and returns an error response with a 400 status")
	@RequestMapping(value = "/*", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<ResponseExceptionHandler.ErrorInfo> emptyId() {
		throw new IllegalArgumentException("The requested end-point does not exist.");
	}

	/**
	 * Get core information about a list of gene products in comma-separated-value (CSV) format
	 *
	 * @param ids gene product identifiers in CSV format
	 * @return
	 * <ul>
	 *     <li>all ids are valid: response consists of a 200 with the chosen information about the gene product ids</li>
	 *     <li>any id is not found: response returns 404</li>
	 *     <li>any id is of the an invalid format: response returns 400</li>
	 * </ul>
	 */
	@RequestMapping(value = "/{ids}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<QueryResult<GeneProduct>> findById(@PathVariable String ids) {
		return getGeneProductResponse(geneProductService.findById(controllerValidationHelper.validateCSVIds(ids)));
	}

	/**
	 * Creates a {@link ResponseEntity} containing a {@link QueryResult} for a list of documents.
	 *
	 * @param docList a list of results
	 * @return a {@link ResponseEntity} containing a {@link QueryResult} for a list of documents
	 */
	private ResponseEntity<QueryResult<GeneProduct>> getGeneProductResponse(List<GeneProduct> docList) {
		QueryResult.Builder<GeneProduct> builder;
		if (docList == null) {
			builder = new QueryResult.Builder<>(0, Collections.emptyList());
		} else {
 			builder = new QueryResult.Builder<>(docList.size(), docList);
		}
		return new ResponseEntity<>(builder.build(), HttpStatus.OK);
	}
}
