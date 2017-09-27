package uk.ac.ebi.quickgo.geneproduct.controller;

import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProductRequest;
import uk.ac.ebi.quickgo.geneproduct.service.GeneProductService;
import uk.ac.ebi.quickgo.geneproduct.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.ParameterBindingException;
import uk.ac.ebi.quickgo.rest.ParameterException;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.search.DefaultSearchQueryTemplate;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.query.RegularPage;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
@Api(tags = {"gene products"})
@RequestMapping(value = "/geneproduct")
public class GeneProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneProductController.class);

    private final GeneProductService geneProductService;
    private final SearchService<GeneProduct> geneProductSearchService;
    private final ControllerValidationHelper controllerValidationHelper;
    private final DefaultSearchQueryTemplate requestTemplate;
    private final FilterConverterFactory converterFactory;

    @Autowired
    public GeneProductController(
            GeneProductService geneProductService,
            SearchService<GeneProduct> geneProductSearchService,
            SearchServiceConfig.GeneProductCompositeRetrievalConfig geneProductRetrievalConfig,
            ControllerValidationHelper controllerValidationHelper,
            FilterConverterFactory converterFactory) {

        checkArgument(geneProductService != null,
                "The GeneProductService instance passed to the constructor of GeneProductController must not be null.");
        checkArgument(geneProductSearchService != null, "The SearchService<GeneProduct> must not be null.");
        checkArgument(geneProductRetrievalConfig != null, "The GeneProductCompositeRetrievalConfig must not be null");
        checkArgument(controllerValidationHelper != null, "The ControllerValidationHelper must not be null");
        checkArgument(converterFactory != null, "The FilterConverterFactory must not be null");

        this.geneProductService = geneProductService;
        this.geneProductSearchService = geneProductSearchService;
        this.controllerValidationHelper = controllerValidationHelper;
        this.converterFactory = converterFactory;

        this.requestTemplate = new DefaultSearchQueryTemplate();
        this.requestTemplate.setReturnedFields(geneProductRetrievalConfig.getSearchReturnedFields());
        this.requestTemplate.setHighlighting(geneProductRetrievalConfig.repo2DomainFieldMap().keySet(),
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
        throw new ParameterException("The requested end-point does not exist.");
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
     * @param request an object that wraps all possible configurations for this endpoint
     * @return the search results
     */
    @RequestMapping(value = "/search", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<GeneProduct>> geneProductSearch(
            @Valid @ModelAttribute GeneProductRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ParameterBindingException(bindingResult);
        }

        DefaultSearchQueryTemplate.Builder requestBuilder = requestTemplate.newBuilder()
                .setQuery(request.createQuery())
                .addFacets(request.getFacet() == null ? null : Arrays.asList(request.getFacet()))
                .addFilters(convertFilterRequestsToQueries(request.createFilterRequests()))
                .useHighlighting(request.isHighlighting())
                .setPage(new RegularPage(request.getPage(), request.getLimit()));

        return search(requestBuilder.build(), geneProductSearchService);
    }

    private List<QuickGOQuery> convertFilterRequestsToQueries(List<FilterRequest> filterRequests) {
        return filterRequests.stream()
                .map(converterFactory::convert)
                .filter(Objects::nonNull)
                .map(ConvertedFilter::getConvertedValue)
                .collect(Collectors.toList());
    }

    /**
     * Perform a lookup of gene products associated to a target set.
     * @param name name of target set
     * @return lookup results
     */
    @RequestMapping(value = "/targetset/{name}", method = {RequestMethod.GET}, produces = {MediaType
            .APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<GeneProduct>> findByTargetSet(@PathVariable String name) {
        return getGeneProductResponse(geneProductService.findByTargetSet(name));
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
