package uk.ac.ebi.quickgo.rest.search.request;

import java.util.List;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created 08/06/16
 * @author Edd
 */
public class SimpleRequestTest {
    @Test
    public void nonNullFieldAndNonNullOrEmptyValuesProducesValidRequest() {
        String field = "field";
        List<String> values = asList("value1", "value2");

        SimpleRequest request = new SimpleRequest(field, values);
        assertThat(request.getSignature(), is(field));
        assertThat(request.getValues(), is(values));
    }

    @Test
    public void nonNullFieldProducesValidRequest() {
        String field = "field";
        List<String> values = emptyList();

        SimpleRequest request = new SimpleRequest(field);
        assertThat(request.getSignature(), is(field));
        assertThat(request.getValues(), is(values));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFieldAndValidListProducesConstructorException() {
        new SimpleRequest(null, singletonList("value1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFieldProducesConstructorException() {
        new SimpleRequest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullValuesProducesConstructorException() {
        new SimpleRequest("field", null);
    }
}