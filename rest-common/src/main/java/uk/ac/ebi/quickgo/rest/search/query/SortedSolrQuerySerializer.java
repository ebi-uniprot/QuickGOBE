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
public class SortedSolrQuerySerializer implements QueryVisitor<String> {
    private final SolrQueryStringSanitizer queryStringSanitizer;

    public SortedSolrQuerySerializer() {
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

            return queries.stream()
                    .map(q -> q.accept(this))
                    .collect(Collectors.joining(operatorText, "(", ")"));
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
