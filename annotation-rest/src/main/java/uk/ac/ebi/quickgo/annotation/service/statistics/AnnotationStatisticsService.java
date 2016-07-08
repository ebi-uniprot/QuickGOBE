package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.*;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.results.AggregationResult;
import uk.ac.ebi.quickgo.rest.search.results.Aggregation;
import uk.ac.ebi.quickgo.rest.search.results.AggregationBucket;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields.*;
import static uk.ac.ebi.quickgo.rest.search.AggregateFunction.*;

/**
 * Service that collects distribution statistics of annotations and gene products throughout a given set of annotation
 * fields.
 *
 * @author Ricardo Antunes
 */
@Service
public class AnnotationStatisticsService implements StatisticsService {
    private static final long NO_COUNT_FOR_GROUP_ERROR = -1L;

//    private static final int FIRST_PAGE = 1;
//    private static final int RESULTS_PER_PAGE = 0;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //    private final RequestConverterFactory converterFactory;
    //    private final FacetedSearchQueryTemplate queryTemplate;
    //    private final SearchService searchService;

    @Override public QueryResult<StatisticsGroup> calculate(AnnotationRequest request) {
        List<AnnotationRequest.StatsRequest> statsRequest = request.createStatsRequests();

        //        QueryRequest queryRequest = queryTemplate.newBuilder()
        //                .setFacets(null)
        //                .setQuery(QuickGOQuery.createAllQuery())
        //                .setFilters(request.createRequestFilters().stream()
        //                        .map(converterFactory::convertAggregationPerField)
        //                        .collect(Collectors.toSet()))
        //                .setPage(FIRST_PAGE)
        //                .setPageSize(RESULTS_PER_PAGE)
        //
        //                .build();

        List<Annotation> results = new ArrayList<>();

        QueryResult<Annotation> annotationQueryResult = new QueryResult.Builder<>(6, results)
                .appendAggregations(mockResponse())
                .build();

        List<Aggregation> aggregations = annotationQueryResult.getAggregations();

        //TODO: remove this check, put something that makes more sense.
        Preconditions.checkArgument(aggregations.size() == 1, "Aggregation size is not 1: " + aggregations.size());

        Aggregation globalAggregation = aggregations.get(0);

        List<StatisticsGroup> statsGroups = statsRequest.stream()
                .map(req -> convert(globalAggregation, req))
                .collect(Collectors.toList());

        return new QueryResult.Builder<>(statsGroups.size(), statsGroups).build();
    }

    private StatisticsGroup convert(Aggregation globalAggregation, AnnotationRequest.StatsRequest statsRequest) {
        StatisticsConverter converter =
                new StatisticsConverter(statsRequest.getGroupName(), statsRequest.getGroupField());

        long totalHits = extractCount(globalAggregation, statsRequest.getGroupField());

        if (totalHits == NO_COUNT_FOR_GROUP_ERROR) {
            throw new RetrievalException("Unable to calculate statistics for group: " + statsRequest.getGroupName());
        }

        return converter.convert(globalAggregation.getNestedAggregations(), totalHits);
    }

    /**
     * Extracts the counts made on the whole data set for a given group.
     *
     * @see AnnotationRequest.StatsRequest#getGroupName()
     *
     * @param globalAggregation the aggregation object containing the group count values
     * @param groupField the name of the groupField the count was made upon
     * @return an object containing the global counts of things that are of interest
     */
    private long extractCount(Aggregation globalAggregation, String groupField) {
        return globalAggregation.getAggregationResult(COUNT, groupField)
                .map(agg -> (long) agg.getResult()).orElse(NO_COUNT_FOR_GROUP_ERROR);
    }

    private Aggregation mockResponse() {
        Aggregation goIdAgg = new Aggregation(GO_ID);

        AggregationBucket goIdBucket1 = new AggregationBucket("GO:0016020");
        goIdBucket1.addAggregateResult(UNIQUE, ID, 2);
        goIdBucket1.addAggregateResult(UNIQUE, GENE_PRODUCT_ID, 3);
        goIdAgg.addBucket(goIdBucket1);

        AggregationBucket goIdBucket2 = new AggregationBucket("GO:0016021");
        goIdBucket2.addAggregateResult(UNIQUE, ID, 2);
        goIdBucket2.addAggregateResult(UNIQUE, GENE_PRODUCT_ID, 3);
        goIdAgg.addBucket(goIdBucket2);

        AggregationBucket goIdBucket3 = new AggregationBucket("GO:0005737");
        goIdBucket3.addAggregateResult(UNIQUE, ID, 2);
        goIdBucket3.addAggregateResult(UNIQUE, GENE_PRODUCT_ID, 4);
        goIdAgg.addBucket(goIdBucket3);

        Aggregation globalAgg = new Aggregation("global");
        globalAgg.addAggregationResult(COUNT, ID, 6);
        globalAgg.addAggregationResult(COUNT, GENE_PRODUCT_ID, 10);
        globalAgg.addAggregation(goIdAgg);

        return globalAgg;
    }

    /**
     * Converts a collection of {@link Aggregation} data retrieved from a {@link QueryResult}, into a
     * {@link StatisticsGroup}, that can be presented to the client.
     * </p>
     * This class is capable of creating a single {@link StatisticsGroup} per call to the
     * {@link StatisticsConverter#convert(Collection, long)} method.
     */
    private class StatisticsConverter {
        private final String groupField;
        private final String groupName;

        StatisticsConverter(String groupName, String groupField) {
            this.groupName = groupName;
            this.groupField = groupField;
        }

        StatisticsGroup convert(Collection<Aggregation> aggregations, long totalHits) {
            StatisticsGroup statsGroup = new StatisticsGroup(groupName, totalHits);

            aggregations.stream()
                    .map(agg -> createStatsType(agg, totalHits))
                    .forEach(statsGroup::addStatsType);

            return statsGroup;
        }

        private StatisticsByType createStatsType(Aggregation aggregation, long totalHits) {
            StatisticsByType type = new StatisticsByType(aggregation.getName());

            Set<AggregationBucket> buckets = aggregation.getBuckets();

            buckets.stream()
                    .map(bucket -> createStatsValue(bucket, totalHits))
                    .forEach(value -> {
                        if (value.isPresent()) {
                            type.addValue(value.get());
                        } else {
                            logger.warn("No stats for groupField {}, for type {}", groupField, type.getType());
                        }
                    });

            return type;
        }

        private Optional<StatisticsValue> createStatsValue(AggregationBucket bucket, long totalHits) {
            Optional<AggregationResult> resultOpt = bucket.getAggregationResult(UNIQUE, groupField);

            return resultOpt.
                    map(aggResult -> new StatisticsValue(bucket.getValue(), (long) aggResult.getResult(), totalHits));
        }
    }
}