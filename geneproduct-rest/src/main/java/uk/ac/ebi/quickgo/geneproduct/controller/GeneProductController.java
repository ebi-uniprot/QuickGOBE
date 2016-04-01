package uk.ac.ebi.quickgo.geneproduct.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.GeneProductService;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.ControllerHelper;
import uk.ac.ebi.quickgo.rest.search.ControllerHelperImpl;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 29/03/2016
 * Time: 10:09
 *
 * Provides RESTful endpoints for retrieving Gene Product Information
 *
 * Created with IntelliJ IDEA.
 */
public class GeneProductController {

	Logger LOGGER = LoggerFactory.getLogger(GeneProductController.class);
	static final int MAX_PAGE_RESULTS = 100;
	private static final String RESOURCE_PATH = "geneproducts";

	private final ControllerHelper controllerHelper;
	private final GeneProductService geneProductService;

	public GeneProductController(GeneProductService gpService, ControllerHelper controllerHelper) {
		this.geneProductService = gpService;
		this.controllerHelper = controllerHelper;
	}

	/**
	 * An empty or unknown path should result in a bad request
	 *
	 * @return a 400 response
	 */
	@RequestMapping(value = "/*", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<ResponseExceptionHandler.ErrorInfo> emptyId() {
		throw new IllegalArgumentException("The requested end-point does not exist.");
	}


	/**
	 * Get core information about a list of gene products in comma-separated-value (CSV) format
	 *
	 * @param ids gene product identifiers in CSV format
	 * @return
	 * <ul>
	 *     <li>all ids are valid: response consists of a 200 with the chosen information about the ontology terms</li>
	 *     <li>any id is not found: response returns 404</li>
	 *     <li>any id is of the an invalid format: response returns 400</li>
	 * </ul>
	 */
	@RequestMapping(value = RESOURCE_PATH + "/{ids}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<QueryResult<GeneProduct>> findById(@PathVariable(value = "ids") String ids) {
		return getGeneProductResponse(geneProductService.findById(validateIds(ids)));
	}


	/**
	 * Creates a {@link ResponseEntity} containing a {@link QueryResult} for a list of documents.
	 *
	 * @param docList a list of results
	 * @return a {@link ResponseEntity} containing a {@link QueryResult} for a list of documents
	 */
	protected ResponseEntity<QueryResult<GeneProduct>> getGeneProductResponse(List<GeneProduct> docList) {
		List<GeneProduct> resultsToShow;
		if (docList == null) {
			resultsToShow = Collections.emptyList();
		} else {
			resultsToShow = docList;
		}

		QueryResult<GeneProduct> queryResult = new QueryResult<>(resultsToShow.size(), resultsToShow, null, null, null);
		return new ResponseEntity<>(queryResult, HttpStatus.OK);
	}


	/**
	 * Checks the validity of a list of IDs in CSV format.
	 * @param ids a list of IDs in CSV format
	 * @throws IllegalArgumentException is thrown if an ID is not valid, or if
	 * number of IDs listed is greater than {@link #MAX_PAGE_RESULTS}.
	 */
	protected java.util.List<String> validateIds(String ids) {
		java.util.List<String> idList =  controllerHelper.csvToList(ids);
//		validateRequestedResults(idList.size());
//		idList
//				.stream()
//				.forEach(this::checkValidId);

		return idList;
	}

	/**
	 * Checks whether the requested number of results is valid.
	 * @param requestedResultsSize the number of results being requested
	 * @throws IllegalArgumentException if the number is greater than {@link #MAX_PAGE_RESULTS}
	 */
	protected void validateRequestedResults(int requestedResultsSize) {
		if (requestedResultsSize > MAX_PAGE_RESULTS) {
			String errorMessage = "Cannot retrieve more than " + MAX_PAGE_RESULTS + " results in one request. " +
					"Please consider using end-points that return paged results.";
			LOGGER.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
	}


	/**
	 * Checks the validity of a geneproduct id.
	 *
	 * @param id the term id to check
	 * @throws IllegalArgumentException is thrown if the ID is not valid
	 */
	protected void checkValidId(String id) {
		if (!isValidId(id)) {
			throw new IllegalArgumentException("Provided ID: '" + id + "' is invalid");
		}
	}

	/**
	 * Are there any requirements for the validity of a gene product id
	 * @param id
	 * @return
	 */
	protected boolean isValidId(String id) {
		return true;
	}


}
