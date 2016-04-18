package uk.ac.ebi.quickgo.geneproduct.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.GeneProductService;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(GeneProductController.class);

	public static final int MAX_PAGE_RESULTS = 100;
	private static final String DEFAULT_ENTRIES_PER_PAGE = "25";
	private static final String DEFAULT_PAGE_NUMBER = "1";

	private final GeneProductService geneProductService;
	private final SearchServiceConfig.GeneProductCompositeRetrievalConfig geneProductRetrievalConfig;
	private final SearchService<GeneProduct> geneProductSearchService;
	private final SearchableField geneProductSearchableField;
	private final StringToQuickGOQueryConverter geneProductQueryConverter;
	private final ControllerValidationHelper controllerValidationHelper;

	@Autowired
	public GeneProductController(
			GeneProductService geneProductService,
			SearchService<GeneProduct> geneProductSearchService,
			SearchableField geneProductSearchableField,
			SearchServiceConfig.GeneProductCompositeRetrievalConfig geneProductRetrievalConfig) {
		Objects.requireNonNull(geneProductService, "The GeneProductService instance passed to the constructor of " +
				"GeneProductController should not be null.");

		this.geneProductService = geneProductService;
		this.geneProductRetrievalConfig = geneProductRetrievalConfig;
		this.geneProductSearchService = geneProductSearchService;
		this.geneProductSearchableField = geneProductSearchableField;
		this.geneProductQueryConverter = new StringToQuickGOQueryConverter(geneProductSearchableField);
		this.controllerValidationHelper = new ControllerValidationHelperImpl(MAX_PAGE_RESULTS);
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
	 * Perform a custom client search
	 *
	 * @param query the user query
	 * @param limit number of entries per page
	 * @param page which page number of entries to retrieve
	 * @param filterQueries an optional list of filter queries
	 * @param facets an optional list of facet fields
	 * @param highlighting whether or not to highlight the search results
     * @return the search results
     */
	@RequestMapping(value = "/search", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<QueryResult<GeneProduct>> geneProductSearch(
			@RequestParam(value = "query") String query,
			@RequestParam(value = "limit", defaultValue = DEFAULT_ENTRIES_PER_PAGE) int limit,
			@RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "filterQuery", required = false) List<String> filterQueries,
			@RequestParam(value = "facet", required = false) List<String> facets,
			@RequestParam(value = "highlighting", required = false) boolean highlighting) {

		DefaultSearchQueryRequestBuilder requestBuilder = new DefaultSearchQueryRequestBuilder(
				query,
				geneProductQueryConverter,
				geneProductSearchableField,
				geneProductRetrievalConfig.getSearchReturnedFields(),
				geneProductRetrievalConfig.repo2DomainFieldMap().keySet(),
				geneProductRetrievalConfig.getHighlightStartDelim(),
				geneProductRetrievalConfig.getHighlightEndDelim())

				.addFacets(facets)
				.addFilters(filterQueries)
				.useHighlighting(highlighting)
				.setPage(page)
				.setPageSize(limit);

		return search(requestBuilder.build(), geneProductSearchService);
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
