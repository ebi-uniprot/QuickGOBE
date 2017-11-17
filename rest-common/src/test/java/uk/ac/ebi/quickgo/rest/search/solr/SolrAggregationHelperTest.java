package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.*;
import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.NUM_BUCKETS;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.*;

/**
 * Tests the behaviour of the {@link SolrAggregationHelper} class.
 */
public class SolrAggregationHelperTest {
    private static final AggregateFunction COUNT_FUNCTION = AggregateFunction.COUNT;

    private static final String GP_ID_FIELD = "geneProductId";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullAggregateFunctionThrowsExceptionWhenCreatingAggregateFieldTitle() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot create aggregate field title with null aggregate function");

        aggregateFieldTitle(null, GP_ID_FIELD);
    }

    @Test
    public void nullFieldThrowsExceptionWhenCreatingAggregateFieldTitle() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot create aggregate field title with null field");

        aggregateFieldTitle(COUNT_FUNCTION, null);
    }

    @Test
    public void emptyFieldThrowsExceptionWhenCreatingAggregateFieldTitle() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot create aggregate field title with null field");

        aggregateFieldTitle(COUNT_FUNCTION, "");
    }

    @Test
    public void validFunctionAndFieldCreateAggregateFieldTitle() throws Exception {
        String aggTitleField = aggregateFieldTitle(COUNT_FUNCTION, GP_ID_FIELD);

        assertThat(aggTitleField, is(COUNT_FUNCTION.getName() + AGG_NAME_SEPARATOR + GP_ID_FIELD));
    }

    @Test
    public void nullTypeThrowsExceptionWhenCreatingAggregateTypeTitle() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot create aggregate type title with null or empty type");

        aggregatePrefixWithTypeTitle(null);
    }

    @Test
    public void emptyTypeThrowsExceptionWhenCreatingAggregateTypeTitle() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot create aggregate type title with null or empty type");

        aggregatePrefixWithTypeTitle("");
    }

    @Test
    public void populatedTypeCreatesAggregateTypeTitle() throws Exception {
        String typeTitle = aggregatePrefixWithTypeTitle(GP_ID_FIELD);

        assertThat(typeTitle, is(AGG_TYPE_PREFIX + AGG_NAME_SEPARATOR + GP_ID_FIELD));
    }

    @Test
    public void nullPrefixedFieldThrowsExceptionWhenExtractingPrefix() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot extract prefix from null input");

        fieldPrefixExtractor(null);
    }

    @Test
    public void fieldWithNoPrefixReturnsAnEmptyPrefixWhenExtractingPrefix() throws Exception {
        String prefix = fieldPrefixExtractor(GP_ID_FIELD);

        assertThat(prefix, isEmptyString());
    }

    @Test
    public void fieldWithCountPrefixReturnsAnCountPrefixWhenExtractingPrefix() throws Exception {
        String prefix = fieldPrefixExtractor(aggregateFieldTitle(COUNT_FUNCTION, GP_ID_FIELD));

        assertThat(prefix, is(COUNT_FUNCTION.getName()));
    }

    @Test
    public void nullPrefixedFieldThrowsExceptionWhenExtractingField() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot extract field from null input");

        fieldNameExtractor(null);
    }

    @Test
    public void emptyPrefixedFieldThrowsExceptionWhenExtractingField() throws Exception {
        String fieldName = fieldNameExtractor("");

        assertThat(fieldName, isEmptyString());
    }

    @Test
    public void fieldWithCountPrefixReturnsAnTheFieldWhenExtractingField() throws Exception {
        String field = fieldNameExtractor(aggregateFieldTitle(COUNT_FUNCTION, GP_ID_FIELD));

        assertThat(field, is(GP_ID_FIELD));
    }

    @Test
    public void testForNumBucketsReturnsTrue(){
        assertThat(distinctValueCountTester(NUM_BUCKETS), is(true)) ;
    }

    @Test
    public void testForNumBucketsReturnsFalse(){
        assertThat(distinctValueCountTester("bucketHead"), is(false)) ;
    }
}
