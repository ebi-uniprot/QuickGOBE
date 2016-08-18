package uk.ac.ebi.quickgo.client.model.ontology;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import javax.validation.Valid;
import javax.validation.constraints.*;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;
import static uk.ac.ebi.quickgo.rest.search.DefaultSearchQueryTemplate.DEFAULT_PAGE_NUMBER;

/**
 * A data structure used to store the input parameters a client can submit to the Ontology search enpoint
 *
 * Once the comma separated values have been set, then turn then into an object (SimpleFilter) that
 * encapsulates the list and solr field name to use for that argument.
 */
public class OntologyRequest {
    static final int DEFAULT_ENTRIES_PER_PAGE = 25;
    static final int MAX_ENTRIES_PER_PAGE = 100;

    private boolean useHighlighting;

    private int page = DEFAULT_PAGE_NUMBER;

    private int limit = DEFAULT_ENTRIES_PER_PAGE;

    private String[] facets;

    private String query;

    private String filterByAspect;

    private String filterByType;

    @Min(value = 1, message = "Page number cannot be less than 1")
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Min(value = 0, message = "Number of results per page cannot be less than 0")
    @Max(value = MAX_ENTRIES_PER_PAGE,
            message = "Number of results per page cannot be greater than " + MAX_ENTRIES_PER_PAGE)
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @NotNull(message = "Query cannot be null")
    @Size(min = 1, message = "Query cannot be empty")
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isUseHighlighting() {
        return useHighlighting;
    }

    public void setUseHighlighting(boolean useHighlighting) {
        this.useHighlighting = useHighlighting;
    }

    public String[] getFacets() {
        return facets;
    }

    public void setFacets(String[] facets) {
        this.facets = facets;
    }

    @Pattern(regexp = "biological_process|molecular_function|cellular_component", flags = CASE_INSENSITIVE,
            message = "Provided aspect is invalid: ${validatedValue}")
    public String getFilterByAspect() {
        return filterByAspect;
    }

    public void setFilterByAspect(String filterByAspect) {
        this.filterByAspect = filterByAspect;
    }

    @Pattern(regexp = "go|eco", flags = CASE_INSENSITIVE,
            message = "Provided ontology type is invalid: ${validatedValue}")
    public String getFilterByType() {
        return filterByType;
    }

    public void setFilterByType(String filterByType) {
        this.filterByType = filterByType;
    }

    public QuickGOQuery createQuery() {
        return QuickGOQuery.createQuery(query);
    }
}