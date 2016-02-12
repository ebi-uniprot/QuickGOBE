package uk.ac.ebi.quickgo.client.controller;

import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.client.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.SearchableField;
import uk.ac.ebi.quickgo.rest.search.StringToQuickGOQueryConverter;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
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

import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.*;
import static uk.ac.ebi.quickgo.rest.search.query.QueryRequest.*;

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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String DEFAULT_ENTRIES_PER_PAGE = "25";
    private static final String DEFAULT_PAGE_NUMBER = "1";

    private final StringToQuickGOQueryConverter ontologyQueryConverter;
    private final SearchService<OntologyTerm> ontologySearchService;
    private final SearchableField ontologySearchableField;
    private final SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig;

    @Autowired
    public SearchController(
            SearchService<OntologyTerm> ontologySearchService,
            SearchableField ontologySearchableField,
            SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig) {

        Preconditions.checkArgument(ontologySearchService != null, "Ontology search service cannot be null");
        Preconditions.checkArgument(ontologyRetrievalConfig != null, "Ontology retrieval configuration cannot be null");

        this.ontologySearchService = ontologySearchService;
        this.ontologySearchableField = ontologySearchableField;
        this.ontologyQueryConverter = new StringToQuickGOQueryConverter(ontologySearchableField);
        this.ontologyRetrievalConfig = ontologyRetrievalConfig;
    }

    /**
     * Method is invoked when a client wants to search for an ontology term via its identifier, or a generic query
     * search
     *
     * @param query the query to search against
     * @param limit the amount of queries to return
     */
    @RequestMapping(value = "/ontology", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<OntologyTerm>> ontologySearch(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "limit", defaultValue = DEFAULT_ENTRIES_PER_PAGE) int limit,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "filterQuery", required = false) List<String> filterQueries,
            @RequestParam(value = "facet", required = false) List<String> facets,
            @RequestParam(value = "highlighting", required = false) boolean highlighting) {

        QueryRequest request = buildRequest(
                query,
                limit,
                page,
                filterQueries,
                facets,
                highlighting,
                ontologyQueryConverter,
                ontologySearchableField);

        return search(request, ontologySearchService);
    }

    private QueryRequest buildRequest(String query,
            int limit,
            int page,
            List<String> filterQueries,
            List<String> facets,
            boolean highlighting,
            StringToQuickGOQueryConverter converter,
            SearchableField fieldSpec) {

        checkFacets(fieldSpec, facets);
        checkFilters(fieldSpec, filterQueries);

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

            if (highlighting) {
                ontologyRetrievalConfig.repo2DomainFieldMap().keySet().stream()
                        .forEach(builder::addHighlightedField);
                builder.setHighlightStartDelim(ontologyRetrievalConfig.getHighlightStartDelim());
                builder.setHighlightEndDelim(ontologyRetrievalConfig.getHighlightEndDelim());
            }

            ontologyRetrievalConfig.getSearchReturnedFields().stream()
                    .forEach(builder::addProjectedField);

        return builder.build();
    }

    private void checkFacets(SearchableField fieldSpec, List<String> facets) {
        if (!isValidFacets(fieldSpec, facets)) {
            throw new IllegalArgumentException("At least one of the provided facets is not searchable: " + facets);
        }
    }

    private void checkFilters(SearchableField fieldSpec, List<String> filterQueries) {
        if (!isValidFilterQueries(fieldSpec, filterQueries)) {
            throw new IllegalArgumentException("At least one of the provided filter queries is not filterable: " +
                    filterQueries);
        }
    }
}