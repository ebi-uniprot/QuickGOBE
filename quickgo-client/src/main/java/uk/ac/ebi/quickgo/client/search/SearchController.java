package uk.ac.ebi.quickgo.client.search;

import uk.ac.ebi.quickgo.common.search.SearchService;
import uk.ac.ebi.quickgo.common.search.query.QueryRequest;
import uk.ac.ebi.quickgo.common.search.results.QueryResult;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.requireNonNull;
import static uk.ac.ebi.quickgo.client.search.SearchDispatcher.*;
import static uk.ac.ebi.quickgo.common.search.query.QueryRequest.*;

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
    private final SearchService ontologySearchService;
    private final OntologyFieldSpec ontologyFieldSpec;

    @Autowired
    public SearchController(
            SearchService ontologySearchService,
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
    @RequestMapping(value = "/ontology", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult> ontologySearch(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "limit", defaultValue = DEFAULT_ENTRIES_PER_PAGE) int limit,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
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

    private QueryRequest buildRequest(String query,
            int limit,
            int page,
            List<String> filterQueries,
            List<String> facets,
            StringToQuickGOQueryConverter converter,
            SearchableField fieldSpec) {

        if (!isValidQuery(query)
                || !isValidNumRows(limit)
                || !isValidPage(page)
                || !isValidFacets(fieldSpec, facets)
                || !isValidFilterQueries(fieldSpec, filterQueries)) {
            return null;
        } else {
            Builder builder = new Builder(converter.convert(query));
            builder.setPageParameters(page, limit);

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
}