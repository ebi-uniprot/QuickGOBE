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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static uk.ac.ebi.quickgo.annotation.service.statistics.SlimmedStatsHelper.extractMatchingStat;
import static uk.ac.ebi.quickgo.annotation.service.statistics.SlimmedStatsInjector.ANNOTATIONS_FOR_GO_SLIMS_NAME;
import static uk.ac.ebi.quickgo.annotation.service.statistics.SlimmedStatsInjector.SLIMMING_GROUP_NAME;

/**
 * Created 21/11/17
 * @author Edd
 */
public class SlimmedStatsInjectorTest {
    public static final String GO_ID = "goId";
    private static final int TOTAL = 10;
    private static final int UNTOUCHED_HITS = 5;
    private Map<String, List<String>> slimMap;
    private SlimmedStatsInjector slimStatsInjector;
    private List<StatisticsGroup> stats;
    private StatisticsGroup annotationGroup;
    private StatisticsByType goStatsType;
    private StatisticsByType gpGoStatsType = new StatisticsByType(GO_ID);
    private StatisticsValue gpGoValue = new StatisticsValue(go(1), UNTOUCHED_HITS, TOTAL);

    @Before
    public void setUp() {
        slimMap = new HashMap<>();
        slimStatsInjector = new SlimmedStatsInjector();
        stats = new ArrayList<>();
        annotationGroup = new StatisticsGroup("annotation", TOTAL);
        goStatsType = new StatisticsByType("goId");
    }

    @Test
    public void termSlimsToNoTerm() {
        slimMap.put(go(1), singletonList(go(2)));
        ;
        goStatsType.addValue(new StatisticsValue(go(4444), 5, TOTAL));
        setUpStats();

        slimStatsInjector.process(stats, slimMap);

        List<StatisticsValue> slimValues = validateSlimGroup();
        assertThat(slimValues, hasSize(0));
    }

    @Test
    public void termSlimsToOneTerm() {
        slimMap.put(go(1), singletonList(go(2)));
        ;
        goStatsType.addValue(new StatisticsValue(go(1), 5, TOTAL));
        setUpStats();

        slimStatsInjector.process(stats, slimMap);

        List<StatisticsValue> slimValues = validateSlimGroup();
        assertThat(slimValues, hasSize(1));
        slimValuesContain(slimValues, go(2), (long) 5, (double) (long) 5 / TOTAL);
    }

    @Test
    public void twoTermsSlimToSameTerm() {
        slimMap.put(go(1), singletonList(go(2)));
        slimMap.put(go(2), singletonList(go(2)));
        goStatsType.addValue(new StatisticsValue(go(1), 5, TOTAL));
        goStatsType.addValue(new StatisticsValue(go(2), 3, TOTAL));
        setUpStats();

        slimStatsInjector.process(stats, slimMap);

        List<StatisticsValue> slimValues = validateSlimGroup();
        assertThat(slimValues, hasSize(1));
        slimValuesContain(slimValues, go(2), (long) 8, (double) (long) 8 / TOTAL);
    }

    @Test
    public void termSlimsToTwoTerms() {
        slimMap.put(go(1), asList(go(2), go(3)));
        goStatsType.addValue(new StatisticsValue(go(1), 5, TOTAL));
        setUpStats();

        slimStatsInjector.process(stats, slimMap);

        List<StatisticsValue> slimValues = validateSlimGroup();
        assertThat(slimValues, hasSize(2));
        slimValuesContain(slimValues, go(2), 5L, (double) 5 / TOTAL);
    }

    @Test
    public void existingGroupsRemainAfterInjection() {
        // TODO: 21/11/17  
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
        annotationGroup.addStatsType(goStatsType);
        stats.add(annotationGroup);

        StatisticsGroup geneProductGroup = new StatisticsGroup("geneProduct", TOTAL);
        gpGoStatsType.addValue(gpGoValue);
        geneProductGroup.addStatsType(gpGoStatsType);
        stats.add(geneProductGroup);

        assertThat(stats, hasSize(2));
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
        return slimmedGoStatsType.getValues();
    }

    private String go(int id) {
        return "GO:" + id;
    }
}