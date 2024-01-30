package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.ac.ebi.quickgo.rest.search.query.AggregateRequest.DEFAULT_AGGREGATE_LIMIT;
import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.NUM_BUCKETS_TRUE;
import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.convertToSolrAggregation;
import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.createFacetField;
import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.createFacetType;
import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.createLimitField;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.*;

/**
 * Tests the behaviour of the {@link AggregateToStringConverter} class.
 */
class AggregateRequestToStringConverterTest {
    private static final AggregateFunction UNIQUE_FUNCTION = AggregateFunction.UNIQUE;
    private static final AggregateFunction COUNT_FUNCTION = AggregateFunction.COUNT;

    private static final String GP_ID_FIELD = "geneProductId";
    private static final String ANN_ID_FIELD = "annId";
    private static final String GO_ID_TYPE = "goId";

    private AggregateToStringConverter converter;

    private AggregateRequest aggregate;

    @BeforeEach
    void setUp() {
        converter = new AggregateToStringConverter();

        aggregate = new AggregateRequest(GLOBAL_ID);
    }

    @Test
    void nullFieldInSolrAggregationConversionThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> convertToSolrAggregation(null, UNIQUE_FUNCTION));
        assertTrue(exception.getMessage().contains("Cannot merge null or empty field"));
    }

    @Test
    void emptyFieldInSolrAggregationConversionThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> convertToSolrAggregation("", UNIQUE_FUNCTION));
        assertTrue(exception.getMessage().contains("Cannot merge null or empty field"));
    }

    @Test
    void nullFunctionInSolrAggregationConversionThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> convertToSolrAggregation(GP_ID_FIELD, null));
        assertTrue(exception.getMessage().contains("Cannot merge null aggregation function"));
    }

    @Test
    void fieldAndFunctionAreConvertedSuccessfully() {
        String mergedText = convertToSolrAggregation(GP_ID_FIELD, COUNT_FUNCTION);

        assertThat(mergedText, is("\""+COUNT_FUNCTION.getName() + "(" + GP_ID_FIELD + ")\""));
    }

    @Test
    void nullTypeThrowsExceptionWhenCreatingFacetTypeDeclaration() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> createFacetType(null));
        assertTrue(exception.getMessage().contains("Cannot create facet type declaration with null or empty input parameter"));
    }

    @Test
    void emptyTypeThrowsExceptionWhenCreatingFacetTypeDeclaration() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> createFacetType(""));
        assertTrue(exception.getMessage().contains("Cannot create facet type declaration with null or empty input parameter"));
    }

    @Test
    void termFacetTypeCreatingFacetTypeDeclarationSuccessfully() {
        assertThat(createFacetType(FACET_TYPE_TERM), is("type:" + FACET_TYPE_TERM));
    }

    @Test
    void nullFacetFieldThrowsExceptionWhenCreatingFacetFieldDeclaration() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> createFacetField(null));
        assertTrue(exception.getMessage().contains("Cannot create facet field declaration with null or empty input parameter"));
    }

    @Test
    void emptyFacetFieldThrowsExceptionWhenCreatingFacetFieldDeclaration() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> createFacetField(""));
        assertTrue(exception.getMessage().contains("Cannot create facet field declaration with null or empty input parameter"));
    }

    @Test
    void gpIdFacetFieldCreatingFacetTypeDeclarationSuccessfully() {
        assertThat(createFacetField(GP_ID_FIELD), is("field:" + GP_ID_FIELD));
    }

    @Test
    void nullAggregateThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> converter.convert(null));
        assertTrue(exception.getMessage().contains("AggregateRequest to convert cannot be null"));
    }

    @Test
    void aggregateWith1AggregationFieldIsConvertedInto1SolrFunction() {
        aggregate.addField(GP_ID_FIELD, UNIQUE_FUNCTION);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(createSolrAggregation(GP_ID_FIELD, UNIQUE_FUNCTION)));
    }

    @Test
    void aggregateWith2AggregationFieldsIsConvertedInto2SolrFunctions() {
        aggregate.addField(GP_ID_FIELD, UNIQUE_FUNCTION);
        aggregate.addField(ANN_ID_FIELD, COUNT_FUNCTION);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(createSolrAggregation(GP_ID_FIELD, UNIQUE_FUNCTION)));
        assertThat(convertedAggregation, containsString(createSolrCOUNTAggregation(ANN_ID_FIELD, COUNT_FUNCTION)));
    }

    @Test
    void aggregateWith2AggregationFieldsIsConvertedInto2SolrFunctionsSeparatedCorrectly() {
        aggregate.addField(GP_ID_FIELD, UNIQUE_FUNCTION);
        aggregate.addField(ANN_ID_FIELD, COUNT_FUNCTION);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(DECLARATION_SEPARATOR));
        assertThat(convertedAggregation, not(startsWith(DECLARATION_SEPARATOR)));
        assertThat(convertedAggregation, not(endsWith(DECLARATION_SEPARATOR)));
    }

    @Test
    void aggregateWithNestedAggregateIsConvertedIntoSolrSubFacet() {
        AggregateRequest goIDAggregate = new AggregateRequest(GO_ID_TYPE);

        aggregate.addNestedAggregate(goIDAggregate);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(aggregatePrefixWithTypeTitle(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createFacetType(FACET_TYPE_TERM)));
        assertThat(convertedAggregation, containsString(createFacetField(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(NUM_BUCKETS_TRUE));
    }

    @Test
    void aggregateWithNestedAggregateAndLimitIsConvertedIntoSolrSubFacet() {
        int limit = 200;
        AggregateRequest goIDAggregate = new AggregateRequest(GO_ID_TYPE, limit);

        aggregate.addNestedAggregate(goIDAggregate);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(aggregatePrefixWithTypeTitle(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createFacetType(FACET_TYPE_TERM)));
        assertThat(convertedAggregation, containsString(createFacetField(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createLimitField(limit)));
        assertThat(convertedAggregation, containsString(NUM_BUCKETS_TRUE));
    }

    @Test
    void aggregateWithNestedAggregateAndNoLimitIsConvertedIntoSolrSubFacet() {
        AggregateRequest goIDAggregate = new AggregateRequest(GO_ID_TYPE);

        aggregate.addNestedAggregate(goIDAggregate);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(aggregatePrefixWithTypeTitle(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createFacetType(FACET_TYPE_TERM)));
        assertThat(convertedAggregation, containsString(createFacetField(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createLimitField(DEFAULT_AGGREGATE_LIMIT)));
        assertThat(convertedAggregation, containsString(NUM_BUCKETS_TRUE));
    }

    @Test
    void aggregateWithNestedAggregateWithFunctionsOnFieldsIsConvertedIntoSolrSubFacet() {
        AggregateRequest goIDAggregate = new AggregateRequest(GO_ID_TYPE);
        goIDAggregate.addField(GO_ID_TYPE, COUNT_FUNCTION);

        aggregate.addNestedAggregate(goIDAggregate);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(aggregatePrefixWithTypeTitle(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createFacetType(FACET_TYPE_TERM)));
        assertThat(convertedAggregation, containsString(createFacetField(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createSolrCOUNTAggregation(GO_ID_TYPE, COUNT_FUNCTION)));
        assertThat(convertedAggregation, containsString(NUM_BUCKETS_TRUE));
    }

    private String createSolrAggregation(String field, AggregateFunction function) {
        return aggregateFieldTitle(function, field)
                + NAME_TO_VALUE_SEPARATOR
                + convertToSolrAggregation(field, function);
    }

    /**
     * Solr does not have a simple count function. To circumvent this issue, we employ the SUM function, and apply it
     * to the constant 1. This is equivalent to counting rows with a given field.
     */
    private String createSolrCOUNTAggregation(String field, AggregateFunction function) {
        return aggregateFieldTitle(function, field)
                + NAME_TO_VALUE_SEPARATOR
                + convertToSolrAggregation("1", AggregateFunction.SUM);
    }
}
