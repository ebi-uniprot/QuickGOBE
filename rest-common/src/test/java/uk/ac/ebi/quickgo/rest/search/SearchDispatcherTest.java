package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;
import uk.ac.ebi.quickgo.rest.search.query.AllQuery;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.results.PageInfo;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformerChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.*;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createFirstCursorPage;

/**
 * Unit tests for the {@link SearchDispatcher}. Primarily tests
 * user request validation logic. Functional / integration tests are covered by
 * (higher-level) callers of {@link SearchDispatcher}'s methods.
 *
 * Created 07/04/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchDispatcherTest {
    private static class MockSearchableField implements SearchableField {

        private static final String SEARCHABLE_FIELD = "searchableField";

        @Override public boolean isSearchable(String field) {
            return field.equals(SEARCHABLE_FIELD);
        }

        @Override public Stream<String> searchableFields() {
            return Stream.empty();
        }
    }

    private MockSearchableField searchableField;

    @Before
    public void setUp() {
        this.searchableField = new MockSearchableField();
    }

    // validate query ----------------------------------------------
    @Test
    public void determinesThatQueryIsValidForProcessing() {
        assertThat(isValidQuery("query"), is(true));
    }

    @Test
    public void determinesThatNullQueryIsInvalidForProcessing() {
        assertThat(isValidQuery(null), is(false));
    }

    @Test
    public void determinesThatEmptyQueryIsInvalidForProcessing() {
        assertThat(isValidQuery(""), is(false));
    }

    // validate row num in request ----------------------------------------------
    @Test
    public void determinesThat1IsAValidRowNumsRequest() {
        assertThat(isValidNumRows(1), is(true));
    }

    @Test
    public void determinesThat0IsAnInvalidRowNumsRequest() {
        assertThat(isValidNumRows(0), is(false));
    }

    @Test
    public void determinesThatMinus1IsAnInvalidRowNumsRequest() {
        assertThat(isValidNumRows(-1), is(false));
    }

    // validate page num in request ----------------------------------------------
    @Test
    public void determinesThat1IsAValidPageNumRequest() {
        assertThat(isValidPage(1), is(true));
    }

    @Test
    public void determinesThat0IsAnInvalidPageNumRequest() {
        assertThat(isValidPage(0), is(false));
    }

    @Test
    public void determinesThatMinus1IsAnInvalidPageNumRequest() {
        assertThat(isValidPage(-1), is(false));
    }

    // validate facets ----------------------------------------------
    @Test
    public void allSearchableFieldsColonValueAreValidForFacets() {
        List<String> facets = Collections.singletonList(MockSearchableField.SEARCHABLE_FIELD);

        assertThat(isValidFacets(searchableField, facets), is(true));
    }

    @Test
    public void aNonSearchableFieldsCannotBeInFacets() {
        List<String> facets = new ArrayList<>();
        facets.add("aFieldThatDoesntExist");
        assertThat(isValidFacets(searchableField, facets), is(false));
    }

    @Test
    public void aSearchableFieldAndANonSearchableFieldsCannotBothBeInFacets() {
        List<String> facets = new ArrayList<>();

        // add a searchable, valid filter query
        facets.add(MockSearchableField.SEARCHABLE_FIELD);
        assertThat(isValidFacets(searchableField, facets), is(true));

        facets.add("aFieldThatDoesntExist"); // then add a non-searchable field
        assertThat(isValidFacets(searchableField, facets), is(false));
    }

    // validate filter queries ----------------------------------------------
    @Test
    public void allSearchableFieldsColonValueAreValidForFilterQueries() {
        List<String> filterQueries = Stream.of(MockSearchableField.SEARCHABLE_FIELD)
                .map(field -> field + ":pretendValue")
                .collect(Collectors.toList());

        assertThat(isValidFilterQueries(searchableField, filterQueries), is(true));
    }

    @Test
    public void aNonSearchableFieldsCannotBeInAFilterQuery() {
        List<String> filterQueries = new ArrayList<>();
        filterQueries.add("aFieldThatDoesntExist:value");
        assertThat(isValidFilterQueries(searchableField, filterQueries), is(false));
    }

    @Test
    public void aSearchableFieldAndANonSearchableFieldsCannotBothBeInAFilterQuery() {
        List<String> filterQueries = new ArrayList<>();

        // add a searchable, valid filter query
        filterQueries.add(MockSearchableField.SEARCHABLE_FIELD + ":value");
        assertThat(isValidFilterQueries(searchableField, filterQueries), is(true));

        filterQueries.add("aFieldThatDoesntExist:value"); // then add a non-searchable field
        assertThat(isValidFilterQueries(searchableField, filterQueries), is(false));
    }

    @Mock
    private QueryRequest queryRequest;

    private DefaultSearchQueryTemplate queryTemplate = new DefaultSearchQueryTemplate();

    @Mock
    private SearchService<String> searchService;

    private ResultTransformerChain<QueryResult<String>> transformer =
            new ResultTransformerChain<QueryResult<String>>() {
                @Override public QueryResult<String> applyTransformations(QueryResult<String> result,
                        FilterContext filterContext) {
                    return result;
                }
            };

    @Mock
    private FilterContext context;

    @Test
    public void streamingSearchResultsCreatesStreamCorrectly() {
        /**
         * QueryRequest queryRequest,
         DefaultSearchQueryTemplate queryTemplate,
         SearchService<T> searchService,
         ResultTransformerChain<QueryResult<T>> transformer,
         FilterContext context
         */

        Stream<QueryResult<String>> resultStream = streamSearchResults(
                queryRequest,
                queryTemplate,
                searchService,
                transformer,
                context,
                10);
        System.out.println(resultStream);

        int increment = 3;
        int max = 1;
        int iterations = (int) Math.ceil((double) max / increment);
        List<Integer> firstList = rangeList(0, increment);
        MutableValue<Integer> nextStart = new MutableValue<>(firstList.size());

        Stream.iterate(
                firstList,
                i -> {
                    int actualIncrement;
                    if (nextStart.getValue() + increment < max) {
                        actualIncrement = increment;
                    } else {
                        actualIncrement = max - nextStart.getValue();
                    }
                    List<Integer> nextList = rangeList(nextStart.getValue(), actualIncrement);

                    nextStart.setValue(nextStart.getValue() + nextList.size());

                    return nextList;
                }).limit(iterations).forEach(System.out::println);
    }

    @Test
    public void checkStreamingResultsOneIterationExactly() {
        int pageSize = 10;
        QueryResult<String> firstResult =
                new QueryResult.Builder<>(10, rangeStringList(1, 10))
                        .withPageInfo(new PageInfo.Builder().withNextCursor("*").build())
                        .build();
        when(searchService.findByQuery(any()))
                .thenReturn(firstResult);
        when(queryRequest.getPage()).thenReturn(createFirstCursorPage(pageSize));
        Stream<QueryResult<String>> resultStream = streamSearchResults(
                queryRequest,
                queryTemplate,
                searchService,
                transformer,
                context,
                10);
        resultStream.map(QueryResult::getResults).forEach(System.out::println);
    }

    @Test
    public void checkStreamingResultsTwoIterationsExactly() {
        when(queryRequest.getQuery()).thenReturn(new AllQuery());
        when(queryRequest.getFilters()).thenReturn(emptyList());
        when(queryRequest.getAggregate()).thenReturn(new AggregateRequest("value"));
        when(queryRequest.getSortCriteria()).thenReturn(emptyList());

        int pageSize = 10;
        String secondCursor = "secondCursor";
        QueryResult<String> firstResult =
                new QueryResult.Builder<>(20, rangeStringList(1, 10))
                        .withPageInfo(new PageInfo.Builder().withNextCursor(secondCursor).build())
                        .build();

        QueryResult<String> secondResult =
                new QueryResult.Builder<>(20, rangeStringList(1, 10))
                        .withPageInfo(new PageInfo.Builder().withNextCursor(secondCursor).build())
                        .build();

        when(searchService.findByQuery(any()))
                .thenReturn(firstResult)
                .thenReturn(secondResult);

        when(queryRequest.getPage()).thenReturn(createFirstCursorPage(pageSize));
        Stream<QueryResult<String>> resultStream = streamSearchResults(
                queryRequest,
                queryTemplate,
                searchService,
                transformer,
                context,
                20);
        resultStream.map(QueryResult::getResults).forEach(System.out::println);
    }

    @Test
    public void checkStreamingResultsTwoIterations() {
        when(queryRequest.getQuery()).thenReturn(new AllQuery());
        when(queryRequest.getFilters()).thenReturn(emptyList());
        when(queryRequest.getAggregate()).thenReturn(new AggregateRequest("value"));
        when(queryRequest.getSortCriteria()).thenReturn(emptyList());

        int pageSize = 10;
        int startElement = 1;
        int limit = 15;
        int lastPageSize = limit - pageSize;
        String secondCursor = "secondCursor";

        QueryResult<String> firstResult =
                new QueryResult.Builder<>(18, rangeStringList(startElement, pageSize))
                        .withPageInfo(new PageInfo.Builder().withNextCursor(secondCursor).build())
                        .build();

        QueryResult<String> secondResult =
                new QueryResult.Builder<>(18, rangeStringList(startElement, lastPageSize))
                        .withPageInfo(new PageInfo.Builder().withNextCursor(secondCursor).build())
                        .build();

        when(searchService.findByQuery(any()))
                .thenReturn(firstResult)
                .thenReturn(secondResult);

        when(queryRequest.getPage()).thenReturn(createFirstCursorPage(pageSize));
        Stream<QueryResult<String>> resultStream = streamSearchResults(
                queryRequest,
                queryTemplate,
                searchService,
                transformer,
                context,
                limit);

        assertThat(
                resultStream.map(QueryResult::getResults)
                        .flatMap(Collection::stream)
                        .peek(System.out::println)
                        .collect(Collectors.toList()),
                hasSize(limit));

        ArgumentCaptor<QueryRequest> argument = ArgumentCaptor.forClass(QueryRequest.class);

        verify(searchService, times(2)).findByQuery(argument.capture());
        assertThat(argument.getAllValues().get(0).getPage().getPageSize(), is(10));
        assertThat(argument.getAllValues().get(1).getPage().getPageSize(), is(5));
    }

    private List<Integer> rangeList(int start, int size) {
        return IntStream.range(start, start + size).boxed().collect(Collectors.toList());
    }

    private List<String> rangeStringList(int start, int size) {
        return IntStream.range(start, start + size).boxed().map(i -> "element " + i).collect(Collectors.toList());
    }

    class MutableValue<V> {
        private V value;

        private MutableValue(V value) {
            setValue(value);
        }

        void setValue(V value) {
            this.value = value;
        }

        V getValue() {
            return this.value;
        }
    }
}