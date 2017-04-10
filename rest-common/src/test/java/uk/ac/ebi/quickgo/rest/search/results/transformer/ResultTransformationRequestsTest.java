package uk.ac.ebi.quickgo.rest.search.results.transformer;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created 10/04/17
 * @author Edd
 */
public class ResultTransformationRequestsTest {
    @Test
    public void canCreateInstance() {
        ResultTransformationRequests resultTransformationRequests = new ResultTransformationRequests();
        assertThat(resultTransformationRequests, is(notNullValue()));
        assertThat(resultTransformationRequests.getRequests(), hasSize(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddNullResultTransformationRequest() {
        ResultTransformationRequests resultTransformationRequests = new ResultTransformationRequests();
        resultTransformationRequests.addTransformationRequest(null);
    }

    @Test
    public void canAddNonNullResultTransformationRequest() {
        ResultTransformationRequests resultTransformationRequests = new ResultTransformationRequests();
        String myId = "myId";

        resultTransformationRequests.addTransformationRequest(new ResultTransformationRequest(myId));

        assertThat(resultTransformationRequests.getRequests(), hasSize(1));
        assertThat(resultTransformationRequests.getRequests().iterator().next().getId(), is(myId));
    }
}