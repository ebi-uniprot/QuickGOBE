package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static uk.ac.ebi.quickgo.rest.search.query.AggregateRequest.DEFAULT_AGGREGATE_LIMIT;
import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.convertToSolrAggregation;
import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.createFacetField;
import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.createFacetType;
import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.createLimitField;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.*;

/**
 * Tests the behaviour of the {@link AggregateToStringConverter} class.
 */
public class AggregateRequestToStringConverterTest {
    private static final AggregateFunction UNIQUE_FUNCTION = AggregateFunction.UNIQUE;
    private static final AggregateFunction COUNT_FUNCTION = AggregateFunction.COUNT;

    private static final String GP_ID_FIELD = "geneProductId";
    private static final String ANN_ID_FIELD = "annId";
    private static final String GO_ID_TYPE = "goId";
    private static final String LIMIT = "limit";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AggregateToStringConverter converter;

    private AggregateRequest aggregate;

    @Before
    public void setUp() throws Exception {
        converter = new AggregateToStringConverter();

        aggregate = new AggregateRequest(GLOBAL_ID);
    }

    @Test
    public void nullFieldInSolrAggregationConversionThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot merge null or empty field");

        convertToSolrAggregation(null, UNIQUE_FUNCTION);
    }

    @Test
    public void emptyFieldInSolrAggregationConversionThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot merge null or empty field");

        convertToSolrAggregation("", UNIQUE_FUNCTION);
    }

    @Test
    public void nullFunctionInSolrAggregationConversionThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot merge null aggregation function");

        convertToSolrAggregation(GP_ID_FIELD, null);
    }

    @Test
    public void fieldAndFunctionAreConvertedSuccessfully() throws Exception {
        String mergedText = convertToSolrAggregation(GP_ID_FIELD, COUNT_FUNCTION);

        assertThat(mergedText, is("\""+COUNT_FUNCTION.getName() + "(" + GP_ID_FIELD + ")\""));
    }

    @Test
    public void nullTypeThrowsExceptionWhenCreatingFacetTypeDeclaration() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot create facet type declaration with null or empty input parameter");

        createFacetType(null);
    }

    @Test
    public void emptyTypeThrowsExceptionWhenCreatingFacetTypeDeclaration() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot create facet type declaration with null or empty input parameter");

        createFacetType("");
    }

    @Test
    public void termFacetTypeCreatingFacetTypeDeclarationSuccessfully() throws Exception {
        assertThat(createFacetType(FACET_TYPE_TERM), is("type:" + FACET_TYPE_TERM));
    }

    @Test
    public void nullFacetFieldThrowsExceptionWhenCreatingFacetFieldDeclaration() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot create facet field declaration with null or empty input parameter");

        createFacetField(null);
    }

    @Test
    public void emptyFacetFieldThrowsExceptionWhenCreatingFacetFieldDeclaration() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot create facet field declaration with null or empty input parameter");

        createFacetField("");
    }

    @Test
    public void gpIdFacetFieldCreatingFacetTypeDeclarationSuccessfully() throws Exception {
        assertThat(createFacetField(GP_ID_FIELD), is("field:" + GP_ID_FIELD));
    }

    @Test
    public void nullAggregateThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("AggregateRequest to convert cannot be null");

        converter.convert(null);
    }

    @Test
    public void aggregateWith1AggregationFieldIsConvertedInto1SolrFunction() throws Exception {
        aggregate.addField(GP_ID_FIELD, UNIQUE_FUNCTION);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(createSolrAggregation(GP_ID_FIELD, UNIQUE_FUNCTION)));
    }

    @Test
    public void aggregateWith2AggregationFieldsIsConvertedInto2SolrFunctions() throws Exception {
        aggregate.addField(GP_ID_FIELD, UNIQUE_FUNCTION);
        aggregate.addField(ANN_ID_FIELD, COUNT_FUNCTION);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(createSolrAggregation(GP_ID_FIELD, UNIQUE_FUNCTION)));
        assertThat(convertedAggregation, containsString(createSolrCOUNTAggregation(ANN_ID_FIELD, COUNT_FUNCTION)));
    }

    @Test
    public void aggregateWith2AggregationFieldsIsConvertedInto2SolrFunctionsSeparatedCorrectly() throws Exception {
        aggregate.addField(GP_ID_FIELD, UNIQUE_FUNCTION);
        aggregate.addField(ANN_ID_FIELD, COUNT_FUNCTION);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(DECLARATION_SEPARATOR));
        assertThat(convertedAggregation, not(startsWith(DECLARATION_SEPARATOR)));
        assertThat(convertedAggregation, not(endsWith(DECLARATION_SEPARATOR)));
    }

    @Test
    public void aggregateWithNestedAggregateIsConvertedIntoSolrSubFacet() throws Exception {
        AggregateRequest goIDAggregate = new AggregateRequest(GO_ID_TYPE);

        aggregate.addNestedAggregate(goIDAggregate);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(aggregatePrefixWithTypeTitle(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createFacetType(FACET_TYPE_TERM)));
        assertThat(convertedAggregation, containsString(createFacetField(GO_ID_TYPE)));
    }

    @Test
    public void aggregateWithNestedAggregateAndLimitIsConvertedIntoSolrSubFacet() {
        int limit = 200;
        AggregateRequest goIDAggregate = new AggregateRequest(GO_ID_TYPE, limit);

        aggregate.addNestedAggregate(goIDAggregate);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(aggregatePrefixWithTypeTitle(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createFacetType(FACET_TYPE_TERM)));
        assertThat(convertedAggregation, containsString(createFacetField(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createLimitField(limit)));
    }

    @Test
    public void aggregateWithNestedAggregateAndNoLimitIsConvertedIntoSolrSubFacet() {
        AggregateRequest goIDAggregate = new AggregateRequest(GO_ID_TYPE);

        aggregate.addNestedAggregate(goIDAggregate);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(aggregatePrefixWithTypeTitle(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createFacetType(FACET_TYPE_TERM)));
        assertThat(convertedAggregation, containsString(createFacetField(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createLimitField(DEFAULT_AGGREGATE_LIMIT)));
    }

    @Test
    public void aggregateWithNestedAggregateWithFunctionsOnFieldsIsConvertedIntoSolrSubFacet() throws Exception {
        AggregateRequest goIDAggregate = new AggregateRequest(GO_ID_TYPE);
        goIDAggregate.addField(GO_ID_TYPE, COUNT_FUNCTION);

        aggregate.addNestedAggregate(goIDAggregate);

        String convertedAggregation = converter.convert(aggregate);

        assertThat(convertedAggregation, containsString(aggregatePrefixWithTypeTitle(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createFacetType(FACET_TYPE_TERM)));
        assertThat(convertedAggregation, containsString(createFacetField(GO_ID_TYPE)));
        assertThat(convertedAggregation, containsString(createSolrCOUNTAggregation(GO_ID_TYPE, COUNT_FUNCTION)));
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