package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by edd on 03/08/2016.
 */
public class QuickGOQueryTest {
    @Test(expected = IllegalArgumentException.class)
    public void generalisedOrWithNullQueryCausesException() {
        QuickGOQuery.generalisedOr((QuickGOQuery) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generalisedAndWithNullQueryCausesException() {
        QuickGOQuery.generalisedAnd((QuickGOQuery) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generalisedOrWithNullArrayCausesException() {
        QuickGOQuery.generalisedOr((QuickGOQuery[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generalisedAndWithNullArrayCausesException() {
        QuickGOQuery.generalisedAnd((QuickGOQuery[]) null);
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

    @Test
    public void canCreateOrQuery() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");
        QuickGOQuery query2 = new FieldQuery("field2", "value2");

        QuickGOQuery compositeQuery = query1.or(query2);
        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), containsInAnyOrder(query1, query2));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.OR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingOrQueryWithNullValueCausesException() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");
        QuickGOQuery query2 = null;

        query1.or(query2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingOrQueryWithNullArrayCausesException() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");
        QuickGOQuery[] queries = null;

        query1.or(queries);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingOrQueryWithZeroValuesCausesException() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");

        query1.or();
    }

    @Test
    public void canCreateAndQuery() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");
        QuickGOQuery query2 = new FieldQuery("field2", "value2");

        QuickGOQuery compositeQuery = query1.and(query2);
        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), containsInAnyOrder(query1, query2));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.AND));
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingAndQueryWithNullValueCausesException() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");
        QuickGOQuery query2 = null;

        query1.and(query2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingAndQueryWithNullArrayCausesException() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");
        QuickGOQuery[] queries = null;

        query1.and(queries);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingAndQueryWithZeroValuesCausesException() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");

        query1.and();

    }

    @Test
    public void canCreateNotQuery() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");

        QuickGOQuery compositeQuery = query1.not();
        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), contains(query1));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.NOT));

    }
}