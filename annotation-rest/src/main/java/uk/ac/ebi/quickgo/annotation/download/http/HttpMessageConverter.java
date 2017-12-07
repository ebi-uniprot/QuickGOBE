package uk.ac.ebi.quickgo.annotation.download.http;

import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * An HTTP message converter wrapper that defers the actual writing to an instance of {@link OutputStreamWriter}.
 *
 * Created 19/01/17
 * @author Tony Wardell
 */
public class HttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    private final OutputStreamWriter writer;

    public HttpMessageConverter(OutputStreamWriter dispatchWriter, MediaType mediaType) {
        super(mediaType);
        this.writer = dispatchWriter;
    }

    @Override protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override protected void writeInternal(Object downloadPackage, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        writer.write(downloadPackage, outputMessage.getBody());
    }
}
