package uk.ac.ebi.quickgo.ontology.model;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    @Min(value = 1, message = "Page number cannot be less than 1")
    private int page = DEFAULT_PAGE_NUMBER;

    @Min(value = 0, message = "Number of results per page cannot be less than 0")
    @Max(value = MAX_ENTRIES_PER_PAGE,
            message = "Number of results per page cannot be greater than " + MAX_ENTRIES_PER_PAGE)
    private int limit = DEFAULT_ENTRIES_PER_PAGE;

    @NotNull(message = "Query cannot be null")
    @Size(min = 1, message = "Query cannot be empty")
    private String query;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public QuickGOQuery createQuery() {
        return QuickGOQuery.createQuery(query);
    }
}