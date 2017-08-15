package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.query.AggregateFunctionRequest;
import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.StatsRequestType.statsRequestType;
import static uk.ac.ebi.quickgo.annotation.service.statistics.StatsRequestConverterImpl.DEFAULT_GLOBAL_AGGREGATE_NAME;

/**
 * Created 15/07/16
 * @author Edd
 */
public class StatsRequestConverterImplTest {
    private static final String UNIQUE_FUNCTION = AggregateFunction.UNIQUE.getName();
    private static final String COUNT_FUNCTION = AggregateFunction.COUNT.getName();

    private ArrayList<AnnotationRequest.StatsRequest> statsRequests;
    private StatsRequestConverter converter;

    @Before
    public void setUp() {
        this.statsRequests = new ArrayList<>();
        this.converter = new StatsRequestConverterImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void initWithNullStatsRequestCausesException() {
        converter.convert(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initWithEmptyStatsRequestCausesException() {
        converter.convert(Collections.emptyList());
    }

    @Test
    public void oneStatsRequestWithNoTypesMakeGlobalAggregateWithOneField() {
        useStatsRequest(
                new AnnotationRequest.StatsRequest("group1", "groupField1", UNIQUE_FUNCTION, Collections.emptyList()));

        AggregateRequest aggregate = convertStats();

        assertThat(aggregate.getName(), is(DEFAULT_GLOBAL_AGGREGATE_NAME));
        assertThat(aggregate.getAggregateFunctionRequests(), contains(aggrField("groupField1", UNIQUE_FUNCTION)));
        assertThat(aggregate.getNestedAggregateRequests(), is(empty()));
    }

    @Test
    public void twoStatsRequestWithNoTypesMakeGlobalAggregateWithTwoFields() {
        useStatsRequest(
                new AnnotationRequest.StatsRequest("group1", "groupField1", UNIQUE_FUNCTION, Collections.emptyList()));
        useStatsRequest(
                new AnnotationRequest.StatsRequest("group2", "groupField2", COUNT_FUNCTION, Collections.emptyList()));

        AggregateRequest aggregate = convertStats();

        assertThat(aggregate.getName(), is(DEFAULT_GLOBAL_AGGREGATE_NAME));
        assertThat(aggregate.getAggregateFunctionRequests(), containsInAnyOrder(
                aggrField("groupField1", UNIQUE_FUNCTION),
                aggrField("groupField2", COUNT_FUNCTION)));
        assertThat(aggregate.getNestedAggregateRequests(), is(empty()));
    }

    @Test
    public void oneStatsRequestWithMultipleTypesMakeGlobalAggregateAndMultipleAggregatesWithOneField() {
        String group = "group1";
        String groupField = "groupField1";
        String type1 = "type1";
        String type2 = "type2";

        useStatsRequest(
                new AnnotationRequest.StatsRequest(group, groupField, COUNT_FUNCTION, asList(
                        statsRequestType(type1), statsRequestType(type2))));

        AggregateRequest aggregate = convertStats();

        assertThat(aggregate.getName(), is(DEFAULT_GLOBAL_AGGREGATE_NAME));
        assertThat(aggregate.getAggregateFunctionRequests(), contains(aggrField(groupField, COUNT_FUNCTION)));
        assertThat(aggregate.getNestedAggregateRequests(), hasSize(2));

        Map<String, List<AggregateRequest>> nestedAggrMap = extractNestedAggregates(aggregate);
        assertThat(nestedAggrMap.get(type1), hasSize(1));
        assertThat(nestedAggrMap.get(type2), hasSize(1));
        AggregateRequest type1Aggregate = nestedAggrMap.get(type1).get(0);
        AggregateRequest type2Aggregate = nestedAggrMap.get(type2).get(0);

        assertThat(type1Aggregate.getAggregateFunctionRequests(), contains(aggrField(groupField, COUNT_FUNCTION)));
        assertThat(type1Aggregate.getNestedAggregateRequests(), is(empty()));
        assertThat(type2Aggregate.getAggregateFunctionRequests(), contains(aggrField(groupField, COUNT_FUNCTION)));
        assertThat(type2Aggregate.getNestedAggregateRequests(), is(empty()));
    }

    @Test
    public void twoStatsRequestsWithMultipleTypesMakeGlobalAggregateAndMultipleAggregatesWithTwoFields() {
        String group1 = "group1";
        String groupField1 = "groupField1";
        String group2 = "group2";
        String groupField2 = "groupField2";

        String type1 = "type1";
        String type2 = "type2";
        String type3 = "type3";

        useStatsRequest(
                new AnnotationRequest.StatsRequest(group1, groupField1, UNIQUE_FUNCTION,
                        asList(statsRequestType(type1), statsRequestType(type2), statsRequestType(type3))));
        useStatsRequest(
                new AnnotationRequest.StatsRequest(group2, groupField2, UNIQUE_FUNCTION,
                        asList(statsRequestType(type1), statsRequestType(type3))));

        AggregateRequest aggregate = convertStats();

        assertThat(aggregate.getName(), is(DEFAULT_GLOBAL_AGGREGATE_NAME));
        assertThat(aggregate.getAggregateFunctionRequests(), containsInAnyOrder(
                aggrField(groupField1, UNIQUE_FUNCTION),
                aggrField(groupField2, UNIQUE_FUNCTION)));
        assertThat(aggregate.getNestedAggregateRequests(), hasSize(3));

        Map<String, List<AggregateRequest>> nestedAggrMap = extractNestedAggregates(aggregate);
        assertThat(nestedAggrMap.get(type1), hasSize(1));
        assertThat(nestedAggrMap.get(type2), hasSize(1));
        assertThat(nestedAggrMap.get(type3), hasSize(1));
        AggregateRequest type1Aggregate = nestedAggrMap.get(type1).get(0);
        AggregateRequest type2Aggregate = nestedAggrMap.get(type2).get(0);
        AggregateRequest type3Aggregate = nestedAggrMap.get(type3).get(0);

        assertThat(type1Aggregate.getAggregateFunctionRequests(),
                containsInAnyOrder(aggrField(groupField1, UNIQUE_FUNCTION), aggrField(groupField2, UNIQUE_FUNCTION)));
        assertThat(type1Aggregate.getNestedAggregateRequests(), is(empty()));
        assertThat(type2Aggregate.getAggregateFunctionRequests(), contains(aggrField(groupField1, UNIQUE_FUNCTION)));
        assertThat(type2Aggregate.getNestedAggregateRequests(), is(empty()));
        assertThat(type3Aggregate.getAggregateFunctionRequests(),
                containsInAnyOrder(aggrField(groupField1, UNIQUE_FUNCTION), aggrField(groupField2, UNIQUE_FUNCTION)));
        assertThat(type3Aggregate.getNestedAggregateRequests(), is(empty()));
    }

    private static AggregateFunctionRequest aggrField(String fieldName, String function) {
        return new AggregateFunctionRequest(fieldName, AggregateFunction.typeOf(function));
    }

    private Map<String, List<AggregateRequest>> extractNestedAggregates(AggregateRequest aggregate) {
        return aggregate.getNestedAggregateRequests().stream()
                .collect(Collectors.groupingBy(AggregateRequest::getName));
    }

    private AggregateRequest convertStats() {return converter.convert(statsRequests);}

    private void useStatsRequest(AnnotationRequest.StatsRequest statsRequest) {
        this.statsRequests.add(statsRequest);
    }
}