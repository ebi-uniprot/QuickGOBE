package uk.ac.ebi.quickgo.client.service.loader.presets;

import uk.ac.ebi.quickgo.rest.comm.ResponseType;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;

/**
 * Defines tests to ensure a given response converter ({@link FilterConverter} instance)
 * transforms correctly a response ({@link ResponseType}), representing the response from a REST call
 * encapsulating information about a top N list of items, into a raw {@link List} of {@link String}s.
 * Each item in the list is the identifier of the relevant terms, and its order indicates its importance.
 *
 * Created 22/09/16
 * @author Edd
 */
public abstract class AbstractRelevancyResponseConverterTest<
        R extends ResponseType,
        C extends FilterConverter<R, List<String>>> {

    private C responseConverter;

    @Before
    public void setUp() {
        responseConverter = createResponseConverter();
    }

    @Test
    public void transformsValidAssignedByResponse() {
        R response = createResponseType(new ArrayList<>());

        String term1 = "term1";
        String term2 = "term2";
        insertRelevancy(response, term1, "count1");
        insertRelevancy(response, term2, "count2");

        ConvertedFilter<List<String>> convertedFilter = responseConverter.transform(response);
        assertThat(convertedFilter.getConvertedValue(), contains(term1, term2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullResponseCausesException() {
        responseConverter.transform(null);
    }

    @Test
    public void nullTermsIsTransformedGracefully() {
        R response = createResponseType(null);

        ConvertedFilter<List<String>> convertedFilter = responseConverter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(empty()));
    }

    @Test
    public void nullAssignedByIsTransformedGracefully() {
        R response = createResponseTypeWithNullTerms();

        ConvertedFilter<List<String>> convertedFilter = responseConverter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(empty()));
    }

    protected abstract R createResponseType(List<String> terms);

    protected abstract R createResponseTypeWithNullTerms();

    protected abstract C createResponseConverter();

    protected abstract void insertRelevancy(R response, String term, String count);
}
