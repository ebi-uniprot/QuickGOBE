package uk.ac.ebi.quickgo.common.search;

import uk.ac.ebi.quickgo.common.search.query.QueryRequest;
import uk.ac.ebi.quickgo.common.search.results.QueryResult;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class that dispatches search requests, in the form of
 * {@link QueryRequest} instances, to a {@link SearchService}.
 *
 * Validity check methods are also provided for common search parameters.
 * Note that this class is stateless.
 *
 * Created 27/01/16
 * @author Edd
 */
public final class SearchDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchDispatcher.class);

    protected static final Pattern VALID_FILTER_QUERY_FORMAT = Pattern.compile("(\\w+):\\w+");

    private SearchDispatcher() { }

    /**
     * Dispatch a {@link QueryRequest} to a {@link SearchService} and handle its responses
     * appropriately.
     * @param request the request
     * @param searchService the service in which to search
     * @param <T> the type of object being returned
     * @return the response
     */
    public static <T> QueryResult<T> search(QueryRequest request, SearchService<T> searchService) {
        return searchService.findByQuery(request);
    }

    /**
     * Determines if a given query string is valid.
     * @param query the query string
     * @return validity of query
     */
    public static boolean isValidQuery(String query) {
        return query != null && query.trim().length() > 0;
    }

    /**
     * Determines if a specified row number is valid.
     * @param rows the row number specified
     * @return validity of row number
     */
    public static boolean isValidNumRows(int rows) {
        return rows > 0;
    }

    /**
     * Determines if a specified page number is valid.
     * @param page the page number specified
     * @return validity of page number
     */
    public static boolean isValidPage(int page) {
        return page > 0;
    }

    /**
     * Determines if a given list of facets is valid, with respect to
     * a specification of which fields are searchable. A facet is
     * valid if it is also a searchable field. This method returns {@code false}
     * if any of the specified facets are not searchable.
     * @param searchableField a specification of which fields are searchable
     * @param facets a list of facet names
     * @return validity of the facets
     */
    public static boolean isValidFacets(SearchableField searchableField, List<String> facets) {
        if (Objects.nonNull(facets)) {
            for (String facet : facets) {
                if (!searchableField.isSearchable(facet)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if a given list of filters is valid, with respect to a
     * specification of which fields are searchable. Each filter query is
     * of the form {@code "field:value"}, and a query is valid if its {@code field}
     * refers to a searchable field. This method returns {@code false}
     * if any of the specified fields are not searchable.
     * @param searchableField a specification of which fields are searchable
     * @param filterQueries a list of filter queries
     * @return validity of the filter queries
     */
    public static boolean isValidFilterQueries(SearchableField searchableField, List<String> filterQueries) {
        if (Objects.nonNull(filterQueries)) {
            for (String filterQuery : filterQueries) {
                Matcher filterQueryMatcher = VALID_FILTER_QUERY_FORMAT.matcher(filterQuery);
                if (!filterQueryMatcher.matches()
                        || !searchableField.isSearchable(filterQueryMatcher.group(1))) {
                    return false;
                }
            }
        }
        return true;
    }
}