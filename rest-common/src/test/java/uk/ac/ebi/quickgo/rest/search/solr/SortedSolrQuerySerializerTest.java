package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.query.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.rest.TestUtil.asSet;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.SELECT_ALL_WHERE_FIELD_IS_NOT_EMPTY;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.and;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrQueryConverter.CROSS_CORE_JOIN_SYNTAX;
import static uk.ac.ebi.quickgo.rest.search.solr.SortedSolrQuerySerializer.RETRIEVE_ALL_NON_EMPTY;

/**
 * Created 02/08/16
 * @author Edd
 */
public class SortedSolrQuerySerializerTest {
    public static final String FIELD_WC = "fieldWC";
    private SortedSolrQuerySerializer serializer;
    private static Set<String> wildCardCompatibleFields = new HashSet<>();

    static {
        wildCardCompatibleFields.add(FIELD_WC);
    }

    @Before
    public void setUp() {
        this.serializer = new SortedSolrQuerySerializer();
    }

    @Test
    public void visitTransformsFieldQueryToString() throws Exception {
        String field = "field1";
        String value = "value1";
        FieldQuery fieldQuery = new FieldQuery(field, value);

        String queryString = serializer.visit(fieldQuery);

        assertThat(queryString, is(buildFieldQueryString(field, value)));
    }

    @Test
    public void visitTransformsNoFieldQueryToString() throws Exception {
        String value = "value1";
        NoFieldQuery noFieldQuery = new NoFieldQuery(value);

        String queryString = serializer.visit(noFieldQuery);

        assertThat(queryString, is(buildValueOnlyQuery(value)));
    }

    @Test
    public void visitTransformsFieldQueryWithSolrReservedCharacterToString() throws Exception {
        String field = "field1";
        String value = "prefix:value1";
        String escapedValue = "prefix\\:value1";

        FieldQuery fieldQuery = new FieldQuery(field, value);

        String queryString = serializer.visit(fieldQuery);

        assertThat(queryString, is(buildFieldQueryString(field, escapedValue)));
    }

    @Test
    public void visitTransformsCompositeQueryToString() throws Exception {
        CompositeQuery complexQuery = createComplexQuery();

        String queryString = serializer.visit(complexQuery);

        String expectedQuery = "(((field1:value1) AND (field2:value2)) OR (field3:value3))";
        assertThat(queryString, is(expectedQuery));
    }

    @Test
    public void visitTransformsAllQueryToString() {
        AllQuery allQuery = new AllQuery();

        String queryString = serializer.visit(allQuery);

        String expectedQuery = "*:*";
        assertThat(queryString, is(expectedQuery));
    }

    @Test
    public void visitTransformsNegatedAllQueryToString() {
        CompositeQuery nothingQuery =
                new CompositeQuery(asSet(QuickGOQuery.createAllQuery()), CompositeQuery.QueryOp.NOT);

        String queryString = serializer.visit(nothingQuery);

        String expectedQuery = "NOT (*:*)";
        assertThat(queryString, is(expectedQuery));
    }

    @Test
    public void visitTransformsJoinQueryWithNoFromFilterToString() {
        String joinFromTable = "annotation";
        String joinFromAttribute = "id";
        String joinToTable = "ontology";
        String joinToAttribute = "id";

        String fromFilterString = "";

        JoinQuery query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);

        String solrJoinString = serializer.visit(query);

        assertThat(solrJoinString, is(String.format(CROSS_CORE_JOIN_SYNTAX, joinFromAttribute, joinToAttribute,
                joinFromTable, fromFilterString)));
    }

    @Test
    public void visitTransformsJoinQueryWithAFromFilterToString() {
        String joinFromTable = "annotation";
        String joinFromAttribute = "id";
        String joinToTable = "ontology";
        String joinToAttribute = "id";

        String fromFilterField = "aspect";
        String fromFilterValue = "molecular_function";
        QuickGOQuery fromFilter = QuickGOQuery.createQuery(fromFilterField, fromFilterValue);

        String fromFilterString = buildFieldQueryString(fromFilterField, fromFilterValue);

        JoinQuery query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute,
                fromFilter);

        String solrJoinString = serializer.visit(query);

        assertThat(solrJoinString, is(String.format(CROSS_CORE_JOIN_SYNTAX, joinFromAttribute, joinToAttribute,
                joinFromTable, fromFilterString)));
    }

    @Test
    public void visitTransforms3OrQueriesToString() {
        FieldQuery query1 = new FieldQuery("field1", "value1");
        FieldQuery query2 = new FieldQuery("field2", "value2");
        FieldQuery query3 = new FieldQuery("field3", "value3");

        QuickGOQuery compositeQuery = or(query1, query2, query3);

        String queryString = serializer.visit((CompositeQuery) compositeQuery);
        System.out.println(queryString);

        assertThat(queryString, is(
                String.format("((%s:%s) OR (%s:%s) OR (%s:%s))",
                        query1.field(), query1.value(),
                        query2.field(), query2.value(),
                        query3.field(), query3.value())
        ));
    }

    @Test
    public void visitTransforms3AndQueriesToString() {
        FieldQuery query1 = new FieldQuery("field1", "value1");
        FieldQuery query2 = new FieldQuery("field2", "value2");
        FieldQuery query3 = new FieldQuery("field3", "value3");

        QuickGOQuery compositeQuery = and(query1, query2, query3);

        String queryString = serializer.visit((CompositeQuery) compositeQuery);
        System.out.println(queryString);

        assertThat(queryString, is(
                String.format("((%s:%s) AND (%s:%s) AND (%s:%s))",
                        query1.field(), query1.value(),
                        query2.field(), query2.value(),
                        query3.field(), query3.value())
        ));
    }

    @Test
    public void visitWithWildCard(){
        SortedSolrQuerySerializer serializerWithWildCard = new SortedSolrQuerySerializer(wildCardCompatibleFields);
        AllNonEmptyFieldQuery allNonEmptyFieldQuery = new AllNonEmptyFieldQuery(FIELD_WC, SELECT_ALL_WHERE_FIELD_IS_NOT_EMPTY);

        String queryString = serializerWithWildCard.visit(allNonEmptyFieldQuery);

        assertThat(queryString, is(String.format("(%s:%s)", allNonEmptyFieldQuery.field(), RETRIEVE_ALL_NON_EMPTY )));
    }

    private String buildFieldQueryString(String field, String value) {
        return "(" + field + SolrQueryConverter.SOLR_FIELD_SEPARATOR + value + ")";
    }

    private String buildValueOnlyQuery(String value) {
        return "(" + value + ")";
    }

    private CompositeQuery createComplexQuery() {
        FieldQuery query1 = new FieldQuery("field1", "value1");
        FieldQuery query2 = new FieldQuery("field2", "value2");

        CompositeQuery andQuery = new CompositeQuery(asSet(query1, query2), CompositeQuery.QueryOp.AND);

        FieldQuery query3 = new FieldQuery("field3", "value3");

        return new CompositeQuery(asSet(andQuery, query3), CompositeQuery.QueryOp.OR);
    }

}
