package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.SolrQueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.search.query.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.ac.ebi.quickgo.rest.search.solr.SolrQueryConverter.CROSS_CORE_JOIN_SYNTAX;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrQueryConverter.SOLR_FIELD_SEPARATOR;

/**
 * <p>This class defines an algorithm for serializing {@link QuickGOQuery}s into a corresponding
 * Solr query String. The implementation creates a corresponding String query that returns
 * results in the expected order of relevance, where Solr scoring is performed on all subclasses
 * of {@link QuickGOQuery}.
 *
 * Created 02/08/16
 * @author Edd
 */
public class SortedSolrQuerySerializer implements QueryVisitor<String> {
    public static final String RETRIEVE_ALL_NON_EMPTY = "[ '' TO * ]";
    private final SolrQueryStringSanitizer queryStringSanitizer;
    private final Set<String> wildCardFieldQueryCompatibleFields;

    public SortedSolrQuerySerializer(Set<String> wildCardFieldQueryCompatibleFields) {
        this.queryStringSanitizer = new SolrQueryStringSanitizer();
        this.wildCardFieldQueryCompatibleFields = wildCardFieldQueryCompatibleFields;
    }

    public SortedSolrQuerySerializer() {
        this.queryStringSanitizer = new SolrQueryStringSanitizer();
        this.wildCardFieldQueryCompatibleFields = Collections.emptySet();
    }

    @Override public String visit(FieldQuery query) {
        return "(" + query.field() + SOLR_FIELD_SEPARATOR + queryStringSanitizer.sanitize(query.value()) + ")";
    }

    @Override public String visit(CompositeQuery query) {
        CompositeQuery.QueryOp operator = query.queryOperator();
        Set<QuickGOQuery> queries = query.queries();

        if (queries.size() == 1 && operator == CompositeQuery.QueryOp.NOT) {
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

    @Override public String visit(AllNonEmptyFieldQuery query) {
        if(wildCardFieldQueryCompatibleFields.contains(query.field())) {
            return "(" + query.field() + SOLR_FIELD_SEPARATOR + RETRIEVE_ALL_NON_EMPTY + ")";
        }
        throw new IllegalArgumentException("It's invalid to search for all non-empty values of " + query.field());
    }
}
