package uk.ac.ebi.quickgo.rest.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.ac.ebi.quickgo.common.SolrCollectionName;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.query.*;
import uk.ac.ebi.quickgo.rest.search.results.PageInfo;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformerChain;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.*;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createFirstCursorPage;

/**
 * Tests the streaming of search results provided by {@link SearchDispatcher}.
 * <p>
 * Created 20/01/17
 *
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SearchDispatcherStreamedResultsTest {
    private static final String COLLECTION = SolrCollectionName.COLLECTION;
    @Mock
    private QueryRequest queryRequest;

    @Mock
    private SearchService<String> searchService;

    @Mock
    private FilterContext context;

    private DefaultSearchQueryTemplate queryTemplate = new DefaultSearchQueryTemplate();

    private ResultTransformerChain<QueryResult<String>> transformer;

    @BeforeEach
    void setUp() {
        transformer = new IdentityResultsTransformationChain();
        when(queryRequest.getQuery()).thenReturn(new AllQuery());
        when(queryRequest.getCollection()).thenReturn(COLLECTION);
        when(queryRequest.getFilters()).thenReturn(emptyList());
        when(queryRequest.getAggregate()).thenReturn(new AggregateRequest("value"));
        when(queryRequest.getSortCriteria()).thenReturn(emptyList());
    }

    @Test
    void nullQueryRequestProducesEmptyStream() {
        Stream<QueryResult<String>> resultStream = streamSearchResults(
                null,
                queryTemplate,
                searchService,
                transformer,
                context,
                10);

        assertStreamHasCorrectNumberOfResults(resultStream, 0);
    }

    @Test
    void checkStreamingResultsOneIterationExactly() {
        int startElement = 1;
        int pageSize = 10;
        int limit = 10;
        int hitCount = 10;

        QueryResult<String> firstResult =
                new QueryResult.Builder<>(hitCount, rangeStringList(startElement, pageSize))
                        .withPageInfo(new PageInfo.Builder().withNextCursor("anything").build())
                        .build();

        when(searchService.findByQuery(any())).thenReturn(firstResult);

        when(queryRequest.getPage()).thenReturn(createFirstCursorPage(pageSize));
        Stream<QueryResult<String>> resultStream = getQueryResultStream(limit);

        assertStreamHasCorrectNumberOfResults(resultStream, limit);

        ArgumentCaptor<QueryRequest> argument = ArgumentCaptor.forClass(QueryRequest.class);

        verify(searchService, times(1)).findByQuery(argument.capture());
        assertThat(argument.getValue().getPage().getPageSize(), is(limit));
    }

    private void assertStreamHasCorrectNumberOfResults(Stream<QueryResult<String>> resultStream, int limit) {
        assertThat(
                resultStream.map(QueryResult::getResults)
                        .flatMap(Collection::stream)
                        .peek(System.out::println)
                        .collect(Collectors.toList()),
                hasSize(limit));
    }

    @Test
    void checkStreamingResultsOneIteration() {
        int startElement = 1;
        int pageSize = 10;
        int limit = 9;
        int hitCount = 100;

        QueryResult<String> firstResult =
                new QueryResult.Builder<>(hitCount, rangeStringList(startElement, pageSize))
                        .withPageInfo(new PageInfo.Builder().withNextCursor("anything").build())
                        .build();

        when(searchService.findByQuery(any())).thenReturn(firstResult);

        when(queryRequest.getPage()).thenReturn(createFirstCursorPage(pageSize));
        Stream<QueryResult<String>> resultStream = getQueryResultStream(limit);

        assertStreamHasCorrectNumberOfResults(resultStream, limit);

        ArgumentCaptor<QueryRequest> argument = ArgumentCaptor.forClass(QueryRequest.class);

        verify(searchService, times(1)).findByQuery(argument.capture());
        assertThat(argument.getValue().getPage().getPageSize(), is(pageSize));
    }

    @Test
    void checkStreamingResultsTwoIterationsExactly() {
        int startElement = 1;
        int pageSize = 10;
        int limit = 20;
        int hitCount = 20;
        int lastPageSize = limit - pageSize;
        String secondCursor = "secondCursor";

        QueryResult<String> firstResult =
                new QueryResult.Builder<>(hitCount, rangeStringList(startElement, pageSize))
                        .withPageInfo(new PageInfo.Builder().withNextCursor(secondCursor).build())
                        .build();

        QueryResult<String> secondResult =
                new QueryResult.Builder<>(hitCount, rangeStringList(startElement, lastPageSize))
                        .withPageInfo(new PageInfo.Builder().withNextCursor(secondCursor).build())
                        .build();

        when(searchService.findByQuery(any()))
                .thenReturn(firstResult)
                .thenReturn(secondResult);

        when(queryRequest.getPage()).thenReturn(createFirstCursorPage(pageSize));
        Stream<QueryResult<String>> resultStream = getQueryResultStream(limit);

        assertStreamHasCorrectNumberOfResults(resultStream, limit);

        ArgumentCaptor<QueryRequest> argument = ArgumentCaptor.forClass(QueryRequest.class);

        verify(searchService, times(2)).findByQuery(argument.capture());
        assertThat(argument.getAllValues().get(0).getPage().getPageSize(), is(10));
        assertThat(argument.getAllValues().get(1).getPage().getPageSize(), is(10));
    }

    @Test
    void checkStreamingResultsTwoIterations() {
        int startElement = 1;
        int pageSize = 10;
        int limit = 15;
        int hitCount = 18;
        int lastPageSize = limit - pageSize;
        String secondCursor = "secondCursor";

        QueryResult<String> firstResult =
                new QueryResult.Builder<>(hitCount, rangeStringList(startElement, pageSize))
                        .withPageInfo(new PageInfo.Builder().withResultsPerPage(pageSize).withNextCursor(secondCursor).build())
                        .build();

        QueryResult<String> secondResult =
                new QueryResult.Builder<>(hitCount, rangeStringList(startElement, lastPageSize))
                        .withPageInfo(new PageInfo.Builder().withResultsPerPage(lastPageSize).withNextCursor(secondCursor).build())
                        .build();

        when(searchService.findByQuery(any()))
                .thenReturn(firstResult)
                .thenReturn(secondResult);

        when(queryRequest.getPage()).thenReturn(createFirstCursorPage(pageSize));
        Stream<QueryResult<String>> resultStream = getQueryResultStream(limit);

        assertStreamHasCorrectNumberOfResults(resultStream, limit);

        ArgumentCaptor<QueryRequest> argument = ArgumentCaptor.forClass(QueryRequest.class);

        verify(searchService, times(2)).findByQuery(argument.capture());
        assertThat(argument.getAllValues().get(0).getPage().getPageSize(), is(10));
        assertThat(argument.getAllValues().get(1).getPage().getPageSize(), is(5));
    }

    @Test
    void createsCorrectlyNextQueryRequest() {
        QuickGOQuery query = new AllQuery();
        String sortField1 = "sortField1";
        SortCriterion.SortOrder sortOrder1 = SortCriterion.SortOrder.ASC;
        String sortField2 = "sortField2";
        SortCriterion.SortOrder sortOrder2 = SortCriterion.SortOrder.DESC;

        AggregateRequest aggregate = new AggregateRequest("aggregateName");
        List<Facet> facets = singletonList(new Facet("facet1"));
        List<QuickGOQuery> filters = singletonList(new FieldQuery("field", "value"));
        int pageSize = 10;
        int nextPageSize = 4;
        String nextCursor = "nextCursor";

        QueryRequest queryRequest = queryTemplate.newBuilder()
                .setQuery(query)
                .setCollection(COLLECTION)
                .addSortCriterion(sortField1, sortOrder1)
                .addSortCriterion(sortField2, sortOrder2)
                .setAggregate(aggregate)
                .setPage(createFirstCursorPage(pageSize))
                .addFacets(extractStrings(facets, AbstractField::getField))
                .addFilters(filters)
                .build();

        QueryRequest nextQueryRequest = createNextCursorQueryRequest(queryTemplate, queryRequest, nextCursor, nextPageSize);

        assertThat(
                nextQueryRequest.getSortCriteria(),
                contains(
                        new SortCriterion(sortField1, sortOrder1),
                        new SortCriterion(sortField2, sortOrder2)));
        assertThat(nextQueryRequest.getQuery(), is(query));
        assertThat(nextQueryRequest.getAggregate(), is(aggregate));
        assertThat(
                extractStrings(nextQueryRequest.getFacets(), AbstractField::getField),
                is(extractStrings(facets, AbstractField::getField)));
        assertThat(
                extractStrings(nextQueryRequest.getFilters(), QuickGOQuery::toString),
                is(extractStrings(filters, QuickGOQuery::toString)));
        assertThat(nextQueryRequest.getPage().getPageSize(), is(nextPageSize));
        assertThat(((CursorPage) nextQueryRequest.getPage()).getCursor(), is(nextCursor));
    }

    @Test
    void resultsNotResizedWhenTheirNumberIsTheSameAsLimit() {
        QueryResult<String> result =
                new QueryResult.Builder<>(100, rangeStringList(1, 100))
                        .withPageInfo(new PageInfo.Builder().withNextCursor("anything").build())
                        .build();
        QueryResult<String> resizedResults = resizeResultsIfRequired(result, 100);

        assertThat(resizedResults.getResults(), hasSize(100));
    }

    @Test
    void resultsNotResizedWhenTheirNumberIsLessThanLimit() {
        QueryResult<String> result =
                new QueryResult.Builder<>(100, rangeStringList(1, 100))
                        .withPageInfo(new PageInfo.Builder().withNextCursor("anything").build())
                        .build();
        QueryResult<String> resizedResults = resizeResultsIfRequired(result, 500);

        assertThat(resizedResults.getResults(), hasSize(100));
    }

    @Test
    void resultsResizedWhenTheirNumberIsGreaterThanLimit() {
        QueryResult<String> result =
                new QueryResult.Builder<>(100, rangeStringList(1, 100))
                        .withPageInfo(new PageInfo.Builder().withNextCursor("anything").build())
                        .build();
        QueryResult<String> resizedResults = resizeResultsIfRequired(result, 80);

        assertThat(resizedResults.getResults(), hasSize(80));
    }

    @Test
    void checkGetNextPageSizeFunctionsCorrectly() {
        assertThat(getNextPageSize(0, 100, 10), is(10));
        assertThat(getNextPageSize(0, 10, 10), is(10));
        assertThat(getNextPageSize(0, 9, 10), is(9));
        assertThat(getNextPageSize(1, 8, 10), is(7));
        assertThat(getNextPageSize(100, 100, 10), is(0));
    }

    @Test
    void checkGetRequiredIterationsFunctionsCorrectly() {
        assertThat(getRequiredNumberOfPagesToFetch(10, 1000, 10), is(1));
        assertThat(getRequiredNumberOfPagesToFetch(10, 10, 10), is(1));
        assertThat(getRequiredNumberOfPagesToFetch(10, 9, 10), is(1));
        assertThat(getRequiredNumberOfPagesToFetch(10, 9, 11), is(1));
        assertThat(getRequiredNumberOfPagesToFetch(10, 100, 11), is(2));
        assertThat(getRequiredNumberOfPagesToFetch(5000, 350000000, 50000), is(10));
        assertThat(getRequiredNumberOfPagesToFetch(10, 88, 100), is(9));
    }

    private Stream<QueryResult<String>> getQueryResultStream(int limit) {
        return streamSearchResults(
                queryRequest,
                queryTemplate,
                searchService,
                transformer,
                context,
                limit);
    }

    private <T> List<String> extractStrings(Collection<T> fields, Function<T, String> toStringFunction) {
        return fields.stream().map(toStringFunction).collect(Collectors.toList());
    }

    private List<String> rangeStringList(int start, int size) {
        return IntStream.range(start, start + size).boxed().map(i -> "element " + i).collect(Collectors.toList());
    }

    private static class IdentityResultsTransformationChain extends ResultTransformerChain<QueryResult<String>> {
        @Override
        public QueryResult<String> applyTransformations(QueryResult<String> result, FilterContext filterContext) {
            return result;
        }
    }
}