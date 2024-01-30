package uk.ac.ebi.quickgo.rest.comm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Stubber;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Created 31/05/16
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class RESTRequesterImplTest {

    private static final String SERVICE_ENDPOINT = "useful/endpoint";
    private RESTRequesterImpl.Builder requesterBuilder;
    private static final String EMPTY_BACKUP_URL = "";

    @Mock
    private RestTemplate restTemplateMock;
    private Map<String, String> requestParameters;

    @BeforeEach
    void setUp() {
        requesterBuilder = RESTRequesterImpl.newBuilder(restTemplateMock, SERVICE_ENDPOINT, EMPTY_BACKUP_URL);

        requestParameters = new HashMap<>();
    }

    @Test
    void canCreateNonNullRequester() {
        RESTRequesterImpl requester = this.requesterBuilder.build();
        assertThat(requester, is(notNullValue()));
    }

    @Test
    void creatingWithNullRestOperationsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> RESTRequesterImpl.newBuilder(null, "value", EMPTY_BACKUP_URL));
    }

    @Test
    void creatingWithNullURLThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> RESTRequesterImpl.newBuilder(restTemplateMock, null, EMPTY_BACKUP_URL));
    }

    @Test
    void creatingWithEmptyURLThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> RESTRequesterImpl.newBuilder(restTemplateMock, "", EMPTY_BACKUP_URL));
    }

    @Test
    void resettingURLWithNullValueThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> requesterBuilder.resetURL(null));
    }

    @Test
    void resettingURLWithEmptyValueThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> requesterBuilder.resetURL(""));
    }

    @Test
    void addingRequestParamWithNullNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> requesterBuilder.addRequestParameter(null, "value"));
    }

    @Test
    void addingRequestParamWithEmptyNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> requesterBuilder.addRequestParameter("", "value"));
    }

    @Test
    void addingRequestParamWithNullValueThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> requesterBuilder.addRequestParameter("name", null));
    }

    @Test
    void addingRequestParamsResultsInTheseParamsBeingUsed() throws ExecutionException, InterruptedException {
        String param1 = "param1";
        String value1 = "value1";
        String url = SERVICE_ENDPOINT + "/something-else";

        RestTemplate restTemplateMock = mock(RestTemplate.class);
        RESTRequesterImpl.Builder requesterBuilder = RESTRequesterImpl.newBuilder(restTemplateMock, url, EMPTY_BACKUP_URL);
        Map<String, String> requestParameters = new HashMap<>();

        requesterBuilder.addRequestParameter(param1, value1);
        requestParameters.put(param1, value1);

        RESTRequesterImpl requester = requesterBuilder.build();

        CompletableFuture<FakeDTO> completableFuture = requester.get(url, restTemplateMock, FakeDTO.class);
        completableFuture.get();

        verify(restTemplateMock, times(1)).getForObject(url, FakeDTO.class, requestParameters);
    }

    @Test
    void resettingURLResultsInNewURLBeingAccessed() throws ExecutionException, InterruptedException {
        String newURL = "new url";
        String dtoValue = "value";
        when(restTemplateMock.getForObject(newURL, FakeDTO.class, requestParameters))
                .thenReturn(new FakeDTO(dtoValue));

        requesterBuilder.resetURL(newURL);

        RESTRequesterImpl requester = requesterBuilder.build();

        CompletableFuture<FakeDTO> completableFuture = requester.get(newURL, restTemplateMock, FakeDTO.class);
        FakeDTO fakeDTO = completableFuture.get();

        verify(restTemplateMock, times(1)).getForObject(newURL, FakeDTO.class, requestParameters);
        assertThat(fakeDTO.value, is(dtoValue));
    }

    @Test
    void showSuccessfulURLGetWithNoDelay() throws ExecutionException, InterruptedException {
        String dtoValue = "value";
        when(restTemplateMock.getForObject(SERVICE_ENDPOINT, FakeDTO.class, requestParameters))
                .thenReturn(new FakeDTO(dtoValue));

        RESTRequesterImpl requester = requesterBuilder.build();

        CompletableFuture<FakeDTO> completableFuture = requester.get(SERVICE_ENDPOINT, restTemplateMock, FakeDTO.class);

        FakeDTO fakeDTO = completableFuture.get();
        assertThat(fakeDTO.value, is(dtoValue));
    }

    @Test
    void showSuccessfulURLGetWithDelay() throws ExecutionException, InterruptedException {
        String dtoValue = "value";
        delayAnswer(500, new FakeDTO(dtoValue)).when(restTemplateMock)
                .getForObject(SERVICE_ENDPOINT, FakeDTO.class, requestParameters);

        RESTRequesterImpl requester = requesterBuilder.build();

        CompletableFuture<FakeDTO> completableFuture = requester.get(SERVICE_ENDPOINT, restTemplateMock, FakeDTO.class);

        FakeDTO fakeDTO = completableFuture.get();
        assertThat(fakeDTO.value, is(dtoValue));
    }

    @Test
    void showHandlingOfAFailedURLGet() throws ExecutionException, InterruptedException {
        doThrow(new RestClientException("Didn't work", new Exception("For some reason"))).when(restTemplateMock)
                .getForObject(SERVICE_ENDPOINT, FakeDTO.class, requestParameters);

        RESTRequesterImpl requester = requesterBuilder.build();

        String failed = "Failed";
        CompletableFuture<FakeDTO> completableFuture = requester.get(SERVICE_ENDPOINT, restTemplateMock, FakeDTO.class)
                .exceptionally(ex -> new FakeDTO(failed));

        FakeDTO fakeDTO = completableFuture.get();
        assertThat(fakeDTO.value, is(failed));
    }

    @Test
    void whenBackupUrlEmpty_hasBackup_returnFalse() {
        RESTRequesterImpl requester = requesterBuilder.build();
        assertThat(requester.hasBackup(), is(false));
    }

    @Test
    void whenBackupUrlNull_hasBackup_returnFalse() {
        RESTRequesterImpl requester = RESTRequesterImpl.newBuilder(restTemplateMock, SERVICE_ENDPOINT, null).build();
        assertThat(requester.hasBackup(), is(false));
    }

    @Test
    void whenBackupUrlEmptySpaces_hasBackup_returnFalse() {
        RESTRequesterImpl requester = RESTRequesterImpl.newBuilder(restTemplateMock, SERVICE_ENDPOINT, "  ").build();
        assertThat(requester.hasBackup(), is(false));
    }

    @Test
    void whenBackupUrlNonEmpty_hasBackup_returnTrue() {
        RESTRequesterImpl requester = RESTRequesterImpl.newBuilder(restTemplateMock, SERVICE_ENDPOINT, "nonEmpty").build();
        assertThat(requester.hasBackup(), is(true));
    }

    private static Stubber delayAnswer(int delay, Object toReturn) {
        ReturnWithDelay answer = new ReturnWithDelay(toReturn);
        answer.setDelay(delay);
        return doAnswer(answer);
    }

    private void addRequestParameter(String param, String value) {
        requestParameters.put(param, value);
        requesterBuilder.addRequestParameter(param, value);
    }

    static class FakeDTO {
        String value;

        FakeDTO(String value) {
            this.value = value;
        }

        @Override public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            FakeDTO fakeDTO = (FakeDTO) o;

            return value != null ? value.equals(fakeDTO.value) : fakeDTO.value == null;

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