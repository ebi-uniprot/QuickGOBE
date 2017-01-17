package uk.ac.ebi.quickgo.rest.search.results;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests the {@link QueryResult} implementation
 */
public class QueryResultTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void negativeTotalNumberResultsThrowsException() throws Exception {
        long numberOfHits = -1;
        List<String> results = Collections.emptyList();

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Total number of hits can not be negative");

        new QueryResult.Builder<>(numberOfHits, results).build();
    }

    @Test
    public void nullResultsListThrowsException() throws Exception {
        long numberOfHits = 1;
        List<String> results = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Results list can not be null");

        new QueryResult.Builder<>(numberOfHits, results).build();
    }

    @Test
    public void totalNumberOfHitsLessThanResultsListSizeThrowsException() throws Exception {
        long numberOfHits = 1;
        List<String> results = Arrays.asList("result1", "result2");

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Total number of results is less than number of results in list");

        new QueryResult.Builder<>(numberOfHits, results).build();
    }

    @Test
    public void validQueryResultWithNoPageInfoAndNoFacet() throws Exception {
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
    public void validFullQueryResult() throws Exception {
        long numberOfHits = 2;
        List<String> results = Arrays.asList("result1", "result2");
        uk.ac.ebi.quickgo.rest.search.results.PageInfo pageInfo = new PageInfo.Builder()
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