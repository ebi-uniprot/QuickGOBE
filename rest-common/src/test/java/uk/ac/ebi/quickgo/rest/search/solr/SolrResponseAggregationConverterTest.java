package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.results.Aggregation;
import uk.ac.ebi.quickgo.rest.search.results.AggregationBucket;
import uk.ac.ebi.quickgo.rest.search.results.AggregationResult;
import uk.ac.ebi.quickgo.rest.service.ServiceRetrievalConfig;

import java.util.*;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.common.util.NamedList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.BUCKETS_ID;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.BUCKET_FIELD_ID;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.FACETS_MARKER;

/**
 * Tests the behaviour of the {@link SolrResponseAggregationConverter} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrResponseAggregationConverterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private SolrResponse responseMock;

    @Mock
    private ServiceRetrievalConfig serviceRetrievalConfigMock;

    private SolrResponseAggregationConverter converter;
    private SolrFacet aggFacet;

    @Before
    public void setUp() throws Exception {
        aggFacet = new SolrFacet();

        converter = new SolrResponseAggregationConverter(serviceRetrievalConfigMock);

        addFacetsToResponse(aggFacet);
    }

    @Test
    public void creationWithNullServiceRetrievalConfigCausesException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ServiceRetrievalConfig cannot be null");

        new SolrResponseAggregationConverter(null);
    }

    @Test
    public void conversionOfNullSolrResponseThrowsException() throws Exception {
        responseMock = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot convert null Solr response to an aggregation");

        converter.convert(responseMock);
    }

    @Test
    public void solrResponseWithNoFacetsConvertsToNullAggregate() throws Exception {
        when(responseMock.getResponse()).thenReturn(new NamedList<>());

        Aggregation agg = converter.convert(responseMock);

        assertThat(agg, is(nullValue()));
    }

    @Test
    public void solrResponseWithNonAggregatedFacetConvertToNullAggregate() throws Exception {
        Aggregation agg = converter.convert(responseMock);

        assertThat(agg, is(nullValue()));
    }

    @Test
    public void solrResponseWithAggregatedFunctionInFacetConvertssToANonNullAggregate() throws Exception {
        SolrAggregationResult result = new SolrAggregationResult("id", AggregateFunction.COUNT, 3);
        aggFacet.addFunction(result);

        Aggregation agg = converter.convert(responseMock);

        assertThat(agg, is(notNullValue()));
    }

    @Test
    public void solrResponseWithNonAggregatedBucketInGlobalFacetConvertsToANullAggregate() throws Exception {
        SolrBucket bucket = new SolrBucket("goId");
        aggFacet.addBucket(bucket);

        Aggregation agg = converter.convert(responseMock);

        assertThat(agg, is(nullValue()));
    }

    @Test
    public void solrResponseWithAggregatedBucketInGlobalFacetConvertToANonNullAggregate() throws Exception {
        String aggTypeGoId = SolrAggregationHelper.aggregatePrefixWithTypeTitle("goId");
        SolrBucket bucket = new SolrBucket(aggTypeGoId);
        aggFacet.addBucket(bucket);

        Aggregation agg = converter.convert(responseMock);

        assertThat(agg, is(notNullValue()));
    }

    @Test
    public void solrResponseWithTwoAggregatedFunctionsReturnsAnAggregationWithTwoAggregationResults() throws Exception {
        AggregateFunction countFunc = AggregateFunction.COUNT;
        String gpIdField = "geneProductId";
        double aggGpIdHits = 3;

        SolrAggregationResult countGpIdResult = new SolrAggregationResult(gpIdField, countFunc, aggGpIdHits);

        aggFacet.addFunction(countGpIdResult);

        AggregateFunction uniqueFunc = AggregateFunction.UNIQUE;
        String annIdField = "annotationId";
        double aggAnnIdHits = 4;

        SolrAggregationResult uniqueAnnIdResult = new SolrAggregationResult(annIdField, uniqueFunc, aggAnnIdHits);

        aggFacet.addFunction(uniqueAnnIdResult);

        Aggregation agg = converter.convert(responseMock);

        Set<AggregationResult> aggregationResults = agg.getAggregationResults();

        assertThat(aggregationResults, hasSize(2));
        assertThat(agg.getAggregationResult(countFunc, gpIdField).isPresent(), is(true));
        assertThat(agg.getAggregationResult(uniqueFunc, annIdField).isPresent(), is(true));
    }

    @Test
    public void solrResponseWithTwoValuesInBucketReturnsANestedAggregationWithTwoBucketValues() throws Exception {
        String bucketValue1 = "GO:0000001";
        String bucketValue2 = "GO:0000002";

        String aggTypeGoId = SolrAggregationHelper.aggregatePrefixWithTypeTitle("goId");
        SolrBucket bucket = new SolrBucket(aggTypeGoId);
        bucket.addValueAndAggResults(bucketValue1);
        bucket.addValueAndAggResults(bucketValue2);

        aggFacet.addBucket(bucket);

        Aggregation agg = converter.convert(responseMock);

        Set<Aggregation> retrievedNestedAggregations = agg.getNestedAggregations();
        assertThat(retrievedNestedAggregations, hasSize(1));

        Aggregation goIdAggregation = retrievedNestedAggregations.iterator().next();

        Set<AggregationBucket> retrievedBuckets = goIdAggregation.getBuckets();

        checkBucketValues(retrievedBuckets, Arrays.asList(bucketValue1, bucketValue2));
    }

    @Test
    public void
    solrResponseWithAnAggregateFunctionWithinABucketValueReturnsAnNestedAggregationWithABucketContainingAnAggregationFunction()
            throws Exception {
        String bucketValue1 = "GO:0000001";

        AggregateFunction countFunc = AggregateFunction.COUNT;
        String field = "geneProductId";
        double hits = 3;

        SolrAggregationResult result = new SolrAggregationResult(field, countFunc, hits);

        String aggTypeGoId = SolrAggregationHelper.aggregatePrefixWithTypeTitle("goId");
        SolrBucket bucket = new SolrBucket(aggTypeGoId);
        bucket.addValueAndAggResults(bucketValue1, result);

        aggFacet.addBucket(bucket);

        Aggregation agg = converter.convert(responseMock);

        Set<Aggregation> retrievedNestedAggregations = agg.getNestedAggregations();
        assertThat(retrievedNestedAggregations, hasSize(1));

        Aggregation goIdAggregation = retrievedNestedAggregations.iterator().next();

        Set<AggregationBucket> retrievedBuckets = goIdAggregation.getBuckets();

        assertThat(retrievedBuckets, hasSize(1));

        AggregationBucket retrievedBucket = retrievedBuckets.iterator().next();

        assertThat(retrievedBucket.getAggregationResult(countFunc, field).isPresent(), is(true));
    }

    private void addFacetsToResponse(SolrFacet facet) {
        NamedList<Object> queryResponse = new NamedList<>();
        queryResponse.add(FACETS_MARKER, facet.facetValues);

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
     * Facade to represent an {@link Aggregation} in a native Solr response.
     */
    private class SolrFacet {
        private final NamedList<Object> facetValues;

        public SolrFacet() {
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