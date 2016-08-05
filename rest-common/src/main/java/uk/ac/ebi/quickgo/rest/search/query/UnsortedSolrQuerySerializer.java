package uk.ac.ebi.quickgo.rest.search.query;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>This class defines an algorithm for serializing {@link QuickGOQuery}s into a corresponding
 * Solr query String. The implementation creates a corresponding String query that returns
 * results that cannot be guaranteed to be in the order of relevance: instead, results
 * may be returned in the order the entities were inserted into the underlying repository.
 *
 * <p>A reason for using this implementation would be for improved performance when searching for
 * large numbers of Solr documents by an indexed value, e.g. "find me all documents whose field1
 * has value id1 OR id2 OR ....".
 *
 * <p>Specifically, this class decorates the {@link SortedSolrQuerySerializer} with
 * specific behaviour for {@link CompositeQuery}s containing ORs and {@link FieldQuery}s.
 *
 *
 * Created 02/08/16
 *
 * @author Edd
 */
public class UnsortedSolrQuerySerializer implements QueryVisitor<String> {
    static final String TERMS_LOCAL_PARAMS_QUERY_FORMAT = "({!terms f=%s}%s)";

    private final SortedSolrQuerySerializer sortedQuerySerializer;
    private final Set<String> termsQueryCompatibleFields;

    public UnsortedSolrQuerySerializer() {
        this.sortedQuerySerializer = new SortedSolrQuerySerializer();
        this.termsQueryCompatibleFields = Stream.of(
                "goId",
                "qualifier",
                //                "geneProductId",
                "goId_join",
                "geneProductType",
                "dbObjectSymbol",
                "dbSubset",
                "goEvidence",
                "ecoId",
                "reference",
                "referenceSearch",
                "withFrom",
                "withFromSearch",
                "taxonId",
                "interactingTaxonId",
                "assignedBy",
                "extension").collect(Collectors.toSet());
    }

    /**
     * Handles {@link CompositeQuery} instances identically to the behaviour in
     * {@link SortedSolrQuerySerializer}, except for when handling disjunctions (ORs). In this case,
     * it is required that the disjunction is only <b>one</b> level deep. This is because a
     * Solr "LocalParams" terms query is created, which allows one to perform a more performant
     * disjunction directly on the index, by passing any scoring. This becomes important if one
     * constructs a query with many (e.g., thousands) of disjunctions.
     *
     * @param query the {@link CompositeQuery} which is to be serialized
     * @return the serialized String representation of the supplied {@code query}
     */
    @Override
    public String visit(CompositeQuery query) {
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
                try {
                    // assume all queries in this OR are on the same field, and proceed to construct a terms query
                    NestedOrSerializer nestedOrSerializer = new NestedOrSerializer();
                    String termsCSV = queries.stream()
                            .map(q -> q.accept(nestedOrSerializer))
                            .collect(Collectors.joining(","));
                    return String.format(TERMS_LOCAL_PARAMS_QUERY_FORMAT, nestedOrSerializer.field, termsCSV);
                } catch (IllegalArgumentException iae) {
                    // otherwise, re-use the default sorted serializer
                    return queries.stream()
                            .map(q -> q.accept(this))
                            .collect(Collectors.joining(operatorText, "(", ")"));
                }
            }
        }
    }

    @Override
    public String visit(FieldQuery query) {
        if (isTermsQueryCompatible(query)) {
            return String.format(TERMS_LOCAL_PARAMS_QUERY_FORMAT, query.field(), query.value());
        } else {
            return sortedQuerySerializer.visit(query);
        }
    }

    @Override
    public String visit(NoFieldQuery query) {
        return sortedQuerySerializer.visit(query);
    }

    @Override
    public String visit(AllQuery query) {
        return sortedQuerySerializer.visit(query);
    }

    @Override
    public String visit(JoinQuery query) {
        return sortedQuerySerializer.visit(query);
    }

    /**
     * The serializer that handles nested {@link CompositeQuery}s involving ORs, referred to in
     * {@link UnsortedSolrQuerySerializer#visit(uk.ac.ebi.quickgo.rest.search.query.CompositeQuery)}.
     * Specifically, this OR serializer, handles (visits) only simple {@link FieldQuery} instances. No
     * other {@link QuickGOQuery} subclass is handled.
     */
    private class NestedOrSerializer implements QueryVisitor<String> {
        private static final String VISITOR_IMPLEMENTATION_NOT_PROVIDED_ERROR_FORMAT =
                "UnsortedSolrQuerySerializer does not handle nested %s instances in a disjunction";

        private String field;

        @Override
        public String visit(FieldQuery query) {
            if (isTermsQueryCompatible(query)) {
                if (field != null && !field.equals(query.field())) {
                    throw new IllegalArgumentException(
                            "Fields must all be the same. Encountered: " + field + ", and " + query.field());
                } else {
                    field = query.field();
                }
                return query.value();
            } else {
                throw new IllegalArgumentException("this fieldquery is not termsable");
            }
        }

        @Override
        public String visit(CompositeQuery query) {
            throw new IllegalArgumentException(
                    String.format(VISITOR_IMPLEMENTATION_NOT_PROVIDED_ERROR_FORMAT, "CompositeQuery"));
        }

        @Override
        public String visit(NoFieldQuery query) {
            throw new IllegalArgumentException(
                    String.format(VISITOR_IMPLEMENTATION_NOT_PROVIDED_ERROR_FORMAT, "NoFieldQuery"));
        }

        @Override
        public String visit(AllQuery query) {
            throw new IllegalArgumentException(
                    String.format(VISITOR_IMPLEMENTATION_NOT_PROVIDED_ERROR_FORMAT, "AllQuery"));
        }

        @Override
        public String visit(JoinQuery query) {
            throw new IllegalArgumentException(
                    String.format(VISITOR_IMPLEMENTATION_NOT_PROVIDED_ERROR_FORMAT, "JoinQuery"));
        }
    }

    private boolean isTermsQueryCompatible(FieldQuery query) {
        if (termsQueryCompatibleFields.contains(query.field())) {
            return true;
        } else {
            return false;
        }
    }
}
