package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.SELECT_ALL_WHERE_FIELD_IS_NOT_EMPTY;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.and;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.not;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

/**
 * Created by edd on 03/08/2016.
 */
class QuickGOQueryTest {
    @Test
    void generalisedOrWithNullQueryCausesException() {
        assertThrows(IllegalArgumentException.class, () -> or((QuickGOQuery) null));
    }

    @Test
    void generalisedAndWithNullQueryCausesException() {
        assertThrows(IllegalArgumentException.class, () -> QuickGOQuery.and((QuickGOQuery) null));
    }

    @Test
    void generalisedOrWithNullArrayCausesException() {
        assertThrows(IllegalArgumentException.class, () -> or((QuickGOQuery[]) null));
    }

    @Test
    void generalisedAndWithNullArrayCausesException() {
        assertThrows(IllegalArgumentException.class, () -> QuickGOQuery.and((QuickGOQuery[]) null));
    }

    @Test
    void generalisedOrWithNoQueriesCausesException() {
        assertThrows(IllegalArgumentException.class, QuickGOQuery::or);
    }

    @Test
    void generalisedAndWithNoQueriesCausesException() {
        assertThrows(IllegalArgumentException.class, QuickGOQuery::and);
    }

    @Test
    void generalisedOrWith1QueryReturnsThatQuery() {
        FieldQuery originalQuery = new FieldQuery("field1", "value1");
        QuickGOQuery quickGOQuery = or(originalQuery);

        assertThat(quickGOQuery, is(originalQuery));
    }

    @Test
    void generalisedAndWith1QueryReturnsThatQuery() {
        FieldQuery originalQuery = new FieldQuery("field1", "value1");
        QuickGOQuery quickGOQuery = and(originalQuery);

        assertThat(quickGOQuery, is(originalQuery));
    }

    @Test
    void canCreateGeneralisedOr() {
        FieldQuery query1 = new FieldQuery("field1", "value1");
        FieldQuery query2 = new FieldQuery("field2", "value2");
        FieldQuery query3 = new FieldQuery("field3", "value3");
        QuickGOQuery compositeQuery = or(query1, query2, query3);

        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), containsInAnyOrder(query1, query2, query3));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.OR));
    }

    @Test
    void canCreateGeneralisedAnd() {
        FieldQuery query1 = new FieldQuery("field1", "value1");
        FieldQuery query2 = new FieldQuery("field2", "value2");
        FieldQuery query3 = new FieldQuery("field3", "value3");
        QuickGOQuery compositeQuery = and(query1, query2, query3);

        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), containsInAnyOrder(query1, query2, query3));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.AND));
    }

    @Test
    void canCreateOrQuery() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");
        QuickGOQuery query2 = new FieldQuery("field2", "value2");

        QuickGOQuery compositeQuery = or(query1, query2);
        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), containsInAnyOrder(query1, query2));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.OR));
    }

    @Test
    void canCreateAndQuery() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");
        QuickGOQuery query2 = new FieldQuery("field2", "value2");

        QuickGOQuery compositeQuery = and(query1, query2);
        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), containsInAnyOrder(query1, query2));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.AND));
    }

    @Test
    void canCreateNotQuery() {
        QuickGOQuery query1 = new FieldQuery("field1", "value1");

        QuickGOQuery compositeQuery = not(query1);
        assertThat(compositeQuery, instanceOf(CompositeQuery.class));
        assertThat(((CompositeQuery) compositeQuery).queries(), contains(query1));
        assertThat(((CompositeQuery) compositeQuery).queryOperator(), is(CompositeQuery.QueryOp.NOT));
    }

    @Test
    void notWithNullQueryCausesException() {
        QuickGOQuery query1 = null;
        assertThrows(IllegalArgumentException.class, () -> not(query1));
    }

    @Test
    void canCreateFieldQuery() {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", "value1");

        assertThat(query, instanceOf(FieldQuery.class));
    }

    @Test
    void canCreateAllNotEmptyQuery() {
        QuickGOQuery query = QuickGOQuery.createQuery("field1", SELECT_ALL_WHERE_FIELD_IS_NOT_EMPTY);

        assertThat(query, instanceOf(AllNonEmptyFieldQuery.class));
    }

    @Test
    void canCreateContainFieldQuery() {
        QuickGOQuery query = QuickGOQuery.createContainQuery("field1", "value1");

        assertThat(query, instanceOf(ContainsFieldQuery.class));
    }
}
