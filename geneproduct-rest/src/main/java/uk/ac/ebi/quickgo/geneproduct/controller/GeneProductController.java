package uk.ac.ebi.quickgo.geneproduct.controller;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.GeneProductService;
import uk.ac.ebi.quickgo.geneproduct.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.search.DefaultSearchQueryTemplate;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.StringToQuickGOQueryConverter;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import io.swagger.annotations.ApiOperation;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.google.common.base.Preconditions.checkArgument;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.search;

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
@RequestMapping(value = "/geneproduct")
public class GeneProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneProductController.class);
    private static final String DEFAULT_ENTRIES_PER_PAGE = "25";
    private static final String DEFAULT_PAGE_NUMBER = "1";

    private final GeneProductService geneProductService;
    private final SearchService<GeneProduct> geneProductSearchService;
    private final ControllerValidationHelper controllerValidationHelper;
    private final DefaultSearchQueryTemplate requestTemplate;

    @Autowired
    public GeneProductController(
            GeneProductService geneProductService,
            SearchService<GeneProduct> geneProductSearchService,
            SearchableField geneProductSearchableField,
            SearchServiceConfig.GeneProductCompositeRetrievalConfig geneProductRetrievalConfig,
            ControllerValidationHelper controllerValidationHelper) {
        checkArgument(geneProductService != null,
                "The GeneProductService instance passed to the constructor of GeneProductController must not be null.");
        checkArgument(geneProductSearchService != null, "The SearchService<GeneProduct> must not be null.");
        checkArgument(geneProductSearchableField != null, "The gene product SearchableField must not be null");
        checkArgument(geneProductRetrievalConfig != null, "The GeneProductCompositeRetrievalConfig must not be null");
        checkArgument(controllerValidationHelper != null, "The ControllerValidationHelper must not be null");

        this.geneProductService = geneProductService;
        this.geneProductSearchService = geneProductSearchService;
        this.controllerValidationHelper = controllerValidationHelper;

        this.requestTemplate = new DefaultSearchQueryTemplate(
                new StringToQuickGOQueryConverter(geneProductSearchableField),
                geneProductSearchableField,
                geneProductRetrievalConfig.getSearchReturnedFields(),
                geneProductRetrievalConfig.repo2DomainFieldMap().keySet(),
                geneProductRetrievalConfig.getHighlightStartDelim(),
                geneProductRetrievalConfig.getHighlightEndDelim());
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
     *     <li>any id is not found: response returns 200 with an empty result set.</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @RequestMapping(value = "/{ids}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
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

        DefaultSearchQueryTemplate.Builder requestBuilder = requestTemplate.newBuilder()
                .setQuery(query)
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
