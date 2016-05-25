package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyFields;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelper;
import uk.ac.ebi.quickgo.ontology.controller.validation.OBOControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.SearchDispatcher;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.SearchableField;
import uk.ac.ebi.quickgo.rest.search.StringToQuickGOQueryConverter;
import uk.ac.ebi.quickgo.rest.search.query.Page;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiOperation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.DEFAULT_TRAVERSAL_TYPES_CSV;

/**
 * Abstract controller defining common end-points of an OBO related
 * REST API.
 *
 * Created 27/11/15
 * @author Edd
 */
public abstract class OBOController<T extends OBOTerm> {
    static final int MAX_PAGE_RESULTS = 100;
    private static final Logger LOGGER = LoggerFactory.getLogger(OBOController.class);
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String DEFAULT_ENTRIES_PER_PAGE = "25";
    private static final String DEFAULT_PAGE_NUMBER = "1";
    private static final String TERMS = "terms";
    private final OntologyService<T> ontologyService;
    private final SearchService<OBOTerm> ontologySearchService;
    private final StringToQuickGOQueryConverter ontologyQueryConverter;
    private final SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig;
    private final OBOControllerValidationHelper validationHelper;

    public OBOController(OntologyService<T> ontologyService,
            SearchService<OBOTerm> ontologySearchService,
            SearchableField searchableField,
            SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig) {
        Preconditions.checkArgument(ontologyService != null, "Ontology service cannot be null");
        Preconditions.checkArgument(ontologySearchService != null, "Ontology search service cannot be null");
        Preconditions.checkArgument(searchableField != null, "Ontology searchable field cannot be null");
        Preconditions.checkArgument(ontologyRetrievalConfig != null, "Ontology retrieval configuration cannot be null");

        this.ontologyService = ontologyService;
        this.ontologySearchService = ontologySearchService;
        this.ontologyQueryConverter = new StringToQuickGOQueryConverter(searchableField);
        this.ontologyRetrievalConfig = ontologyRetrievalConfig;
        this.validationHelper = new OBOControllerValidationHelperImpl(MAX_PAGE_RESULTS, idValidator());
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
     * Get all information about all terms and page through the results.
     *
     * @param page the page number of results to retrieve
     * @return the specified page of results as a {@link QueryResult} instance or a 400 response
     *          if the page number is invalid
     */
    @ApiOperation(value = "Get all information on all terms and page through the results")
    @RequestMapping(value = "/" + TERMS, method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> baseUrl(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page) {

        return new ResponseEntity<>(ontologyService.findAllByOntologyType(getOntologyType(),
                new Page(page, MAX_PAGE_RESULTS)), HttpStatus.OK);
    }

    /**
     * Get core information about a list of terms in comma-separated-value (CSV) format
     *
     * @param ids ontology identifiers in CSV format
     * @return
     * <ul>
     *     <li>all ids are valid: response consists of a 200 with the chosen information about the ontology terms</li>
     *     <li>any id is not found: response returns 404</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get core information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, ancestors, synonyms, " +
                    "aspect and usage.")
    @RequestMapping(value = TERMS + "/{ids}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsCoreAttr(@PathVariable(value = "ids") String ids) {
        return getResultsResponse(ontologyService.findCoreInfoByOntologyId(validationHelper.validateCSVIds
                (ids)));
    }

    /**
     * Get complete information about a list of terms in comma-separated-value (CSV) format
     *
     * @param ids ontology identifiers in CSV format
     * @return
     * <ul>
     *     <li>all ids are valid: response consists of a 200 with the chosen information about the ontology terms</li>
     *     <li>any id is not found: response returns 404</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get complete information about a (CSV) list of terms based on their ids",
            notes = "All fields will be populated providing they have a value.")
    @RequestMapping(value = TERMS + "/{ids}/complete", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsComplete(@PathVariable(value = "ids") String ids) {
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
     *     <li>any id is not found: response returns 404</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get history information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, history.")
    @RequestMapping(value = TERMS + "/{ids}/history", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsHistory(@PathVariable(value = "ids") String ids) {
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
     *     <li>any id is not found: response returns 404</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get cross-reference information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, xRefs.")
    @RequestMapping(value = TERMS + "/{ids}/xrefs", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsXRefs(@PathVariable(value = "ids") String ids) {
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
     *     <li>any id is not found: response returns 404</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get taxonomy constraint information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, taxonConstraints.")
    @RequestMapping(value = TERMS + "/{ids}/constraints", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsTaxonConstraints(@PathVariable(value = "ids") String ids) {
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
     *     <li>any id is not found: response returns 404</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get cross ontology relationship information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, xRelations.")
    @RequestMapping(value = TERMS + "/{ids}/xontologyrelations", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsXOntologyRelations(@PathVariable(value = "ids") String ids) {
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
     *     <li>any id is not found: response returns 404</li>
     *     <li>any id is of the an invalid format: response returns 400</li>
     * </ul>
     */
    @ApiOperation(value = "Get annotation guideline information about a (CSV) list of terms based on their ids",
            notes = "If possible, response fields include: id, isObsolete, name, definition, annotationGuidelines.")
    @RequestMapping(value = TERMS + "/{ids}/guidelines", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsAnnotationGuideLines(@PathVariable(value = "ids") String ids) {
        return getResultsResponse(ontologyService
                .findAnnotationGuideLinesInfoByOntologyId(validationHelper.validateCSVIds(ids)));
    }

    /**
     * Search for an ontology term via its identifier, or a generic query search
     *
     * @param query the query to search against
     * @param limit the amount of queries to return
     * @return a {@link QueryResult} instance containing the results of the search
     */
    @ApiOperation(value = "Searches a simple user query, e.g., query=apopto",
            notes = "If possible, response fields include: id, name, definition, isObsolete")
    @RequestMapping(value = "/search", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<OBOTerm>> ontologySearch(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "limit", defaultValue = DEFAULT_ENTRIES_PER_PAGE) int limit,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page) {

        validationHelper.validateRequestedResults(limit);

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
    @RequestMapping(value = TERMS + "/{ids}/ancestors", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<String>> findAncestors(
            @PathVariable(value = "ids") String ids,
            @RequestParam(value = "relations", defaultValue = DEFAULT_TRAVERSAL_TYPES_CSV) String relations) {
        return getResultsResponse(
                asList(
                        ontologyService.ancestors(
                                asSet(validationHelper.validateCSVIds(ids)),
                                asArray(validationHelper.validateRelationTypes(relations))
                        )));
    }

    /**
     * Retrieves the descendants of ontology terms
     * @param ids the term ids in CSV format
     * @param relations the ontology relationships over which descendants will be found
     * @return a result containing the descendants
     */
    @ApiOperation(value = "Retrieves the descendants of specified ontology terms")
    @RequestMapping(value = TERMS + "/{ids}/descendants", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<String>> findDescendants(
            @PathVariable(value = "ids") String ids,
            @RequestParam(value = "relations", defaultValue = DEFAULT_TRAVERSAL_TYPES_CSV) String relations) {
        return getResultsResponse(
                asList(
                        ontologyService.descendants(
                                asSet(validationHelper.validateCSVIds(ids)),
                                asArray(validationHelper.validateRelationTypes(relations))
                        )));
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
    @RequestMapping(value = TERMS + "/{ids}/paths/{toIds}", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<OntologyRelationship>>> findPaths(
            @PathVariable(value = "ids") String ids,
            @PathVariable(value = "toIds") String toIds,
            @RequestParam(value = "relations", defaultValue = DEFAULT_TRAVERSAL_TYPES_CSV) String relations) {
        return getResultsResponse(
                ontologyService.paths(
                        asSet(validationHelper.validateCSVIds(ids)),
                        asSet(validationHelper.validateCSVIds(toIds)),
                        asArray(validationHelper.validateRelationTypes(relations))
                ));
    }

    /**
     * Predicate that determines the validity of an ID.
     * @return {@link Predicate<String>} indicating the validity of an ID.
     */
    protected abstract Predicate<String> idValidator();

    /**
     * Returns the {@link OntologyType} that corresponds to this controller.
     *
     * @return the ontology type corresponding to this controller's behaviour.
     */
    protected abstract OntologyType getOntologyType();

    /**
     * Wrap a collection as a {@link Set}
     * @param items the items to wrap as a {@link Set}
     * @param <ItemType> the type of the {@link Collection}, i.e., this method works for any type
     * @return a {@link Set} wrapping the items in a {@link Collection}
     */
    private static <ItemType> Set<ItemType> asSet(Collection<ItemType> items) {
        return items.stream().collect(Collectors.toSet());
    }

    private static <ItemType> List<ItemType> asList(Collection<ItemType> items) {
        return items.stream().collect(Collectors.toList());
    }

    /**
     * Converts a {@link Collection} of {@link OntologyRelationType}s to a corresponding array of
     * {@link OntologyRelationType}s
     * @param relations the {@link OntologyRelationType}s
     * @return an array of {@link OntologyRelationType}s
     */
    private static OntologyRelationType[] asArray(Collection<OntologyRelationType> relations) {
        return relations.stream().toArray(OntologyRelationType[]::new);
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
        return new ResponseEntity<>(queryResult, HttpStatus.OK);
    }

    private QueryRequest buildRequest(String query,
            int limit,
            int page,
            StringToQuickGOQueryConverter converter) {

        QuickGOQuery userQuery = converter.convert(query);
        QuickGOQuery restrictedUserQuery = restrictQueryToOTypeResults(userQuery);

        QueryRequest.Builder builder = new QueryRequest
                .Builder(restrictedUserQuery)
                .setPageParameters(page, limit);

        if (!ontologyRetrievalConfig.getSearchReturnedFields().isEmpty()) {
            ontologyRetrievalConfig.getSearchReturnedFields().stream()
                    .forEach(builder::addProjectedField);
        }

        return builder.build();
    }

    /**
     * Given a {@link QuickGOQuery}, create a composite {@link QuickGOQuery} by
     * performing a conjunction with another query, which restricts all results
     * to be of a type corresponding to that provided by {@link #getOntologyType()}.
     *
     * @param query the query that is constrained
     * @return the new constrained query
     */
    private QuickGOQuery restrictQueryToOTypeResults(QuickGOQuery query) {
        return query.and(
                ontologyQueryConverter.convert(
                        OntologyFields.Searchable.ONTOLOGY_TYPE + COLON + getOntologyType().name()));
    }
}