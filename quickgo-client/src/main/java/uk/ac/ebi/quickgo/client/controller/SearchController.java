package uk.ac.ebi.quickgo.client.controller;

import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.client.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.DefaultSearchQueryRequestBuilder;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.SearchableField;
import uk.ac.ebi.quickgo.rest.search.StringToQuickGOQueryConverter;
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

import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.search;

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
     * Perform a custom client search
     *
     * @param query the user query
     * @param limit number of entries per page
     * @param page which page number of entries to retrieve
     * @param filterQueries an optional list of filter queries
     * @param facets an optional list of facet fields
     * @param highlighting whether or not to highlight the search results
     * @return the search results
     */

    @RequestMapping(value = "/ontology", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<OntologyTerm>> ontologySearch(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "limit", defaultValue = DEFAULT_ENTRIES_PER_PAGE) int limit,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "filterQuery", required = false) List<String> filterQueries,
            @RequestParam(value = "facet", required = false) List<String> facets,
            @RequestParam(value = "highlighting", required = false) boolean highlighting) {

        DefaultSearchQueryRequestBuilder requestBuilder = new DefaultSearchQueryRequestBuilder(
                query,
                ontologyQueryConverter,
                ontologySearchableField,
                ontologyRetrievalConfig.getSearchReturnedFields(),
                ontologyRetrievalConfig.repo2DomainFieldMap().keySet(),
                ontologyRetrievalConfig.getHighlightStartDelim(),
                ontologyRetrievalConfig.getHighlightEndDelim())

                .addFacets(facets)
                .addFilters(filterQueries)
                .useHighlighting(highlighting)
                .setPage(page)
                .setPageSize(limit);

        return search(requestBuilder.build(), ontologySearchService);
    }

}