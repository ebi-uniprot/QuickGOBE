package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.Optional;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
            RequiredStatisticType statsType = statsType("name");
            int limit = 1;
            statsType.setLimit(limit);

            assertThat(statsType.getLimit(), is(Optional.of(limit)));
        }

        @Test
        public void statsTypeWithoutLimitReturnsEmptyOptionalAsLimit() {
            RequiredStatisticType statsType = statsType("name");

            assertThat(statsType.getLimit(), is(Optional.empty()));
        }

        @Test(expected = IllegalArgumentException.class)
        public void statsTypeWithLimitZeroIndicatesLimitNotSet() {
            RequiredStatisticType statsType = statsType("name");
            statsType.setLimit(0);
        }

        @Test(expected = IllegalArgumentException.class)
        public void statsTypeWithNegativeLimitIndicatesLimitNotSet() {
            RequiredStatisticType statsType = statsType("name");
            statsType.setLimit(-1);
        }
}