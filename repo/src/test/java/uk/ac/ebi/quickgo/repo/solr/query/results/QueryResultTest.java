package uk.ac.ebi.quickgo.repo.solr.query.results;

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
        PageInfo pageInfo = null;
        Facet facet = null;

        try {
            new QueryResult<>(numberOfHits, results, pageInfo, facet);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("Total number of hits can not be negative"));
        }
    }

    @Test
    public void nullResultsListThrowsException() throws Exception {
        long numberOfHits = 1;
        List<String> results = null;
        PageInfo pageInfo = null;
        Facet facet = null;

        try {
            new QueryResult<>(numberOfHits, results, pageInfo, facet);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("Results list can not be null"));
        }
    }

    @Test
    public void totalNumberOfHitsLessThanResultsListSizeThrowsException() throws Exception {
        long numberOfHits = 1;
        List<String> results = Arrays.asList("result1", "result2");
        PageInfo pageInfo = null;
        Facet facet = null;

        try {
            new QueryResult<>(numberOfHits, results, pageInfo, facet);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("Total number of results is less than number of results in list"));
        }
    }

    @Test
    public void validQueryResultWithNoPageInfoAndNoFacet() throws Exception {
        long numberOfHits = 2;
        List<String> results = Arrays.asList("result1", "result2");
        PageInfo pageInfo = null;
        Facet facet = null;

        QueryResult<String> result = new QueryResult<>(numberOfHits, results, pageInfo, facet);

        assertThat(result.getNumberOfHits(), is(numberOfHits));
        assertThat(result.getResults(), is(results));
        assertThat(result.getPageInfo(), is(nullValue()));
        assertThat(result.getFacet(), is(nullValue()));
    }

    @Test
    public void validFullQueryResultW() throws Exception {
        long numberOfHits = 2;
        List<String> results = Arrays.asList("result1", "result2");
        PageInfo pageInfo = new PageInfo(1, 1, 5);
        Facet facet = new Facet();

        QueryResult<String> result = new QueryResult<>(numberOfHits, results, pageInfo, facet);

        assertThat(result.getNumberOfHits(), is(numberOfHits));
        assertThat(result.getResults(), is(results));
        assertThat(result.getPageInfo(), is(pageInfo));
        assertThat(result.getFacet(), is(facet));
    }
}