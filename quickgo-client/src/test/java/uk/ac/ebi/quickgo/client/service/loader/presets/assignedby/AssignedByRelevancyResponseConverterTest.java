package uk.ac.ebi.quickgo.client.service.loader.presets.assignedby;

import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;

/**
 * Created 06/09/16
 * @author Edd
 */
public class AssignedByRelevancyResponseConverterTest {

    private AssignedByRelevancyResponseConverter responseConverter;

    @Before
    public void setUp() {
        responseConverter = new AssignedByRelevancyResponseConverter();
    }

    @Test
    public void transformsValidAssignedByResponse() {
        AssignedByRelevancyResponseType response = new AssignedByRelevancyResponseType();
        response.terms = new AssignedByRelevancyResponseType.Terms();
        response.terms.assignedBy = new ArrayList<>();

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
        AssignedByRelevancyResponseType response = new AssignedByRelevancyResponseType();
        assertThat(response.terms, is(nullValue()));

        ConvertedFilter<List<String>> convertedFilter = responseConverter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(empty()));
    }

    @Test
    public void nullAssignedByIsTransformedGracefully() {
        AssignedByRelevancyResponseType response = new AssignedByRelevancyResponseType();
        response.terms = new AssignedByRelevancyResponseType.Terms();
        assertThat(response.terms.assignedBy, is(nullValue()));

        ConvertedFilter<List<String>> convertedFilter = responseConverter.transform(response);

        assertThat(convertedFilter.getConvertedValue(), is(empty()));
    }

    private static void insertRelevancy(AssignedByRelevancyResponseType response, String term, String count) {
        response.terms.assignedBy.add(term);
        response.terms.assignedBy.add(count);
    }
}