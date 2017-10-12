package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * A simple implementation of the {@link StatsConverter} interface.
 *
 * @author Ricardo Antunes
 */
@Component public class StatsConverterImpl implements StatsConverter {

    static final String DEFAULT_GLOBAL_AGGREGATE_NAME = "global";

    @Override public AggregateRequest convert(Collection<RequiredStatistic> requiredStatistics) {
        Preconditions.checkArgument(requiredStatistics != null && !requiredStatistics.isEmpty(),
                                    "Stats request collection cannot be null or empty");

        AggregateRequest globalAggregate = new AggregateRequest(DEFAULT_GLOBAL_AGGREGATE_NAME);
        Map<RequiredStatisticType, AggregateRequest> nestedAggregateMap = new HashMap<>();

        requiredStatistics.forEach(statistic -> buildAggregation(statistic, nestedAggregateMap, globalAggregate));

        // add all values of map as nested aggregates to global aggregate
        combineGlobalAndNestedAggregates(globalAggregate, nestedAggregateMap);
        return globalAggregate;
    }

    private void buildAggregation(RequiredStatistic statistic,
            Map<RequiredStatisticType, AggregateRequest> nestedAggregateMap, AggregateRequest globalAggregate) {
        populateAggregate(statistic, globalAggregate);
        populateNestedAggregationWithRequiredStatisticTypes(statistic, nestedAggregateMap);
    }

    private void populateNestedAggregationWithRequiredStatisticTypes(RequiredStatistic statistic,
            Map<RequiredStatisticType, AggregateRequest> nestedAggregateMap) {
        statistic.getTypes()
                 .stream()
                 .map(type -> nestedAggregateMap.computeIfAbsent(type, StatsConverterImpl::createRequest))
                 .forEach(aggregateRequestForType -> populateAggregate(statistic, aggregateRequestForType));
    }

    private void populateAggregate(RequiredStatistic request, AggregateRequest aggregate) {
        aggregate.addField(request.getGroupField(), AggregateFunction.typeOf(request.getAggregateFunction()));
    }

    private void combineGlobalAndNestedAggregates(AggregateRequest globalAggregate,
            Map<RequiredStatisticType, AggregateRequest> nestedAggregateMap) {
        nestedAggregateMap.values().forEach(globalAggregate::addNestedAggregate);
    }

    private static AggregateRequest createRequest(RequiredStatisticType k) {
        return new AggregateRequest(k.getName(), k.getLimit());
    }
}
