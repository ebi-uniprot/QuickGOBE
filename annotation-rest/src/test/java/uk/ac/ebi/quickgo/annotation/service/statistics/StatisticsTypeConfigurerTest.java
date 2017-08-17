package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.*;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.DEFAULT_LIMIT;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.statsType;

/**
 * Checks that the {@link StatisticsTypeConfigurer} correctly updates the limits of {@link RequiredStatisticType}
 * according to a property map.
 *
 * Created 15/08/17
 * @author Edd
 */
public class StatisticsTypeConfigurerTest {

    public static final String GO_ID = "goId";
    private static final String TAXON_ID = "taxonId";
    private static final RequiredStatisticType EXPECTED_TAXON_ID_TYPE = statsType(TAXON_ID);
    private static final int GO_ID_LIMIT = 5555;
    private static final RequiredStatisticType EXPECTED_GO_ID_TYPE = statsType(GO_ID, GO_ID_LIMIT);
    private StatisticsTypeConfigurer typeConfigurer;
    private Map<String, Integer> typeLimitProperties;
    private List<RequiredStatisticType> types;

    @Before
    public void setUp() {
        typeLimitProperties = new HashMap<>();
        typeConfigurer = new StatisticsTypeConfigurer(typeLimitProperties);
        types = new ArrayList<>();
    }

    @Test
    public void emptyRequiredStatsRemainEmpty() {
        // Given
        typeLimitProperties.put(GO_ID, 1111);

        assertThat(types, is(empty()));

        // When
        List<RequiredStatisticType> configuredTypes = typeConfigurer.getConfiguredStatsTypes(types);

        // Then
        assertThat(configuredTypes, is(empty()));
    }

    @Test
    public void noMatchingTypeLeavesRequiredStatsUntouched() {
        // Given
        typeLimitProperties.put(GO_ID, 1111);
        RequiredStatisticType statsType = statsType(TAXON_ID);
        types.add(statsType);

        assertThat(statsType, is(equalTo(EXPECTED_TAXON_ID_TYPE)));

        // When
        List<RequiredStatisticType> configuredTypes = typeConfigurer.getConfiguredStatsTypes(types);

        // Then
        RequiredStatisticType configuredType = extractType(configuredTypes, TAXON_ID);
        assertThat(configuredType, is(equalTo(EXPECTED_TAXON_ID_TYPE)));
    }

    @Test
    public void emptyTypePropertiesLeavesRequiredStatsUntouched() {
        // Given
        RequiredStatisticType statsType = statsType(TAXON_ID);
        types.add(statsType);

        assertThat(typeLimitProperties.entrySet(), is(empty()));
        assertThat(statsType, is(equalTo(EXPECTED_TAXON_ID_TYPE)));

        // When
        List<RequiredStatisticType> configuredTypes = typeConfigurer.getConfiguredStatsTypes(types);

        // Then
        RequiredStatisticType configuredType = extractType(configuredTypes, TAXON_ID);
        assertThat(configuredType, is(equalTo(EXPECTED_TAXON_ID_TYPE)));
    }

    @Test
    public void matchingTypeUpdatesRequiredStats() {
        // Given
        typeLimitProperties.put(GO_ID, GO_ID_LIMIT);
        typeLimitProperties.put("aspectId", 20);
        RequiredStatisticType goIdType = statsType(GO_ID);
        types.add(goIdType);

        assertThat(EXPECTED_TAXON_ID_TYPE.getName(), is(TAXON_ID));
        assertThat(EXPECTED_TAXON_ID_TYPE.getLimit(), is(DEFAULT_LIMIT));
        assertThat(goIdType.getName(), is(GO_ID));
        assertThat(goIdType.getLimit(), is(DEFAULT_LIMIT));
        assertThat(goIdType, is(not(equalTo(EXPECTED_GO_ID_TYPE)))); // limits differ

        // When
        List<RequiredStatisticType> configuredTypes = typeConfigurer.getConfiguredStatsTypes(types);

        // Then
        RequiredStatisticType configuredType = extractType(configuredTypes, GO_ID);
        assertThat(configuredType, is(equalTo(EXPECTED_GO_ID_TYPE))); // where limits are now equal
    }

    private RequiredStatisticType extractType(List<RequiredStatisticType> types, String name) {
        return types.stream().filter(type -> type.getName().equals(name)).findFirst().orElse(null);
    }
}