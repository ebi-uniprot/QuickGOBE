package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.SolrQueryStringSanitizer;

import java.util.Set;
import java.util.stream.Collectors;

import static uk.ac.ebi.quickgo.rest.search.query.SolrQueryConverter.CROSS_CORE_JOIN_SYNTAX;
import static uk.ac.ebi.quickgo.rest.search.query.SolrQueryConverter.SOLR_FIELD_SEPARATOR;

/**
 * Created 02/08/16
 * @author Edd
 */
public class UnsortedSolrQuerySerializer implements QueryVisitor<String> {
    private final SolrQueryStringSanitizer queryStringSanitizer;

    public UnsortedSolrQuerySerializer() {
        this.queryStringSanitizer = new SolrQueryStringSanitizer();
    }

    @Override public String visit(FieldQuery query) {
        return "(" + query.field() + SOLR_FIELD_SEPARATOR + queryStringSanitizer.sanitize(query.value()) + ")";
    }

    @Override public String visit(CompositeQuery query) {
        CompositeQuery.QueryOp operator = query.queryOperator();
        Set<QuickGOQuery> queries = query.queries();

        if (queries.size() == 1 && operator.equals(CompositeQuery.QueryOp.NOT)) {
            String singletonQuery = queries.iterator().next().accept(this);
            return CompositeQuery.QueryOp.NOT + " (" + singletonQuery + ")";
        } else {
            String operatorText = " " + operator.name() + " ";

            if (operator.equals(CompositeQuery.QueryOp.AND)) {
                return queries.stream()
                        .map(q -> q.accept(this))
                        .collect(Collectors.joining(operatorText, "(", ")"));
            } else {
                NestedSerializer nestedSerializer = new NestedSerializer();
                String termsCSV = queries.stream()
                        .map(q -> q.accept(nestedSerializer))
                        .collect(Collectors.joining(","));
                return "({!terms f=" + nestedSerializer.field + "}" + termsCSV + ")";
            }

        }
    }

    private class NestedSerializer implements QueryVisitor<String> {
        private String field;

        @Override public String visit(FieldQuery query) {
            if (field != null && !field.equals(query.field())) {
                throw new IllegalArgumentException("Fields must all be the same");
            } else {
                field = query.field();
            }
            return query.value();
        }

        @Override public String visit(CompositeQuery query) {
            throw new IllegalArgumentException(
                    "UnsortedSolrQuerySerializer does not handle nested CompositeQuery instances");
        }

        @Override public String visit(NoFieldQuery query) {
            throw new IllegalArgumentException(
                    "UnsortedSolrQuerySerializer does not handle nested NoFieldQuery instances");
        }

        @Override public String visit(AllQuery query) {
            throw new IllegalArgumentException(
                    "UnsortedSolrQuerySerializer does not handle nested AllQuery instances");
        }

        @Override public String visit(JoinQuery query) {
            throw new IllegalArgumentException(
                    "UnsortedSolrQuerySerializer does not handle nested JoinQuery instances");
        }
    }

    @Override public String visit(NoFieldQuery query) {
        return "(" + queryStringSanitizer.sanitize(query.getValue()) + ")";
    }

    @Override public String visit(AllQuery query) {
        return "*:*";
    }

    @Override public String visit(JoinQuery query) {
        String fromFilterString;

        if (query.getFromFilter() != null) {
            fromFilterString = query.getFromFilter().accept(this);
        } else {
            fromFilterString = "";
        }

        return String.format(CROSS_CORE_JOIN_SYNTAX, query.getJoinFromAttribute(), query.getJoinToAttribute(),
                query.getJoinFromTable(), fromFilterString);
    }
}
