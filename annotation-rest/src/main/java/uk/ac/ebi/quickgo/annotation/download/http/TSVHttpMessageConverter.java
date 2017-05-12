package uk.ac.ebi.quickgo.annotation.download.http;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.TSV_MEDIA_TYPE;

/**
 * Writes a stream of {@link QueryResult}s containing {@link Annotation} instances to a response's output stream,
 * using the provided converter to format the output.
 *
 * @author Tony Wardell
 * Date: 26/04/2017
 * Time: 14:49
 * Created with IntelliJ IDEA.
 */
public class TSVHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TSVHttpMessageConverter.class);
    private final ConverterHelper helper;

    public TSVHttpMessageConverter(ConverterHelper helper) {
        super(TSV_MEDIA_TYPE);
        this.helper = helper;
    }

    @Override protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override protected void writeInternal(Object annotationStream, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        LOGGER.info("GenericHttpMessageConverter writeInternal called.");
        OutputStream out = outputMessage.getBody();
        helper.dispatchWriting(annotationStream, out);
    }
}
