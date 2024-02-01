package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

/**
 * A fake implementation of {@link SearchQueryRequestBuilder} to be used for only testing purposes in query templae
 * classes that require a composite builder.
 */
final class FakeCompositeBuilder implements SearchQueryRequestBuilder {
    private final QuickGOQuery query;

    FakeCompositeBuilder() {
        query = QuickGOQuery.createAllQuery();
    }

    FakeCompositeBuilder(QuickGOQuery query) {
        this.query = query;
    }

    @Override public QueryRequest build() {
        return builder().build();
    }

    @Override public QueryRequest.Builder builder() {
        return new QueryRequest.Builder(query, "");
    }
}
