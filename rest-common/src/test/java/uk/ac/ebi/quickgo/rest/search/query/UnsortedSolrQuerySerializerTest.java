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

    @Test
    public void visitTransformsTwoDisjunctionQueryOnSameFieldsToString() {
        FieldQuery term1 = new FieldQuery("field1", "value1");
        FieldQuery term2 = new FieldQuery("field1", "value2");
        CompositeQuery compositeQuery = new CompositeQuery(asSet(term1, term2), CompositeQuery.QueryOp.OR);

        String queryString = serializer.visit(compositeQuery);
        System.out.println(queryString);
    }

    @Test
    public void visitTransformsThreeDisjunctionQueryOnSameFieldsToString() {
        FieldQuery term1 = new FieldQuery("field1", "value1");
        FieldQuery term2 = new FieldQuery("field1", "value2");
        FieldQuery term3 = new FieldQuery("field1", "value3");
        CompositeQuery compositeQuery = new CompositeQuery(asSet(term1, term2, term3), CompositeQuery.QueryOp.OR);

        String queryString = serializer.visit(compositeQuery);
        System.out.println(queryString);
    }
}