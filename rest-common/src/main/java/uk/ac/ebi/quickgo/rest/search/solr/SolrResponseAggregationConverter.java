package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.results.AggregateResponse;
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
        NamedList<?> aggregateResponse = extractAggregationsFromResponse(response);

        AggregateResponse globalAgg = null;

        if (aggregateResponse != null && aggregateResponse.size() > 0) {
            globalAgg = new AggregateResponse(GLOBAL_ID);

            convertSolrAggregationsToDomainAggregations(aggregateResponse, globalAgg);

            if (!globalAgg.isPopulated()) {
                globalAgg = null;
            }
        }

        return globalAgg;
    }

    /**
     * Extracts the section of the {@link SolrResponse} that is used specifically for the aggregation.
     *
     * @param response the native Solr resposne
     * @return The data structure used to store the aggregation results
     */
    private NamedList<?> extractAggregationsFromResponse(SolrResponse response) {
        return (NamedList<?>) response.getResponse().get(AGGREGATIONS_MARKER);
    }

    private void convertSolrAggregationsToDomainAggregations(NamedList<?> facetData, AggregateResponse aggregation) {
        for (Map.Entry<String, ?> facetDataEntry : facetData) {
            convertAggregateValue(facetDataEntry.getKey(), facetDataEntry.getValue(), aggregation);
        }
    }

    /**
     * Converts an element found within a {@link NamedList} into an value that makes sense within an
     * {@link AggregateResponse}
     */
    private void convertAggregateValue(String field, Object value, AggregateResponse aggregation) {
        String fieldPrefix = SolrAggregationHelper.fieldPrefixExtractor(field);

        if (!fieldPrefix.isEmpty()) {
            String name = SolrAggregationHelper.fieldNameExtractor(field);

            if (isNestedAggregate(fieldPrefix)) {
                AggregateResponse nestedAggregation = createNestedAggregation(name, value);
                aggregation.addNestedAggregation(nestedAggregation);
            } else if (isAggregateFunction(fieldPrefix)) {
                addAggregationFunctionToAggregation(fieldPrefix, name, value, aggregation);
            } else {
                logger.debug("Unable to process field:{}, with prefix:{}", field, fieldPrefix);
            }
        } else if (isFieldBucket(field)) {
            List<NamedList<?>> buckets = (List<NamedList<?>>) value;
            buckets.forEach(bucket -> convertBucket((NamedList<?>) bucket, aggregation));
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
        AggregateResponse nestedAggregation = new AggregateResponse(responseFieldName2DomainFieldName(name));

        convertSolrAggregationsToDomainAggregations((NamedList) nestedFacets, nestedAggregation);

        return nestedAggregation;
    }

    /**
     * Converts the raw types retrieve from the Solr response into an
     * {@link uk.ac.ebi.quickgo.rest.search.results.AggregationResult} that is added to the {@code aggregation}.
     *
     * @param functionText the aggregation function expressed as a string
     * @param field the name of the field that was aggregated
     * @param hitsObject the result of the application of the function defined in {@code functionText} to the
     * aggregation field defined in {@code field}.
     * @param aggregation the aggregation where the aggragetion result will be added to.
     */
    private void addAggregationFunctionToAggregation(String functionText, String field, Object hitsObject,
            AggregateResponse aggregation) {
        AggregateFunction function = AggregateFunction.typeOf(functionText);

        double hits = convertToDouble(hitsObject);

        aggregation.addAggregationResult(function, field, hits);
    }

    private String responseFieldName2DomainFieldName(String name) {
        if (fieldNameTransformationMap.containsKey(name)) {
            return fieldNameTransformationMap.get(name);
        }
        return name;
    }

    private void convertBucket(NamedList<?> facetBucket, AggregateResponse aggregation) {
        AggregationBucket aggBucket = new AggregationBucket(String.valueOf(facetBucket.get(BUCKET_FIELD_ID)));
        aggregation.addBucket(aggBucket);

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

        if (number instanceof Double) {
            convertedValue = (double) number;
        } else if (number instanceof Integer) {
            convertedValue = ((Integer) number).doubleValue();
        } else {
            throw new IllegalArgumentException("Unable to convert number: " + number);
        }

        return convertedValue;
    }
}