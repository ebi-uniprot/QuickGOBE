package uk.ac.ebi.quickgo.geneproduct.controller;

import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.GeneProductService;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

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

	@Autowired
	public GeneProductController(GeneProductService gpService) {
		Objects.requireNonNull(gpService, "The GeneProductService instance passed to the constructor of " +
				"GeneProductController should not be null.");
		this.geneProductService = gpService;
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
	@RequestMapping(value = "/{ids}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<QueryResult<GeneProduct>> findById(@PathVariable String[] ids) {
		validateRequestedResults(ids.length);
		return getGeneProductResponse(geneProductService.findById(ids));
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

	/**
	 * Checks whether the requested number of results is valid.
	 * @param requestedResultsSize the number of results being requested
	 * @throws IllegalArgumentException if the number is greater than {@link #MAX_PAGE_RESULTS}
	 */
	private void validateRequestedResults(int requestedResultsSize) {
		if (requestedResultsSize > MAX_PAGE_RESULTS) {
			String errorMessage = "Cannot retrieve more than " + MAX_PAGE_RESULTS + " results in one request. " +
					"Please consider using end-points that return paged results.";
			LOGGER.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
	}
}
