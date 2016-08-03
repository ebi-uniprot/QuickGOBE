package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by edd on 03/08/2016.
 */
public class QuickGOQueryTest {
    private QuickGOQuery quickGOQuery;

    @Before
    public void setup() {
        quickGOQuery = new TestableQuickGOQuery();
    }

    @Test(expected = IllegalArgumentException.class)
    public void generalisedOrWithNoQueriesCausesException() {
        QuickGOQuery.generalisedOr();
    }

    @Test(expected = IllegalArgumentException.class)
    public void generalisedAndWithNoQueriesCausesException() {
        QuickGOQuery.generalisedAnd();
    }

    @Test(expected = IllegalArgumentException.class)
    public void generalisedOrWith1QueryOnlyCausesException() {
        QuickGOQuery.generalisedOr(new FieldQuery("field1", "value1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void generalisedAndWith1QueryOnlyCausesException() {
        QuickGOQuery.generalisedAnd(new FieldQuery("field1", "value1"));
    }

    @Test
    public void canCreateGeneralisedOr() {
        FieldQuery query1 = new FieldQuery("field1", "value1");
        FieldQuery query2 = new FieldQuery("field2", "value2");
        FieldQuery query3 = new FieldQuery("field3", "value3");
        QuickGOQuery compositeQuery = QuickGOQuery.generalisedOr(query1, query2, query3);

        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), containsInAnyOrder(query1, query2, query3));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.OR));
    }

    @Test
    public void canCreateGeneralisedAnd() {
        FieldQuery query1 = new FieldQuery("field1", "value1");
        FieldQuery query2 = new FieldQuery("field2", "value2");
        FieldQuery query3 = new FieldQuery("field3", "value3");
        QuickGOQuery compositeQuery = QuickGOQuery.generalisedAnd(query1, query2, query3);

        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), containsInAnyOrder(query1, query2, query3));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.AND));
    }

    /**
     * Stub class, whose concrete methods in {@link QuickGOQuery} we can test.
     */
    private class TestableQuickGOQuery extends QuickGOQuery {
        @Override
        public <T> T accept(QueryVisitor<T> visitor) {
            return null;
        }
    }

}