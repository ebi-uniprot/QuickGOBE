package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.results.AggregateResponse;
import uk.ac.ebi.quickgo.rest.search.results.AggregateResponseBuilder;
import uk.ac.ebi.quickgo.rest.search.results.AggregationBucket;
import uk.ac.ebi.quickgo.rest.service.ServiceRetrievalConfig;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.*;

/**
 * Converts the facet/analytics section of a {@link SolrResponse} into an {@link AggregateResponse}.
 * <p/>
 * Note: At the moment of writing this code there was no direct support for Solr's new JSON facet search in SolrJ. So
 * it was necessary to create a customized solution to extract the aggregation results from the response.
 * <p/>
 * A response to a facet query with analytics imbued within it, will normally look like this:
 * <pre>
 * {
 *  count=3,
 *  unique_id=3,
 *  unique_geneProductId=3,
 *  agg_goId={
 *    numBuckets: 11
 *    buckets=[
 *      {
 *        val=GO:0003824,
 *        count=3,
 *        unique_id=3,
 *        unique_geneProductID=3
 *      }
 *    ]
 *  },
 *  agg_ecoId={
 *    numBuckets: 20
 *    buckets=[
 *      {
 *        val=ECO:0000256,
 *        count=3,
 *        unique_id=3,
 *        unique_geneProductID=3
 *      }
 *    ]
 *  }
 * }
 * </pre>
 *
 * Notes:
 * <br/>
 * &nbsp;&nbsp;&nbsp;Within an aggregate Solr response:
 * <ul>
 *     <li>A nested aggregate will be identified by "agg_" prefixed to the field
 *     name, e.g. agg_goId.</li>
 *     <li>An aggregation result will be identified by the name of the aggregation function followed by the name of
 *     the field, e.g. unique_geneProductID</li>
 * </ul>
 *
 *
 *
 * @author Ricardo Antunes
 */
public class SolrResponseAggregationConverter implements AggregationConverter<SolrResponse, AggregateResponse> {
    private static final Logger logger = LoggerFactory.getLogger(SolrResponseAggregationConverter.class);
    private final Map<String, String> fieldNameTransformationMap;

    public SolrResponseAggregationConverter(ServiceRetrievalConfig serviceRetrievalConfig) {
        Preconditions.checkArgument(serviceRetrievalConfig != null, "ServiceRetrievalConfig cannot be null");

        fieldNameTransformationMap = serviceRetrievalConfig.repo2DomainFieldMap();
    }

    @Override public AggregateResponse convert(SolrResponse response) {
        Preconditions.checkArgument(response != null, "Cannot convert null Solr response to an aggregation");
        NamedList<?> facetData = extractAggregationsFromResponse(response);

        AggregateResponseBuilder globalAggBuilder = new AggregateResponseBuilder(GLOBAL_ID);

        if (facetData != null && facetData.size() > 0) {
            convertSolrAggregationsToDomainAggregations(facetData, globalAggBuilder);
        }

        return globalAggBuilder.createAggregateResponse();
    }

    /**
     * Extracts the section of the {@link SolrResponse} that is used specifically for the aggregation.
     *
     * @param response the native Solr response
     * @return The data structure used to store the aggregation results
     */
    private NamedList<?> extractAggregationsFromResponse(SolrResponse response) {
        return (NamedList<?>) response.getResponse().get(AGGREGATIONS_MARKER);
    }

    private void convertSolrAggregationsToDomainAggregations(NamedList<?> facetData, AggregateResponseBuilder
            aggregationBuilder) {
        for (Map.Entry<String, ?> facetDataEntry : facetData) {
            convertAggregateValue(facetDataEntry.getKey(), facetDataEntry.getValue(), aggregationBuilder);
        }
    }

    /**
     * Converts an element found within a {@link NamedList} into an value that makes sense within an
     * {@link AggregateResponse}
     */
    private void convertAggregateValue(String field, Object value, AggregateResponseBuilder aggregationBuilder) {
        String fieldPrefix = SolrAggregationHelper.fieldPrefixExtractor(field);

        if (!fieldPrefix.isEmpty()) {
            String name = SolrAggregationHelper.fieldNameExtractor(field);
            if (isNestedAggregate(fieldPrefix)) {
                AggregateResponse nestedAggregation = createNestedAggregation(name, value);
                aggregationBuilder.addNestedAggregation(nestedAggregation);
            } else if (isAggregateFunction(fieldPrefix)) {
                addAggregationFunctionToAggregation(fieldPrefix, name, value, aggregationBuilder);
            } else {
                logger.debug("Unable to process field:{}, with prefix:{}", field, fieldPrefix);
            }
        } else if (isDistinctValueCount(field)) {
            aggregationBuilder.setDistinctValuesCount(((Number) value).intValue());

        } else if (isFieldBucket(field)) {
            List<NamedList<?>> buckets = (List<NamedList<?>>) value;
            buckets.forEach(bucket -> convertBucket(bucket, aggregationBuilder));
        } else {
            logger.debug("Did not process field: {} with value: {}", field, value);
        }
    }

    /**
     * Determines whether the prefix found within the field name represents a nested aggregate.
     *
     * @param fieldPrefix the prefix found within the Solr field name
     * @return true if the field prefix represents a nested aggregate, false otherwise.
     */
    private boolean isNestedAggregate(String fieldPrefix) {
        return (AGG_TYPE_PREFIX.equals(fieldPrefix));
    }

    /**
     * Determines whether the prefix found within the field name represents an aggregate function.
     *
     * @param fieldPrefix the prefix found within the Solr field name
     * @return true if the field prefix represents an aggregate function, false otherwise.
     */
    private boolean isAggregateFunction(String fieldPrefix) {
        boolean isAggregateFunction = true;

        try {
            AggregateFunction.typeOf(fieldPrefix);
        } catch (IllegalArgumentException e) {
            isAggregateFunction = false;
        }

        return isAggregateFunction;
    }

    /**
     * Indicates whether the {@code field} represents a marker for a Solr bucket within an aggregation result.
     *
     * @param field the name of the field
     * @return true if the field name represents a bucket, false otherwise
     */
    private boolean isFieldBucket(String field) {
        return BUCKETS_ID.equals(field);
    }

    /**
     * Creates a nested {@link AggregateResponse} based on the provided {@code nestedFacets}.
     *
     * @param name the name of the aggregation
     * @param nestedFacets the values used to populate the newly created {@link AggregateResponse}
     * @return creates a new {@link AggregateResponse} base on the method arguments
     */
    private AggregateResponse createNestedAggregation(String name, Object nestedFacets) {
        AggregateResponseBuilder nestedAggregationBuilder =
                new AggregateResponseBuilder(responseFieldName2DomainFieldName(name));
        convertSolrAggregationsToDomainAggregations((NamedList) nestedFacets, nestedAggregationBuilder);
        return nestedAggregationBuilder.createAggregateResponse();
    }

    /**
     * Converts the raw types retrieve from the Solr response into an
     * {@link uk.ac.ebi.quickgo.rest.search.results.AggregationResult} that is added to the {@code aggregation}.
     *
     * @param functionText the aggregation function expressed as a string
     * @param field the name of the field that was aggregated
     * @param hitsObject the result of the application of the function defined in {@code functionText} to the
     * aggregation field defined in {@code field}.
     * @param aggregationBuilder the aggregation builder the aggregation result will be added to.
     */
    private void addAggregationFunctionToAggregation(String functionText, String field, Object hitsObject,
            AggregateResponseBuilder aggregationBuilder) {
        AggregateFunction function = AggregateFunction.typeOf(functionText);

        double hits = convertToDouble(hitsObject);

        aggregationBuilder.addAggregationResult(function, field, hits);
    }

    private String responseFieldName2DomainFieldName(String name) {
        if (fieldNameTransformationMap.containsKey(name)) {
            return fieldNameTransformationMap.get(name);
        }
        return name;
    }

    private void convertBucket(NamedList<?> facetBucket, AggregateResponseBuilder aggregationBuilder) {
        AggregationBucket aggBucket = new AggregationBucket(String.valueOf(facetBucket.get(BUCKET_FIELD_ID)));
        aggregationBuilder.addBucket(aggBucket);

        for (Map.Entry<String, ?> bucketEntry : facetBucket) {
            convertBucketValue(bucketEntry.getKey(), bucketEntry.getValue(), aggBucket);
        }
    }

    private void convertBucketValue(String bucketField, Object bucketValue, AggregationBucket bucket) {
        String prefix = SolrAggregationHelper.fieldPrefixExtractor(bucketField);

        if (!prefix.isEmpty()) {
            String facetName = SolrAggregationHelper.fieldNameExtractor(bucketField);

            AggregateFunction function = AggregateFunction.typeOf(prefix);

            double hits = convertToDouble(bucketValue);

            bucket.addAggregationResult(function, facetName, hits);
        } else {
            logger.debug("Did not process field: {} with value: {}", bucketField, bucketValue);
        }
    }

    private double convertToDouble(Object number) {
        double convertedValue;

        if (number instanceof Double double1) {
            convertedValue = double1;
        } else if (number instanceof Long long1) {
            convertedValue = long1;
        } else if (number instanceof Integer integer) {
            convertedValue = integer.doubleValue();
        } else {
            throw new IllegalArgumentException("Unable to convert number: " + number);
        }

        return convertedValue;
    }
}
