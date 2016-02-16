package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyFields;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.ResourceNotFoundException;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.*;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import java.util.Arrays;
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

import static java.util.Collections.singletonList;

/**
 * Abstract controller defining common end-points of an OBO related
 * REST API.
 *
 * Created 27/11/15
 * @author Edd
 */
public abstract class OBOController<T extends OBOTerm> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OBOController.class);
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String DEFAULT_ENTRIES_PER_PAGE = "25";
    private static final String DEFAULT_PAGE_NUMBER = "1";
    private static final String TERM = "term";
    private static final String TERMS = "terms";
    static final int MAX_PAGE_RESULTS = 100;
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
    @RequestMapping(value = "/*", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseExceptionHandler.ErrorInfo> emptyId() {
        throw new IllegalArgumentException("The requested end-point does not exist.");
    }

    /**
     * Get core information about a term based on its id
     *
     * @param id ontology identifier
     *
     * @return
     * <ul>
     * <li>id is found: response consists of a 200 with the core information of the ontology term</li>
     * <li>id is not found: response returns 404</li>
     * <li>id is not in correct format: response returns 400</li>
     * </ul>
     */
    @RequestMapping(value = TERM + "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findCoreTerm(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(id, ontologyService.findCoreInfoByOntologyId(singletonList(id)));
    }

    @RequestMapping(value = TERMS + "/{ids}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findCoreTerms(@PathVariable(value = "ids") String ids) {
        checkValidIds(ids);

        return getTermsResponse(ontologyService.findCoreInfoByOntologyId(createIdList(ids)));
    }

    /**
     * Get complete information about a term based on its id
     * @param id ontology identifier
     * @return
     * <ul>
     * <li>id is found: response consists of a 200 with all of the information the ontology term has</li>
     * <li>id is not found: response returns 404</li>
     * <li>id is not in correct format: response returns 400</li>
     * </ul>
     */
    @RequestMapping(value = TERM + "/{id}/complete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findCompleteTerm(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(id, ontologyService.findCompleteInfoByOntologyId(singletonList(id)));
    }

    @RequestMapping(value = TERMS + "/{ids}/complete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findCompleteTerms(@PathVariable(value = "ids") String ids) {
        checkValidIds(ids);

        return getTermsResponse(ontologyService.findCompleteInfoByOntologyId(createIdList(ids)));
    }

    /**
     * Get history information about a term based on its id
     *
     * @param id ontology identifier
     * @return
     * <ul>
     * <li>id is found: response consists of a 200 with the history of the ontology term</li>
     * <li>id is not found: response returns 404</li>
     * <li>id is not in correct format: response returns 400</li>
     * </ul>
     */
    @RequestMapping(value = TERM + "/{id}/history", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermHistory(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(id, ontologyService.findHistoryInfoByOntologyId(singletonList(id)));
    }

    @RequestMapping(value = TERMS + "/{ids}/history", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsHistory(@PathVariable(value = "ids") String ids) {
        checkValidIds(ids);

        return getTermsResponse(ontologyService.findHistoryInfoByOntologyId(createIdList(ids)));
    }

    /**
     * Get cross-reference information about a term based on its id
     * @param id ontology identifier
     * @return
     * <ul>
     * <li>id is found: response consists of a 200 with the cross-references of the ontology term</li>
     * <li>id is not found: response returns 404</li>
     * <li>id is not in correct format: response returns 400</li>
     * </ul>
     */
    @RequestMapping(value = TERM + "/{id}/xrefs", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermXRefs(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(id, ontologyService.findXRefsInfoByOntologyId(singletonList(id)));
    }


    @RequestMapping(value = TERMS + "/{ids}/xrefs", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsXRefs(@PathVariable(value = "ids") String ids) {
        checkValidIds(ids);

        return getTermsResponse(ontologyService.findXRefsInfoByOntologyId(createIdList(ids)));
    }

    /**
     * Get taxonomy constraint information (and blacklist, for GO terms) about a term based on its id
     * @param id ontology identifier
     * @return
     * <ul>
     * <li>id is found: response consists of a 200 with the constraint and blacklist of an ontology term</li>
     * <li>id is not found: response returns 404</li>
     * <li>id is not in correct format: response returns 400</li>
     * </ul>
     */
    @RequestMapping(value = TERM + "/{id}/constraints", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermTaxonConstraints(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(id, ontologyService.findTaxonConstraintsInfoByOntologyId(singletonList(id)));
    }


    @RequestMapping(value = TERMS + "/{ids}/constraints", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsTaxonConstraints(@PathVariable(value = "ids") String ids) {
        checkValidIds(ids);

        return getTermsResponse(ontologyService.findTaxonConstraintsInfoByOntologyId(createIdList(ids)));
    }

    /**
     * Get cross-ontology relationship information about a term based on its id
     * @param id ontology identifier
     * @return
     * <ul>
     * <li>id is found: response consists of a 200 with cross-ontology relations of the ontology term</li>
     * <li>id is not found: response returns 404</li>
     * <li>id is not in correct format: response returns 400</li>
     * </ul>
     */
    @RequestMapping(value = TERM + "/{id}/xontologyrelations", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermXOntologyRelations(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(id, ontologyService.findXORelationsInfoByOntologyId(singletonList(id)));
    }

    @RequestMapping(value = TERMS + "/{ids}/xontologyrelations", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsXOntologyRelations(@PathVariable(value = "ids") String ids) {
        checkValidIds(ids);

        return getTermsResponse(ontologyService.findXORelationsInfoByOntologyId(createIdList(ids)));
    }

    /**
     * Get annotation guideline information about a term based on its id
     * @param id ontology identifier
     * @return
     * <ul>
     *      <li>id is found: response consists of a 200 with annotation guidelines of the ontology term</li>
     *      <li>id is not found: response returns 404</li>
     *      <li>id is not in correct format: response returns 400</li>
     * </ul>
     */
    @RequestMapping(value = TERM + "/{id}/guidelines", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermAnnotationGuideLines(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(id, ontologyService.findAnnotationGuideLinesInfoByOntologyId(singletonList(id)));
    }

    @RequestMapping(value = TERMS + "/{ids}/guidelines", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsAnnotationGuideLines(@PathVariable(value = "ids") String ids) {
        checkValidIds(ids);

        return getTermsResponse(ontologyService.findAnnotationGuideLinesInfoByOntologyId(createIdList(ids)));
    }

    /**
     * Search for an ontology term via its identifier, or a generic query search
     *
     * @param query the query to search against
     * @param limit the amount of queries to return
     * @return a {@link QueryResult} instance containing the results of the search
     */
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

    private List<String> createIdList(String ids) {
        return Arrays.asList(ids.split(COMMA));
    }

    /**
     * Creates a response from a list of terms, which should have a size of 1.
     *
     * @param requestedId the original ID that was requested
     * @param termList a singleton list of terms
     * @return
     * <ul>
     *      <li>termList's size is 1: response consists of a 200 with annotation guidelines of the ontology term</li>
     *      <li>termList's size is 0: response returns 404</li>
     *      <li>otherwise: response returns 500</li>
     * </ul>
     */
    protected ResponseEntity<T> getTermResponse(String requestedId, List<T> termList) {
        if (termList == null) {
            LOGGER.error("Provided ID: '{}' caused a server error because the specified termList was null", requestedId);
            throw new RetrievalException("Provided ID: '" + requestedId + "' caused a server error.");
        } else if(termList.size() > 1) {
            LOGGER.error("Provided ID: '{}' caused a server error because the specified termList contains more than 1" +
                    " element", requestedId);
            throw new RetrievalException("Provided ID: '" + requestedId + "' caused a server error.");
        } else if (termList.size() == 0) {
            throw new ResourceNotFoundException("Provided ID: '" + requestedId + "' was not found");
        } else {
            return new ResponseEntity<>(termList.get(0), HttpStatus.OK);
        }
    }

    private ResponseEntity<QueryResult<T>> getTermsResponse(List<T> docList) {
        QueryResult<T> queryResult = new QueryResult<>(docList.size(), docList, null, null, null);
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

    /**
     * Checks the validity of a list of IDs in CSV format.
     * @param ids a list of IDs in CSV format
     * @throws IllegalArgumentException is thrown if an ID is not valid, or if
     * number of IDs listed is greater than {@link #MAX_PAGE_RESULTS}.
     */
    protected void checkValidIds(String ids) {
        List<String> idList = createIdList(ids);

        validateRequestedResults(idList.size());

        idList
                .stream()
                .forEach(this::checkValidId);
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
}