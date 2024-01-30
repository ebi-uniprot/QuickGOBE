package uk.ac.ebi.quickgo.client.service.loader.presets;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * <p>The class under test typically will be automatically populated by data-binding libraries
 * using the result from REST calls. This test class is to ensure the class can be populated
 * as expected.
 *
 * <p>Specifically, the tests should ensure the class can store any $.terms.* values
 * from a REST endpoint (with responses of this form), but make sure only the first one processed
 * is subsequently available.
 *
 * Created 23/09/16
 * @author Edd
 */
class RelevancyResponseTypeTest {
    @Test
    void canSetAndRetrieveRelevanciesFromResponseWith0Elements() {
        RelevancyResponseType responseType = new RelevancyResponseType();
        responseType.terms = new RelevancyResponseType.Terms();

        responseType.terms.set(null, null);

        assertThat(responseType.terms.termName, is(nullValue()));
        assertThat(responseType.terms.relevancies, is(nullValue()));
    }

    @Test
    void canSetAndRetrieveRelevanciesFromResponseWith1Element() {
        RelevancyResponseType responseType = new RelevancyResponseType();
        responseType.terms = new RelevancyResponseType.Terms();
        String termName = "field1";
        List<String> relevancies = asList("fieldValue1", "1000", "fieldValue2", "100");

        responseType.terms.set(termName, relevancies);

        assertThat(responseType.terms.termName, is(termName));
        assertThat(responseType.terms.relevancies, is(relevancies));
    }

    @Test
    void canSetAndRetrieveRelevanciesFromResponseWith2Elements() {
        RelevancyResponseType responseType = new RelevancyResponseType();
        responseType.terms = new RelevancyResponseType.Terms();
        String termName = "field1";
        List<String> relevancies = asList("fieldValue1", "1000", "fieldValue2", "100");

        responseType.terms.set(termName, relevancies);
        responseType.terms.set("field2", asList("anotherFieldValue1", "2222", "anotherFieldValue2", "333"));

        assertThat(responseType.terms.termName, is(termName));
        assertThat(responseType.terms.relevancies, is(relevancies));
    }

}