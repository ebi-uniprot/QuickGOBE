package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyFields;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static com.google.common.base.Strings.isNullOrEmpty;

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

    public OBOController(OntologyService<T> ontologyService,
            SearchService<OBOTerm> ontologySearchService,
            SearchableField searchableField,
            SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig) {
        Preconditions.checkArgument(ontologyService != null, "Ontology service can not be null");
        Preconditions.checkArgument(ontologySearchService != null, "Ontology search service can not be null");

        this.ontologyService = ontologyService;
        this.ontologySearchService = ontologySearchService;
        this.ontologyQueryConverter = new StringToQuickGOQueryConverter(searchableField);
        this.ontologyRetrievalConfig = ontologyRetrievalConfig;
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
     * @return  the specified page of results as a {@link QueryResult} instance or a 400 response
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
        return getTermsResponse(ontologyService.findCoreInfoByOntologyId(validateIds(ids)));
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
    @RequestMapping(value = TERMS + "/{ids}/complete", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsComplete(@PathVariable(value = "ids") String ids) {
        return getTermsResponse(ontologyService.findCompleteInfoByOntologyId(validateIds(ids)));
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
    @RequestMapping(value = TERMS + "/{ids}/history", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsHistory(@PathVariable(value = "ids") String ids) {
        return getTermsResponse(ontologyService.findHistoryInfoByOntologyId(validateIds(ids)));
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
    @RequestMapping(value = TERMS + "/{ids}/xrefs", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsXRefs(@PathVariable(value = "ids") String ids) {
        return getTermsResponse(ontologyService.findXRefsInfoByOntologyId(validateIds(ids)));
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
    @RequestMapping(value = TERMS + "/{ids}/constraints", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsTaxonConstraints(@PathVariable(value = "ids") String ids) {
        return getTermsResponse(ontologyService.findTaxonConstraintsInfoByOntologyId(validateIds(ids)));
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
    @RequestMapping(value = TERMS + "/{ids}/xontologyrelations", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsXOntologyRelations(@PathVariable(value = "ids") String ids) {
        return getTermsResponse(ontologyService.findXORelationsInfoByOntologyId(validateIds(ids)));
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
    @RequestMapping(value = TERMS + "/{ids}/guidelines", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsAnnotationGuideLines(@PathVariable(value = "ids") String ids) {
        return getTermsResponse(ontologyService.findAnnotationGuideLinesInfoByOntologyId(validateIds(ids)));
    }

    /**
     * Search for an ontology term via its identifier, or a generic query search
     *
     * @param query the query to search against
     * @param limit the amount of queries to return
     * @return a {@link QueryResult} instance containing the results of the search
     */
    @ApiOperation(value="Searches a simple user query, e.g., query=apopto",
            notes = "If possible, response fields include: id, name, definition, isObsolete")
    @RequestMapping(value = "/search", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<OBOTerm>> ontologySearch(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "limit", defaultValue = DEFAULT_ENTRIES_PER_PAGE) int limit,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page) {

        validateRequestedResults(limit);

        QueryRequest request = buildRequest(
                query,
                limit,
                page,
                ontologyQueryConverter);

        return SearchDispatcher.search(request, ontologySearchService);
    }

    /**
     * Checks whether an ID has a valid format.
     * @param id the ID
     * @return boolean indicating whether or not the specified ID is valid
     */
    protected abstract boolean isValidId(String id);

    /**
     * Returns the {@link OntologyType} that corresponds to this controller.
     *
     * @return the ontology type corresponding to this controller's behaviour.
     */
    protected abstract OntologyType getOntologyType();

    /**
     * Checks the validity of a list of IDs in CSV format.
     * @param ids a list of IDs in CSV format
     * @throws IllegalArgumentException is thrown if an ID is not valid, or if
     * number of IDs listed is greater than {@link #MAX_PAGE_RESULTS}.
     */
    protected List<String> validateIds(String ids) {
        List<String> idList = csvToList(ids);

        validateRequestedResults(idList.size());

        idList.stream()
                .forEach(this::checkValidId);

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
     * Checks the validity of a term id.
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
     * Creates a list of items from a scalar representation of a list, in CSV format. If the
     * parameter is null, an empty list is returned.
     *
     * @param csv a CSV list of items
     * @return a list of values originally comprising the CSV input String
     */
    protected List<String> csvToList(String csv) {
        if (!isNullOrEmpty(csv)) {
            return Arrays.asList(csv.split(COMMA));
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Creates a {@link ResponseEntity} containing a {@link QueryResult} for a list of documents.
     *
     * @param docList a list of results
     * @return a {@link ResponseEntity} containing a {@link QueryResult} for a list of documents
     */
    protected ResponseEntity<QueryResult<T>> getTermsResponse(List<T> docList) {
        List<T> resultsToShow;
        if (docList == null) {
            resultsToShow = Collections.emptyList();
        } else {
            resultsToShow = docList;
        }

        QueryResult<T> queryResult = new QueryResult.Builder<>(resultsToShow.size(), resultsToShow).build();
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