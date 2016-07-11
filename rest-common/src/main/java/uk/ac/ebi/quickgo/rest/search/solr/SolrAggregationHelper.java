package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

/**
 * Defines a set of constants and utility methods to aid in the processing of aggregation results in Solr
 * requests/responses.
 *
 * @author Ricardo Antunes
 */
class SolrAggregationHelper {
    static String GLOBAL_ID = "global";

    static String AGG_TYPE_PREFIX = "agg";
    static String AGG_SEPARATOR = "_";

    static String BUCKETS_ID = "buckets";
    static String BUCKET_FIELD_ID = "val";

    static String FACET_MARKER = "facets";

    static String mergeFunctionWithField(AggregateFunction function, String field) {
        return function.getName() + AGG_SEPARATOR + field;
    }

    static String mergeAggPrefixWithField(String field) {
        return AGG_TYPE_PREFIX + AGG_SEPARATOR + field;
    }

    static String fieldPrefixExtractor(String field) {
        int separatorPos = field.indexOf(AGG_SEPARATOR);

        String prefix = null;

        if (separatorPos != -1) {
            prefix = field.substring(0, separatorPos);
        }

        return prefix;
    }

    static String fieldNameExtractor(String field) {
        int separatorPos = field.indexOf(AGG_SEPARATOR);

        String fieldName = null;

        if (separatorPos != -1) {
            fieldName = field.substring(separatorPos + 1, field.length());
        }

        return fieldName;
    }
}
