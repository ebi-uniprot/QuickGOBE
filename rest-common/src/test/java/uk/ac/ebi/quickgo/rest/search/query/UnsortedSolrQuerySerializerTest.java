package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Before;
import org.junit.Test;

import static uk.ac.ebi.quickgo.rest.TestUtil.asSet;

/**
 * Created 02/08/16
 * @author Edd
 */
public class UnsortedSolrQuerySerializerTest {
    private UnsortedSolrQuerySerializer serializer;

    @Before
    public void setUp() {
        this.serializer = new UnsortedSolrQuerySerializer();
    }

    // all tests in sortedsolrqueryserializer
    // include nested tests to guarantee term query being used

    @Test
    public void visitTransformsOneDisjuctionQueryToString() {
        FieldQuery term1 = new FieldQuery("field1", "value1");

        String queryString = serializer.visit(term1);
        System.out.println(queryString);
    }

    @Test
    public void visitTransformsComplexCompositeToString() {
        FieldQuery term1 = new FieldQuery("field1", "value1");
        FieldQuery term2 = new FieldQuery("field1", "value2");

        CompositeQuery compositeAndQuery = new CompositeQuery(asSet(term1, term2), CompositeQuery.QueryOp.AND);
        CompositeQuery compositeORQuery = new CompositeQuery(asSet(term1, term2), CompositeQuery.QueryOp.OR);

        CompositeQuery compositeQuery = new CompositeQuery(asSet(compositeAndQuery, compositeORQuery), CompositeQuery.QueryOp.AND);

        String queryString = serializer.visit(compositeQuery);
        System.out.println(queryString);
    }

    @Test
    public void visitTransformsTwoConjunctionQueriesOnSameFieldsToString() {
        FieldQuery term1 = new FieldQuery("field1", "value1");
        FieldQuery term2 = new FieldQuery("field1", "value2");
        CompositeQuery compositeQuery = new CompositeQuery(asSet(term1, term2), CompositeQuery.QueryOp.AND);

        String queryString = serializer.visit(compositeQuery);
        System.out.println(queryString);
    }

    @Test
    public void visitTransformsTwoDisjunctionQueriesOnSameFieldsToString() {
        FieldQuery term1 = new FieldQuery("field1", "value1");
        FieldQuery term2 = new FieldQuery("field1", "value2");
        CompositeQuery compositeQuery = new CompositeQuery(asSet(term1, term2), CompositeQuery.QueryOp.OR);

        String queryString = serializer.visit(compositeQuery);
        System.out.println(queryString);
    }

    @Test
    public void visitTransformsThreeDisjunctionQueriesOnSameFieldsToString() {
        FieldQuery term1 = new FieldQuery("field1", "value1");
        FieldQuery term2 = new FieldQuery("field1", "value2");
        FieldQuery term3 = new FieldQuery("field1", "value3");
        CompositeQuery compositeQuery = new CompositeQuery(asSet(term1, term2, term3), CompositeQuery.QueryOp.OR);

        String queryString = serializer.visit(compositeQuery);
        System.out.println(queryString);
    }
}