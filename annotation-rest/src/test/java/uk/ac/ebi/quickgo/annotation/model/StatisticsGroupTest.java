package uk.ac.ebi.quickgo.annotation.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the behaviour of the {@link StatisticsGroup} class.
 */
class StatisticsGroupTest {

    @Test
    void nullGroupNameThrowsException() {
        String groupName = null;
        int totalHits = 0;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new StatisticsGroup(groupName, totalHits));
        assertTrue(exception.getMessage().contains("Stats groupName cannot be null or empty"));
    }

    @Test
    void emptyGroupNameThrowsException() {
        String groupName = "";
        int totalHits = 0;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new StatisticsGroup(groupName, totalHits));
        assertTrue(exception.getMessage().contains("Stats groupName cannot be null or empty"));
    }

    @Test
    void negativeTotalHitsNameThrowsException() {
        String groupName = "group";
        int totalHits = -1;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new StatisticsGroup(groupName, totalHits));
        assertTrue(exception.getMessage().contains("Stats total hits can not be negative"));
    }

    @Test
    void addingNullStatisticsTypeThrowsException() {
        String groupName = "group";
        int totalHits = 0;

        StatisticsByType statsType = null;

        StatisticsGroup statsGroup = new StatisticsGroup(groupName, totalHits);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> statsGroup.addStatsType(statsType));
        assertTrue(exception.getMessage().contains("Statistics type cannot be null"));
    }

    @Test
    void addedStatisticsTypeIsRetrievedCorrectly() {
        String groupName = "group";
        int totalHits = 0;

        StatisticsByType statsType = new StatisticsByType("type", 0);

        StatisticsGroup statsGroup = new StatisticsGroup(groupName, totalHits);
        statsGroup.addStatsType(statsType);

        assertThat(statsGroup.getTypes(), contains(statsType));
    }
}
