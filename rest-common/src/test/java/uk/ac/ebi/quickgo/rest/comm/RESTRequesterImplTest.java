package uk.ac.ebi.quickgo.rest.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Stubber;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created 31/05/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class RESTRequesterImplTest {

    private static final String SERVICE_ENDPOINT = "useful/endpoint";
    private RESTRequesterImpl.Builder requesterBuilder;

    @Mock
    private RestTemplate restTemplateMock;
    private HashMap<String, List<String>> requestParameters;

    @Before
    public void setUp() {
        requesterBuilder = RESTRequesterImpl.newBuilder(SERVICE_ENDPOINT);

        requestParameters = new HashMap<>();
        requesterBuilder.setRequestParameters(requestParameters);
    }

    @Test
    public void canCreateNonNullRequester() {
        RESTRequesterImpl requester = this.requesterBuilder.build();
        assertThat(requester, is(notNullValue()));
    }

    @Test
    public void addingRequestParamsResultsInTheseParamsBeingUsed() {
        addRequestParameter("param1", "value1");
        requesterBuilder.setRequestParameters(requestParameters);

        RESTRequesterImpl requester = requesterBuilder.build();

        requester.get(restTemplateMock, FakeDTO.class);
        verify(restTemplateMock, times(1)).getForObject(SERVICE_ENDPOINT, FakeDTO.class, requestParameters);
    }

    @Test
    public void resettingURLResultsInNewURLBeingAccessed() {
        String newURL = "another url";
        requesterBuilder.resetURL(newURL);

        RESTRequesterImpl requester = requesterBuilder.build();

        requester.get(restTemplateMock, FakeDTO.class);
        verify(restTemplateMock, times(1)).getForObject(newURL, FakeDTO.class, requestParameters);
    }

    @Test
    public void showSuccessfulURLGet() throws ExecutionException, InterruptedException {
        String dtoValue = "value";
        delayAnswer(1000, new FakeDTO(dtoValue)).when(restTemplateMock)
                .getForObject(SERVICE_ENDPOINT, FakeDTO.class, requestParameters);

        RESTRequesterImpl requester = requesterBuilder.build();

        CompletableFuture<FakeDTO> completableFuture = requester.get(restTemplateMock, FakeDTO.class);

        FakeDTO fakeyFakey = completableFuture.get();
        assertThat(fakeyFakey.value, is(dtoValue));
    }

    @Test
    public void showHandlingOfAFailedURLGet() throws ExecutionException, InterruptedException {

        doThrow(new RestClientException("Didn't work", new Exception("For some reason"))).when(restTemplateMock)
                .getForObject(SERVICE_ENDPOINT, FakeDTO.class, requestParameters);

        RESTRequesterImpl requester = requesterBuilder.build();

        String failed = "Failed";
        CompletableFuture<FakeDTO> completableFuture = requester.get(restTemplateMock, FakeDTO.class)
                .exceptionally(ex -> new FakeDTO(failed));

        FakeDTO fakeyFakey = completableFuture.get();
        assertThat(fakeyFakey.value, is(failed));
    }

    private static Stubber delayAnswer(int delay, Object toReturn) {
        ReturnWithDelay answer = new ReturnWithDelay(toReturn);
        answer.setDelay(delay);
        return doAnswer(answer);
    }

    private void addRequestParameter(String param, String value) {
        if (!requestParameters.containsKey(param)) {
            requestParameters.put(param, new ArrayList<>());
        }
        requestParameters.get(param).add(value);
    }

    static class FakeDTO {
        String value;

        FakeDTO(String value) {
            this.value = value;
        }
    }

    static class ReturnWithDelay extends Returns {

        private long delay;

        ReturnWithDelay(Object value) {
            super(value);
        }

        public Object answer(InvocationOnMock invocation) throws Throwable {
            Thread.sleep(delay);
            return super.answer(invocation);
        }

        void setDelay(int delay) {
            this.delay = delay;
        }
    }
}