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
 * Given a list of {@link StatisticsGroup}s, inject an additional group that summarises slimming information.
 *
 * Created 15/11/17
 * @author Edd
 */
class SlimmedStatsInjector {
    static final String SLIMMING_GROUP_NAME = "slimming";
    static final String ANNOTATIONS_FOR_GO_SLIMS_NAME = "annotationsForGoId";
    static final String GO_ID_TYPE_NAME = "goId";
    static final String ANNOTATION_GROUP_NAME = "annotation";
    private static final StatisticsGroup EMPTY_SLIM_STATS_GROUP = new StatisticsGroup(SLIMMING_GROUP_NAME, 0);

    /**
     * Processes the list of {@link StatisticsGroup}s, and adds a summary of slimming information as an
     * additional group, using the supplied slimming map as the source of knowledge of GO term / slimmed GO term
     * associations.
     * @param statsGroups the initial list of statistics groups
     * @param slimmingMap a map of GO term to slimmed GO terms
     */
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

    /**
     * Create a {@link StatisticsGroup} summarising the clustering of annotations around slimmed
     * GO terms.
     * @param totalHits the number of hits made to annotations
     * @param values the {@link StatisticsValue}s for the non-slimmed GO terms
     * @param slimmingMap the map of GO term to slimmed GO terms
     * @return {@link StatisticsGroup} summarising how many annotations there are to each slimmed GO term
     */
    private StatisticsGroup aggregateGoIds(long totalHits, List<StatisticsValue> values,
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
        StatisticsByType gpsForGoId = new StatisticsByType(ANNOTATIONS_FOR_GO_SLIMS_NAME, slimAggregation.values()
                .size());
        slimAggregation.values().forEach(gpsForGoId::addValue);
        slimInfoGroup.addStatsType(gpsForGoId);
        return slimInfoGroup;
    }
}
