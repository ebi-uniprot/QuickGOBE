package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.query.AbstractField;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.results.PageInfo;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformerChain;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.FIRST_CURSOR;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createCursorPage;

/**
 * Helper class that dispatches search requests, in the form of
 * {@link QueryRequest} instances, to a {@link SearchService}.
 * <p>
 * Validity check methods are also provided for common search parameters.
 * Note that this class is stateless.
 * <p>
 * Created 27/01/16
 *
 * @author Edd
 */
public final class SearchDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchDispatcher.class);

    private static final Pattern VALID_FILTER_QUERY_FORMAT = Pattern.compile("(\\w+):(\\w|-)+");

    private SearchDispatcher() {
    }

    /**
     * Dispatch a {@link QueryRequest} to a {@link SearchService} and handle its responses
     * appropriately.
     * <ul>
     * <li>If the request is {@code null} a {@link ResponseEntity} denoting an HTTP
     * {@code BAD REQUEST (400)}, is returned</li>
     * <li>If the service responds successfully, a {@link ResponseEntity} is returned
     * containing the {@link QueryResult}.</li>
     * <li>If an error occurs when processing the response, a
     * {@link ResponseEntity}, denoting an HTTP {@code INTERNAL SERVER ERROR (500)}, is
     * returned</li>
     * </ul>
     *
     * @param request       the request
     * @param searchService the service in which to search
     * @param <T>           the type of object being returned
     * @return the response
     */
    public static <T> ResponseEntity<QueryResult<T>> search(QueryRequest request, SearchService<T> searchService) {
        ResponseEntity<QueryResult<T>> response;

        if (request == null) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            try {
                QueryResult<T> queryResult = searchService.findByQuery(request);
                response = new ResponseEntity<>(queryResult, HttpStatus.OK);
            } catch (RetrievalException e) {
                LOGGER.error(createErrorMessage(request), e);
                throw e;
            }
        }

        return response;
    }

    /**
     * Dispatch a {@link QueryRequest} to a {@link SearchService} and handle its responses
     * appropriately.
     * <ul>
     * <li>If the request is {@code null} a {@link ResponseEntity} denoting an HTTP
     * {@code BAD REQUEST (400)}, is returned</li>
     * <li>If the service responds successfully, a {@link ResponseEntity} is returned
     * containing the {@link QueryResult}.</li>
     * <li>If an error occurs when processing the response, a
     * {@link ResponseEntity}, denoting an HTTP {@code INTERNAL SERVER ERROR (500)}, is
     * returned</li>
     * </ul>
     *
     * @param request       the request
     * @param searchService the service in which to search
     * @param transformer   the transformations to apply to the results
     * @param context       data made available to the result transformations
     * @param <T>           the type of object being returned
     * @return the response
     */
    public static <T> ResponseEntity<QueryResult<T>> searchAndTransform(
            QueryRequest request,
            SearchService<T> searchService,
            ResultTransformerChain<QueryResult<T>> transformer,
            FilterContext context) {
        ResponseEntity<QueryResult<T>> response;

        if (request == null) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            try {
                QueryResult<T> queryResult = transformer.applyTransformations(searchService.findByQuery(request),
                        context);
                response = new ResponseEntity<>(queryResult, HttpStatus.OK);
            } catch (RetrievalException e) {
                LOGGER.error(createErrorMessage(request), e);
                throw e;
            }
        }

        return response;
    }

    /**
     * <p>Dispatch a request to a {@link SearchService} and return a stream of
     * its results.
     *
     * <p>If the number of required results are greater than the page size used
     * in the request, this method makes use of the request/response cursor
     * to iterate through the results (i.e., via multiple requests) and stream
     * them back to the caller.
     *
     * @param firstQueryRequest the {@link QueryRequest}
     * @param queryTemplate the template used to build any required subsequent {@link QueryRequest}s
     * @param searchService the service which is to be searched
     * @param transformer the result transformer
     * @param context data made available to the result transformations
     * @param limit the number of results to be streamed back to the caller
     * @param <T> the type of the {@link QueryResult}
     * @return a stream of {@link QueryResult} instances, containing up to {@code limit} results in total
     */
    public static <T> Stream<QueryResult<T>> streamSearchResults(
            QueryRequest firstQueryRequest,
            DefaultSearchQueryTemplate queryTemplate,
            SearchService<T> searchService,
            ResultTransformerChain<QueryResult<T>> transformer,
            FilterContext context,
            int limit) {

        Stream<QueryResult<T>> resultStream;

        if (firstQueryRequest == null) {
            resultStream = Stream.empty();
        } else {
            try {
                final QueryResult<T> firstQueryResult = searchService.findByQuery(firstQueryRequest);
                int pageSize = firstQueryRequest.getPage().getPageSize();

                QueryResult<T> firstTransformedQueryResult = transformer.applyTransformations(firstQueryResult, context);
                long totalHits = firstTransformedQueryResult.getNumberOfHits();

                firstTransformedQueryResult = resizeResultsIfRequired(firstTransformedQueryResult, limit);
                MutableValue<String> cursor = new MutableValue<>(FIRST_CURSOR);
                MutableValue<Integer> fetchedCount = new MutableValue<>(0);
                int requiredIterations = getRequiredNumberOfPagesToFetch(pageSize, totalHits, limit);

                resultStream = Stream.iterate(firstTransformedQueryResult, qr -> {
                    String nextCursor = qr.getPageInfo().getNextCursor();
                    if (isCursorAtEnd(cursor.getValue(), nextCursor)) {
                        return qr;
                    } else {
                        cursor.setValue(nextCursor);

                        incrementFetchedCount(fetchedCount, qr);
                        int nextPageSize = getNextPageSize(fetchedCount.getValue(), limit, pageSize);
                        QueryRequest nextQueryRequest =
                                createNextCursorQueryRequest(queryTemplate, firstQueryRequest, nextCursor, nextPageSize);

                        return transformer.applyTransformations(
                                searchService.findByQuery(nextQueryRequest),
                                context);
                    }
                }).limit(requiredIterations);
            } catch (RetrievalException e) {
                LOGGER.error(createErrorMessage(firstQueryRequest), e);
                throw e;
            }
        }

        return resultStream;
    }

    /**
     * Finds the size of the next page, that can be used in a {@link QueryRequest}, given
     * that one has already fetched {@code fetchedCount} results, wants {@code limit} results
     * and is using a current page size of {@code pageSize}.
     *
     * @param fetchedCount the number of results already fetched
     * @param limit the number of results to fetch
     * @param pageSize the current page size being used to fetch results
     * @return the page size of the next page of results to fetch
     */
    static int getNextPageSize(int fetchedCount, int limit, int pageSize) {
        int itemsStillRequired = limit - fetchedCount;
        if (itemsStillRequired > pageSize) {
            return pageSize;
        } else {
            return itemsStillRequired;
        }
    }

    private static <T> void incrementFetchedCount(MutableValue<Integer> fetchedCount, QueryResult<T> qr) {
        fetchedCount.setValue(fetchedCount.getValue() + qr.getPageInfo().getResultsPerPage());
    }

    /**
     * Finds the number of pages of {@code pageSize} that are required to fetch {@code limit}
     * results, given that there are {@code totalHits}.
     * @param pageSize the page size
     * @param totalHits the total number of hits available to fetch
     * @param limit the desired number of results to fetch
     * @return the number of pages required to fetch {@code limit} results.
     */
    static int getRequiredNumberOfPagesToFetch(int pageSize, long totalHits, int limit) {
        int maxResultsToFetch = totalHits < limit ?
                (int) totalHits : limit;
        return (int) Math.ceil((double) maxResultsToFetch / pageSize);
    }

    /**
     * Determines if a given query string is valid.
     *
     * @param query the query string
     * @return validity of query
     */
    public static boolean isValidQuery(String query) {
        return query != null && query.trim().length() > 0;
    }

    /**
     * Determines if a specified row number is valid.
     *
     * @param rows the row number specified
     * @return validity of row number
     */
    public static boolean isValidNumRows(int rows) {
        return rows > 0;
    }

    /**
     * Determines if a specified page number is valid.
     *
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
     *
     * @param searchableField a specification of which fields are searchable
     * @param facets          the facet names
     * @return validity of the facets
     */
    public static boolean isValidFacets(SearchableField searchableField, Iterable<String> facets) {
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
     *
     * @param searchableField a specification of which fields are searchable
     * @param filterQueries   the filter queries
     * @return validity of the filter queries
     */
    public static boolean isValidFilterQueries(SearchableField searchableField, Iterable<String> filterQueries) {
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

    private static boolean isCursorAtEnd(String cursor, String nextCursor) {
        return cursor.equals(nextCursor);
    }

    /**
     * Creates the next {@link QueryRequest} in a sequence of cursored requests
     * being made to satisfy an initial request. This next request is based primarily
     * on the initial request, but differs in its {@code nextCursor} value.
     *
     * @param queryTemplate the builder used to create (partially pre-populated) {@link QueryRequest}s
     * @param queryRequest  the initial {@link QueryRequest}
     * @param nextCursor    the next cursor
     * @return the next {@link QueryRequest}
     */
    static QueryRequest createNextCursorQueryRequest(
            DefaultSearchQueryTemplate queryTemplate,
            QueryRequest queryRequest,
            String nextCursor,
            int nextPageSize) {
        DefaultSearchQueryTemplate.Builder queryRequestBuilder = queryTemplate.newBuilder()
                .setQuery(queryRequest.getQuery())
                .addFilters(queryRequest.getFilters())
                .addFacets(queryRequest.getFacets().stream().map(AbstractField::getField).collect(Collectors.toList()))
                .setAggregate(queryRequest.getAggregate())
                .setPage(createCursorPage(nextCursor, nextPageSize));
        queryRequest.getSortCriteria()
                .forEach(criterion ->
                        queryRequestBuilder
                                .addSortCriterion(criterion.getSortField().getField(), criterion.getSortOrder()));
        return queryRequestBuilder.build();
    }

    /**
     * Retains only the first {@code limit} elements of the results stored in {@link QueryResult}.
     *
     * @param queryResult the {@link QueryResult} whose list of results should be adjusted in size
     * @param limit       the number of results in {@code queryResult} to retain
     * @param <T>         the type of result stored in {@code queryResult}
     * @return the adjusted {@code queryResult}
     */
    static <T> QueryResult<T> resizeResultsIfRequired(QueryResult<T> queryResult, int limit) {
        if (queryResult.getResults().size() <= limit) {
            return queryResult;
        } else {
            return new QueryResult.Builder<>(
                    queryResult.getNumberOfHits(),
                    queryResult.getResults().subList(0, limit))
                    .withFacets(queryResult.getFacet())
                    .withPageInfo(new PageInfo.Builder()
                            .withNextCursor(queryResult.getPageInfo().getNextCursor())
                            .withResultsPerPage(limit)
                            .withTotalPages(queryResult.getPageInfo().getTotal())
                            .build())
                    .withAggregation(queryResult.getAggregation())
                    .build();
        }
    }

    private static String createErrorMessage(QueryRequest request) {
        return "Unable to process search query request: [" + request + "]";
    }

    private static class MutableValue<V> {
        private V value;

        private MutableValue(V value) {
            setValue(value);
        }

        V getValue() {
            return this.value;
        }

        void setValue(V value) {
            this.value = value;
        }
    }
}
