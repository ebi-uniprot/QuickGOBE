package uk.ac.ebi.quickgo.rest.search.results.transformer;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 10/04/17
 * @author Edd
 */
class ResultTransformationRequestsTest {
    @Test
    void canCreateInstance() {
        ResultTransformationRequests resultTransformationRequests = new ResultTransformationRequests();
        assertThat(resultTransformationRequests, is(notNullValue()));
        assertThat(resultTransformationRequests.getRequests(), hasSize(0));
    }

    @Test
    void cannotAddNullResultTransformationRequest() {
        ResultTransformationRequests resultTransformationRequests = new ResultTransformationRequests();
        assertThrows(IllegalArgumentException.class, () -> resultTransformationRequests.addTransformationRequest(null));
    }

    @Test
    void canAddNonNullResultTransformationRequest() {
        ResultTransformationRequests resultTransformationRequests = new ResultTransformationRequests();
        String myId = "myId";

        resultTransformationRequests.addTransformationRequest(new ResultTransformationRequest(myId));

        assertThat(resultTransformationRequests.getRequests(), hasSize(1));
        assertThat(resultTransformationRequests.getRequests().iterator().next().getId(), is(myId));
    }
}