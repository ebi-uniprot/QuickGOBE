package uk.ac.ebi.quickgo.annotation.download.header;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Tony Wardell
 * Date: 16/08/2017
 * Time: 17:02
 * Created with IntelliJ IDEA.
 */
class AbstractHeaderCreatorTest {

    private final ResponseBodyEmitter mockEmitter = mock(ResponseBodyEmitter.class);
    private final HeaderContent mockContent = mock(HeaderContent.class);
    private final AbstractHeaderCreator abstractHeaderCreator = new AbstractHeaderCreator() {
        @Override protected void output(ResponseBodyEmitter emitter, HeaderContent content) throws IOException {
            emitter.send(content);
        }
    };

    @Test
    void callToWriteInvokesOutput() throws Exception{
        abstractHeaderCreator.write(mockEmitter, mockContent);

        verify(mockEmitter).send(mockContent);
    }

    @Test
    void whenEmitterThrowsIOExceptionNoExceptionIsThrown() throws Exception {
        Exception exception = null;
        doThrow(new IOException("Test IOException")).when(mockEmitter).send(mockContent);

        try {
        abstractHeaderCreator.write(mockEmitter, mockContent);
        } catch (Exception e) {
            exception = e;
        }

        assertThat(exception, nullValue());
    }

    @Test
    void nullEmitterCausesExceptionToBeThrown(){
        assertThrows(IllegalArgumentException.class, () -> abstractHeaderCreator.write(null, mockContent));
    }

    @Test
    void nullContentCausesExceptionToBeThrown(){
        assertThrows(IllegalArgumentException.class, () -> abstractHeaderCreator.write(mockEmitter, null));
    }
}
