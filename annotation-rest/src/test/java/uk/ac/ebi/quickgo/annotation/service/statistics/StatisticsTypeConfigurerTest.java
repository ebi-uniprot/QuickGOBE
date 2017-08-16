package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.*;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
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
    private static final int GO_ID_LIMIT = 5555;
    private StatisticsTypeConfigurer typeConfigurer;
    private Map<String, Integer> typeLimitProperties;
    private List<RequiredStatistic> requests;
    private static final String TAXON_ID = "taxonId";
    private static final RequiredStatisticType TAXON_ID_TYPE = statsType(TAXON_ID);

    @Before
    public void setUp() {
        typeLimitProperties = new HashMap<>();
        typeConfigurer = new StatisticsTypeConfigurer(typeLimitProperties);
        requests = new ArrayList<>();
    }

    @Test
    public void emptyStatsRequestsRemainEmpty() {
        // Given
        typeLimitProperties.put(GO_ID, 1111);

        assertThat(requests, is(empty()));

        // When
        typeConfigurer.configureStatsRequests(requests);

        // Then
        assertThat(requests, is(empty()));
    }

    @Test
    public void noMatchingTypeLeavesStatsRequestsUntouched() {
        // Given
        typeLimitProperties.put(GO_ID, 1111);
        RequiredStatisticType requestType = statsType(TAXON_ID);
        RequiredStatistic statsReq = statsReq(requestType);
        requests.add(statsReq);
        
        assertThat(requestType.getName(), is(TAXON_ID));
        assertThat(requestType.getLimit(), is(Optional.empty()));

        // When
        typeConfigurer.configureStatsRequests(requests);

        // Then
        assertThat(requests, contains(statsReq));
        assertThat(statsReq.getTypes(), contains(requestType));
        assertThat(requestType.getName(), is(TAXON_ID));
        assertThat(requestType.getLimit(), is(Optional.empty()));
    }

    @Test
    public void emptyTypePropertiesLeavesStatsRequestsUntouched() {
        // Given
        RequiredStatisticType requestType = statsType(TAXON_ID);
        RequiredStatistic statsReq = statsReq(requestType);
        requests.add(statsReq);

        assertThat(requestType.getName(), is(TAXON_ID));
        assertThat(requestType.getLimit(), is(Optional.empty()));

        // When
        typeConfigurer.configureStatsRequests(requests);

        // Then
        assertThat(requestType.getLimit(), is(Optional.empty())); // unchanged limit
        assertThat(requests, contains(statsReq));
        assertThat(statsReq.getTypes(), contains(requestType));
        assertThat(requestType.getName(), is(TAXON_ID));
    }

    @Test
    public void matchingTypeUpdatesStatsRequests() {
        // Given
        typeLimitProperties.put(GO_ID, GO_ID_LIMIT);
        typeLimitProperties.put("aspectId", 20);
        RequiredStatisticType goIdType = statsType(GO_ID);
        RequiredStatistic statsReq = statsReq(TAXON_ID_TYPE, goIdType);
        requests.add(statsReq);

        assertThat(TAXON_ID_TYPE.getName(), is(TAXON_ID));
        assertThat(TAXON_ID_TYPE.getLimit(), is(Optional.empty()));
        assertThat(goIdType.getName(), is(GO_ID));
        assertThat(goIdType.getLimit(), is(Optional.empty()));

        // When
        typeConfigurer.configureStatsRequests(requests);

        // Then
        assertThat(goIdType.getLimit(), is(Optional.of(GO_ID_LIMIT))); // limit updated
        assertThat(requests, contains(statsReq));
        assertThat(statsReq.getTypes(), contains(TAXON_ID_TYPE, goIdType));
        assertThat(TAXON_ID_TYPE.getName(), is(TAXON_ID));
        assertThat(TAXON_ID_TYPE.getLimit(), is(Optional.empty()));
        assertThat(goIdType.getName(), is(GO_ID));
    }

    private RequiredStatistic statsReq(RequiredStatisticType... types) {
        return new RequiredStatistic(
                "fakeGroupName",
                "fakeGroupField",
                "fakeAggFunction",
                asList(types));
    }
}