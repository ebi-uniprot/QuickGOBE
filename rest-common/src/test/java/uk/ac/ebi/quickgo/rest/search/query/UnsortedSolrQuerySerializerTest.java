package uk.ac.ebi.quickgo.rest.search.query;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.StringJoiner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.rest.TestUtil.asSet;
import static uk.ac.ebi.quickgo.rest.search.query.SolrQueryConverter.CROSS_CORE_JOIN_SYNTAX;
import static uk.ac.ebi.quickgo.rest.search.query.UnsortedSolrQuerySerializer.TERMS_LOCAL_PARAMS_QUERY_FORMAT;

/**
 * Created 02/08/16
 *
 * @author Edd
 */
@RunWith(HierarchicalContextRunner.class)
public class UnsortedSolrQuerySerializerTest {
    private UnsortedSolrQuerySerializer serializer;

    @Before
    public void setUp() {
        this.serializer = new UnsortedSolrQuerySerializer();
    }

    public class TransformationsOneLevelDeep {
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

        @Test(expected = IllegalArgumentException.class)
        public void visitTransformsCompositeQueryToString() throws Exception {
            CompositeQuery complexQuery = createComplexQuery();

            serializer.visit(complexQuery);
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

    public class TransformationsToTermsQueries {

        @Test
        public void visitTransformsTwoOrsOnSameFieldToTermsQueryString() {
            FieldQuery query1 = new FieldQuery("field1", "value1");
            FieldQuery query2 = new FieldQuery("field1", "value2");

            QuickGOQuery compositeQuery = query1.or(query2);

            String queryString = serializer.visit((CompositeQuery) compositeQuery);
            System.out.println(queryString);

            assertThat(queryString, is(
                    String.format(TERMS_LOCAL_PARAMS_QUERY_FORMAT,
                            query1.field(),
                            query1.value() + "," + query2.value())
            ));
        }

        @Test
        public void visitTransformsThreeOrsOnSameFieldToString() {
            FieldQuery query1 = new FieldQuery("field1", "value1");
            FieldQuery query2 = new FieldQuery("field1", "value2");
            FieldQuery query3 = new FieldQuery("field1", "value3");

            QuickGOQuery compositeQuery = query1.or(query2, query3);

            String queryString = serializer.visit((CompositeQuery) compositeQuery);
            System.out.println(queryString);

            assertThat(queryString, is(
                    String.format(TERMS_LOCAL_PARAMS_QUERY_FORMAT,
                            query1.field(),
                            query1.value() + "," + query2.value() + "," + query3.value())
            ));
        }

        @Test
        public void visitTransformsTwoOrsWithinAndToString() {
            FieldQuery query1 = new FieldQuery("field1", "value1");
            FieldQuery query2 = new FieldQuery("field1", "value2");

            QuickGOQuery orQuery = query1.or(query2);

            FieldQuery otherQuery = new FieldQuery("field2", "value3");

            QuickGOQuery compositeQuery = otherQuery.and(orQuery);

            String queryString = serializer.visit((CompositeQuery) compositeQuery);
            System.out.println(queryString);

            assertThat(queryString, is(
                    "(" +
                            buildTermsQuery(otherQuery.field(), otherQuery.value()) +
                            " AND " +
                            buildTermsQuery(query1.field(), query1.value(), query2.value()) +
                            ")"
            ));
        }

        private String buildTermsQuery(String field, String... values) {
            StringJoiner stringJoiner = new StringJoiner(",");
            for (String value : values) {
                stringJoiner.add(value);
            }
            return String.format(TERMS_LOCAL_PARAMS_QUERY_FORMAT, field, stringJoiner.toString());
        }

        @Test
        public void visitTransformsTwoOrsWhereOneClauseIsAnAndToString() {
            FieldQuery query1 = new FieldQuery("field1", "value1");
            FieldQuery query2 = new FieldQuery("field1", "value2");

            QuickGOQuery andQuery = query1.and(query2);

            FieldQuery otherQuery = new FieldQuery("field2", "value3");

            QuickGOQuery compositeQuery = otherQuery.or(andQuery);

            String queryString = serializer.visit((CompositeQuery) compositeQuery);

            assertThat(queryString, is(
                    "(" +
                            buildTermsQuery(otherQuery.field(), otherQuery.value()) +
                            " OR (" +
                            buildTermsQuery(query1.field(), query1.value()) +
                            " AND " +
                            buildTermsQuery(query2.field(), query2.value()) +
                            "))"
            ));
        }

        @Test
        public void visitTransformsTwoOrsWhereOneClauseIsAnOrToString() {
            FieldQuery query1 = new FieldQuery("field1", "value1");
            FieldQuery query2 = new FieldQuery("field1", "value2");

            QuickGOQuery orQuery = query1.or(query2);

            FieldQuery otherQuery = new FieldQuery("field2", "value3");

            QuickGOQuery compositeQuery = otherQuery.or(orQuery);

            String queryString = serializer.visit((CompositeQuery) compositeQuery);

            assertThat(queryString, is(
                    "(" +
                            buildTermsQuery(otherQuery.field(), otherQuery.value()) +
                            " OR " +
                            buildTermsQuery(query1.field(), query1.value(), query2.value())
                            + ")"));
        }

        @Test
        public void canTransformOrOfFieldAndAnotherCompositeOr() {
            QuickGOQuery innerOr = QuickGOQuery.generalisedOr(
                    new FieldQuery("field1", "value1"),
                    new FieldQuery("field1", "value2"));
            QuickGOQuery compositeOr = QuickGOQuery.generalisedOr(
                    new FieldQuery("field3", "value3"),
                    innerOr
            );

            String queryString = serializer.visit((CompositeQuery) compositeOr);

            System.out.println(queryString);
        }
    }
}