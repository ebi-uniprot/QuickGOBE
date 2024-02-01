package uk.ac.ebi.quickgo.rest.search.solr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.rest.search.query.*;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
class SortedSolrQuerySerializerTest {
    private static final String FIELD_WILDCARD = "fieldWC";
    private SortedSolrQuerySerializer serializer;
    private static final Set<String> wildCardCompatibleFields = new HashSet<>();

    static {
        wildCardCompatibleFields.add(FIELD_WILDCARD);
    }

    @BeforeEach
    void setUp() {
        this.serializer = new SortedSolrQuerySerializer();
    }

    @Test
    void visitTransformsFieldQueryToString()  {
        String field = "field1";
        String value = "value1";
        FieldQuery fieldQuery = new FieldQuery(field, value);

        String queryString = serializer.visit(fieldQuery);

        assertThat(queryString, is(buildFieldQueryString(field, value)));
    }

    @Test
    void visitTransformsNoFieldQueryToString()  {
        String value = "value1";
        NoFieldQuery noFieldQuery = new NoFieldQuery(value);

        String queryString = serializer.visit(noFieldQuery);

        assertThat(queryString, is(buildValueOnlyQuery(value)));
    }

    @Test
    void visitTransformsFieldQueryWithSolrReservedCharacterToString()  {
        String field = "field1";
        String value = "prefix:value1";
        String escapedValue = "prefix\\:value1";

        FieldQuery fieldQuery = new FieldQuery(field, value);

        String queryString = serializer.visit(fieldQuery);

        assertThat(queryString, is(buildFieldQueryString(field, escapedValue)));
    }

    @Test
    void visitTransformsCompositeQueryToString()  {
        CompositeQuery complexQuery = createComplexQuery();

        String queryString = serializer.visit(complexQuery);

        String expectedQuery = "(((field1:value1) AND (field2:value2)) OR (field3:value3))";
        assertThat(queryString, is(expectedQuery));
    }

    @Test
    void visitTransformsAllQueryToString() {
        AllQuery allQuery = new AllQuery();

        String queryString = serializer.visit(allQuery);

        String expectedQuery = "*:*";
        assertThat(queryString, is(expectedQuery));
    }

    @Test
    void visitTransformsNegatedAllQueryToString() {
        CompositeQuery nothingQuery =
                new CompositeQuery(asSet(QuickGOQuery.createAllQuery()), CompositeQuery.QueryOp.NOT);

        String queryString = serializer.visit(nothingQuery);

        String expectedQuery = "NOT (*:*)";
        assertThat(queryString, is(expectedQuery));
    }

    @Test
    void visitTransformsJoinQueryWithNoFromFilterToString() {
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
    void visitTransformsJoinQueryWithAFromFilterToString() {
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
    void visitTransforms3OrQueriesToString() {
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
    void visitTransforms3AndQueriesToString() {
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
    void visitWithWildCard(){
        SortedSolrQuerySerializer serializerWithWildCard = new SortedSolrQuerySerializer(wildCardCompatibleFields);
        AllNonEmptyFieldQuery allNonEmptyFieldQuery = new AllNonEmptyFieldQuery(FIELD_WILDCARD, SELECT_ALL_WHERE_FIELD_IS_NOT_EMPTY);

        String queryString = serializerWithWildCard.visit(allNonEmptyFieldQuery);

        assertThat(queryString, is(String.format("(%s:%s)", allNonEmptyFieldQuery.field(), RETRIEVE_ALL_NON_EMPTY )));
    }

    @Test
    void passingNullWildcardCompatibleSetThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> new SortedSolrQuerySerializer(null));
    }

    @Test
    void visitTransformsContainFieldQueryToString()  {
        String field = "field1";
        String value = "value1";
        ContainsFieldQuery fieldQuery = new ContainsFieldQuery(field, value);

        String queryString = serializer.visit(fieldQuery);

        assertThat(queryString, is(buildContainFieldQueryString(field, value)));
    }

    @Test
    void visitTransformsContainFieldQueryWithSolrEscape()  {
        String field = "field1";
        String value = "*value1*";
        ContainsFieldQuery fieldQuery = new ContainsFieldQuery(field, value);

        String queryString = serializer.visit(fieldQuery);

        assertThat(queryString, is(buildContainFieldQueryString(field, "\\*value1\\*")));
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

    private String buildContainFieldQueryString(String field, String value) {
        return "(" + field + SolrQueryConverter.SOLR_FIELD_SEPARATOR + SolrQueryConverter.SOLR_FIELD_STAR + value + SolrQueryConverter.SOLR_FIELD_STAR + ")";
    }

}
