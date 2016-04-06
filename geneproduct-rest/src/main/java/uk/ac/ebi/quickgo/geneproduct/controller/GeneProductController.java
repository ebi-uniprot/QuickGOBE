package uk.ac.ebi.quickgo.geneproduct.controller;

import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.GeneProductService;
import uk.ac.ebi.quickgo.geneproduct.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.ControllerHelper;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.SearchableField;
import uk.ac.ebi.quickgo.rest.search.StringToQuickGOQueryConverter;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.isValidFacets;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.isValidFilterQueries;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.search;

/**
 * Provides RESTful end-points for retrieving gene product information.
 *
 * Created 29/03/2016
 * @author Tony Wardell, Edd
 */
@RestController
@RequestMapping(value = "/QuickGO/services/geneproduct")
public class GeneProductController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeneProductController.class);

	public static final int MAX_PAGE_RESULTS = 100;
	private static final String DEFAULT_ENTRIES_PER_PAGE = "25";
	private static final String DEFAULT_PAGE_NUMBER = "1";

	private final ControllerHelper controllerHelper;
	private final GeneProductService geneProductService;
	private final SearchServiceConfig.GeneProductCompositeRetrievalConfig geneProductRetrievalConfig;
	private final SearchService<GeneProduct> geneProductSearchService;
	private final SearchableField geneProductSearchableField;
	private final StringToQuickGOQueryConverter geneProductQueryConverter;

	@Autowired
	public GeneProductController(
			GeneProductService gpService,
			ControllerHelper controllerHelper,
			SearchService<GeneProduct> geneProductSearchService,
			SearchableField geneProductSearchableField,
			SearchServiceConfig.GeneProductCompositeRetrievalConfig geneProductRetrievalConfig) {
		this.geneProductService = gpService;
		this.controllerHelper = controllerHelper;
		this.geneProductRetrievalConfig = geneProductRetrievalConfig;
		this.geneProductSearchService = geneProductSearchService;
		this.geneProductSearchableField = geneProductSearchableField;
		this.geneProductQueryConverter = new StringToQuickGOQueryConverter(geneProductSearchableField);
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
	@RequestMapping(value = "/{ids}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<QueryResult<GeneProduct>> findById(@PathVariable(value = "ids") String ids) {
		return getGeneProductResponse(geneProductService.findById(validateIds(ids)));
	}

	/**
	 * Method is invoked when a client wants to search for an ontology term via its identifier, or a generic query
	 * search
	 *
	 * @param query the query to search against
	 * @param limit the amount of queries to return
	 */
	@RequestMapping(value = "/search", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<QueryResult<GeneProduct>> geneProductSearch(
			@RequestParam(value = "query") String query,
			@RequestParam(value = "limit", defaultValue = DEFAULT_ENTRIES_PER_PAGE) int limit,
			@RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value = "filterQuery", required = false) List<String> filterQueries,
			@RequestParam(value = "facet", required = false) List<String> facets,
			@RequestParam(value = "highlighting", required = false) boolean highlighting) {
		QueryRequest request = buildRequest(
				query,
				limit,
				page,
				filterQueries,
				facets,
				highlighting,
				geneProductQueryConverter,
				geneProductSearchableField);

		return search(request, geneProductSearchService);
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

	private QueryRequest buildRequest(String query,
			int limit,
			int page,
			List<String> filterQueries,
			List<String> facets,
			boolean highlighting,
			StringToQuickGOQueryConverter converter,
			SearchableField fieldSpec) {

		checkFacets(fieldSpec, facets);
		checkFilters(fieldSpec, filterQueries);

		QueryRequest.Builder builder = new QueryRequest.Builder(converter.convert(query));
		builder.setPageParameters(page, limit);

		if (facets != null) {
			facets.forEach(builder::addFacetField);
		}

		if (filterQueries != null) {
			filterQueries.stream()
					.map(converter::convert)
					.forEach(builder::addQueryFilter);
		}

		if (highlighting) {
			geneProductRetrievalConfig.repo2DomainFieldMap().keySet().stream()
					.forEach(builder::addHighlightedField);
			builder.setHighlightStartDelim(geneProductRetrievalConfig.getHighlightStartDelim());
			builder.setHighlightEndDelim(geneProductRetrievalConfig.getHighlightEndDelim());
		}

		geneProductRetrievalConfig
				.getSearchReturnedFields()
				.stream()
				.forEach(builder::addProjectedField);

		return builder.build();
	}

	private void checkFacets(SearchableField fieldSpec, List<String> facets) {
		if (!isValidFacets(fieldSpec, facets)) {
			throw new IllegalArgumentException("At least one of the provided facets is not searchable: " + facets);
		}
	}

	private void checkFilters(SearchableField fieldSpec, List<String> filterQueries) {
		if (!isValidFilterQueries(fieldSpec, filterQueries)) {
			throw new IllegalArgumentException("At least one of the provided filter queries is not filterable: " +
					filterQueries);
		}
	}
}
