package uk.ac.ebi.quickgo.rest.search.results.transformer;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 10/04/17
 * @author Edd
 */
class ResultTransformationRequestTest {
    @Test
    void nullIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new ResultTransformationRequest(null));
    }

    @Test
    void emptyIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new ResultTransformationRequest(""));
    }

    @Test
    void nonNullOrEmptyidIsValid() {
        ResultTransformationRequest transformationRequest = new ResultTransformationRequest("validIdentifier");
        assertThat(transformationRequest, is(notNullValue()));
    }
}