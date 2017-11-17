package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import com.google.common.base.Preconditions;

import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.NUM_BUCKETS;

/**
 * Defines a set of constants and utility methods to aid in the processing of aggregation requests and results from
 * Solr to the domain model, and vice versa.
 *
 * @author Ricardo Antunes
 */
class SolrAggregationHelper {
    static final String GLOBAL_ID = "global";

    static final String AGG_TYPE_PREFIX = "agg";

    static final String NAME_TO_VALUE_SEPARATOR = ":";
    static final String DECLARATION_SEPARATOR = ",";

    static final String BUCKETS_ID = "buckets";
    static final String BUCKET_FIELD_ID = "val";

    static final String AGGREGATIONS_MARKER = "facets";
    static final String FACET_MARKER = "facet";
    static final String FACET_TYPE_TERM = "terms";

    static final String AGG_NAME_SEPARATOR = "_";

    static String aggregateFieldTitle(AggregateFunction function, String field) {
        Preconditions.checkArgument(function != null,
                "Cannot create aggregate field title with null aggregate function");
        Preconditions.checkArgument(field != null && !field.trim().isEmpty(),
                "Cannot create aggregate field title with null field");

        return function.getName() + AGG_NAME_SEPARATOR + field;
    }

    static String aggregatePrefixWithTypeTitle(String type) {
        Preconditions.checkArgument(type != null && !type.trim().isEmpty(),
                "Cannot create aggregate type title with null or empty type");

        return AGG_TYPE_PREFIX + AGG_NAME_SEPARATOR + type;
    }

    static String fieldPrefixExtractor(String field) {
        Preconditions.checkArgument(field != null, "Cannot extract prefix from null input");

        int separatorPos = field.indexOf(AGG_NAME_SEPARATOR);

        String prefix = "";

        if (separatorPos != -1) {
            prefix = field.substring(0, separatorPos);
        }

        return prefix;
    }

    static String fieldNameExtractor(String field) {
        Preconditions.checkArgument(field != null, "Cannot extract field from null input");

        int separatorPos = field.indexOf(AGG_NAME_SEPARATOR);

        String fieldName = "";

        if (separatorPos != -1) {
            fieldName = field.substring(separatorPos + 1, field.length());
        }

        return fieldName;
    }

    /**
     * Test the field to see if holds the result of the distinct value count.
     * @param field element of the aggregation result
     * @return true if field holds distinct value count field definition.
     */
    static boolean distinctValueCountTester(String field) {
        return NUM_BUCKETS.equals(field);
    }
}
