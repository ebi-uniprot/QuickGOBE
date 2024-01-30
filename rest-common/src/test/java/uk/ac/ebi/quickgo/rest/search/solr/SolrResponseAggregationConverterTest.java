package uk.ac.ebi.quickgo.rest.search.solr;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.results.AggregateResponse;
import uk.ac.ebi.quickgo.rest.search.results.AggregationBucket;
import uk.ac.ebi.quickgo.rest.search.results.AggregationResult;
import uk.ac.ebi.quickgo.rest.service.ServiceRetrievalConfig;

import java.util.*;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.common.util.NamedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.NUM_BUCKETS;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.AGGREGATIONS_MARKER;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.BUCKETS_ID;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.BUCKET_FIELD_ID;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.GLOBAL_ID;

/**
 * Tests the behaviour of the {@link SolrResponseAggregationConverter} class.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SolrResponseAggregationConverterTest {

    @Mock
    private SolrResponse responseMock;

    @Mock
    private ServiceRetrievalConfig serviceRetrievalConfigMock;

    private SolrResponseAggregationConverter converter;
    private SolrAggregate solrAggregate;

    @BeforeEach
    void setUp()  {
        solrAggregate = new SolrAggregate();

        converter = new SolrResponseAggregationConverter(serviceRetrievalConfigMock);

        addFacetsToResponse(solrAggregate);
    }

    @Test
    void creationWithNullServiceRetrievalConfigCausesException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new SolrResponseAggregationConverter(null));
        assertTrue(exception.getMessage().contains("ServiceRetrievalConfig cannot be null"));
    }

    @Test
    void conversionOfNullSolrResponseThrowsException()  {
        responseMock = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> converter.convert(responseMock));
        assertTrue(exception.getMessage().contains("Cannot convert null Solr response to an aggregation"));
    }

    @Test
    void solrResponseWithNoAggregatesConvertsToEmptyAggregation()  {
        when(responseMock.getResponse()).thenReturn(new NamedList<>());

        AggregateResponse agg = converter.convert(responseMock);

        assertThat(agg.getName(), is(GLOBAL_ID));
        assertThat(agg.getAggregationResults(), hasSize(0));
        assertThat(agg.getBuckets(), hasSize(0));
        assertThat(agg.getNestedAggregations(), hasSize(0));
    }

    @Test
    void solrResponseWithNonAggregatesConvertToEmptyAggregation()  {
        AggregateResponse agg = converter.convert(responseMock);

        assertThat(agg.getName(), is(GLOBAL_ID));
        assertThat(agg.getAggregationResults(), hasSize(0));
        assertThat(agg.getBuckets(), hasSize(0));
        assertThat(agg.getNestedAggregations(), hasSize(0));
    }

    @Test
    void solrResponseWithAggregatedFunctionConvertsToANonNullAggregation()  {
        SolrAggregationResult result = new SolrAggregationResult("id", AggregateFunction.COUNT, 3);
        solrAggregate.addFunction(result);

        AggregateResponse agg = converter.convert(responseMock);

        assertThat(agg, is(notNullValue()));
    }

    @Test
    void solrResponseWithNonAggregatedBucketInGlobalAggregateConvertsToAEmptyAggregation()  {
        SolrBucket bucket = new SolrBucket("goId");
        solrAggregate.addBucket(bucket);

        AggregateResponse agg = converter.convert(responseMock);

        assertThat(agg.getName(), is(GLOBAL_ID));
        assertThat(agg.getAggregationResults(), hasSize(0));
        assertThat(agg.getBuckets(), hasSize(0));
        assertThat(agg.getNestedAggregations(), hasSize(0));
    }

    @Test
    void solrResponseWithTotalDistinctCountForFacetIsAddedToNestedAggregation()  {
        String bucketValue1 = "GO:0000001";
        String bucketValue2 = "GO:0000002";

        String aggTypeGoId = SolrAggregationHelper.aggregatePrefixWithTypeTitle("goId");
        SolrBucket bucket = new SolrBucket(aggTypeGoId);
        bucket.addValueAndAggResults(bucketValue1);
        bucket.addValueAndAggResults(bucketValue2);
        solrAggregate.addBucket(bucket);

        Object namedList = solrAggregate.facetValues.get("agg_goId");
        ((NamedList)namedList).add(NUM_BUCKETS,12);

        AggregateResponse agg = converter.convert(responseMock);

        Optional<AggregateResponse> nestedAggregation = agg.getNestedAggregations().stream().findFirst();
        assertThat(nestedAggregation.isPresent(), is(true));
        AggregateResponse nestedResponse = nestedAggregation.get();
        assertThat(nestedResponse.getDistinctValuesCount(), is(12));
    }

    @Test
    void solrResponseWithAggregatedBucketInGlobalAggregateConvertToANonNullAggregation()  {
        String aggTypeGoId = SolrAggregationHelper.aggregatePrefixWithTypeTitle("goId");
        SolrBucket bucket = new SolrBucket(aggTypeGoId);
        solrAggregate.addBucket(bucket);

        AggregateResponse agg = converter.convert(responseMock);

        assertThat(agg, is(notNullValue()));
    }

    @Test
    void solrResponseWithTwoAggregatedFunctionsReturnsAnAggregationWithTwoAggregationResults()  {
        AggregateFunction countFunc = AggregateFunction.COUNT;
        String gpIdField = "geneProductId";
        double aggGpIdHits = 3;

        SolrAggregationResult countGpIdResult = new SolrAggregationResult(gpIdField, countFunc, aggGpIdHits);

        solrAggregate.addFunction(countGpIdResult);

        AggregateFunction uniqueFunc = AggregateFunction.UNIQUE;
        String annIdField = "annotationId";
        double aggAnnIdHits = 4;

        SolrAggregationResult uniqueAnnIdResult = new SolrAggregationResult(annIdField, uniqueFunc, aggAnnIdHits);

        solrAggregate.addFunction(uniqueAnnIdResult);

        AggregateResponse agg = converter.convert(responseMock);

        Set<AggregationResult> aggregationResults = agg.getAggregationResults();

        assertThat(aggregationResults, hasSize(2));
        assertThat(agg.getAggregationResult(countFunc, gpIdField).isPresent(), is(true));
        assertThat(agg.getAggregationResult(uniqueFunc, annIdField).isPresent(), is(true));
    }

    @Test
    void solrResponseWithTwoValuesInBucketReturnsANestedAggregationWithTwoBucketValues()  {
        String bucketValue1 = "GO:0000001";
        String bucketValue2 = "GO:0000002";

        String aggTypeGoId = SolrAggregationHelper.aggregatePrefixWithTypeTitle("goId");
        SolrBucket bucket = new SolrBucket(aggTypeGoId);
        bucket.addValueAndAggResults(bucketValue1);
        bucket.addValueAndAggResults(bucketValue2);

        solrAggregate.addBucket(bucket);

        AggregateResponse agg = converter.convert(responseMock);

        Set<AggregateResponse> retrievedNestedAggregations = agg.getNestedAggregations();
        assertThat(retrievedNestedAggregations, hasSize(1));

        AggregateResponse goIdAggregation = retrievedNestedAggregations.iterator().next();

        Set<AggregationBucket> retrievedBuckets = goIdAggregation.getBuckets();

        checkBucketValues(retrievedBuckets, Arrays.asList(bucketValue1, bucketValue2));
    }

    @Test
    void
    solrResponseWithAnAggregateFunctionWithinABucketValueReturnsAnNestedAggregationWithABucketContainingAnAggregationFunction()
             {
        String bucketValue1 = "GO:0000001";

        AggregateFunction countFunc = AggregateFunction.COUNT;
        String field = "geneProductId";
        double hits = 3;

        SolrAggregationResult result = new SolrAggregationResult(field, countFunc, hits);

        String aggTypeGoId = SolrAggregationHelper.aggregatePrefixWithTypeTitle("goId");
        SolrBucket bucket = new SolrBucket(aggTypeGoId);
        bucket.addValueAndAggResults(bucketValue1, result);

        solrAggregate.addBucket(bucket);

        AggregateResponse agg = converter.convert(responseMock);

        Set<AggregateResponse> retrievedNestedAggregations = agg.getNestedAggregations();
        assertThat(retrievedNestedAggregations, hasSize(1));

        AggregateResponse goIdAggregation = retrievedNestedAggregations.iterator().next();

        Set<AggregationBucket> retrievedBuckets = goIdAggregation.getBuckets();

        assertThat(retrievedBuckets, hasSize(1));

        AggregationBucket retrievedBucket = retrievedBuckets.iterator().next();

        assertThat(retrievedBucket.getAggregationResult(countFunc, field).isPresent(), is(true));
    }

    private void addFacetsToResponse(SolrAggregate facet) {
        NamedList<Object> queryResponse = new NamedList<>();
        queryResponse.add(AGGREGATIONS_MARKER, facet.facetValues);

        when(responseMock.getResponse()).thenReturn(queryResponse);
    }

    private void checkBucketValues(Collection<AggregationBucket> actualBuckets, Collection<String> bucketValues) {
        assertThat(actualBuckets.size(), is(bucketValues.size()));

        List<String> actualBucketValues = actualBuckets.stream()
                .map(AggregationBucket::getValue)
                .collect(Collectors.toList());

        assertThat(actualBucketValues, containsInAnyOrder(bucketValues.toArray()));
    }

    /**
     * Facade to represent an {@link AggregateResponse} in a native Solr response.
     */
    private class SolrAggregate {
        private final NamedList<Object> facetValues;

        public SolrAggregate() {
            facetValues = new NamedList<>();
        }

        private void addFunction(SolrAggregationResult result) {

            String aggField = SolrAggregationHelper.aggregateFieldTitle(result.function, result.field);
            facetValues.add(aggField, result.hits);
        }

        private void addBucket(SolrBucket bucket) {
            NamedList<Object> bucketValues = new NamedList<>();
            bucketValues.add(BUCKETS_ID, bucket.values);

            facetValues.add(bucket.field, bucketValues);
        }
    }

    /**
     * Facade to represent an {@link AggregationBucket} in a native Solr response.
     */
    private class SolrBucket {
        private final String field;
        private final List<NamedList> values;

        SolrBucket(String field) {
            this.field = field;
            values = new ArrayList<>();
        }

        private void addValueAndAggResults(String value, SolrAggregationResult... results) {
            NamedList<Object> bucketValue = new NamedList<>();

            bucketValue.add(BUCKET_FIELD_ID, value);

            Arrays.stream(results).forEach(result -> {
                        String aggField = SolrAggregationHelper.aggregateFieldTitle(result.function, result.field);
                        bucketValue.add(aggField, result.hits);
                    }

            );

            values.add(bucketValue);
        }
    }

    /**
     * Facade to represent an {@link AggregationResult} in a native Solr response.
     */
    private class SolrAggregationResult {
        private final String field;
        private final AggregateFunction function;
        private final double hits;

        SolrAggregationResult(String field, AggregateFunction function, double hits) {
            this.field = field;
            this.function = function;
            this.hits = hits;
        }
    }
}
