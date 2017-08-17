package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.*;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.statsType;

/**
 * Checks that the {@link StatisticsTypeConfigurer} correctly updates the limits of {@link RequiredStatisticType}
 * according to a property map.
 *
 * Created 15/08/17
 * @author Edd
 */
public class StatisticsTypeConfigurerTest {

    private StatisticsTypeConfigurer typeConfigurer;
    private Map<String, Integer> typeLimitProperties;
    private List<RequiredStatistic> requests;
    private static final String TAXON_ID = "taxonId";
    private static final RequiredStatisticType EXPECTED_TAXON_ID_TYPE = statsType(TAXON_ID);
    public static final String GO_ID = "goId";
    private static final int GO_ID_LIMIT = 5555;
    private static final RequiredStatisticType EXPECTED_GO_ID_TYPE = statsType(GO_ID, GO_ID_LIMIT);

    @Before
    public void setUp() {
        typeLimitProperties = new HashMap<>();
        typeConfigurer = new StatisticsTypeConfigurer(typeLimitProperties);
        requests = new ArrayList<>();
    }

    @Test
    public void emptyRequiredStatsRemainEmpty() {
        // Given
        typeLimitProperties.put(GO_ID, 1111);

        assertThat(requests, is(empty()));

        // When
        typeConfigurer.configureStatsRequests(requests);

        // Then
        assertThat(requests, is(empty()));
    }

    @Test
    public void noMatchingTypeLeavesRequiredStatsUntouched() {
        // Given
        typeLimitProperties.put(GO_ID, 1111);
        RequiredStatisticType requestType = statsType(TAXON_ID);
        RequiredStatistic stat = stat(requestType);
        requests.add(stat);
        
        assertThat(requestType, is(equalTo(EXPECTED_TAXON_ID_TYPE)));

        // When
        typeConfigurer.configureStatsRequests(requests);

        // Then
        assertThat(requests, contains(stat));
        assertThat(stat.getTypes(), contains(requestType));
        assertThat(requestType, is(equalTo(EXPECTED_TAXON_ID_TYPE)));
    }

    @Test
    public void emptyTypePropertiesLeavesRequiredStatsUntouched() {
        // Given
        RequiredStatisticType requestType = statsType(TAXON_ID);
        RequiredStatistic stat = stat(requestType);
        requests.add(stat);

        assertThat(typeLimitProperties.entrySet(), is(empty()));
        assertThat(requestType, is(equalTo(EXPECTED_TAXON_ID_TYPE)));

        // When
        typeConfigurer.configureStatsRequests(requests);

        // Then
        assertThat(requests, contains(stat));
        assertThat(stat.getTypes(), contains(requestType));
        assertThat(requestType, is(equalTo(EXPECTED_TAXON_ID_TYPE)));
    }

    @Test
    public void matchingTypeUpdatesRequiredStats() {
        // Given
        typeLimitProperties.put(GO_ID, GO_ID_LIMIT);
        typeLimitProperties.put("aspectId", 20);
        RequiredStatisticType goIdType = statsType(GO_ID);
        RequiredStatistic stat = stat(EXPECTED_TAXON_ID_TYPE, goIdType);
        requests.add(stat);

        assertThat(EXPECTED_TAXON_ID_TYPE.getName(), is(TAXON_ID));
        assertThat(EXPECTED_TAXON_ID_TYPE.getLimit(), is(Optional.empty()));
        assertThat(goIdType.getName(), is(GO_ID));
        assertThat(goIdType.getLimit(), is(Optional.empty()));
        assertThat(goIdType, is(not(equalTo(EXPECTED_GO_ID_TYPE)))); // limits differ

        // When
        typeConfigurer.configureStatsRequests(requests);

        // Then
        assertThat(requests, contains(stat));
        assertThat(stat.getTypes(), contains(EXPECTED_TAXON_ID_TYPE, goIdType));
        assertThat(EXPECTED_TAXON_ID_TYPE.getName(), is(TAXON_ID));
        assertThat(EXPECTED_TAXON_ID_TYPE.getLimit(), is(Optional.empty()));
        assertThat(goIdType, is(equalTo(EXPECTED_GO_ID_TYPE))); // where limits are now equal
    }

    private RequiredStatistic stat(RequiredStatisticType... types) {
        return new RequiredStatistic(
                "fakeGroupName",
                "fakeGroupField",
                "fakeAggFunction",
                asList(types));
    }
}