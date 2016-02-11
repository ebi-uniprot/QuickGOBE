package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyFields;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.OntologyService;
import uk.ac.ebi.quickgo.rest.search.*;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Abstract controller defining common end-points of an OBO related
 * REST API.
 *
 * Created 27/11/15
 * @author Edd
 */
public abstract class OBOController<T extends OBOTerm> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String COLON = ":";

    private static final String DEFAULT_ENTRIES_PER_PAGE = "25";
    private static final String DEFAULT_PAGE_NUMBER = "1";

    private final OntologyService<T> ontologyService;
    private final SearchService<OBOTerm> ontologySearchService;
    private final StringToQuickGOQueryConverter ontologyQueryConverter;

    public OBOController(OntologyService<T> ontologyService,
            SearchService<OBOTerm> ontologySearchService,
            SearchableField searchableField) {
        Preconditions.checkArgument(ontologyService != null, "Ontology service can not be null");
        Preconditions.checkArgument(ontologySearchService != null, "Ontology search service can not be null");

        this.ontologyService = ontologyService;
        this.ontologySearchService = ontologySearchService;
        this.ontologyQueryConverter = new StringToQuickGOQueryConverter(searchableField);
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
    @RequestMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findCoreTerm(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(ontologyService.findCoreInfoByOntologyId(id));
    }

    public abstract boolean isValidId(String id);

    private ResponseEntity<T> getTermResponse(Optional<T> optionalECODoc) {
        return optionalECODoc.map(ontology -> new ResponseEntity<>(ontology, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
    @RequestMapping(value = "/{id}/complete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findCompleteTerm(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(ontologyService.findCompleteInfoByOntologyId(id));
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
    @RequestMapping(value = "/{id}/history", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermHistory(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(ontologyService.findHistoryInfoByOntologyId(id));
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
    @RequestMapping(value = "/{id}/xrefs", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermXRefs(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(ontologyService.findXRefsInfoByOntologyId(id));
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
    @RequestMapping(value = "/{id}/constraints", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermTaxonConstraints(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(ontologyService.findTaxonConstraintsInfoByOntologyId(id));
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
    @RequestMapping(value = "/{id}/xontologyrelations", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermXOntologyRelations(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(ontologyService.findXORelationsInfoByOntologyId(id));
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
    @RequestMapping(value = "/{id}/guidelines", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<T> findTermAnnotationGuideLines(@PathVariable(value = "id") String id) {
        checkValidId(id);

        return getTermResponse(ontologyService.findAnnotationGuideLinesInfoByOntologyId(id));
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

        return new QueryRequest
                .Builder(restrictedUserQuery)
                .setPageParameters(page, limit)
                .build();
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