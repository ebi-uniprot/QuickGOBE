package uk.ac.ebi.quickgo.annotation.service.statistics;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.ac.ebi.quickgo.annotation.common.AnnotationFields.Facetable.GO_ID;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatistics.ANNOTATION;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatistics.GENE_PRODUCT;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatistics.STATS_TYPES;

/**
 * Created 17/08/17
 * @author Edd
 */
class RequiredStatisticsTest {

    @Test
    void cannotCreateWithNullConfigurer() {
        assertThrows(IllegalArgumentException.class, () -> new RequiredStatistics(null));
    }

    @Test
    void configurationTakesPlaceUponCreation() {
        StatisticsTypeConfigurer mockConfigurer = mock(StatisticsTypeConfigurer.class);

        new RequiredStatistics(mockConfigurer);

        verify(mockConfigurer, times(1)).getConfiguredStatsTypes(any());
    }

    @Test
    void ensureCorrectStatsGroupsAreCreated() {
        StatisticsTypeConfigurer configurer = configurerWithProperties();

        RequiredStatistics reqStats = new RequiredStatistics(configurer);

        List<RequiredStatistic> stats = reqStats.getRequiredStatistics();
        List<String> statsGroupNames = stats.stream().map(RequiredStatistic::getGroupName).collect(Collectors.toList());
        assertThat(statsGroupNames, hasItems(ANNOTATION, GENE_PRODUCT));
    }

    @Test
    void ensureStatsGroupsAreConfiguredWhenConfigurerIsNotDefault() {
        StatisticsTypeConfigurer configurer = configurerWithProperties();

        RequiredStatistics reqStats = new RequiredStatistics(configurer);

        List<RequiredStatistic> stats = reqStats.getRequiredStatistics();

        for (RequiredStatistic stat : stats) {
            assertThat(stat.getTypes(), not(equalTo(STATS_TYPES)));
        }
    }

    @Test
    void ensureStatsGroupsAreNotConfiguredWhenConfigurerIsDefault() {
        StatisticsTypeConfigurer configurer = configurerWithNoProperties();

        RequiredStatistics reqStats = new RequiredStatistics(configurer);

        List<RequiredStatistic> stats = reqStats.getRequiredStatistics();

        for (RequiredStatistic stat : stats) {
            assertThat(stat.getTypes(), equalTo(STATS_TYPES));
        }
    }

    private StatisticsTypeConfigurer configurerWithProperties() {
        Map<String, Integer> typeProperties = new HashMap<>();

        // property value can be anything;
        // values are tested in {@link StatisticsTypeConfigurerTest}
        typeProperties.put(GO_ID, 6);

        return new StatisticsTypeConfigurer(typeProperties);
    }

    private StatisticsTypeConfigurer configurerWithNoProperties() {
        return new StatisticsTypeConfigurer(new HashMap<>());
    }
}
