package uk.ac.ebi.quickgo.annotation.download.http;

import uk.ac.ebi.quickgo.annotation.download.model.DownloadContent;

import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
@RunWith(MockitoJUnitRunner.class)
public class HttpMessageConverterTest {
    @Mock
    private OutputStreamWriter mockDispatchWriter;

    @Mock
    private HttpOutputMessage mockHttpOutputMessage;

    @Mock
    private HttpInputMessage mockHttpInputMessage;

    private HttpMessageConverter httpMessageConverter;

    @Before
    public void setUp() {
        MediaType fakeMediaType = new MediaType("text", "plain", Charset.forName("UTF-8"));
        httpMessageConverter = new HttpMessageConverter(mockDispatchWriter, fakeMediaType);
    }

    @Test
    public void writingDeferredSuccessfullyToDispatchWriter() throws IOException {
        String downloadPackage = "mock object contents";
        httpMessageConverter.writeInternal(downloadPackage, mockHttpOutputMessage);

        verify(mockDispatchWriter, times(1)).write(downloadPackage, mockHttpOutputMessage.getBody());
    }

    @Test
    public void readReturnsNull() throws IOException {
        assertThat(httpMessageConverter.readInternal(String.class, mockHttpInputMessage), is(nullValue()));
    }

    @Test
    public void supportsDownloadContent() {
        assertThat(httpMessageConverter.supports(DownloadContent.class), is(true));
    }
}
