package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link AggregateSearchQueryTemplate} class.
 */
public class AggregateSearchQueryTemplateTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void creatingAggregateBuilderInstanceWithNullCompositeBuilderFails() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Composite builder cannot be null");

        createBuilder(null);
    }

    @Test
    public void aggregateBuilderInheritsConfigurationFromCompositeBuilder() throws Exception {
        int page = 1;
        int pageSize = 25;

        FakeBuilder compositeBuilder = new FakeBuilder().setPage(page).setPageSize(pageSize);

        AggregateSearchQueryTemplate.Builder aggBuilder = createBuilder(compositeBuilder);

        QueryRequest queryRequest = aggBuilder.build();

        assertThat(queryRequest.getPage().getPageNumber(), is(page));
        assertThat(queryRequest.getPage().getPageSize(), is(pageSize));
    }

    @Test
    public void aggregateBuilderSetsNoAggregationConfigurationBuildsQueryRequestWithNoAggregates() throws Exception {
        FakeBuilder compositeBuilder = prePopulatedFakeBuilder();

        AggregateSearchQueryTemplate.Builder aggBuilder = createBuilder(compositeBuilder);

        QueryRequest queryRequest = aggBuilder.build();

        AggregateRequest retrievedAggregates = queryRequest.getAggregate();

        assertThat(retrievedAggregates, is(nullValue()));
    }

    @Test
    public void aggregateBuilderSetsNullAggregationConfigurationBuildsQueryRequestWithNoAggregates() throws Exception {
        FakeBuilder compositeBuilder = prePopulatedFakeBuilder();

        AggregateSearchQueryTemplate.Builder aggBuilder = createBuilder(compositeBuilder);
        aggBuilder.setAggregate(null);

        QueryRequest queryRequest = aggBuilder.build();

        AggregateRequest retrievedAggregate = queryRequest.getAggregate();

        assertThat(retrievedAggregate, is(nullValue()));
    }

    @Test
    public void aggregateBuilderSetsAggregationConfigBuildsQueryRequestWithAggregationConfig() throws Exception {
        FakeBuilder compositeBuilder = prePopulatedFakeBuilder();

        AggregateRequest expectedAggregate = new AggregateRequest("annotation");
        AggregateSearchQueryTemplate.Builder aggBuilder = createBuilder(compositeBuilder);
        aggBuilder.setAggregate(expectedAggregate);

        QueryRequest queryRequest = aggBuilder.build();

        AggregateRequest retrievedAggregate = queryRequest.getAggregate();

        assertThat(retrievedAggregate, is(expectedAggregate));
    }

    private AggregateSearchQueryTemplate.Builder createBuilder(FakeBuilder compositeBuilder) {
        AggregateSearchQueryTemplate template = new AggregateSearchQueryTemplate();
        return template.newBuilder(compositeBuilder);
    }

    private FakeBuilder prePopulatedFakeBuilder() {
        return new FakeBuilder().setPage(2).setPageSize(10);
    }

    /**
     * Fake builder class used for test purposes only.
     * <p/>
     * This fake builder is only used as the composite builder necessary for the
     * {@link AggregateSearchQueryTemplate#newBuilder(SearchQueryRequestBuilder)} method.
     */
    private class FakeBuilder implements SearchQueryRequestBuilder {
        private int pageSize;
        private int page;

        public FakeBuilder setPageSize(int pageSize) {
            this.pageSize = pageSize;

            return this;
        }

        public FakeBuilder setPage(int page) {
            this.page = page;

            return this;
        }

        @Override public QueryRequest build() {
            return builder().build();
        }

        @Override public QueryRequest.Builder builder() {
            QueryRequest.Builder builder = new QueryRequest.Builder(QuickGOQuery.createAllQuery());
            builder.setPageParameters(page, pageSize);

            return builder;
        }
    }
}