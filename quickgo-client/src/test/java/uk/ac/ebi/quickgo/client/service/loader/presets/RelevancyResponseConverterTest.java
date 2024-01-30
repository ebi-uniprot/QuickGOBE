package uk.ac.ebi.quickgo.client.service.loader.presets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 23/09/16
 * @author Edd
 */
class RelevancyResponseConverterTest {
    private RelevancyResponseConverter responseConverter;

    @BeforeEach
    void setUp() {
        responseConverter = new RelevancyResponseConverter();
    }

    @Test
    void transformsValidAssignedByResponse() {
        RelevancyResponseType response = new RelevancyResponseType();
        response.terms = new RelevancyResponseType.Terms();
        response.terms.relevancies = new ArrayList<>();

        String term1 = "term1";
        String term2 = "term2";
        insertRelevancy(response, term1, "count1");
        insertRelevancy(response, term2, "count2");

        ConvertedFilter<List<String>> convertedFilter = responseConverter.transform(response);
        assertThat(convertedFilter.getConvertedValue(), contains(term1, term2));
    }

    @Test
    void nullResponseCausesException() {
        assertThrows(IllegalArgumentException.class, () -> responseConverter.transform(null));
    }

    @Test
    void nullTermsIsTransformedGracefully() {
        RelevancyResponseType response = new RelevancyResponseType();
        assertThat(response.terms, is(nullValue()));

        ConvertedFilter<List<String>> convertedFilter = responseConverter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(empty()));
    }

    @Test
    void nullAssignedByIsTransformedGracefully() {
        RelevancyResponseType response = new RelevancyResponseType();
        response.terms = new RelevancyResponseType.Terms();
        assertThat(response.terms.relevancies, is(nullValue()));

        ConvertedFilter<List<String>> convertedFilter = responseConverter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(empty()));
    }

    private static void insertRelevancy(RelevancyResponseType response, String term, String count) {
        response.terms.relevancies.add(term);
        response.terms.relevancies.add(count);
    }

}