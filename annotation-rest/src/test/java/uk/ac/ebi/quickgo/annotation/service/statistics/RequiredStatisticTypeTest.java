package uk.ac.ebi.quickgo.annotation.service.statistics;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.DEFAULT_LIMIT;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.statsType;

/**
 * Created 16/08/17
 * @author Edd
 */
class RequiredStatisticTypeTest {
    @Test
    void cannotCreateRequiredStatisticTypeWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> statsType(""));
    }

    @Test
    void cannotCreateRequiredStatisticTypeWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> statsType(null));
    }

    @Test
    void canCreateRequiredStatisticTypeWithName() {
        String name = "name";

        RequiredStatisticType statsType = statsType(name);

        assertThat(statsType.getName(), is(name));
    }

    @Test
    void canCreateRequiredStatisticTypeWithPositiveLimit() {
        int limit = 1;
        RequiredStatisticType statsType = statsType("name", limit);

        assertThat(statsType.getLimit(), is(limit));
    }

    @Test
    void statsTypeWithoutLimitReturnsEmptyOptionalAsLimit() {
        RequiredStatisticType statsType = statsType("name");

        assertThat(statsType.getLimit(), is(DEFAULT_LIMIT));
    }

    @Test
    void statsTypeWithLimitZeroIndicatesLimitNotSet() {
        RequiredStatisticType statsType = statsType("name", 0);

        assertThat(statsType.getLimit(), is(DEFAULT_LIMIT));
    }

    @Test
    void statsTypeWithNegativeLimitIndicatesLimitNotSet() {
        RequiredStatisticType statsType = statsType("name", -1);

        assertThat(statsType.getLimit(), is(DEFAULT_LIMIT));
    }
}