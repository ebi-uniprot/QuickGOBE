package uk.ac.ebi.quickgo.annotation.download.http;

import uk.ac.ebi.quickgo.annotation.download.model.DownloadContent;

import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created 11/07/17
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class HttpMessageConverterTest {
    @Mock
    private OutputStreamWriter mockDispatchWriter;

    @Mock
    private HttpOutputMessage mockHttpOutputMessage;

    @Mock
    private HttpInputMessage mockHttpInputMessage;

    private HttpMessageConverter httpMessageConverter;

    @BeforeEach
    void setUp() {
        MediaType fakeMediaType = new MediaType("text", "plain", Charset.forName("UTF-8"));
        httpMessageConverter = new HttpMessageConverter(mockDispatchWriter, fakeMediaType);
    }

    @Test
    void writingDeferredSuccessfullyToDispatchWriter() throws IOException {
        String downloadPackage = "mock object contents";
        httpMessageConverter.writeInternal(downloadPackage, mockHttpOutputMessage);

        verify(mockDispatchWriter, times(1)).write(downloadPackage, mockHttpOutputMessage.getBody());
    }

    @Test
    void readReturnsNull() throws IOException {
        assertThat(httpMessageConverter.readInternal(String.class, mockHttpInputMessage), is(nullValue()));
    }

    @Test
    void supportsDownloadContent() {
        assertThat(httpMessageConverter.supports(DownloadContent.class), is(true));
    }
}
