package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.annotation.model.StatisticsByType;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.SLIM_USAGE;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.statsType;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatistics.DEFAULT_GO_TERM_LIMIT;

/**
 * // TODO: 21/11/17
 * Created 21/11/17
 * @author Edd
 */
class SlimmedStatsHelper {
    private SlimmedStatsHelper() {}

    static <T> Optional<T> extractMatchingStat(Collection<T> statInfos, Function<T, String> valueSource,
            String statValue) {
        return statInfos.stream()
                .filter(value -> valueSource.apply(value).equals(statValue))
                .findFirst();
    }

    static List<RequiredStatistic> updateRequiredStatsForSlimming(List<RequiredStatistic> requiredStatistics) {
        List<RequiredStatistic> requiredStatisticsForSlimUsage = new ArrayList<>();

        requiredStatistics.stream()
                .map(SlimmedStatsHelper::slimmedStat)
                .forEach(requiredStatisticsForSlimUsage::add);

        return requiredStatisticsForSlimUsage;
    }

    private static RequiredStatistic slimmedStat(RequiredStatistic requiredStatistic) {
        List<RequiredStatisticType> statsTypes = new ArrayList<>();
        for (RequiredStatisticType type : requiredStatistic.getTypes()) {
            RequiredStatisticType transformedType;
            if (type.getName().equals("goId")) {
                transformedType = statsType(type.getName(), 30000);
            } else {
                transformedType = statsType(type.getName(), type.getLimit());
            }
            statsTypes.add(transformedType);
        }

        return new RequiredStatistic(requiredStatistic.getGroupName(), requiredStatistic.getGroupField(),
                requiredStatistic.getAggregateFunction(), statsTypes);
    }

    static boolean isSlimRequest(AnnotationRequest request) {
        return request.getGoUsage().equals(SLIM_USAGE);
    }

    static void adjustStatsGroupsAfterSlimming(List<RequiredStatistic> requiredStatistics,
            List<StatisticsGroup> statsGroups) {
        for (RequiredStatistic requiredStatistic : requiredStatistics) {
            String groupName = requiredStatistic.getGroupName();
            Integer requestedGoIdLimit =
                    extractMatchingStat(requiredStatistic.getTypes(), RequiredStatisticType::getName, "goId")
                            .map(RequiredStatisticType::getLimit)
                            .orElse(DEFAULT_GO_TERM_LIMIT);
            List<StatisticsByType> statsTypes =
                    extractMatchingStat(statsGroups, StatisticsGroup::getGroupName, groupName)
                            .map(StatisticsGroup::getTypes)
                            .orElse(emptyList());
            Optional<StatisticsByType> statsType = extractMatchingStat(statsTypes, StatisticsByType::getType, "goId");
            Integer limit = statsType
                    .map(type -> type.getValues().size())
                    .map(size -> size > requestedGoIdLimit ? requestedGoIdLimit : size)
                    .orElse(DEFAULT_GO_TERM_LIMIT);
            statsType.ifPresent(type -> type.getValues().subList(limit, type.getValues().size()).clear());
        }
    }
}
