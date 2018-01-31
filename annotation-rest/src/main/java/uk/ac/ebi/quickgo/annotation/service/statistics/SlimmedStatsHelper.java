package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.annotation.model.StatisticsByType;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static uk.ac.ebi.quickgo.annotation.common.AnnotationFields.Facetable.GO_ID;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.SLIM_USAGE;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.statsType;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatistics.DEFAULT_GO_TERM_LIMIT;

/**
 * Contains methods common to computing statistics in the context of slimming.
 * 
 * Created 21/11/17
 * @author Edd
 */
class SlimmedStatsHelper {

    private static final int GO_IDS_IN_ANNOTATIONS_LIMIT = 50000;

    private SlimmedStatsHelper() {}

    /**
     * Given {@link RequiredStatistic}s in the context of slimming, these required statistics need adjusting
     * so that more GO terms are fetched, so that the slimming subgroup can be computed.
     * @param requiredStatistics the statistics that are required
     * @return a list of {@link RequiredStatistic}s, based on those passed in, adjusted for slimming
     */
    static List<RequiredStatistic> updateRequiredStatsForSlimming(List<RequiredStatistic> requiredStatistics) {
        return requiredStatistics.stream()
                .map(SlimmedStatsHelper::slimmedStat)
                .collect(Collectors.toList());
    }

    /**
     * After slimming, the {@link StatisticsGroup}s will contain excessive information about GO terms, which was
     * required to compute the additional {@link StatisticsGroup} that summarises slimming information; see
     * {@link SlimmedStatsInjector}. This method adjusts the {@link StatisticsGroup}s to show only the amount of
     * information about GO terms that was originally requested.
     * @param requiredStatistics the original statistics request
     * @param statsGroups the statistics that have been computed
     */
    static void adjustStatsGroupsAfterSlimming(List<RequiredStatistic> requiredStatistics,
            List<StatisticsGroup> statsGroups) {
        for (RequiredStatistic requiredStatistic : requiredStatistics) {
            String groupName = requiredStatistic.getGroupName();
            Integer requestedGoIdLimit =
                    extractMatchingStat(requiredStatistic.getTypes(), RequiredStatisticType::getName, GO_ID)
                            .map(RequiredStatisticType::getLimit)
                            .orElse(DEFAULT_GO_TERM_LIMIT);
            List<StatisticsByType> statsTypes =
                    extractMatchingStat(statsGroups, StatisticsGroup::getGroupName, groupName)
                            .map(StatisticsGroup::getTypes)
                            .orElse(emptyList());
            Optional<StatisticsByType> statsType = extractMatchingStat(statsTypes, StatisticsByType::getType, GO_ID);
            Integer limit = statsType
                    .map(type -> type.getValues().size())
                    .map(size -> size > requestedGoIdLimit ? requestedGoIdLimit : size)
                    .orElse(DEFAULT_GO_TERM_LIMIT);
            statsType.ifPresent(type -> type.getValues().subList(limit, type.getValues().size()).clear());
        }
    }

    /**
     * Adjust a {@link RequiredStatistic} so that it fetches enough GO term data suitable for computing
     * slimming summaries.
     * @param requiredStatistic the statistic to adjust
     * @return a {@link RequiredStatistic} based on {@code requiredStatistic}, with adjusted limits.
     */
    private static RequiredStatistic slimmedStat(RequiredStatistic requiredStatistic) {
        List<RequiredStatisticType> statsTypes = new ArrayList<>();
        for (RequiredStatisticType type : requiredStatistic.getTypes()) {
            RequiredStatisticType transformedType;
            if (type.getName().equals(GO_ID)) {
                transformedType = statsType(type.getName(), GO_IDS_IN_ANNOTATIONS_LIMIT);
            } else {
                transformedType = statsType(type.getName(), type.getLimit());
            }
            statsTypes.add(transformedType);
        }

        return new RequiredStatistic(requiredStatistic.getGroupName(), requiredStatistic.getGroupField(),
                requiredStatistic.getAggregateFunction(), statsTypes);
    }

    static <T> Optional<T> extractMatchingStat(Collection<T> statInfos, Function<T, String> valueSource,
            String statValue) {
        return statInfos.stream()
                .filter(value -> valueSource.apply(value).equals(statValue))
                .findFirst();
    }

    static boolean isSlimRequest(AnnotationRequest request) {
        return request.getGoUsage().equals(SLIM_USAGE);
    }
}
