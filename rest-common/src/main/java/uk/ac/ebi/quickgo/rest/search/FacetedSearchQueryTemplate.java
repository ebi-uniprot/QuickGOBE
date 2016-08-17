package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;

import com.google.common.base.Preconditions;
import java.util.*;

/**
 * Reduces the amount of boiler plate code necessary to setup the mandatory elements to configure a
 * {@link QueryRequest}, that holds facet request definitions.
 *
 * @author Ricardo Antunes
 */
public class FacetedSearchQueryTemplate {
    /**
     * Creates a builder that is capable of creating a {@link QueryRequest} that holds
     * {@link uk.ac.ebi.quickgo.rest.search.query.Facet} definitions.
     *
     * @param compositeBuilder builds upon the definitions setup by this builder
     * @return a builder capable of creating a {@link QueryRequest} with
     * {@link uk.ac.ebi.quickgo.rest.search.query.Facet} definitions
     * @throws IllegalArgumentException if the {@param compositeBuilder} is null
     */
    public Builder newBuilder(SearchQueryRequestBuilder compositeBuilder) {
        Preconditions.checkArgument(compositeBuilder != null, "Composite builder cannot be null");

        return new Builder(
                compositeBuilder
        );
    }

    public static class Builder implements SearchQueryRequestBuilder {
        private final QueryRequest.Builder compositeBuilder;
        private final Set<String> facets;

        private Builder(SearchQueryRequestBuilder builder) {
            this.compositeBuilder = builder.builder();
            this.facets = new HashSet<>();
        }

        /**
         * Add to the collection of facets the facets should be displayed.
         *
         * @param facets the facets to display
         * @return this {@link Builder} instance
         */
        public Builder addFacets(String... facets) {
            if (facets != null) {
                this.facets.addAll(Arrays.asList(facets));
            }

            return this;
        }

        @Override public QueryRequest build() {
            return builder().build();
        }

        @Override public QueryRequest.Builder builder() {
            QueryRequest.Builder builder = compositeBuilder;

            facets.stream()
                    .forEach(builder::addFacetField);

            return builder;
        }
    }
}