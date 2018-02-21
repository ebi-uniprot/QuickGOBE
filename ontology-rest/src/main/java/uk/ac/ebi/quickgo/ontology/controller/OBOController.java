package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.graphics.model.GraphImageLayout;
import uk.ac.ebi.quickgo.ontology.model.GraphRequest;
import uk.ac.ebi.quickgo.graphics.ontology.GraphPresentation;
import uk.ac.ebi.quickgo.graphics.ontology.RenderingGraphException;
import uk.ac.ebi.quickgo.graphics.service.GraphImageService;
import uk.ac.ebi.quickgo.ontology.OntologyRestConfig;
import uk.ac.ebi.quickgo.ontology.common.OntologyFields;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;
import uk.ac.ebi.quickgo.ontology.model.OntologySpecifier;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorGraph;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorVertex;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.ParameterBindingException;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.headers.HttpHeadersProvider;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.SearchDispatcher;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.StringToQuickGOQueryConverter;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.query.RegularPage;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.http.HttpHeaders.CONTENT_ENCODING;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.DEFAULT_TRAVERSAL_TYPES;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.DEFAULT_TRAVERSAL_TYPES_CSV;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.and;

/**
 * Abstract controller defining common end-points of an OBO related
 * REST API.
 *
 * Created 27/11/15
 * @author Edd
 */
public abstract class OBOController<T extends OBOTerm> {
    static final String TERMS_RESOURCE = "terms";
    static final String SEARCH_RESOUCE = "search";

    static final String COMPLETE_SUB_RESOURCE = "complete";
    static final String HISTORY_SUB_RESOURCE = "history";
    static final String XREFS_SUB_RESOURCE = "xrefs";
    static final String CONSTRAINTS_SUB_RESOURCE = "constraints";
    static final String XRELATIONS_SUB_RESOURCE = "xontologyrelations";
    static final String GUIDELINES_SUB_RESOURCE = "guidelines";
    static final String ANCESTORS_SUB_RESOURCE = "ancestors";
    static final String DESCENDANTS_SUB_RESOURCE = "descendants";
    static final String PATHS_SUB_RESOURCE = "paths";
    static final String CHART_SUB_RESOURCE = "chart";
    static final String CHART_COORDINATES_SUB_RESOURCE = CHART_SUB_RESOURCE + "/coords";
    static final String BASE_64_CONTENT_ENCODING = "base64";

    private static final Logger LOGGER = LoggerFactory.getLogger(OBOController.class);
    private static final String COLON = ":";
    private static final String DEFAULT_ENTRIES_PER_PAGE = "25";
    private static final String DEFAULT_PAGE_NUMBER = "1";
    private static final String PNG = "png";

    final OntologyService<T> ontologyService;
    final OBOControllerValidationHelper validationHelper;
    private final SearchService<OBOTerm> ontologySearchService;
    private final StringToQuickGOQueryConverter ontologyQueryConverter;
    private final SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig;
    private final GraphImageService graphImageService;
    private final OntologyRestConfig.OntologyPagingConfig ontologyPagingConfig;
    private final OntologySpecifier ontologySpecifier;
    private final HttpHeadersProvider httpHeadersProvider;

    public OBOController(OntologyService<T> ontologyService,
            SearchService<OBOTerm> ontologySearchService,
            SearchableField searchableField,
            SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig,
            GraphImageService graphImageService,
            OBOControllerValidationHelper oboControllerValidationHelper,
            OntologyRestConfig.OntologyPagingConfig ontologyPagingConfig,
            OntologySpecifier ontologySpecifier,
            HttpHeadersProvider httpHeadersProvider) {
        checkArgument(ontologyService != null, "Ontology service cannot be null");
        checkArgument(ontologySearchService != null, "Ontology search service cannot be null");
        checkArgument(searchableField != null, "Ontology searchable field cannot be null");
        checkArgument(ontologyRetrievalConfig != null, "Ontology retrieval configuration cannot be null");
        checkArgument(graphImageService != null, "Graph image service cannot be null");
        checkArgument(oboControllerValidationHelper != null, "OBO validation helper cannot be null");
        checkArgument(ontologyPagingConfig != null, "Paging config cannot be null");
        checkArgument(ontologySpecifier != null, "Ontology specifier cannot be null");
        checkArgument(httpHeadersProvider != null, "Http Headers Provider cannot be null");

        this.ontologyService = ontologyService;
        this.ontologySearchService = ontologySearchService;
        this.ontologyQueryConverter = new StringToQuickGOQueryConverter(searchableField);
        this.ontologyRetrievalConfig = ontologyRetrievalConfig;
        this.validationHelper = oboControllerValidationHelper;
        this.graphImageService = graphImageService;
        this.ontologyPagingConfig = ontologyPagingConfig;
        this.ontologySpecifier = ontologySpecifier;
        this.httpHeadersProvider = httpHeadersProvider;
    }

    /**
     * An empty or unknown path should result in a bad request
     *
     * @return a 400 response
     */
    @ApiOperation(value = "Catches any bad requests and returns an error response with a 400 status", hidden = true)
    @RequestMapping(value = "/*", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseExceptionHandler.ErrorInfo> emptyId() {
        throw new IllegalArgumentException("The requested end-point does not exist.");
    }

    /**
     * Get information about all terms and page through the results.
     *
     * @param page the page number of results to retrieve
     * @return the specified page of results as a {@link QueryResult} instance or a 400 response
     *          if the page number is invalid
     */
    @ApiOperation(value = "Get information on all terms and page through the results")
    @RequestMapping(value = "/" + TERMS_RESOURCE, method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> baseUrl(
            @ApiParam(value = "The results page to retrieve")
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page) {

        return new ResponseEntity<>(ontologyService.findAllByOntologyType
                (this.ontologySpecifier.ontologyType,
                        new RegularPage(page, ontologyPagingConfig.defaultPageSize())),
                httpHeadersProvider.provide(),
                HttpStatus.OK);
    }

    /**
     * Get core information about a list of terms in comma-separated-value (CSV) format
     *
     * @param ids ontology identifiers in CSV format
     * @return
     * <ul>
     *     <li>all ids are valid: response consists of a 200 with the chosen information about the ontology terms</li>
     *     <li>any id is not found: response returns 200 with an empty result set.</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get core information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, ancestors, synonyms, " +
                    "comment, aspect (for GO) and usage.")
    @RequestMapping(value = TERMS_RESOURCE + "/{ids}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsCoreAttr(
            @ApiParam(value = "Comma-separated term IDs", required = true) @PathVariable(value = "ids") String ids) {
        return getResultsResponse(ontologyService.findCoreInfoByOntologyId(validationHelper.validateCSVIds(ids)));
    }

    /**
     * Get complete information about a list of terms in comma-separated-value (CSV) format
     *
     * @param ids ontology identifiers in CSV format
     * @return
     * <ul>
     *     <li>all ids are valid: response consists of a 200 with the chosen information about the ontology terms</li>
     *     <li>any id is not found: response returns 200 with an empty result set.</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get complete information about a (CSV) list of terms based on their ids",
            notes = "All fields will be populated providing they have a value.")
    @RequestMapping(value = TERMS_RESOURCE + "/{ids}/" + COMPLETE_SUB_RESOURCE, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsComplete(
            @ApiParam(value = "Comma-separated term IDs", required = true) @PathVariable(value = "ids") String ids) {
        return getResultsResponse(
                ontologyService.findCompleteInfoByOntologyId(validationHelper.validateCSVIds(ids)));
    }

    /**
     * Get history information about a list of terms in comma-separated-value (CSV) format
     *
     * @param ids ontology identifiers in CSV format
     * @return
     * <ul>
     *     <li>all ids are valid: response consists of a 200 with the chosen information about the ontology terms</li>
     *     <li>any id is not found: response returns 200 with an empty result set.</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get history information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, history.")
    @RequestMapping(value = TERMS_RESOURCE + "/{ids}/" + HISTORY_SUB_RESOURCE, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsHistory(
            @ApiParam(value = "Comma-separated term IDs", required = true) @PathVariable(value = "ids") String ids) {

        return getResultsResponse(
                ontologyService.findHistoryInfoByOntologyId(validationHelper.validateCSVIds(ids)));
    }

    /**
     * Get cross-reference information about a list of terms in comma-separated-value (CSV) format
     *
     * @param ids ontology identifiers in CSV format
     * @return
     * <ul>
     *     <li>all ids are valid: response consists of a 200 with the chosen information about the ontology terms</li>
     *     <li>any id is not found: response returns 200 with an empty result set.</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get cross-reference information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, comment, xRefs.")
    @RequestMapping(value = TERMS_RESOURCE + "/{ids}/" + XREFS_SUB_RESOURCE, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsXRefs(
            @ApiParam(value = "Comma-separated term IDs", required = true) @PathVariable(value = "ids") String ids) {
        return getResultsResponse(
                ontologyService.findXRefsInfoByOntologyId(validationHelper.validateCSVIds(ids)));
    }

    /**
     * Get taxonomy constraint information about a list of terms in comma-separated-value (CSV) format
     *
     * @param ids ontology identifiers in CSV format
     * @return
     * <ul>
     *     <li>all ids are valid: response consists of a 200 with the chosen information about the ontology terms</li>
     *     <li>any id is not found: response returns 200 with an empty result set.</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get taxonomy constraint information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, taxonConstraints, " +
                    "blacklist.")
    @RequestMapping(value = TERMS_RESOURCE + "/{ids}/" + CONSTRAINTS_SUB_RESOURCE, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsTaxonConstraints(
            @ApiParam(value = "Comma-separated term IDs", required = true) @PathVariable(value = "ids") String ids) {
        return getResultsResponse(
                ontologyService.findTaxonConstraintsInfoByOntologyId(validationHelper.validateCSVIds(ids)));
    }

    /**
     * Get cross-ontology relationship information about a list of terms in comma-separated-value (CSV) format
     *
     * @param ids ontology identifiers in CSV format
     * @return
     * <ul>
     *     <li>all ids are valid: response consists of a 200 with the chosen information about the ontology terms</li>
     *     <li>any id is not found: response returns 200 with an empty result set.</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get cross ontology relationship information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, comment, xRelations.")
    @RequestMapping(value = TERMS_RESOURCE + "/{ids}/" + XRELATIONS_SUB_RESOURCE, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsXOntologyRelations(
            @ApiParam(value = "Comma-separated term IDs", required = true) @PathVariable(value = "ids") String ids) {
        return getResultsResponse(
                ontologyService.findXORelationsInfoByOntologyId(validationHelper.validateCSVIds(ids)));
    }

    /**
     * Get annotation guideline information about a list of terms in comma-separated-value (CSV) format
     *
     * @param ids ontology identifiers in CSV format
     * @return
     * <ul>
     *     <li>all ids are valid: response consists of a 200 with the chosen information about the ontology terms</li>
     *     <li>any id is not found: response returns 200 with an empty result set.</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get annotation guideline information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, " +
                    "comment, annotationGuidelines.")
    @RequestMapping(value = TERMS_RESOURCE + "/{ids}/" + GUIDELINES_SUB_RESOURCE, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsAnnotationGuideLines(
            @ApiParam(value = "Comma-separated term IDs", required = true) @PathVariable(value = "ids") String ids) {
        return getResultsResponse(ontologyService
                .findAnnotationGuideLinesInfoByOntologyId(validationHelper.validateCSVIds
                        (ids)));
    }

    /**
     * Search for an ontology term via its identifier, or a generic query search
     *
     * @param query the query to search against
     * @param limit the amount of queries to return
     * @return a {@link QueryResult} instance containing the results of the search
     */
    @ApiOperation(value = "Searches a simple user query, e.g., query=apopto",
            notes = "If possible, response fields include: id, name, isObsolete, aspect (for GO)")
    @RequestMapping(value = "/" + SEARCH_RESOUCE, method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<OBOTerm>> ontologySearch(
            @ApiParam(value = "Some value to search for in the ontology") @RequestParam(value = "query") String query,
            @ApiParam(value = "The number of results per page [1-600]")
            @RequestParam(value = "limit", defaultValue = DEFAULT_ENTRIES_PER_PAGE) int limit,
            @ApiParam(value = "The results page to retrieve")
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page) {

        validationHelper.validateRequestedResults(limit);
        validationHelper.validatePageIsLessThanPaginationLimit(page);

        QueryRequest request = buildRequest(
                query,
                limit,
                page,
                ontologyQueryConverter);

        return SearchDispatcher.search(request, ontologySearchService);
    }

    /**
     * Retrieves the ancestors of ontology terms
     * @param ids the term ids in CSV format
     * @param relations the ontology relationships over which ancestors will be found
     * @return a result instance containing the ancestors
     */
    @ApiOperation(value = "Retrieves the ancestors of specified ontology terms")
    @RequestMapping(value = TERMS_RESOURCE + "/{ids}/" + ANCESTORS_SUB_RESOURCE, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findAncestors(
            @ApiParam(value = "Comma-separated term IDs", required = true) @PathVariable(value = "ids") String ids,
            @ApiParam(value = "Comma-separated ontology relationships")
            @RequestParam(value = "relations", defaultValue = DEFAULT_TRAVERSAL_TYPES_CSV) String relations) {
        return getResultsResponse(
                ontologyService.findAncestorsInfoByOntologyId(
                        validationHelper.validateCSVIds(ids),
                        asOntologyRelationTypeArray(validationHelper.validateRelationTypes(relations,
                                DEFAULT_TRAVERSAL_TYPES))));
    }

    /**
     * Retrieves the descendants of ontology terms
     * @param ids the term ids in CSV format
     * @param relations the ontology relationships over which descendants will be found
     * @return a result containing the descendants
     */
    @ApiOperation(value = "Retrieves the descendants of specified ontology terms")
    @RequestMapping(value = TERMS_RESOURCE + "/{ids}/" + DESCENDANTS_SUB_RESOURCE, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findDescendants(
            @ApiParam(value = "Comma-separated term IDs", required = true) @PathVariable(value = "ids") String ids,
            @ApiParam(value = "Comma-separated ontology relationships")
            @RequestParam(value = "relations", defaultValue = DEFAULT_TRAVERSAL_TYPES_CSV) String relations) {
        return getResultsResponse(
                ontologyService.findDescendantsInfoByOntologyId(
                        validationHelper.validateCSVIds(ids),
                        asOntologyRelationTypeArray(validationHelper.validateRelationTypes(relations,
                                DEFAULT_TRAVERSAL_TYPES))));
    }

    /**
     * Retrieves the paths between ontology terms
     * @param ids the term ids in CSV format, from which paths begin
     * @param toIds the term ids in CSV format, to which the paths lead
     * @param relations the ontology relationships over which descendants will be found
     * @return a result containing a list of paths between the {@code ids} terms, and {@code toIds} terms
     */
    @ApiOperation(value = "Retrieves the paths between two specified sets of ontology terms. Each path is " +
            "formed from a list of (term, relationship, term) triples.")
    @RequestMapping(value = TERMS_RESOURCE + "/{ids}/" + PATHS_SUB_RESOURCE + "/{toIds}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<OntologyRelationship>>> findPaths(
            @ApiParam(value = "Comma-separated source term IDs") @PathVariable(value = "ids") String ids,
            @ApiParam(value = "Comma-separated target term IDs") @PathVariable(value = "toIds") String toIds,
            @ApiParam(value = "Comma-separated ontology relationships")
            @RequestParam(value = "relations", defaultValue = DEFAULT_TRAVERSAL_TYPES_CSV) String relations) {
        return getResultsResponse(
                ontologyService.paths(
                        asSet(validationHelper.validateCSVIds(ids)),
                        asSet(validationHelper.validateCSVIds(toIds)),
                        asOntologyRelationTypeArray(validationHelper.validateRelationTypes(relations,
                                DEFAULT_TRAVERSAL_TYPES))
                ));
    }

    /**
     * Retrieves the graphical image corresponding to ontology terms.
     *
     * @return the image corresponding to the requested term ids
     */
    @ApiOperation(value = "Retrieves the PNG image corresponding to the specified ontology terms")
    @RequestMapping(value = TERMS_RESOURCE + "/{ids}/" + CHART_SUB_RESOURCE, method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<InputStreamResource> getChart(@Valid @ModelAttribute GraphRequest request, BindingResult
            bindingResult) {
        checkBindingErrors(bindingResult);
        final GraphPresentation graphPresentation = buildGraphPresentation(request);

        try {

            return createChartResponseEntity(validationHelper.validateCSVIds(request.getIds()), request.isBase64(),
                    graphPresentation);
        } catch (IOException | RenderingGraphException e) {
            throw createChartGraphicsException(e);
        }
    }

    /**
     * Retrieves the graphical image coordination information corresponding to ontology terms.
     *
     * @return the coordinate information of the terms in the chart
     */
    @ApiOperation(value = "Retrieves coordinate information about terms within the PNG chart from the " +
            CHART_SUB_RESOURCE + " sub-resource")
    @RequestMapping(value = TERMS_RESOURCE + "/{ids}/" + CHART_COORDINATES_SUB_RESOURCE,
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<GraphImageLayout> getChartCoordinates(@Valid @ModelAttribute GraphRequest request,
            BindingResult bindingResult) {
        checkBindingErrors(bindingResult);
        final GraphPresentation graphPresentation = buildGraphPresentation(request);

        try {
            GraphImageLayout layout = graphImageService
                    .createChart(validationHelper.validateCSVIds(request.getIds()), ontologySpecifier.ontologyType
                            .name(), graphPresentation)
                    .getLayout();
            return ResponseEntity
                    .ok()
                    .body(layout);
        } catch (RenderingGraphException e) {
            throw createChartGraphicsException(e);
        }
    }

    /**
     * Retrieves a sub-graph of the ontology graph between two sets of provided term ids
     *
     * @param startIds the term ids from which to the sub-graph should begin
     * @param stopIds the term ids that indicate the ending of the sub-graph
     * @return the specified ontology sub-graph
     */
    @ApiOperation(value = "Fetches a sub-graph of the ontology")
    @RequestMapping(value = TERMS_RESOURCE + "/graph", method = RequestMethod.GET, produces = {MediaType
            .APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<AncestorGraph>> getGraph(
            @ApiParam(value = "Comma-separated term IDs specifying the beginning of the sub-graph")
            @RequestParam(value = "startIds") String startIds,
            @ApiParam(value = "Comma-separated term IDs specifying the end of the sub-graph")
            @RequestParam(value = "stopIds", required = false) String stopIds,
            @ApiParam(value = "Comma-separated relationships over which the graph will navigate")
            @RequestParam(value = "relations", required = false) String relations) {
        final AncestorGraph<AncestorVertex> ancestorGraph = ontologyService.findOntologySubGraphById(
                asSet(validationHelper.validateCSVIds(startIds)),
                asSet(validationHelper.validateCSVIds(stopIds)),
                validRelations(relations).toArray(new OntologyRelationType[]{}));

        if (ancestorGraph.vertices.size() == 0 && ancestorGraph.edges.size() == 0) {
            return getResultsResponse(Collections.emptyList());
        } else {
            return getResultsResponse(Collections.singletonList(ancestorGraph));
        }
    }

    private List<OntologyRelationType> validRelations(String relations) {
        return Objects.isNull(relations) || relations.isEmpty() ? this.ontologySpecifier.allowedRelations :
                validationHelper.validateRelationTypes(relations, this.ontologySpecifier.allowedRelations);
    }

    /**
     * Wrap a collection as a {@link Set}
     * @param items      the items to wrap as a {@link Set}
     * @param <ItemType> the type of the {@link Collection}, i.e., this method works for any type
     * @return a {@link Set} wrapping the items in a {@link Collection}
     */
    private static <ItemType> Set<ItemType> asSet(Collection<ItemType> items) {
        return new HashSet<>(items);
    }

    /**
     * Converts a {@link Collection} of {@link OntologyRelationType}s to a corresponding array of
     * {@link OntologyRelationType}s
     * @param relations the {@link OntologyRelationType}s
     * @return an array of {@link OntologyRelationType}s
     */
    static OntologyRelationType[] asOntologyRelationTypeArray(Collection<OntologyRelationType> relations) {
        return relations.toArray(new OntologyRelationType[relations.size()]);
    }

    /**
     * Creates a {@link ResponseEntity} containing a {@link QueryResult} for a list of results.
     *
     * @param results a list of results
     * @return a {@link ResponseEntity} containing a {@link QueryResult} for a list of results
     */
    <ResponseType> ResponseEntity<QueryResult<ResponseType>> getResultsResponse(List<ResponseType> results) {
        List<ResponseType> resultsToShow;
        if (results == null) {
            resultsToShow = Collections.emptyList();
        } else {
            resultsToShow = results;
        }

        QueryResult<ResponseType> queryResult = new QueryResult.Builder<>(resultsToShow.size(), resultsToShow).build();
        return new ResponseEntity<>(queryResult, httpHeadersProvider.provide(), HttpStatus.OK);
    }

    private RetrievalException createChartGraphicsException(Throwable throwable) {
        String errorMessage = "Error encountered during creation of ontology chart graphics.";
        LOGGER.error(errorMessage, throwable);
        return new RetrievalException(errorMessage);
    }

    /**
     * Delegates the creation of an graphical image, corresponding to the specified list
     * of {@code ids} and returns the appropriate {@link ResponseEntity}.
     *
     * @param ids the terms whose corresponding graphical image is required
     * @param base64 whether or not to encode the image as base64
     * @param graphPresentation defines the look and attributes of the rendered graph
     * @return the image corresponding to the specified terms
     * @throws IOException if there is an error during creation of the image {@link InputStreamResource}
     * @throws RenderingGraphException if there was an error during the rendering of the image
     */
    private ResponseEntity<InputStreamResource> createChartResponseEntity(List<String> ids, boolean base64,
            GraphPresentation graphPresentation)
            throws IOException, RenderingGraphException {

        RenderedImage renderedImage =
                graphImageService
                        .createChart(ids, ontologySpecifier.ontologyType.name(), graphPresentation)
                        .getGraphImage()
                        .render();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ResponseEntity.BodyBuilder bodyBuilder;
        if (base64) {
            ImageIO.write(renderedImage, PNG, Base64.getMimeEncoder().wrap(os));
            bodyBuilder = buildChartResponseBodyBuilder(os).header(CONTENT_ENCODING, BASE_64_CONTENT_ENCODING);
        } else {
            ImageIO.write(renderedImage, PNG, os);
            bodyBuilder = buildChartResponseBodyBuilder(os);
        }

        return bodyBuilder.body(new InputStreamResource(new ByteArrayInputStream(os.toByteArray())));
    }

    private ResponseEntity.BodyBuilder buildChartResponseBodyBuilder(ByteArrayOutputStream os) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(os.size());
    }

    private QueryRequest buildRequest(String query,
            int limit,
            int page,
            StringToQuickGOQueryConverter converter) {

        QuickGOQuery userQuery = converter.convert(query);
        QuickGOQuery restrictedUserQuery = restrictQueryToOTypeResults(userQuery);

        QueryRequest.Builder builder = new QueryRequest
                .Builder(restrictedUserQuery)
                .setPage(new RegularPage(page, limit));

        if (!ontologyRetrievalConfig.getSearchReturnedFields().isEmpty()) {
            ontologyRetrievalConfig.getSearchReturnedFields()
                    .forEach(builder::addProjectedField);
        }

        return builder.build();
    }

    /**
     * Given a {@link QuickGOQuery}, create a composite {@link QuickGOQuery} by
     * performing a conjunction with another query, which restricts all results
     * to be of a type corresponding to ontology type}.
     *
     * @param query the query that is constrained
     * @return the new constrained query
     */
    private QuickGOQuery restrictQueryToOTypeResults(QuickGOQuery query) {
        return and(query,
                ontologyQueryConverter.convert(
                        OntologyFields.Searchable.ONTOLOGY_TYPE + COLON + ontologySpecifier.ontologyType.name()));
    }

    private void checkBindingErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ParameterBindingException(bindingResult);
        }
    }

    /**
     * Map the values of the graph presentational parameters to an instance of GraphPresentation
     * @param request holds the stylistic parameters requested by the client
     * @return GraphPresentation instance
     */
    private GraphPresentation buildGraphPresentation(GraphRequest request) {
        GraphPresentation.Builder presentationBuilder = new GraphPresentation.Builder();
        presentationBuilder.showKey(request.isShowKey());
        presentationBuilder.showIDs(request.isShowIds());
        presentationBuilder.termBoxWidth(request.getTermBoxWidth());
        presentationBuilder.termBoxHeight(request.getTermBoxHeight());
        presentationBuilder.showSlimColours(request.isShowSlimColours());
        presentationBuilder.showChildren(request.isShowChildren());
        return presentationBuilder.build();
    }
}
