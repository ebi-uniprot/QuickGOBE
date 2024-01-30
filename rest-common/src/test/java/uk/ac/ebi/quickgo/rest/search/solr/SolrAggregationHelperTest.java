package uk.ac.ebi.quickgo.rest.search.solr;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.ac.ebi.quickgo.rest.search.solr.AggregateToStringConverter.NUM_BUCKETS;
import static uk.ac.ebi.quickgo.rest.search.solr.SolrAggregationHelper.*;

/**
 * Tests the behaviour of the {@link SolrAggregationHelper} class.
 */
class SolrAggregationHelperTest {
    private static final AggregateFunction COUNT_FUNCTION = AggregateFunction.COUNT;

    private static final String GP_ID_FIELD = "geneProductId";

    @Test
    void nullAggregateFunctionThrowsExceptionWhenCreatingAggregateFieldTitle()  {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> aggregateFieldTitle(null, GP_ID_FIELD));
        assertTrue(exception.getMessage().contains("Cannot create aggregate field title with null aggregate function"));
    }

    @Test
    void nullFieldThrowsExceptionWhenCreatingAggregateFieldTitle()  {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> aggregateFieldTitle(COUNT_FUNCTION, null));
        assertTrue(exception.getMessage().contains("Cannot create aggregate field title with null field"));
    }

    @Test
    void emptyFieldThrowsExceptionWhenCreatingAggregateFieldTitle()  {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> aggregateFieldTitle(COUNT_FUNCTION, ""));
        assertTrue(exception.getMessage().contains("Cannot create aggregate field title with null field"));
    }

    @Test
    void validFunctionAndFieldCreateAggregateFieldTitle()  {
        String aggTitleField = aggregateFieldTitle(COUNT_FUNCTION, GP_ID_FIELD);

        assertThat(aggTitleField, is(COUNT_FUNCTION.getName() + AGG_NAME_SEPARATOR + GP_ID_FIELD));
    }

    @Test
    void nullTypeThrowsExceptionWhenCreatingAggregateTypeTitle()  {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> aggregatePrefixWithTypeTitle(null));
        assertTrue(exception.getMessage().contains("Cannot create aggregate type title with null or empty type"));
    }

    @Test
    void emptyTypeThrowsExceptionWhenCreatingAggregateTypeTitle()  {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> aggregatePrefixWithTypeTitle(""));
        assertTrue(exception.getMessage().contains("Cannot create aggregate type title with null or empty type"));
    }

    @Test
    void populatedTypeCreatesAggregateTypeTitle()  {
        String typeTitle = aggregatePrefixWithTypeTitle(GP_ID_FIELD);

        assertThat(typeTitle, is(AGG_TYPE_PREFIX + AGG_NAME_SEPARATOR + GP_ID_FIELD));
    }

    @Test
    void nullPrefixedFieldThrowsExceptionWhenExtractingPrefix()  {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> fieldPrefixExtractor(null));
        assertTrue(exception.getMessage().contains("Cannot extract prefix from null input"));
    }

    @Test
    void fieldWithNoPrefixReturnsAnEmptyPrefixWhenExtractingPrefix()  {
        String prefix = fieldPrefixExtractor(GP_ID_FIELD);

        assertThat(prefix, isEmptyString());
    }

    @Test
    void fieldWithCountPrefixReturnsCountPrefixWhenExtractingPrefix()  {
        String prefix = fieldPrefixExtractor(aggregateFieldTitle(COUNT_FUNCTION, GP_ID_FIELD));

        assertThat(prefix, is(COUNT_FUNCTION.getName()));
    }

    @Test
    void nullPrefixedFieldThrowsExceptionWhenExtractingField()  {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> fieldNameExtractor(null));
        assertTrue(exception.getMessage().contains("Cannot extract field from null input"));
    }

    @Test
    void emptyPrefixedFieldThrowsExceptionWhenExtractingField()  {
        String fieldName = fieldNameExtractor("");

        assertThat(fieldName, isEmptyString());
    }

    @Test
    void fieldWithCountPrefixReturnsFieldWhenExtractingField()  {
        String field = fieldNameExtractor(aggregateFieldTitle(COUNT_FUNCTION, GP_ID_FIELD));

        assertThat(field, is(GP_ID_FIELD));
    }

    @Test
    void testForNumBucketsReturnsTrue(){
        assertThat(isDistinctValueCount(NUM_BUCKETS), is(true));
    }

    @Test
    void testForNumBucketsReturnsFalse(){
        assertThat(isDistinctValueCount("bucketHead"), is(false));
    }
}
