package uk.ac.ebi.quickgo.rest.search.results;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link QueryResult} implementation
 */
class QueryResultTest {

    @Test
    void negativeTotalNumberResultsThrowsException()  {
        long numberOfHits = -1;
        List<String> results = Collections.emptyList();
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new QueryResult.Builder<>(numberOfHits, results).build());
        assertTrue(exception.getMessage().contains("Total number of hits can not be negative"));
    }

    @Test
    void nullResultsListThrowsException()  {
        long numberOfHits = 1;
        List<String> results = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new QueryResult.Builder<>(numberOfHits, results).build());
        assertTrue(exception.getMessage().contains("Results list can not be null"));
    }

    @Test
    void totalNumberOfHitsLessThanResultsListSizeThrowsException()  {
        long numberOfHits = 1;
        List<String> results = Arrays.asList("result1", "result2");
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new QueryResult.Builder<>(numberOfHits, results).build());
        assertTrue(exception.getMessage().contains("Total number of results is less than number of results in list"));
    }

    @Test
    void validQueryResultWithNoPageInfoAndNoFacet()  {
        long numberOfHits = 2;
        List<String> results = Arrays.asList("result1", "result2");

        QueryResult<String> result = new QueryResult.Builder<>(numberOfHits, results).build();

        assertThat(result.getNumberOfHits(), is(numberOfHits));
        assertThat(result.getResults(), is(results));
        assertThat(result.getPageInfo(), is(nullValue()));
        assertThat(result.getFacet(), is(nullValue()));
        assertThat(result.getHighlighting(), is(emptyIterable()));
    }

    @Test
    void validFullQueryResult()  {
        long numberOfHits = 2;
        List<String> results = Arrays.asList("result1", "result2");
        PageInfo pageInfo = new PageInfo.Builder()
                .withTotalPages(1)
                .withCurrentPage(1)
                .withResultsPerPage(5)
                .build();
        Facet facet = new Facet();
        List<DocHighlight> highlights = new ArrayList<>();

        QueryResult<String> result = new QueryResult.Builder<>(numberOfHits, results)
                .withPageInfo(pageInfo)
                .withFacets(facet)
                .appendHighlights(highlights)
                .build();

        assertThat(result.getNumberOfHits(), is(numberOfHits));
        assertThat(result.getResults(), is(results));
        assertThat(result.getPageInfo(), is(pageInfo));
        assertThat(result.getFacet(), is(facet));
        assertThat(result.getHighlighting(), is(highlights));
    }
}