package uk.ac.ebi.quickgo.rest.search.results.transformer;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created 10/04/17
 * @author Edd
 */
public class ResultTransformationRequestTest {
    @Test(expected = IllegalArgumentException.class)
    public void nullIdIsInvalid() {
        new ResultTransformationRequest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyIdIsInvalid() {
        new ResultTransformationRequest("");
    }

    @Test
    public void nonNullOrEmptyidIsValid() {
        ResultTransformationRequest transformationRequest = new ResultTransformationRequest("validIdentifier");
        assertThat(transformationRequest, is(notNullValue()));
    }
}