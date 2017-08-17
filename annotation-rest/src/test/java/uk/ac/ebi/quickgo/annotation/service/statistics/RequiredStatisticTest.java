package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.List;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.statsType;

/**
 * Created 16/08/17
 * @author Edd
 */
public class RequiredStatisticTest {

    private static final String GROUP_NAME = "groupName";
    private static final String GROUP_FIELD = "groupField";
    private static final String AGG_FUNCTION = "aggFunction";

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNullGroupNameProducesException() {
        new RequiredStatistic(null, GROUP_FIELD, AGG_FUNCTION, emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithEmptyGroupNameProducesException() {
        new RequiredStatistic("", GROUP_FIELD, AGG_FUNCTION, emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNullGroupFieldProducesException() {
        new RequiredStatistic(GROUP_NAME, null, AGG_FUNCTION, emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithEmptyGroupFieldProducesException() {
        new RequiredStatistic(GROUP_NAME, "", AGG_FUNCTION, emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNullAggregateFunctionProducesException() {
        new RequiredStatistic(GROUP_NAME, GROUP_FIELD, null, emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithEmptyAggregateFunctionProducesException() {
        new RequiredStatistic(GROUP_NAME, GROUP_FIELD, "", emptyList());
    }

    @Test
    public void constructorWithNullStatTypesCreatesEmptyListOfStatsTypes() {
        RequiredStatistic statistic = new RequiredStatistic(GROUP_NAME, GROUP_FIELD, AGG_FUNCTION, null);
        assertThat(statistic.getTypes(), is(empty()));
    }

    @Test
    public void constructorSetsStatsTypes() {
        List<RequiredStatisticType> types = asList(statsType("value1"), statsType("value2"));
        RequiredStatistic statistic = new RequiredStatistic(GROUP_NAME, GROUP_FIELD, AGG_FUNCTION, types);
        assertThat(statistic.getTypes(), is(types));
    }

    @Test
    public void constructorSetsGroupName() {
        RequiredStatistic statistic =
                new RequiredStatistic(GROUP_NAME, GROUP_FIELD, AGG_FUNCTION, emptyList());
        assertThat(statistic.getGroupName(), is(GROUP_NAME));
    }

    @Test
    public void constructorSetsGroupField() {
        RequiredStatistic statistic =
                new RequiredStatistic(GROUP_NAME, GROUP_FIELD, AGG_FUNCTION, emptyList());
        assertThat(statistic.getGroupField(), is(GROUP_FIELD));
    }

    @Test
    public void constructorSetsAggregateFunction() {
        RequiredStatistic statistic =
                new RequiredStatistic(GROUP_NAME, GROUP_FIELD, AGG_FUNCTION, emptyList());
        assertThat(statistic.getAggregateFunction(), is(AGG_FUNCTION));
    }
}