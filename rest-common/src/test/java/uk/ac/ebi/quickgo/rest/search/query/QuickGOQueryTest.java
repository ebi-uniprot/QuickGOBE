package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.SELECT_ALL_WHERE_FIELD_IS_NOT_EMPTY;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.and;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.not;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

/**
 * Created by edd on 03/08/2016.
 */
public class QuickGOQueryTest {
    @Test(expected = IllegalArgumentException.class)
    public void generalisedOrWithNullQueryCausesException() {
        or((QuickGOQuery) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generalisedAndWithNullQueryCausesException() {
        QuickGOQuery.and((QuickGOQuery) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generalisedOrWithNullArrayCausesException() {
        or((QuickGOQuery[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generalisedAndWithNullArrayCausesException() {
        QuickGOQuery.and((QuickGOQuery[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generalisedOrWithNoQueriesCausesException() {
        or();
    }

    @Test(expected = IllegalArgumentException.class)
    public void generalisedAndWithNoQueriesCausesException() {
        QuickGOQuery.and();
    }

    @Test
    public void generalisedOrWith1QueryReturnsThatQuery() {
        FieldQuery originalQuery = new FieldQuery("field1", "value1");
        QuickGOQuery quickGOQuery = or(originalQuery);

        assertThat(quickGOQuery, is(originalQuery));
    }

    @Test
    public void generalisedAndWith1QueryReturnsThatQuery() {
        FieldQuery originalQuery = new FieldQuery("field1", "value1");
        QuickGOQuery quickGOQuery = and(originalQuery);

        assertThat(quickGOQuery, is(originalQuery));
    }

    @Test
    public void canCreateGeneralisedOr() {
        FieldQuery query1 = new FieldQuery("field1", "value1");
        FieldQuery query2 = new FieldQuery("field2", "value2");
        FieldQuery query3 = new FieldQuery("field3", "value3");
        QuickGOQuery compositeQuery = or(query1, query2, query3);

        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), containsInAnyOrder(query1, query2, query3));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.OR));
    }

    @Test
    public void canCreateGeneralisedAnd() {
        FieldQuery query1 = new FieldQuery("field1", "value1");
        FieldQuery query2 = new FieldQuery("field2", "value2");
        FieldQuery query3 = new FieldQuery("field3", "value3");
        QuickGOQuery compositeQuery = and(query1, query2, query3);

        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), containsInAnyOrder(query1, query2, query3));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.AND));
    }

    @Test
    public void canCreateOrQuery() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");
        QuickGOQuery query2 = new FieldQuery("field2", "value2");

        QuickGOQuery compositeQuery = or(query1, query2);
        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), containsInAnyOrder(query1, query2));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.OR));
    }

    @Test
    public void canCreateAndQuery() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");
        QuickGOQuery query2 = new FieldQuery("field2", "value2");

        QuickGOQuery compositeQuery = and(query1, query2);
        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), containsInAnyOrder(query1, query2));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.AND));
    }

    @Test
    public void canCreateNotQuery() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");

        QuickGOQuery compositeQuery = not(query1);
        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), contains(query1));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.NOT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void notWithNullQueryCausesException() {
        QuickGOQuery query1 = null;
        not(query1);
    }

    @Test
    public void canCreateFieldQuery() {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");

        assertThat(query, instanceOf(FieldQuery.class));
    }

    @Test
    public void canCreateAllNotEmptyQuery() {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", SELECT_ALL_WHERE_FIELD_IS_NOT_EMPTY);

        assertThat(query, instanceOf(AllNonEmptyFieldQuery.class));
    }

    @Test
    public void canCreateContainFieldQuery() {
        QuickGOQuery query = QuickGOQuery.createContainQuery("field1", "value1");

        assertThat(query, instanceOf(ContainsFieldQuery.class));
    }
}
