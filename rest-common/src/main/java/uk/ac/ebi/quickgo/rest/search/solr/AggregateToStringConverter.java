package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;
import uk.ac.ebi.quickgo.rest.search.query.AggregateFunctionRequest;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.*;

/**
 * Simple implementation of the {@link AggregateConverter}, that converts an {@link AggregateRequest} into a {@link String}
 * object.
 * <p/>
 * Note: At the time of writing this converter the SolrJ API did not have direct support for the JSON facet API. So
 * the conversion had to be done at the simplest level possible, i.e. create a String that is then fed into SolrJ.
 *
 * @author Ricardo Antunes
 */
public class AggregateToStringConverter implements AggregateConverter<String> {
    //Solr syntax formatters
    private static final String AGG_FUNCTION_FORMAT = "\"%s(%s)\"";
    private static final String AGG_BLOCK_FORMAT = "{%s}";
    private static final String FACET_TYPE_FORMAT = "type" + NAME_TO_VALUE_SEPARATOR + "%s";
    private static final String FACET_FIELD_FORMAT = "field" + NAME_TO_VALUE_SEPARATOR + "%s";

    @Override public String convert(AggregateRequest aggregate) {
        Preconditions.checkArgument(aggregate != null, "AggregateRequest to convert cannot be null");

        String aggFieldsText = convertAggregationFields(aggregate.getAggregateFunctionRequests().stream());
        String nestedAggText = convertNestedAggregates(aggregate.getNestedAggregateRequests().stream());

        String finalText = Stream.of(aggFieldsText, nestedAggText)
                .filter(text -> !text.isEmpty())
                .collect(joining(DECLARATION_SEPARATOR));

        return encloseBlock(finalText);
    }

    static String convertToSolrAggregation(String field, AggregateFunction function) {
        Preconditions.checkArgument(field != null && !field.trim().isEmpty(), "Cannot merge null or empty field");
        Preconditions.checkArgument(function != null, "Cannot merge null aggregation function");

        return String.format(AGG_FUNCTION_FORMAT, function.getName(), field);
    }

    static String createFacetType(String type) {
        Preconditions.checkArgument(type != null && !type.trim().isEmpty(),
                "Cannot create facet type declaration with null or empty input parameter");
        switch (type) {
            case FACET_TYPE_TERM:
                return String.format(FACET_TYPE_FORMAT, FACET_TYPE_TERM);
            default:
                throw new IllegalArgumentException("Provided facet type is not valid: " + type);
        }
    }

    static String createFacetField(String field) {
        Preconditions.checkArgument(field != null && !field.trim().isEmpty(),
                "Cannot create facet field declaration with null or empty input parameter");
        return String.format(FACET_FIELD_FORMAT, field);
    }

    /**
     * Converts a stream of {@link AggregateFunctionRequest} stored within an {@link AggregateRequest} into a format Solr can understand.
     *
     * @param fields aggregate fields to convert
     * @return a Solr String representation of the fields
     */
    private String convertAggregationFields(Stream<AggregateFunctionRequest> fields) {
        return fields.map(this::createSolrAggregateFieldDeclaration)
                .collect(joining(DECLARATION_SEPARATOR));
    }

    /**
     * Given an {@link AggregateFunctionRequest} convert it into a solr aggregation declaration.
     * <p/>
     * For example:
     * <pre>
     *    AggregateFunctionRequest: field=myField; aggregateFunction=COUNT;
     *    is converted into:
     *    count_myField:count(myField)
     * </pre>
     * @param aggregateFunctionRequest the field to convert
     * @return a Solr representation of the aggregation field declaration
     */
    private String createSolrAggregateFieldDeclaration(AggregateFunctionRequest aggregateFunctionRequest) {
        return aggregateFieldTitle(aggregateFunctionRequest.getFunction(), aggregateFunctionRequest.getField())
                + NAME_TO_VALUE_SEPARATOR
                + convertAggregateFieldToText(aggregateFunctionRequest);
    }

    /**
     * Converts an {@link AggregateFunctionRequest} into an aggregation statement that Solr understands.
     *
     * @param aggregateFunctionRequest the aggregation field to convert
     * @return a Solr aggregation statement
     */
    private String convertAggregateFieldToText(AggregateFunctionRequest aggregateFunctionRequest) {
        return convertToSolrAggregation(aggregateFunctionRequest.getField(), aggregateFunctionRequest.getFunction());
    }

    /**
     * Converts nested aggregates into Solr sub facets.
     *
     * @param nestedAggregates the nested aggregates to convert
     * @return a Solr subfacet statement
     */
    private String convertNestedAggregates(Stream<AggregateRequest> nestedAggregates) {
        return nestedAggregates.map(this::createSubFacet)
                .collect(joining(DECLARATION_SEPARATOR));
    }

    /**
     * Given a nested {@link AggregateRequest} convert it into a Solr subfacet declaration.
     * <p/>
     * For example:
     * <pre>
     *    AggregateRequest: field=myField;
     *       AggregateFunctionRequest: field=myField2; aggregateFunction=COUNT;
     *    is converted into:
     *    agg_myField: {
     *       type:terms
     *       field:myField
     *       facet: {
     *          count_myField2:count(myField2)
     *       }
     *    }
     * </pre>
     *
     * @param nestedAggregate the nested aggregate to convert
     * @return a String representation that Solr understands
     */
    private String createSubFacet(AggregateRequest nestedAggregate) {

        Collection<AggregateFunctionRequest> fields = nestedAggregate.getAggregateFunctionRequests();

        String facetBlock = "";

        if (!fields.isEmpty()) {
            facetBlock = FACET_MARKER + NAME_TO_VALUE_SEPARATOR + convert(nestedAggregate);
        }

        String subFacet = createFacetType(FACET_TYPE_TERM) + DECLARATION_SEPARATOR
                + createFacetField(nestedAggregate.getName()) + DECLARATION_SEPARATOR
                + facetBlock;

        return aggregatePrefixWithTypeTitle(nestedAggregate.getName())
                + NAME_TO_VALUE_SEPARATOR
                + encloseBlock(subFacet);
    }

    private static String encloseBlock(String blockContent) {
        return String.format(AGG_BLOCK_FORMAT, blockContent);
    }
}