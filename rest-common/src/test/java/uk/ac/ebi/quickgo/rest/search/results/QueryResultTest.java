package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.results.DocHighlight;
import uk.ac.ebi.quickgo.rest.search.results.Facet;
import uk.ac.ebi.quickgo.rest.search.results.PageInfo;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests the {@link QueryResult} implementation
 */
public class QueryResultTest {

    @Test
    public void negativeTotalNumberResultsThrowsException() throws Exception {
        long numberOfHits = -1;
        List<String> results = Collections.emptyList();
        uk.ac.ebi.quickgo.rest.search.results.PageInfo pageInfo = null;
        Facet facet = null;
        List<DocHighlight> highlights = null;

        try {
            new QueryResult<>(numberOfHits, results, pageInfo, facet, highlights);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("Total number of hits can not be negative"));
        }
    }

    @Test
    public void nullResultsListThrowsException() throws Exception {
        long numberOfHits = 1;
        List<String> results = null;
        uk.ac.ebi.quickgo.rest.search.results.PageInfo pageInfo = null;
        Facet facet = null;
        List<DocHighlight> highlights = null;

        try {
            new QueryResult<>(numberOfHits, results, pageInfo, facet, highlights);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("Results list can not be null"));
        }
    }

    @Test
    public void totalNumberOfHitsLessThanResultsListSizeThrowsException() throws Exception {
        long numberOfHits = 1;
        List<String> results = Arrays.asList("result1", "result2");
        uk.ac.ebi.quickgo.rest.search.results.PageInfo pageInfo = null;
        Facet facet = null;
        List<DocHighlight> highlights = null;

        try {
            new QueryResult<>(numberOfHits, results, pageInfo, facet, highlights);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("Total number of results is less than number of results in list"));
        }
    }

    @Test
    public void validQueryResultWithNoPageInfoAndNoFacet() throws Exception {
        long numberOfHits = 2;
        List<String> results = Arrays.asList("result1", "result2");
        uk.ac.ebi.quickgo.rest.search.results.PageInfo pageInfo = null;
        Facet facet = null;
        List<DocHighlight> highlights = null;

        QueryResult<String> result = new QueryResult<>(numberOfHits, results, pageInfo, facet, highlights);

        assertThat(result.getNumberOfHits(), is(numberOfHits));
        assertThat(result.getResults(), is(results));
        assertThat(result.getPageInfo(), is(nullValue()));
        assertThat(result.getFacet(), is(nullValue()));
        assertThat(result.getHighlighting(), is(nullValue()));
    }

    @Test
    public void validFullQueryResult() throws Exception {
        long numberOfHits = 2;
        List<String> results = Arrays.asList("result1", "result2");
        uk.ac.ebi.quickgo.rest.search.results.PageInfo pageInfo = new PageInfo(1, 1, 5);
        Facet facet = new Facet();
        List<DocHighlight> highlights = new ArrayList<>();

        QueryResult<String> result = new QueryResult<>(numberOfHits, results, pageInfo, facet, highlights);

        assertThat(result.getNumberOfHits(), is(numberOfHits));
        assertThat(result.getResults(), is(results));
        assertThat(result.getPageInfo(), is(pageInfo));
        assertThat(result.getFacet(), is(facet));
        assertThat(result.getHighlighting(), is(highlights));
    }
}