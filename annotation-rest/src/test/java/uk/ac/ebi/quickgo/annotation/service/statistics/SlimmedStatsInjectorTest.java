package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.StatisticsByType;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Created 21/11/17
 * @author Edd
 */
public class SlimmedStatsInjectorTest {
    private Map<String, List<String>> slimMap;
    private SlimmedStatsInjector slimStatsInjector;

    @Before
    public void setUp() {
        slimMap = new HashMap<>();
        slimStatsInjector = new SlimmedStatsInjector();
    }

    @Test
    public void termSlimsToOneTerm() {
        // TODO: 21/11/17  
    }

    @Test
    public void twoTermsSlimToSameTerm() {
        slimMap.put(go(1), singletonList(go(2)));
        slimMap.put(go(2), singletonList(go(2)));

        List<StatisticsGroup> stats = new ArrayList<>();
        StatisticsGroup statsGroup = new StatisticsGroup("annotation", 5);
        StatisticsByType goStatsType = new StatisticsByType("goId");
        goStatsType.addValue(new StatisticsValue(go(1), 5, 10));
        goStatsType.addValue(new StatisticsValue(go(2), 3, 10));
        statsGroup.addStatsType(goStatsType);
        stats.add(statsGroup);

        slimStatsInjector.process(stats, slimMap);
        assertThat(stats, hasSize(2));
        // TODO: 21/11/17  
    }

    @Test
    public void termSlimsToTwoTerms() {
        slimMap.put(go(1), asList(go(2), go(3)));

        List<StatisticsGroup> stats = new ArrayList<>();
        StatisticsGroup statsGroup = new StatisticsGroup("annotation", 5);
        StatisticsByType goStatsType = new StatisticsByType("goId");
        goStatsType.addValue(new StatisticsValue(go(1), 5, 10));
        statsGroup.addStatsType(goStatsType);
        stats.add(statsGroup);

        slimStatsInjector.process(stats, slimMap);
        assertThat(stats, hasSize(2));
        // TODO: 21/11/17  
    }

    @Test
    public void existingGroupsRemainAfterInjection() {
        // TODO: 21/11/17  
    }

    private String go(int id) {
        return "GO:" + id;
    }
}