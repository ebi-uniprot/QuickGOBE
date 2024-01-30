package uk.ac.ebi.quickgo.annotation.service.statistics;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.statsType;

/**
 * Created 16/08/17
 * @author Edd
 */
class RequiredStatisticTest {

    private static final String GROUP_NAME = "groupName";
    private static final String GROUP_FIELD = "groupField";
    private static final String AGG_FUNCTION = "aggFunction";

    @Test
    void constructorWithNullGroupNameProducesException() {
        assertThrows(IllegalArgumentException.class, () -> new RequiredStatistic(null, GROUP_FIELD, AGG_FUNCTION, emptyList()));
    }

    @Test
    void constructorWithEmptyGroupNameProducesException() {
        assertThrows(IllegalArgumentException.class, () -> new RequiredStatistic("", GROUP_FIELD, AGG_FUNCTION, emptyList()));
    }

    @Test
    void constructorWithNullGroupFieldProducesException() {
        assertThrows(IllegalArgumentException.class, () -> new RequiredStatistic(GROUP_NAME, null, AGG_FUNCTION, emptyList()));
    }

    @Test
    void constructorWithEmptyGroupFieldProducesException() {
        assertThrows(IllegalArgumentException.class, () -> new RequiredStatistic(GROUP_NAME, "", AGG_FUNCTION, emptyList()));
    }

    @Test
    void constructorWithNullAggregateFunctionProducesException() {
        assertThrows(IllegalArgumentException.class, () -> new RequiredStatistic(GROUP_NAME, GROUP_FIELD, null, emptyList()));
    }

    @Test
    void constructorWithEmptyAggregateFunctionProducesException() {
        assertThrows(IllegalArgumentException.class, () -> new RequiredStatistic(GROUP_NAME, GROUP_FIELD, "", emptyList()));
    }

    @Test
    void constructorWithNullStatTypesCreatesEmptyListOfStatsTypes() {
        RequiredStatistic statistic = new RequiredStatistic(GROUP_NAME, GROUP_FIELD, AGG_FUNCTION, null);
        assertThat(statistic.getTypes(), is(empty()));
    }

    @Test
    void constructorSetsStatsTypes() {
        List<RequiredStatisticType> types = asList(statsType("value1"), statsType("value2"));
        RequiredStatistic statistic = new RequiredStatistic(GROUP_NAME, GROUP_FIELD, AGG_FUNCTION, types);
        assertThat(statistic.getTypes(), is(types));
    }

    @Test
    void constructorSetsGroupName() {
        RequiredStatistic statistic =
                new RequiredStatistic(GROUP_NAME, GROUP_FIELD, AGG_FUNCTION, emptyList());
        assertThat(statistic.getGroupName(), is(GROUP_NAME));
    }

    @Test
    void constructorSetsGroupField() {
        RequiredStatistic statistic =
                new RequiredStatistic(GROUP_NAME, GROUP_FIELD, AGG_FUNCTION, emptyList());
        assertThat(statistic.getGroupField(), is(GROUP_FIELD));
    }

    @Test
    void constructorSetsAggregateFunction() {
        RequiredStatistic statistic =
                new RequiredStatistic(GROUP_NAME, GROUP_FIELD, AGG_FUNCTION, emptyList());
        assertThat(statistic.getAggregateFunction(), is(AGG_FUNCTION));
    }
}