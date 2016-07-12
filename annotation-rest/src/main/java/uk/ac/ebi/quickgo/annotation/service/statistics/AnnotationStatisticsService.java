package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.*;
import uk.ac.ebi.quickgo.rest.search.*;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.RequestConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.AggregationResult;
import uk.ac.ebi.quickgo.rest.search.results.Aggregation;
import uk.ac.ebi.quickgo.rest.search.results.AggregationBucket;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private static final int FIRST_PAGE = 1;
    private static final int RESULTS_PER_PAGE = 0;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RequestConverterFactory converterFactory;
    private final SearchService<Annotation> searchService;
    private final StatsRequestConverter converter;

    @Autowired
    public AnnotationStatisticsService(RequestConverterFactory converterFactory,
            SearchService<Annotation> searchService,
            StatsRequestConverter converter) {
        this.converterFactory = converterFactory;
        this.searchService = searchService;
        this.converter = converter;
    }

    @Override public QueryResult<StatisticsGroup> calculate(AnnotationRequest request) {
        List<AnnotationRequest.StatsRequest> statsRequest = request.createStatsRequests();

        QueryRequest queryRequest = buildQueryRequest(request);

        QueryResult<Annotation> annotationQueryResult = searchService.findByQuery(queryRequest);

        Aggregation globalAggregation = annotationQueryResult.getAggregation();

        List<StatisticsGroup> statsGroups = statsRequest.stream()
                .map(req -> convertResponse(globalAggregation, req))
                .collect(Collectors.toList());

        return new QueryResult.Builder<>(statsGroups.size(), statsGroups).build();
    }

    private QueryRequest buildQueryRequest(AnnotationRequest request) {
        BasicSearchQueryTemplate basicTemplate = new BasicSearchQueryTemplate(Collections.emptyList());
        AggregateSearchQueryTemplate aggregateTemplate = new AggregateSearchQueryTemplate();

        BasicSearchQueryTemplate.Builder basicBuilder = basicTemplate.newBuilder()
                .setQuery(QuickGOQuery.createAllQuery())
                .setFilters(request.createRequestFilters().stream()
                        .map(converterFactory::convert)
                        .collect(Collectors.toSet()))
                .setPage(FIRST_PAGE)
                .setPageSize(RESULTS_PER_PAGE);

        return aggregateTemplate.newBuilder(basicBuilder)
                .setAggregate(converter.convert(request.createStatsRequests()))
                .build();
    }

    private StatisticsGroup convertResponse(Aggregation globalAggregation, AnnotationRequest.StatsRequest statsRequest) {
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
        return globalAggregation.getAggregationResult(UNIQUE, groupField)
                .map(agg -> (long) agg.getResult()).orElse(NO_COUNT_FOR_GROUP_ERROR);
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