package uk.ac.ebi.quickgo.rest.search.query;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.rest.TestUtil;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static uk.ac.ebi.quickgo.rest.search.query.CompositeQuery.QueryOp;

/**
 * Tests the behaviour of the {@link CompositeQuery} implementation
 */
@ExtendWith(MockitoExtension.class)
class CompositeQueryTest {
    @Mock
    private uk.ac.ebi.quickgo.rest.search.query.QueryVisitor visitor;

    @Test
    void nullQuerySetThrowsException() {
        Set<QuickGOQuery> queries = null;
        QueryOp queryOp = QueryOp.AND;

        try {
            new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(queries, queryOp);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Queries to compose cannot be null or empty"));
        }
    }

    @Test
    void emptyQuerySetThrowsException() {
        Set<QuickGOQuery> queries = Collections.emptySet();
        QueryOp queryOp = QueryOp.AND;

        try {
            new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(queries, queryOp);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Queries to compose cannot be null or empty"));
        }
    }

    @Test
    void nullQueryOperatorThrowsException() {
        QuickGOQuery query = createFieldQuery("field", "name");

        Set<QuickGOQuery> queries = Collections.singleton(query);
        QueryOp queryOp = null;

        try {
            new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(queries, queryOp);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Logical query operator cannot be null"));
        }
    }

    @Test
    void deleteFromQueryCollectionThrowsException() {
        QuickGOQuery query1 = createFieldQuery("field1", "value1");
        QuickGOQuery query2 = createFieldQuery("field2", "value2");

        Set<QuickGOQuery> queries = TestUtil.asSet(query1, query2);

        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery
          compositeQuery = new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(queries, QueryOp.AND);
        assertThrows(UnsupportedOperationException.class, () -> compositeQuery.queries().remove(query1));
    }

    @Test
    void andMultipleFieldQueries() {
        verifyMultipleFieldQuery(QueryOp.AND);
    }

    @Test
    void orMultipleFieldQueries() {
        verifyMultipleFieldQuery(QueryOp.OR);
    }

    private void verifyMultipleFieldQuery(QueryOp op) {
        QuickGOQuery query1 = createFieldQuery("field1", "value1");
        QuickGOQuery query2 = createFieldQuery("field2", "value2");
        QuickGOQuery query3 = createFieldQuery("field3", "value3");

        Set<QuickGOQuery> queries = TestUtil.asSet(query1, query2, query3);

        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery
                compositeQuery = new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(queries, op);

        assertThat(compositeQuery.queries(), hasSize(3));
        assertThat(compositeQuery.queries(), containsInAnyOrder(query1, query2, query3));
        assertThat(compositeQuery.queryOperator(), is(op));
    }

    @Test
    void andMultipleCompositeQueries() {
        verifyMultipleCompositeQueries(QueryOp.AND);
    }

    @Test
    void orMultipleCompositeQueries() {
        verifyMultipleCompositeQueries(QueryOp.OR);
    }

    private void verifyMultipleCompositeQueries(QueryOp op) {
        QuickGOQuery query1 = createFieldQuery("field1", "value1");
        QuickGOQuery query2 = createFieldQuery("field2", "value2");

        Set<QuickGOQuery> querySet1 = TestUtil.asSet(query1, query2);

        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery
                compositeQuery1 = new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(querySet1, op);

        QuickGOQuery query3 = createFieldQuery("field3", "value3");
        QuickGOQuery query4 = createFieldQuery("field4", "value4");

        Set<QuickGOQuery> querySet2 = TestUtil.asSet(query3, query4);

        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery
                compositeQuery2 = new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(querySet2, op);

        Set<QuickGOQuery> finalQuerySet = TestUtil.asSet(compositeQuery1, compositeQuery2);

        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery
                compositeQueryFinal = new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(finalQuerySet, op);

        assertThat(compositeQueryFinal.queries(), hasSize(2));
        assertThat(compositeQueryFinal.queries(), containsInAnyOrder(compositeQuery1, compositeQuery2));
        assertThat(compositeQueryFinal.queryOperator(), is(op));
    }

    @Test
    void negateFieldQuery() {
        QuickGOQuery query = createFieldQuery("field1", "value1");

        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery
                compositeQuery =
                new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(Collections.singleton(query), QueryOp.NOT);

        assertThat(compositeQuery.queries(), hasSize(1));
        assertThat(compositeQuery.queries(), contains(query));
    }

    @Test
    void negateAllQuery() {
        QuickGOQuery query = QuickGOQuery.createAllQuery();

        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery
                compositeQuery = new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(Collections.singleton(query), QueryOp.NOT);

        assertThat(compositeQuery.queries(), hasSize(1));
        assertThat(compositeQuery.queries(), contains(query));
    }

    @Test
    void nestANDAndORAndNOTQueries() {
        QuickGOQuery query1 = createFieldQuery("field1", "value1");
        QuickGOQuery query2 = createFieldQuery("field2", "value2");
        QuickGOQuery query3 = createFieldQuery("field3", "value3");

        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery
                andQuery = new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(TestUtil.asSet(query1, query2), QueryOp.AND);
        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery
                orNestedQuery = new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(TestUtil.asSet(query3, andQuery), QueryOp.OR);
        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery
                notQuery = new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(TestUtil.asSet(orNestedQuery), QueryOp.NOT);

        assertThat(notQuery.queries(), hasSize(1));
        assertThat(notQuery.queries(), contains(orNestedQuery));
        assertThat(notQuery.queryOperator(), is(QueryOp.NOT));

        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery
                expectedOrNestedQuery = (uk.ac.ebi.quickgo.rest.search.query.CompositeQuery)notQuery.queries().iterator().next();
        assertThat(expectedOrNestedQuery.queries(), hasSize(2));
        assertThat(expectedOrNestedQuery.queries(), containsInAnyOrder(query3, andQuery));
        assertThat(expectedOrNestedQuery.queryOperator(), is(QueryOp.OR));

        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery expectedAndQuery = getCompositeNestedQuery(orNestedQuery);
        assertThat(expectedAndQuery.queries(), hasSize(2));
        assertThat(expectedAndQuery.queries(), containsInAnyOrder(query1, query2));
        assertThat(expectedAndQuery.queryOperator(), is(QueryOp.AND));
    }

    @Test
    void visitorIsCalledCorrectly() {
        uk.ac.ebi.quickgo.rest.search.query.FieldQuery
                query = new uk.ac.ebi.quickgo.rest.search.query.FieldQuery("field1", "value1");

        uk.ac.ebi.quickgo.rest.search.query.CompositeQuery
                compQuery = new uk.ac.ebi.quickgo.rest.search.query.CompositeQuery(TestUtil.asSet(query), QueryOp.AND);

        compQuery.accept(visitor);
        verify(visitor).visit(compQuery);
    }


    private uk.ac.ebi.quickgo.rest.search.query.CompositeQuery getCompositeNestedQuery(
            uk.ac.ebi.quickgo.rest.search.query.CompositeQuery compositeQuery) {
        Set<QuickGOQuery> nestedQueries = compositeQuery.queries();

        return (uk.ac.ebi.quickgo.rest.search.query.CompositeQuery) nestedQueries.stream()
                .filter(query -> query instanceof uk.ac.ebi.quickgo.rest.search.query.CompositeQuery)
                .findFirst().get();
    }

    private QuickGOQuery createFieldQuery(String field, String value) {
        return new uk.ac.ebi.quickgo.rest.search.query.FieldQuery(field, value);
    }
}