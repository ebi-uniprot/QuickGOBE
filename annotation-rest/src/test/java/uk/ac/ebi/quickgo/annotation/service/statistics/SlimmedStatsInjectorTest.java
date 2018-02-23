package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.StatisticsByType;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;

import java.util.*;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNot.not;
import static uk.ac.ebi.quickgo.annotation.service.statistics.SlimmedStatsHelper.extractMatchingStat;
import static uk.ac.ebi.quickgo.annotation.service.statistics.SlimmedStatsInjector.ANNOTATIONS_FOR_GO_SLIMS_NAME;
import static uk.ac.ebi.quickgo.annotation.service.statistics.SlimmedStatsInjector.ANNOTATION_GROUP_NAME;
import static uk.ac.ebi.quickgo.annotation.service.statistics.SlimmedStatsInjector.GO_ID_TYPE_NAME;
import static uk.ac.ebi.quickgo.annotation.service.statistics.SlimmedStatsInjector.SLIMMING_GROUP_NAME;

/**
 * Created 21/11/17
 * @author Edd
 */
public class SlimmedStatsInjectorTest {
    private static final int TOTAL = 10;
    private static final int UNTOUCHED_HITS = 5;
    private static final String GENE_PRODUCT = "geneProduct";
    private Map<String, List<String>> slimMap;
    private SlimmedStatsInjector slimStatsInjector;
    private List<StatisticsGroup> stats;
    private StatisticsGroup annotationGroup;
    private StatisticsByType annotationGoStatsType;
    private StatisticsByType gpGoStatsType = new StatisticsByType(GO_ID_TYPE_NAME);
    private StatisticsValue gpGoValue = new StatisticsValue(go(1), UNTOUCHED_HITS, TOTAL);
    private List<StatisticsGroup> initialStatsGroups;

    @Before
    public void setUp() {
        slimMap = new HashMap<>();
        slimStatsInjector = new SlimmedStatsInjector();
        stats = new ArrayList<>();
        annotationGroup = new StatisticsGroup(ANNOTATION_GROUP_NAME, TOTAL);
        annotationGoStatsType = new StatisticsByType(GO_ID_TYPE_NAME);
    }

    @Test
    public void termSlimsToNoTerm() {
        slimMap.put(go(1), singletonList(go(2)));
        annotationGoStatsType.addValue(new StatisticsValue(go(4444), 5, TOTAL));
        setUpStats();

        slimStatsInjector.process(stats, slimMap);

        validateExistingGroups();
        List<StatisticsValue> slimValues = validateSlimGroup();
        assertThat(slimValues, hasSize(0));
    }

    @Test
    public void termSlimsToOneTerm() {
        slimMap.put(go(1), singletonList(go(2)));

        annotationGoStatsType.addValue(new StatisticsValue(go(1), 5, TOTAL));
        setUpStats();

        slimStatsInjector.process(stats, slimMap);

        validateExistingGroups();
        List<StatisticsValue> slimValues = validateSlimGroup();
        assertThat(slimValues, hasSize(1));
        final double percentage = ((double) (long) 5 / TOTAL) * 100;
        slimValuesContain(slimValues, go(2), (long) 5, percentage);
    }

    @Test
    public void twoTermsSlimToSameTerm() {
        slimMap.put(go(1), singletonList(go(2)));
        slimMap.put(go(2), singletonList(go(2)));
        annotationGoStatsType.addValue(new StatisticsValue(go(1), 5, TOTAL));
        annotationGoStatsType.addValue(new StatisticsValue(go(2), 3, TOTAL));
        setUpStats();

        slimStatsInjector.process(stats, slimMap);

        validateExistingGroups();
        List<StatisticsValue> slimValues = validateSlimGroup();
        assertThat(slimValues, hasSize(1));
        final double percentage = ((double) (long) 8 / TOTAL) * 100;
        slimValuesContain(slimValues, go(2), (long) 8, percentage);
    }

    @Test
    public void termSlimsToTwoTerms() {
        slimMap.put(go(1), asList(go(2), go(3)));
        annotationGoStatsType.addValue(new StatisticsValue(go(1), 5, TOTAL));
        setUpStats();

        slimStatsInjector.process(stats, slimMap);

        validateExistingGroups();
        List<StatisticsValue> slimValues = validateSlimGroup();
        assertThat(slimValues, hasSize(2));
        final double percentage = ((double) 5 / TOTAL) * 100;
        slimValuesContain(slimValues, go(2), 5L, percentage);
    }

    private void slimValuesContain(List<StatisticsValue> slimValues,
            String id, long hits, double percentage) {
        StatisticsValue go2SlimValue =
                extractMatchingStat(slimValues, StatisticsValue::getKey, id).orElse(null);
        assertThat(go2SlimValue, is(not(nullValue())));
        assertThat(go2SlimValue.getHits(), is(hits));
        assertThat(go2SlimValue.getPercentage(), is(percentage));
    }

    private void setUpStats() {
        annotationGroup.addStatsType(annotationGoStatsType);
        stats.add(annotationGroup);

        StatisticsGroup geneProductGroup = new StatisticsGroup(GENE_PRODUCT, TOTAL);
        gpGoStatsType.addValue(gpGoValue);
        geneProductGroup.addStatsType(gpGoStatsType);
        stats.add(geneProductGroup);

        assertThat(stats, hasSize(2));

        recordInitialStats();
    }

    private void recordInitialStats() {
        initialStatsGroups = new ArrayList<>();
        for (StatisticsGroup stat : stats) {
            StatisticsGroup copyOfInitialGroup = new StatisticsGroup(stat.getGroupName(), stat.getTotalHits());
            for (StatisticsByType type : stat.getTypes()) {
                StatisticsByType copyOfStatType = new StatisticsByType(type.getType());
                for (StatisticsValue statValue : type.getValues()) {
                    copyOfStatType.addValue(
                            new StatisticsValue(statValue.getKey(), statValue.getHits(), stat.getTotalHits()));
                }
                copyOfInitialGroup.addStatsType(copyOfStatType);
            }
            initialStatsGroups.add(copyOfInitialGroup);
        }
    }

    private void validateExistingGroups() {
        for (StatisticsGroup initialStatsGroup : initialStatsGroups) {
            assertThat(stats, hasItem(initialStatsGroup));
        }
    }

    private List<StatisticsValue> validateSlimGroup() {
        assertThat(stats, hasSize(3));
        StatisticsGroup slimGroup =
                extractMatchingStat(stats, StatisticsGroup::getGroupName, SLIMMING_GROUP_NAME).orElse(null);
        assertThat(slimGroup, is(not(nullValue())));
        List<StatisticsByType> slimmingGroupTypes = slimGroup.getTypes();
        assertThat(slimmingGroupTypes, hasSize(1));
        StatisticsByType slimmedGoStatsType = slimmingGroupTypes.get(0);
        assertThat(slimmedGoStatsType.getType(), is(ANNOTATIONS_FOR_GO_SLIMS_NAME));
        assertThat(slimmedGoStatsType.getApproximateCount(), is(slimmedGoStatsType.getValues().size()));
        return slimmedGoStatsType.getValues();
    }

    private String go(int id) {
        return "GO:" + id;
    }
}
