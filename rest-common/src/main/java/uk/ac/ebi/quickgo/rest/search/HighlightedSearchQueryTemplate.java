package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;

import com.google.common.base.Preconditions;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Reduces the amount of boiler plate code necessary to setup the mandatory elements to configure a
 * {@link QueryRequest}, that holds highlight request definitions.
 *
 * @author Ricardo Antunes
 */
public class HighlightedSearchQueryTemplate {
    private final String startDelim;
    private final String endDelim;
    private final Set<String> fields;

    public HighlightedSearchQueryTemplate(
            String startDelim,
            String endDelim,
            Set<String> fields) {
        Preconditions.checkArgument(startDelim != null && !startDelim.isEmpty(),
                "Highlighting start delimiter cannot be null or empty");
        Preconditions.checkArgument(endDelim != null && !endDelim.isEmpty(),
                "Highlighting end delimiter cannot be null or empty");
        Preconditions.checkArgument(fields != null, "Highlighting field set cannot be null");

        this.startDelim = startDelim;
        this.endDelim = endDelim;
        this.fields = fields;
    }

    public HighlightedSearchQueryTemplate(
            String startDelim,
            String endDelim) {
        this(startDelim, endDelim, new LinkedHashSet<>());
    }

    /**
     * Creates a builder that is capable of creating a {@link QueryRequest} that holds
     * {@link uk.ac.ebi.quickgo.rest.search.query.FieldHighlight} definitions.
     *
     * @param compositeBuilder builds upon the definitions setup by this builder
     * @return a builder capable of creating a {@link QueryRequest} with
     * {@link uk.ac.ebi.quickgo.rest.search.query.FieldHighlight} definitions
     * @throws IllegalArgumentException if the {@param compositeBuilder} is null
     */
    public Builder newBuilder(SearchQueryRequestBuilder compositeBuilder) {
        Preconditions.checkArgument(compositeBuilder != null, "Composite builder cannot be null");

        return new Builder(
                startDelim,
                endDelim,
                fields,
                compositeBuilder
        );
    }

    public static class Builder implements SearchQueryRequestBuilder {
        private final QueryRequest.Builder compositeBuilder;

        private Set<String> highlightedFields;
        private final String highlightStartDelim;
        private final String highlightEndDelim;

        private Builder(String highlightStartDelim,
                String highlightEndDelim,
                Set<String> highlightedFields,
                SearchQueryRequestBuilder builder) {

            this.highlightedFields = highlightedFields;
            this.highlightStartDelim = highlightStartDelim;
            this.highlightEndDelim = highlightEndDelim;

            this.compositeBuilder = builder.builder();
        }

        /**
         * Overwrites the default highlight fields set in the {@link HighlightedSearchQueryTemplate} constuctor.
         *
         * @param fields the fields to highlight
         * @return this {@link Builder} instance
         */
        public Builder setFields(Set<String> fields) {
            if (fields != null) {
                this.highlightedFields = fields;
            }

            return this;
        }

        @Override public QueryRequest build() {
            return builder().build();
        }

        @Override public QueryRequest.Builder builder() {
            QueryRequest.Builder builder = compositeBuilder;

            builder.setHighlightEndDelim(highlightEndDelim);
            builder.setHighlightStartDelim(highlightStartDelim);

            highlightedFields.stream()
                    .forEach(builder::addHighlightedField);

            return builder;
        }
    }
}