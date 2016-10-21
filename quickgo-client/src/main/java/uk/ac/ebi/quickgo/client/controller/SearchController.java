package uk.ac.ebi.quickgo.client.controller;

import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.client.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.rest.search.DefaultSearchQueryTemplate;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.StringToQuickGOQueryConverter;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import io.swagger.annotations.ApiOperation;
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

import static com.google.common.base.Preconditions.checkArgument;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.search;

/**
 * Search controller responsible for providing consistent search
 * functionality over selected services.
 *
 * Created 13/01/16
 * @author Edd
 */
@RestController
@RequestMapping(value = "/internal/search")
public class SearchController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String DEFAULT_ENTRIES_PER_PAGE = "25";
    private static final String DEFAULT_PAGE_NUMBER = "1";

    private final SearchService<OntologyTerm> ontologySearchService;
    private final DefaultSearchQueryTemplate requestTemplate;

    @Autowired
    public SearchController(
            SearchService<OntologyTerm> ontologySearchService,
            SearchableField ontologySearchableField,
            SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig) {
        checkArgument(ontologySearchService != null, "Ontology search service cannot be null");
        checkArgument(ontologySearchableField != null, "Ontology searchable field cannot be null");
        checkArgument(ontologyRetrievalConfig != null, "Ontology retrieval configuration cannot be null");

        this.ontologySearchService = ontologySearchService;

        this.requestTemplate = new DefaultSearchQueryTemplate(
                new StringToQuickGOQueryConverter(ontologySearchableField),
                ontologySearchableField,
                ontologyRetrievalConfig.getSearchReturnedFields(),
                ontologyRetrievalConfig.repo2DomainFieldMap().keySet(),
                ontologyRetrievalConfig.getHighlightStartDelim(),
                ontologyRetrievalConfig.getHighlightEndDelim());
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
    @ApiOperation(value="Searches a user defined query, e.g., query=apopto",
            notes = "Response fields include: id and name (and aspect for GO terms)")
    @RequestMapping(value = "/ontology", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<OntologyTerm>> ontologySearch(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "limit", defaultValue = DEFAULT_ENTRIES_PER_PAGE) int limit,
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "filterQuery", required = false) List<String> filterQueries,
            @RequestParam(value = "facet", required = false) List<String> facets,
            @RequestParam(value = "highlighting", required = false) boolean highlighting) {

        DefaultSearchQueryTemplate.Builder requestBuilder = requestTemplate.newBuilder()
                .setQuery(query)
                .addFacets(facets)
                .addFilters(filterQueries)
                .useHighlighting(highlighting)
                .setPage(page)
                .setPageSize(limit);

        return search(requestBuilder.build(), ontologySearchService);
    }
}