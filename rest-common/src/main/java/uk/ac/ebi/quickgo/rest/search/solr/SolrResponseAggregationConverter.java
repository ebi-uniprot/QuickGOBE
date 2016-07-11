package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.results.Aggregation;
import uk.ac.ebi.quickgo.rest.search.results.AggregationBucket;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.AGG_TYPE_PREFIX;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.BUCKETS_ID;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.BUCKET_FIELD_ID;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.FACET_MARKER;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.GLOBAL_ID;

/**
 * Converts the facet/analytics section of a {@link SolrResponse} into an {@link Aggregation}.
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
 * @author Ricardo Antunes
 */
public class SolrResponseAggregationConverter implements AggregationConverter<SolrResponse, Aggregation> {
    private static final Logger logger = LoggerFactory.getLogger(SolrResponseAggregationConverter.class);

    @Override public Aggregation convert(SolrResponse response) {
        Preconditions.checkArgument(response != null, "Cannot convert null Solr response to an aggregation");
        NamedList<?> facetResponse = extractFacetsFromResponse(response);

        Aggregation globalAgg = null;

        if(facetResponse != null && facetResponse.size() > 0) {
            globalAgg = new Aggregation(GLOBAL_ID);

            convertAggregateFacets(facetResponse, globalAgg);

            if(!globalAgg.isPopulated()) {
                globalAgg = null;
            }
        }

        return globalAgg;
    }

    private NamedList<?> extractFacetsFromResponse(SolrResponse response) {
        return (NamedList<?>) response.getResponse().get(FACET_MARKER);
    }

    private void convertAggregateFacets(NamedList<?> facetData, Aggregation aggregation) {
        for (Map.Entry<String, ?> facetDataEntry : facetData) {
            convertFacetValue(facetDataEntry.getKey(), facetDataEntry.getValue(), aggregation);
        }
    }

    private void convertFacetValue(String field, Object value, Aggregation aggregation) {
        String prefix = SolrAggregationHelper.fieldPrefixExtractor(field);

        if (prefix != null) {
            String name = SolrAggregationHelper.fieldNameExtractor(field);

            if (AGG_TYPE_PREFIX.equals(prefix)) {
                Aggregation nestedAggregation = new Aggregation(name);
                aggregation.addNestedAggregation(nestedAggregation);

                NamedList nestedFacets = (NamedList) value;
                convertAggregateFacets(nestedFacets, nestedAggregation);
            } else {
                AggregateFunction function = AggregateFunction.typeOf(prefix);

                double hits = convertToDouble(value);

                aggregation.addAggregationResult(function, name, hits);
            }
        } else if (BUCKETS_ID.equals(field)) {
            List<NamedList<?>> buckets = (List<NamedList<?>>) value;
            buckets.stream()
                    .forEach(bucket -> convertBucket((NamedList<?>) bucket, aggregation));
        } else {
            logger.debug("Did not process field: {} with value: {}", field, value);
        }
    }

    private void convertBucket(NamedList<?> facetBucket, Aggregation aggregation) {
        AggregationBucket aggBucket = new AggregationBucket((String) facetBucket.get(BUCKET_FIELD_ID));
        aggregation.addBucket(aggBucket);

        for (Map.Entry<String, ?> bucketEntry : facetBucket) {
            convertBucketValue(bucketEntry.getKey(), bucketEntry.getValue(), aggBucket);
        }
    }

    private void convertBucketValue(String bucketField, Object bucketValue, AggregationBucket bucket) {
        String prefix = SolrAggregationHelper.fieldPrefixExtractor(bucketField);

        if (prefix != null) {
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
            convertedValue = (double) (int) number;
        } else {
            throw new IllegalArgumentException("Unable to convert number: " + number);
        }

        return convertedValue;
    }
}