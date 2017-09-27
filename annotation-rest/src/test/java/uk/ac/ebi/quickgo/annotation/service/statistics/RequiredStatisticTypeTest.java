package uk.ac.ebi.quickgo.annotation.service.statistics;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.DEFAULT_LIMIT;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.statsType;

/**
 * Created 16/08/17
 * @author Edd
 */
public class RequiredStatisticTypeTest {
    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateRequiredStatisticTypeWithEmptyName() {
        statsType("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateRequiredStatisticTypeWithNullName() {
        statsType(null);
    }

    @Test
    public void canCreateRequiredStatisticTypeWithName() {
        String name = "name";

        RequiredStatisticType statsType = statsType(name);

        assertThat(statsType.getName(), is(name));
    }

    @Test
    public void canCreateRequiredStatisticTypeWithPositiveLimit() {
        int limit = 1;
        RequiredStatisticType statsType = statsType("name", limit);

        assertThat(statsType.getLimit(), is(limit));
    }

    @Test
    public void statsTypeWithoutLimitReturnsEmptyOptionalAsLimit() {
        RequiredStatisticType statsType = statsType("name");

        assertThat(statsType.getLimit(), is(DEFAULT_LIMIT));
    }

    @Test
    public void statsTypeWithLimitZeroIndicatesLimitNotSet() {
        RequiredStatisticType statsType = statsType("name", 0);

        assertThat(statsType.getLimit(), is(DEFAULT_LIMIT));
    }

    @Test
    public void statsTypeWithNegativeLimitIndicatesLimitNotSet() {
        RequiredStatisticType statsType = statsType("name", -1);

        assertThat(statsType.getLimit(), is(DEFAULT_LIMIT));
    }
}