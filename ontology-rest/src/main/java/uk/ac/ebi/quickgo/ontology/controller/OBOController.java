package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyFields;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.ontology.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.SearchDispatcher;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.SearchableField;
import uk.ac.ebi.quickgo.rest.search.StringToQuickGOQueryConverter;
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
    private static final String COMMA = ",";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String COLON = ":";

    private static final String DEFAULT_ENTRIES_PER_PAGE = "25";
    private static final String DEFAULT_PAGE_NUMBER = "1";

    private static final String TERM = "term";
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
    @RequestMapping(value = "/*", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> emptyId() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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

        return getTermResponse(ontologyService.findCoreInfoByOntologyId(singletonList(id)));
    }

    @RequestMapping(value = TERMS + "/{ids}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findCoreTerms(@PathVariable(value = "ids") String ids) {
        Arrays.asList(ids.split(COMMA))
                .stream()
                .forEach(this::checkValidId);

        return getTermsResponse(ontologyService.findCoreInfoByOntologyId(Arrays.asList(ids.split(COMMA))));
    }

    public abstract boolean isValidId(String id);

    private ResponseEntity<T> getTermResponse(List<T> docList) {
        // 1 result => success
        // 0 => not found
        // null or 1+ => something went wrong
        if (docList == null || docList.size() > 1) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (docList.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(docList.get(0), HttpStatus.OK);
        }
    }

    private ResponseEntity<QueryResult<T>> getTermsResponse(List<T> docList) {
        QueryResult<T> queryResult = new QueryResult<>(docList.size(), docList, null, null, null);
        return new ResponseEntity<>(queryResult, HttpStatus.OK);
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

        return getTermResponse(ontologyService.findCompleteInfoByOntologyId(singletonList(id)));
    }

    @RequestMapping(value = TERMS + "/{ids}/complete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findCompleteTerms(@PathVariable(value = "ids") String ids) {
        Arrays.asList(ids.split(COMMA))
                .stream()
                .forEach(this::checkValidId);

        return getTermsResponse(ontologyService.findCompleteInfoByOntologyId(Arrays.asList(ids.split(COMMA))));
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

        return getTermResponse(ontologyService.findHistoryInfoByOntologyId(singletonList(id)));
    }

    @RequestMapping(value = TERMS + "/{ids}/history", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsHistory(@PathVariable(value = "ids") String ids) {
        Arrays.asList(ids.split(COMMA))
                .stream()
                .forEach(this::checkValidId);

        return getTermsResponse(ontologyService.findHistoryInfoByOntologyId(Arrays.asList(ids.split(COMMA))));
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

        return getTermResponse(ontologyService.findXRefsInfoByOntologyId(singletonList(id)));
    }

    @RequestMapping(value = TERMS + "/{ids}/xrefs", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsXRefs(@PathVariable(value = "ids") String ids) {
        Arrays.asList(ids.split(COMMA))
                .stream()
                .forEach(this::checkValidId);

        return getTermsResponse(ontologyService.findXRefsInfoByOntologyId(Arrays.asList(ids.split(COMMA))));
    }

    /**
     * Get taxonomy constraint and blacklist information about a term based on its id
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

        return getTermResponse(ontologyService.findTaxonConstraintsInfoByOntologyId(singletonList(id)));
    }

    @RequestMapping(value = TERMS + "/{ids}/constraints", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsTaxonConstraints(@PathVariable(value = "ids") String ids) {
        Arrays.asList(ids.split(COMMA))
                .stream()
                .forEach(this::checkValidId);

        return getTermsResponse(ontologyService.findTaxonConstraintsInfoByOntologyId(Arrays.asList(ids.split(COMMA))));
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

        return getTermResponse(ontologyService.findXORelationsInfoByOntologyId(singletonList(id)));
    }

    @RequestMapping(value = TERMS + "/{ids}/xontologyrelations", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsXOntologyRelations(@PathVariable(value = "ids") String ids) {
        Arrays.asList(ids.split(COMMA))
                .stream()
                .forEach(this::checkValidId);

        return getTermsResponse(ontologyService.findXORelationsInfoByOntologyId(Arrays.asList(ids.split(COMMA))));
    }

    /**
     * Get annotation guideline information about a term based on its id
     * @param id ontology identifier
     * @return
     * <ul>
     * <li>id is found: response consists of a 200 with annotation guidelines of the ontology term</li>
     * <li>id is not found: response returns 404</li>
     * <li>id is not in correct format: response returns 400</li>
     * </ul>
     */
    @RequestMapping(value = TERM + "/{id}/guidelines", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermAnnotationGuideLines(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(ontologyService.findAnnotationGuideLinesInfoByOntologyId(singletonList(id)));
    }

    @RequestMapping(value = TERMS + "/{ids}/guidelines", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<T>> findTermsAnnotationGuideLines(@PathVariable(value = "ids") String ids) {
        Arrays.asList(ids.split(COMMA))
                .stream()
                .forEach(this::checkValidId);

        return getTermsResponse(ontologyService.findAnnotationGuideLinesInfoByOntologyId(Arrays.asList(ids.split(COMMA))));
    }

    /**
     * Method is invoked when a client wants to search for an ontology term via its identifier, or a generic query
     * search
     *
     * @param query the query to search against
     * @param limit the amount of queries to return
     */
    @RequestMapping(value = "/search", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<OBOTerm>> ontologySearch(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "limit", defaultValue = DEFAULT_ENTRIES_PER_PAGE) int limit,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page) {

        QueryRequest request = buildRequest(
                query,
                limit,
                page,
                ontologyQueryConverter);

        return SearchDispatcher.search(request, ontologySearchService);
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
     * Returns the {@link OntologyType} that corresponds to this controller.
     *
     * @return the ontology type corresponding to this controller's behaviour.
     */
    protected abstract OntologyType getOntologyType();

    /**
     * Checks the validity of a term id.
     *
     * @param id the term id to check
     * @throws IllegalArgumentException is thrown if the id is not valid
     */
    private void checkValidId(String id) {
        if (!isValidId(id)) {
            throw new IllegalArgumentException("Provided id: " + id + " is invalid");
        }
    }
}