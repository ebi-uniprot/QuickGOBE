package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.query.*;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.rest.TestUtil.asSet;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.and;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrQueryConverter.CROSS_CORE_JOIN_SYNTAX;
import static uk.ac.ebi.quickgo.rest.search.solr.UnsortedSolrQuerySerializer.TERMS_LOCAL_PARAMS_QUERY_FORMAT;

/**
 * Created 02/08/16
 *
 * @author Edd
 */
@RunWith(HierarchicalContextRunner.class)
public class UnsortedSolrQuerySerializerTest {
    private static final String TERMS_COMPATIBLE_FIELD_1 = "termsCompatibleField1";
    private static final String TERMS_COMPATIBLE_FIELD_2 = "termsCompatibleField2";
    private static final String TERMS_COMPATIBLE_FIELD_3 = "termsCompatibleField3";
    private static final String TERMS_INCOMPATIBLE_FIELD_1 = "termsIncompatibleField1";
    private static final String TERMS_INCOMPATIBLE_FIELD_2 = "termsIncompatibleField2";
    private static final String TERMS_INCOMPATIBLE_FIELD_3 = "termsIncompatibleField3";
    private UnsortedSolrQuerySerializer serializer;
    private Set<String> nonEmptyFieldQueryCompatibleFields = new HashSet<>();
    private Set<String> termsQueryCompatibleFields;
    @Before
    public void setUp() {
        termsQueryCompatibleFields = Stream.of(
                TERMS_COMPATIBLE_FIELD_1,
                TERMS_COMPATIBLE_FIELD_2,
                TERMS_COMPATIBLE_FIELD_3)
                .collect(Collectors
                        .toSet());
        this.serializer = new UnsortedSolrQuerySerializer(termsQueryCompatibleFields, nonEmptyFieldQueryCompatibleFields);
    }

    public class Initialization {
        @Test(expected = IllegalArgumentException.class)
        public void initWithNullSetOfFieldsThatAreTermsQueryCompatibleCausesIllegalArgumentException() {
            new UnsortedSolrQuerySerializer(null, nonEmptyFieldQueryCompatibleFields);
        }

        @Test(expected = IllegalArgumentException.class)
        public void initWithNullSetOfFieldsThatAreWildCardCompatibleCausesIllegalArgumentException() {
            new UnsortedSolrQuerySerializer(termsQueryCompatibleFields, null);
        }
    }

    public class TransformationsWithFieldsIncompatibleWithTermsQuery {
        @Test
        public void visitTransformsFieldQueryToString() throws Exception {
            String field = TERMS_INCOMPATIBLE_FIELD_1;
            String value = "value1";
            FieldQuery fieldQuery = new FieldQuery(field, value);

            String queryString = serializer.visit(fieldQuery);

            assertThat(queryString, is(buildFieldQueryString(field, value)));
        }

        @Test
        public void visitTransformsContainFieldQueryToString() throws Exception {
            String field = TERMS_INCOMPATIBLE_FIELD_1;
            String value = "value1";
            ContainsFieldQuery fieldQuery = new ContainsFieldQuery(field, value);

            String queryString = serializer.visit(fieldQuery);

            assertThat(queryString, is(buildFieldQueryString(field, "*value1*")));
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
            String field = TERMS_INCOMPATIBLE_FIELD_1;
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

            String expectedQuery = "(((" + TERMS_INCOMPATIBLE_FIELD_1 + ":value1) AND " +
                    "(" + TERMS_INCOMPATIBLE_FIELD_2 + ":value2)) OR " +
                    "(" + TERMS_INCOMPATIBLE_FIELD_3 + ":value3))";
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
            String joinFromAttribute = TERMS_INCOMPATIBLE_FIELD_1;
            String joinToTable = "ontology";
            String joinToAttribute = TERMS_INCOMPATIBLE_FIELD_2;

            String fromFilterString = "";

            JoinQuery query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);

            String solrJoinString = serializer.visit(query);

            assertThat(solrJoinString, is(String.format(CROSS_CORE_JOIN_SYNTAX, joinFromAttribute, joinToAttribute,
                    joinFromTable, fromFilterString)));
        }

        @Test
        public void visitTransformsJoinQueryWithAFromFilterToString() {
            String joinFromTable = "annotation";
            String joinFromAttribute = TERMS_INCOMPATIBLE_FIELD_1;
            String joinToTable = "ontology";
            String joinToAttribute = TERMS_INCOMPATIBLE_FIELD_2;

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
            FieldQuery query1 = new FieldQuery(TERMS_INCOMPATIBLE_FIELD_1, "value1");
            FieldQuery query2 = new FieldQuery(TERMS_INCOMPATIBLE_FIELD_2, "value2");

            CompositeQuery andQuery = new CompositeQuery(asSet(query1, query2), CompositeQuery.QueryOp.AND);

            FieldQuery query3 = new FieldQuery(TERMS_INCOMPATIBLE_FIELD_3, "value3");

            return new CompositeQuery(asSet(andQuery, query3), CompositeQuery.QueryOp.OR);
        }
    }

    public class TransformationsToTermsQueries {
        @Test
        public void visitTransformsOneQueryToTermsQueryString() {
            FieldQuery query = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value1");

            String queryString = serializer.visit(query);
            System.out.println(queryString);

            assertThat(queryString, is(
                    buildTermsQuery(query.field(), query.value())
            ));
        }

        @Test
        public void visitTransformsTwoOrsOnSameFieldToTermsQueryString() {
            FieldQuery query1 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value1");
            FieldQuery query2 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value2");

            QuickGOQuery compositeQuery = or(query1, query2);

            String queryString = serializer.visit((CompositeQuery) compositeQuery);
            System.out.println(queryString);

            assertThat(queryString, is(
                    buildTermsQuery(query1.field(), query1.value(), query2.value())
            ));
        }

        @Test
        public void visitTransformsThreeOrsOnSameFieldToString() {
            FieldQuery query1 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value1");
            FieldQuery query2 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value2");
            FieldQuery query3 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value3");

            QuickGOQuery compositeQuery = or(query1, query2, query3);

            String queryString = serializer.visit((CompositeQuery) compositeQuery);
            System.out.println(queryString);

            assertThat(queryString, is(
                    buildTermsQuery(query1.field(), query1.value(), query2.value(), query3.value())
            ));
        }

        @Test
        public void visitTransformsTwoOrsOnDifferentFieldsWithinAndToString() {
            FieldQuery query1 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value1");
            FieldQuery query2 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value2");

            QuickGOQuery orQuery = or(query1, query2);

            FieldQuery otherQuery = new FieldQuery(TERMS_COMPATIBLE_FIELD_2, "value3");

            QuickGOQuery compositeQuery = and(otherQuery, orQuery);

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

        @Test
        public void visitTransformsTwoOrsWhereOneClauseIsAnAndToString() {
            FieldQuery query1 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value1");
            FieldQuery query2 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value2");

            QuickGOQuery andQuery = and(query1, query2);

            FieldQuery otherQuery = new FieldQuery(TERMS_COMPATIBLE_FIELD_2, "value3");

            QuickGOQuery compositeQuery = or(otherQuery, andQuery);

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
            FieldQuery query1 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value1");
            FieldQuery query2 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value2");

            QuickGOQuery orQuery = or(query1, query2);

            FieldQuery otherQuery = new FieldQuery(TERMS_COMPATIBLE_FIELD_2, "value3");

            QuickGOQuery compositeQuery = or(otherQuery, orQuery);

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
            FieldQuery innerQuery1 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value1");
            FieldQuery innerQuery2 = new FieldQuery(TERMS_COMPATIBLE_FIELD_1, "value2");
            QuickGOQuery innerOr = or(
                    innerQuery1,
                    innerQuery2);
            FieldQuery query3 = new FieldQuery(TERMS_COMPATIBLE_FIELD_3, "value3");
            QuickGOQuery compositeOr = or(
                    query3,
                    innerOr
            );

            String queryString = serializer.visit((CompositeQuery) compositeOr);

            assertThat(queryString, is("(" +
                    buildTermsQuery(query3.field(), query3.value()) + " OR " +
                    buildTermsQuery(innerQuery1.field(), innerQuery1.value(), innerQuery2.value()) +
                    ")"));
            System.out.println(queryString);
        }

        String buildTermsQuery(String field, String... values) {
            StringJoiner stringJoiner = new StringJoiner(",");
            for (String value : values) {
                stringJoiner.add(value);
            }
            return String.format(TERMS_LOCAL_PARAMS_QUERY_FORMAT, field, stringJoiner.toString());
        }
    }
}
