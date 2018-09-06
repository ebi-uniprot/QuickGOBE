package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.query.*;

import com.google.common.base.Preconditions;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;
import static uk.ac.ebi.quickgo.rest.search.solr.UnsortedSolrQuerySerializer.TermQueryTransformationResult
        .failedTransformationResult;
import static uk.ac.ebi.quickgo.rest.search.solr.UnsortedSolrQuerySerializer.TermQueryTransformationResult
        .successfulTransformationResult;

/**
 * <p>This class defines an algorithm for serializing {@link QuickGOQuery}s into a corresponding
 * Solr query String. The implementation creates a corresponding String query that returns
 * results that cannot be guaranteed to be in the order of relevance: instead, results
 * may be returned in the order the entities were inserted into the underlying repository.
 *
 * <p>A reason for using this implementation would be for improved performance when searching for
 * large numbers of Solr documents by an indexed value, e.g. "find me all documents whose field1
 * has value id1 OR id2 OR ....", i.e., a large disjunction.
 *
 * <p>Specifically, this class produces queries that use the "LocalParams" style of query, as documented here:
 * <ul>
 *     <li>https://cwiki.apache.org/confluence/display/solr/Other+Parsers#OtherParsers-TermsQueryParser</li>
 * </ul>
 *
 * Created 02/08/16
 *
 * @author Edd
 */
public class UnsortedSolrQuerySerializer implements QueryVisitor<String> {
    static final String TERMS_LOCAL_PARAMS_QUERY_FORMAT = "({!terms f=%s}%s)";
    private static final Logger LOGGER = getLogger(UnsortedSolrQuerySerializer.class);

    private final SortedSolrQuerySerializer sortedQuerySerializer;
    private final Set<String> termsQueryCompatibleFields;

    public UnsortedSolrQuerySerializer(Set<String> termsQueryCompatibleFields, Set<String>
            nonEmptyFieldQueryCompatibleFields) {
        Preconditions.checkArgument(termsQueryCompatibleFields != null,
                "The Set<String> of termsQueryCompatibleFields cannot be null");
        Preconditions.checkArgument(nonEmptyFieldQueryCompatibleFields != null,
                                    "The Set<String> of nonEmptyFieldQueryCompatibleFields cannot be null");

        if (termsQueryCompatibleFields.isEmpty()) {
            LOGGER.warn("The Set<String> of termsQueryCompatibleFields is empty: " +
                    "no Solr Terms Queries, e.g., {!terms f=field}fieldValue, will be created");
        }

        this.sortedQuerySerializer = new SortedSolrQuerySerializer(nonEmptyFieldQueryCompatibleFields);

        this.termsQueryCompatibleFields = termsQueryCompatibleFields;
    }

    @Override
    public String visit(FieldQuery query) {
        if (isTermsQueryCompatible(query)) {
            return buildTermsQuery(query.field(), query.value());
        } else {
            return sortedQuerySerializer.visit(query);
        }
    }

    private String buildTermsQuery(String field, String... values) {
        StringJoiner stringJoiner = new StringJoiner(",");
        for (String value : values) {
            stringJoiner.add(value.toLowerCase());
        }
        return String.format(TERMS_LOCAL_PARAMS_QUERY_FORMAT, field, stringJoiner.toString());
    }

    private boolean isTermsQueryCompatible(FieldQuery query) {
        return termsQueryCompatibleFields.contains(query.field());
    }

    static class TermQueryTransformationResult {
        static final String FAILED_TRANSFORMATION_VALUE = "TransformationFailed";
        boolean successful;
        String value;

        private TermQueryTransformationResult(boolean successful, String value) {
            this.successful = successful;
            this.value = value;
        }

        static TermQueryTransformationResult successfulTransformationResult(String value) {
            return new TermQueryTransformationResult(true, value);
        }

        static TermQueryTransformationResult failedTransformationResult() {
            return new TermQueryTransformationResult(false, TermQueryTransformationResult.FAILED_TRANSFORMATION_VALUE);
        }
    }
    /**
     * The serializer that handles nested {@link CompositeQuery}s involving ORs, referred to in
     * {@link UnsortedSolrQuerySerializer#visit(uk.ac.ebi.quickgo.rest.search.query.CompositeQuery)}.
     * Specifically, this OR serializer, handles (visits) only simple {@link FieldQuery} instances. No
     * other {@link QuickGOQuery} subclass is handled.
     */
    private class NestedOrSerializer implements QueryVisitor<TermQueryTransformationResult> {
        private String field;

        @Override
        public TermQueryTransformationResult visit(FieldQuery query) {
            if (isTermsQueryCompatible(query)) {
                if (field != null && !field.equals(query.field())) {
                    return failedTransformationResult();
                } else {
                    field = query.field();
                    return successfulTransformationResult(query.value());
                }
            } else {
                return failedTransformationResult();
            }
        }

        @Override
        public TermQueryTransformationResult visit(CompositeQuery query) {
            return failedTransformationResult();
        }

        @Override
        public TermQueryTransformationResult visit(NoFieldQuery query) {
            return failedTransformationResult();
        }

        @Override
        public TermQueryTransformationResult visit(AllQuery query) {
            return failedTransformationResult();
        }

        @Override
        public TermQueryTransformationResult visit(JoinQuery query) {
            return failedTransformationResult();
        }

        @Override public TermQueryTransformationResult visit(AllNonEmptyFieldQuery query) {
            return failedTransformationResult();
        }

        @Override public TermQueryTransformationResult visit(ContainFieldQuery query) {
            return failedTransformationResult();
        }
    }

    /**
     * Handles {@link CompositeQuery} instances identically to the behaviour in
     * {@link SortedSolrQuerySerializer}, except for when handling disjunctions (ORs). If
     * all of the queries associated with the disjunction are {@link FieldQuery}s, and
     * each are on the same field, then a Solr "LocalParams" terms query is created, which is a
     * more performant disjunction performed directly on the index, by-passing any scoring.
     * This becomes important if one constructs a query with many (e.g., thousands) of disjunctions.
     *
     * @param query the {@link CompositeQuery} which is to be serialized
     * @return the serialized String representation of the supplied {@code query}
     */
    @Override
    public String visit(CompositeQuery query) {
        CompositeQuery.QueryOp operator = query.queryOperator();
        Set<QuickGOQuery> queries = query.queries();
        String operatorText = " " + operator.name() + " ";

        switch (operator) {
            case NOT:
                if (queries.size() == 1) {
                    String singletonQuery = queries.iterator().next().accept(this);
                    return CompositeQuery.QueryOp.NOT + " (" + singletonQuery + ")";
                } else {
                    throw new IllegalStateException("NOT queries can only be applied to 1 query; received " + queries);
                }
            case AND:
                return queries.stream()
                        .map(q -> q.accept(this))
                        .collect(Collectors.joining(operatorText, "(", ")"));
            case OR:
                // assume all queries in this OR are on the same field, and proceed to construct a terms query
                NestedOrSerializer nestedOrSerializer = new NestedOrSerializer();

                StringJoiner termsValuesJoiner = new StringJoiner(",");
                boolean allTransformed = true;
                for (QuickGOQuery q : queries) {
                    TermQueryTransformationResult transformResult = q.accept(nestedOrSerializer);
                    if (transformResult.successful) {
                        termsValuesJoiner.add(transformResult.value);
                    } else {
                        allTransformed = false;
                        break;
                    }
                }

                if (allTransformed) {
                    return buildTermsQuery(nestedOrSerializer.field, termsValuesJoiner.toString());
                } else {
                    // otherwise, re-use the default sorted serializer
                    return queries.stream()
                            .map(q -> q.accept(this))
                            .collect(Collectors.joining(operatorText, "(", ")"));
                }
            default:
                String errorMessage = "UnsortedSolrQuerySerializer.visit(CompositeQuery) " +
                        "cannot process the supplied query: " + query;
                LOGGER.error(errorMessage);
                throw new IllegalStateException(errorMessage);
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

    @Override public String visit(AllNonEmptyFieldQuery query) {
        return sortedQuerySerializer.visit(query);
    }

    @Override public String visit(ContainFieldQuery query) {
        return sortedQuerySerializer.visit(query);
    }
}
