package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.*;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.SlimmingConversionInfo;
import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.DefaultSearchQueryTemplate;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.query.RegularPage;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.AggregateResponse;
import uk.ac.ebi.quickgo.rest.search.results.AggregationBucket;
import uk.ac.ebi.quickgo.rest.search.results.AggregationResult;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static uk.ac.ebi.quickgo.annotation.service.statistics.SlimmedStatsHelper.adjustStatsGroupsAfterSlimming;
import static uk.ac.ebi.quickgo.annotation.service.statistics.SlimmedStatsHelper.isSlimRequest;
import static uk.ac.ebi.quickgo.annotation.service.statistics.SlimmedStatsHelper.updateRequiredStatsForSlimming;

/**
 * Service that collects distribution statistics of annotations and gene products throughout a given set of annotation
 * fields. This class provides statistics for two different request types: requiredStatisticsForStandardUsage defines
 * the statistics
 * required for presentation by the front end of QuickGO, available as a restful service, while downloadStatistics
 * defines statistics that will be downloaded as a file to the client.
 *
 * @author Ricardo Antunes
 */
@Service
public class AnnotationStatisticsService implements StatisticsService {
    private static final long NO_COUNT_FOR_GROUP_ERROR = -1L;

    private static final int FIRST_PAGE = 1;
    private static final int RESULTS_PER_PAGE = 0;
    private final List<RequiredStatistic> requiredStatisticsForStandardUsage;
    private final List<RequiredStatistic> requiredStatisticsForDownloadUsage;
    private final FilterConverterFactory converterFactory;
    private final SearchService<Annotation> searchService;
    private final StatsConverter converter;
    private final DefaultSearchQueryTemplate queryTemplate;
    private final SlimmedStatsInjector slimmedStatsInjector;

    @Autowired
    public AnnotationStatisticsService(FilterConverterFactory converterFactory,
            SearchService<Annotation> searchService,
            StatsConverter converter,
            RequiredStatistics requiredStatisticsForStandardUsage,
            RequiredStatistics requiredStatisticsForDownloadUsage) {
        checkArgument(converterFactory != null, "Filter factory cannot be null.");
        checkArgument(searchService != null, "Search service cannot be null.");
        checkArgument(converter != null, "Stats request converter cannot be null.");
        checkArgument(requiredStatisticsForStandardUsage != null,
                "Required statistics for standard usage cannot be null.");
        checkArgument(requiredStatisticsForDownloadUsage != null,
                "Required statistics for download usage cannot be null.");

        this.converterFactory = converterFactory;
        this.searchService = searchService;
        this.converter = converter;

        checkState(requiredStatisticsForStandardUsage.getStats() != null,
                "Required statistics for standard usage cannot be null.");
        checkState(requiredStatisticsForDownloadUsage.getStats() != null,
                "Required statistics for download cannot be null.");

        this.requiredStatisticsForStandardUsage = requiredStatisticsForStandardUsage.getStats();
        this.requiredStatisticsForDownloadUsage = requiredStatisticsForDownloadUsage.getStats();

        this.queryTemplate = new DefaultSearchQueryTemplate();
        this.slimmedStatsInjector = new SlimmedStatsInjector();
    }

    @Override
    public QueryResult<StatisticsGroup> calculateForStandardUsage(AnnotationRequest request) {
        return calculateForRequiredStatistics(request, requiredStatisticsForStandardUsage);
    }

    @Override
    public QueryResult<StatisticsGroup> calculateForDownloadUsage(AnnotationRequest request) {
        return calculateForRequiredStatistics(request, requiredStatisticsForDownloadUsage);
    }

    private QueryResult<StatisticsGroup> calculateForRequiredStatistics(AnnotationRequest request,
            List<RequiredStatistic> requiredStatistics) {
        checkArgument(request != null, "Annotation request cannot be null");

//        local
//        final List<FilterRequest> filterRequests = request.createFilterRequests();
//        checkArgument(!filterRequests.isEmpty(), "Statistics requests require at least one filtering parameter.");
//        QueryRequest queryRequest = buildQueryRequest(filterRequests, requiredStatistics);


        //server
        StatsQueryInfo queryInfo = createQueryInfo(request, requiredStatistics);
        QueryRequest queryRequest = queryInfo.getQueryRequest();

//        QueryRequest queryRequest = buildQueryRequest(request, requiredStatistics);
        QueryResult<Annotation> annotationQueryResult = searchService.findByQuery(queryRequest);
        AggregateResponse globalAggregation = annotationQueryResult.getAggregation();

        QueryResult<StatisticsGroup> response;
        if (globalAggregation.isPopulated()) {
            List<StatisticsGroup> statsGroups = createStatsGroups(requiredStatistics, queryInfo, globalAggregation);
            response = new QueryResult.Builder<>(statsGroups.size(), statsGroups).build();
        } else {
            response = new QueryResult.Builder<>(0, Collections.<StatisticsGroup>emptyList()).build();
        }

        return response;
    }

    private StatsQueryInfo createQueryInfo(AnnotationRequest request, List<RequiredStatistic> requiredStatistics) {
        List<RequiredStatistic> stats = requiredStatistics;
        if (isSlimRequest(request)) {
            stats = updateRequiredStatsForSlimming(requiredStatistics);
        }

        return buildQueryRequest(request, stats);
    }

    private StatsQueryInfo buildQueryRequest(AnnotationRequest request,
            List<RequiredStatistic> requiredStatistics) {
        Map<String, List<String>> slimmingInfoMap = new HashMap<>();
        QueryRequest queryRequest = queryTemplate.newBuilder()
                .setQuery(QuickGOQuery.createAllQuery())
                .addFilters(request.createFilterRequests().stream()
                        .map(converterFactory::convert)
                        .map(convertedFilter -> captureConvertedFilterInfo(convertedFilter, slimmingInfoMap))
                        .collect(Collectors.toSet()))
                .setPage(new RegularPage(FIRST_PAGE, RESULTS_PER_PAGE))
                .setAggregate(converter.convert(requiredStatistics))
                .build();

        return new StatsQueryInfo() {
            @Override public QueryRequest getQueryRequest() {
                return queryRequest;
            }

            @Override public Optional<Map<String, List<String>>> getSlimmingInfoMap() {
                return slimmingInfoMap.isEmpty() ? Optional.empty() : Optional.of(slimmingInfoMap);
            }
        };
    }

//    private QueryRequest buildQueryRequest(List<FilterRequest> filterRequests, List<RequiredStatistic>
//            requiredStatistics) {
//        return queryTemplate.newBuilder()
//                .setQuery(QuickGOQuery.createAllQuery())
//                .addFilters(filterRequests.stream()
//                        .map(converterFactory::convert)
//                        .map(ConvertedFilter::getConvertedValue)
//                        .collect(Collectors.toSet()))
//                .setPage(new RegularPage(FIRST_PAGE, RESULTS_PER_PAGE))
//                .setAggregate(converter.convert(requiredStatistics))
//                .build();
//    }

    /**
     * Given a {@link ConvertedFilter}, capture the slimming information it may contain, in addition to returning
     * associated {@link QuickGOQuery}.
     * @param filter the {@link ConvertedFilter}
     * @param slimmingMap the map of slimming information
     * @return the {@link QuickGOQuery} associated with the filter
     */
    private QuickGOQuery captureConvertedFilterInfo(ConvertedFilter<QuickGOQuery> filter,
            Map<String, List<String>> slimmingMap) {
        filter.getFilterContext().ifPresent(
                filterContext -> filterContext
                        .get(SlimmingConversionInfo.class)
                        .ifPresent(slimInfo -> slimmingMap.putAll(slimInfo.getInfo()))
        );
        return filter.getConvertedValue();
    }

    private StatisticsGroup convertResponse(AggregateResponse globalAggregation, RequiredStatistic requiredStatistic) {
        StatisticsConverter converter =
                new StatisticsConverter(requiredStatistic.getGroupName(), requiredStatistic.getGroupField());

        long totalHits =
                extractCount(globalAggregation, requiredStatistic.getGroupField(),
                        requiredStatistic.getAggregateFunction());

        if (totalHits == NO_COUNT_FOR_GROUP_ERROR) {
            throw new RetrievalException(
                    "Unable to calculate statistics for group: " + requiredStatistic.getGroupName());
        }

        return converter.convert(globalAggregation.getNestedAggregations(), totalHits);
    }

    private List<StatisticsGroup> createStatsGroups(List<RequiredStatistic> requiredStatistics,
            StatsQueryInfo queryInfo, AggregateResponse
            globalAggregation) {

        List<StatisticsGroup> statsGroups = requiredStatistics.stream()
                .map(req -> convertResponse(globalAggregation, req))
                .collect(Collectors.toList());

        queryInfo.getSlimmingInfoMap().ifPresent(slimmingMap -> {
            slimmedStatsInjector.process(statsGroups, slimmingMap);
            adjustStatsGroupsAfterSlimming(requiredStatistics, statsGroups);
        });

        return statsGroups;
    }

    /**
     * Extracts the counts made on the whole data set for a given group.
     *
     * @see RequiredStatistic#getGroupName()
     *
     * @param globalAggregation the aggregation object containing the group count values
     * @param groupField the name of the groupField the count was made upon
     * @return an object containing the global counts of things that are of interest
     */
    private long extractCount(AggregateResponse globalAggregation, String groupField, String aggregateFunction) {
        return globalAggregation.getAggregationResult(AggregateFunction.typeOf(aggregateFunction), groupField)
                .map(agg -> (long) agg.getResult()).orElse(NO_COUNT_FOR_GROUP_ERROR);
    }

    /**
     * Converts a collection of {@link AggregateResponse} data retrieved from a {@link QueryResult}, into a
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

        StatisticsGroup convert(Collection<AggregateResponse> aggregations, long totalHits) {
            StatisticsGroup statsGroup = new StatisticsGroup(groupName, totalHits);

            aggregations.stream()
                    .map(agg -> createStatsType(agg, totalHits))
                    .forEach(statsGroup::addStatsType);

            return statsGroup;
        }

        private StatisticsByType createStatsType(AggregateResponse aggregation, long totalHits) {
            StatisticsByType type = new StatisticsByType(aggregation.getName(),aggregation.getDistinctValuesCount());
            Set<AggregationBucket> buckets = aggregation.getBuckets();
            buckets.stream()
                    .map(bucket -> createStatsValues(bucket, totalHits))
                    .flatMap(Collection::stream)
                    .forEach(type::addValue);
            return type;
        }

        private Set<StatisticsValue> createStatsValues(AggregationBucket bucket, long totalHits) {
            Set<AggregationResult> resultOpt = bucket.getAggregationResults(groupField);

            return resultOpt.stream()
                    .map(aggResult -> new StatisticsValue(bucket.getValue(), (long) aggResult.getResult(), totalHits))
                    .collect(Collectors.toSet());
        }
    }

    private interface StatsQueryInfo {
        QueryRequest getQueryRequest();

        Optional<Map<String, List<String>>> getSlimmingInfoMap();
    }
}
