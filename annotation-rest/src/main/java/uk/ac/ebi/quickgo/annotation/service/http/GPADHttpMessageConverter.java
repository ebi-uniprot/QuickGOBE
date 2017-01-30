package uk.ac.ebi.quickgo.annotation.service.http;

import uk.ac.ebi.quickgo.annotation.converter.AnnotationToGPAD;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.converter.GPADAnnotationConverter;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * GPAD message converter that writes a stream of {@link QueryResult} containing {@link Annotation} instances,
 * to a response's output stream.
 *
 * Created 19/01/17
 * @author Edd
 */
public class GPADHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
    private static final String TYPE = "text";
    private static final String SUB_TYPE = "gpad";
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static final MediaType GPAD_MEDIA_TYPE = new MediaType(TYPE, SUB_TYPE, DEFAULT_CHARSET);
    public static final String GPAD_MEDIA_TYPE_STRING = TYPE + "/" + SUB_TYPE;

    private static final Logger GPAD_LOGGER = getLogger(GPADHttpMessageConverter.class);
    private static final int FLUSH_INTERVAL = 5000;
    private final AnnotationToGPAD converter;


    public GPADHttpMessageConverter(AnnotationToGPAD converter) {
        super(GPAD_MEDIA_TYPE);
        this.converter = converter;
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    protected Object readInternal(
            Class<?> aClass,
            HttpInputMessage httpInputMessage)
            throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(
            Object annotationStream,
            HttpOutputMessage httpOutputMessage)
            throws IOException, HttpMessageNotWritableException {
        OutputStream out = httpOutputMessage.getBody();

        dispatchWriting(annotationStream, out);
    }

    @SuppressWarnings("unchecked") private void dispatchWriting(Object object, OutputStream out) throws IOException {
        if (object instanceof ResponseExceptionHandler.ErrorInfo) {
            writeError(out, (ResponseExceptionHandler.ErrorInfo) object);
        } else {
            writeAnnotations(out, (Stream<QueryResult<Annotation>>) object);
        }
    }

    private void writeError(OutputStream out, ResponseExceptionHandler.ErrorInfo errorInfo) throws IOException {
        out.write(("URL:\n\t" + errorInfo.getUrl()+"\n").getBytes());
        out.write(("Messages:\n\t" + errorInfo.getMessages().stream().collect(Collectors.joining(",\n"))).getBytes());
    }

    private void writeAnnotations(OutputStream out, Stream<QueryResult<Annotation>> annotationStream) {
        AtomicInteger counter = new AtomicInteger(0);
        annotationStream.forEach(annotationResult -> {
            annotationResult.getResults().forEach(annotation -> {
                try {
                    out.write((converter.apply(annotation) + "\n").getBytes());
                    counter.getAndIncrement();

                    if (counter.get() % FLUSH_INTERVAL == 0) {
                        out.flush();
                    }
                } catch (IOException e) {
                    GPAD_LOGGER.error("Could not write annotation in GPAD format: " + annotation, e);
                }
            });
        });
        GPAD_LOGGER.info("Written " + counter.get() + " GPAD annotations");
    }

    private void writeHeaderLines(OutputStream out, List<String> headerLines) {
        headerLines.forEach(headerLine -> {
            try {
                out.write((headerLine + "\n").getBytes());
            } catch (IOException e) {
                GPAD_LOGGER.error("Could not write header line: " + headerLine, e);
            }
        });
    }
}
