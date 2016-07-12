package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.Aggregate;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Reduces the amount of boiler plate code necessary to setup the mandatory elements to configure a
 * {@link QueryRequest}, that holds aggregate request definitions.
 *
 * @author Ricardo Antunes
 */
public class AggregateSearchQueryTemplate {
    /**
     * Creates a builder that is capable of creating a {@link QueryRequest} that holds {@link Aggregate} definitions.
     *
     * @param compositeBuilder the builder to create is based on definitions that have been setup by this builder
     * @return a builder capable of creating a {@link QueryRequest} with {@link Aggregate} definitions
     * @throws IllegalArgumentException if the {@param compositeBuilder} is null
     */
    public Builder newBuilder(SearchQueryRequestBuilder compositeBuilder) {
        Preconditions.checkArgument(compositeBuilder != null, "Composite builder cannot be null");
        return new Builder(compositeBuilder);
    }

    public static class Builder implements SearchQueryRequestBuilder {
        private final QueryRequest.Builder compositeBuilder;
        private Aggregate aggregate;

        public Builder(SearchQueryRequestBuilder builder) {
            compositeBuilder = builder.builder();
        }

        /**
         * Add to the collection of aggregates which aggregates should be calculated.
         *
         * @param aggregate the aggregate to calculate
         * @return this {@link Builder} instance
         */
        public Builder setAggregate(Aggregate aggregate) {
            this.aggregate = aggregate;

            return this;
        }

        @Override public QueryRequest build() {
            return builder().build();
        }

        @Override public QueryRequest.Builder builder() {
            QueryRequest.Builder builder = compositeBuilder;

            builder.setAggregate(aggregate);

            return builder;
        }
    }
}