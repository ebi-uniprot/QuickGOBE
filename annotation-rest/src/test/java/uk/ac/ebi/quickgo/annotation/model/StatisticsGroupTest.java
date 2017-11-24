package uk.ac.ebi.quickgo.annotation.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Tests the behaviour of the {@link StatisticsGroup} class.
 */
public class StatisticsGroupTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullGroupNameThrowsException() {
        String groupName = null;
        int totalHits = 0;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats groupName cannot be null or empty");

        new StatisticsGroup(groupName, totalHits);
    }

    @Test
    public void emptyGroupNameThrowsException() {
        String groupName = "";
        int totalHits = 0;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats groupName cannot be null or empty");

        new StatisticsGroup(groupName, totalHits);
    }

    @Test
    public void negativeTotalHitsNameThrowsException() {
        String groupName = "group";
        int totalHits = -1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Stats total hits can not be negative");

        new StatisticsGroup(groupName, totalHits);
    }

    @Test
    public void addingNullStatisticsTypeThrowsException() {
        String groupName = "group";
        int totalHits = 0;

        StatisticsByType statsType = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Statistics type cannot be null");

        StatisticsGroup statsGroup = new StatisticsGroup(groupName, totalHits);
        statsGroup.addStatsType(statsType);
    }

    @Test
    public void addedStatisticsTypeIsRetrievedCorrectly() {
        String groupName = "group";
        int totalHits = 0;

        StatisticsByType statsType = new StatisticsByType("type", 0);

        StatisticsGroup statsGroup = new StatisticsGroup(groupName, totalHits);
        statsGroup.addStatsType(statsType);

        assertThat(statsGroup.getTypes(), contains(statsType));
    }
}
