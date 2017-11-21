package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.StatisticsByType;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static uk.ac.ebi.quickgo.annotation.service.statistics.SlimmedStatsHelper.extractMatchingStat;

/**
 * Created 15/11/17
 * @author Edd
 */
class SlimmedStatsInjector {
    private static final String SLIMMING_GROUP_NAME = "slimming";
    private static final StatisticsGroup EMPTY_SLIM_STATS_GROUP = new StatisticsGroup(SLIMMING_GROUP_NAME, 0);
    private static final String ANNOTATION_GROUP_NAME = "annotation";
    private static final String GO_ID_TYPE_NAME = "goId";
    private static final String ANNOTATIONS_FOR_GO_SLIMS_NAME = "annotationsForGoId";

    void process(List<StatisticsGroup> statsGroups, Map<String, List<String>> slimmingMap) {
        extractMatchingStat(statsGroups, StatisticsGroup::getGroupName, ANNOTATION_GROUP_NAME)
                .map(group -> createGeneProductToSlimmedTermsGroup(group, slimmingMap))
                .ifPresent(statsGroups::add);
    }

    private StatisticsGroup createGeneProductToSlimmedTermsGroup(StatisticsGroup group,
            Map<String, List<String>> slimmingMap) {

        return extractMatchingStat(group.getTypes(), StatisticsByType::getType, GO_ID_TYPE_NAME)
                .map(goType -> aggregateGoIds(group.getTotalHits(), goType.getValues(), slimmingMap))
                .orElse(EMPTY_SLIM_STATS_GROUP);
    }

    private StatisticsGroup aggregateGoIds(
            long totalHits,
            List<StatisticsValue> values,
            Map<String, List<String>> slimmingMap) {
        Map<String, StatisticsValue> slimAggregation = new HashMap<>();
        for (StatisticsValue value : values) {
            List<String> slimmedTerms = slimmingMap.getOrDefault(value.getKey(), emptyList());
            for (String slimmedTerm : slimmedTerms) {
                if (!slimAggregation.containsKey(slimmedTerm)) {
                    slimAggregation.put(slimmedTerm, new StatisticsValue(slimmedTerm, value.getHits(), totalHits));
                } else {
                    slimAggregation.compute(slimmedTerm, (key, statsValue) ->
                            new StatisticsValue(key, statsValue.getHits() + value.getHits(), totalHits));
                }
            }
        }

        StatisticsGroup slimInfoGroup = new StatisticsGroup(SLIMMING_GROUP_NAME, totalHits);
        StatisticsByType gpsForGoId = new StatisticsByType(ANNOTATIONS_FOR_GO_SLIMS_NAME);
        slimAggregation.values().forEach(gpsForGoId::addValue);
        slimInfoGroup.addStatsType(gpsForGoId);
        return slimInfoGroup;
    }
}
