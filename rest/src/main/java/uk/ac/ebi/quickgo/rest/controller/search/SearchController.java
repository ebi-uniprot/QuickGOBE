package uk.ac.ebi.quickgo.rest.controller.search;

import uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequest;
import uk.ac.ebi.quickgo.repo.solr.query.results.QueryResult;
import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;
import uk.ac.ebi.quickgo.service.search.RetrievalException;
import uk.ac.ebi.quickgo.service.search.SearchService;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static uk.ac.ebi.quickgo.repo.solr.query.model.QueryRequest.Builder;

/**
 * Search controller responsible for providing consistent search
 * functionality over selected services.
 *
 * Created 13/01/16
 * @author Edd
 */
@RestController
@RequestMapping(value = "/QuickGO/internal/search")
public class SearchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    private final StringToQuickGOQueryConverter ontologyQueryConverter;
    private final SearchService<OBOTerm> ontologySearchService;
    private final OntologyFieldSpec ontologyFieldSpec;

    @Autowired
    public SearchController(
            SearchService<OBOTerm> ontologySearchService,
            OntologyFieldSpec ontologyFieldSpec) {
        this.ontologySearchService = requireNonNull(ontologySearchService);
        this.ontologyFieldSpec = ontologyFieldSpec;
        this.ontologyQueryConverter = new StringToQuickGOQueryConverter(ontologyFieldSpec);
    }

    /**
     * Method is invoked when a client wants to search for an ontology term via its identifier, or a generic query
     * search
     *
     * @param query the query to search against
     * @param limit the amount of queries to return
     */
    @RequestMapping(value = "/ontology", method = {RequestMethod.GET})
    public ResponseEntity<QueryResult<OBOTerm>> ontologySearch(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "limit", defaultValue = "25") int limit,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "filterQuery", required = false) List<String> filterQueries,
            @RequestParam(value = "facet", required = false) List<String> facets) {

        QueryRequest request = buildRequest(
                query,
                limit,
                page,
                filterQueries,
                facets,
                ontologyQueryConverter,
                ontologyFieldSpec);
        return search(request, ontologySearchService);
    }

    private <T> ResponseEntity<QueryResult<T>> search(QueryRequest request, SearchService<T> searchService) {
        ResponseEntity<QueryResult<T>> response;

        if (request == null) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {

            try {
                QueryResult<T> queryResult = searchService.findByQuery(request);
                response = new ResponseEntity<>(queryResult, HttpStatus.OK);
            } catch (RetrievalException e) {
                LOGGER.error(createErrorMessage(request), e);
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return response;
    }

    private boolean isValidQuery(String query) {
        return query != null && query.trim().length() > 0;
    }

    private boolean isValidNumRows(int rows) {
        return rows >= 0;
    }

    private boolean isValidPage(int page) {
        return page >= 0;
    }

    private boolean isValidFacets(SearchableField searchableField, List<String> facets) {
        if (nonNull(facets)) {
            for (String facet : facets) {
                if (!searchableField.isSearchable(facet)) {
                    return false;
                }
            }
        }
        return true;
    }

    private QueryRequest buildRequest(String query, int limit, int page, List<String> filterQueries,
            List<String> facets, StringToQuickGOQueryConverter converter, SearchableField fieldSpec) {

        if (!isValidQuery(query) || !isValidNumRows(limit) || !isValidPage(page) || !isValidFacets
                (fieldSpec, facets)) {
            return null;
        } else {
            Builder builder = new Builder(converter.convert(query));

            if (limit > 0 && page > 0) {
                builder.setPageParameters(page, limit);
            }

            if (facets != null) {
                facets.forEach(builder::addFacetField);
            }

            if (filterQueries != null) {
                filterQueries.stream()
                        .map(converter::convert)
                        .forEach(builder::addQueryFilter);
            }

            return builder.build();
        }
    }

    private String createErrorMessage(QueryRequest request) {
        return "Unable to process search query request: [" + request + "]";
    }
}
