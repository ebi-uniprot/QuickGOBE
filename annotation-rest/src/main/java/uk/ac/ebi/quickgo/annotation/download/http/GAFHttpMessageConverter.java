package uk.ac.ebi.quickgo.annotation.download.http;

import uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToGAF;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
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
 * GAF message converter that writes a stream of {@link QueryResult} containing {@link Annotation} instances,
 * to a response's output stream.
 *
 * Created 19/01/17
 * @author Edd
 */
@Deprecated
public class GAFHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
    private static final String TYPE = "text";
    private static final String SUB_TYPE = "gaf";
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final MediaType GAF_MEDIA_TYPE = new MediaType(TYPE, SUB_TYPE, DEFAULT_CHARSET);
    private static final String GAF_MEDIA_TYPE_STRING = TYPE + "/" + SUB_TYPE;

    private static final Logger GAF_LOGGER = getLogger(GAFHttpMessageConverter.class);
    private static final int FLUSH_INTERVAL = 1000;
    private final AnnotationToGAF converter;

    public GAFHttpMessageConverter(AnnotationToGAF converter) {
        super(GAF_MEDIA_TYPE);
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

    @SuppressWarnings("unchecked")
    private void dispatchWriting(Object object, OutputStream out) throws IOException {
        if (object instanceof ResponseExceptionHandler.ErrorInfo) {
            writeError(out, (ResponseExceptionHandler.ErrorInfo) object);
        } else {
            writeAnnotations(out, (Stream<QueryResult<Annotation>>) object);
        }
    }

    private void writeError(OutputStream out, ResponseExceptionHandler.ErrorInfo errorInfo) throws IOException {
        out.write(("URL:\n\t" + errorInfo.getUrl() + "\n").getBytes());
        out.write(("Messages:\n\t" + errorInfo.getMessages().stream().collect(Collectors.joining(",\n"))).getBytes());
    }

    private void writeAnnotations(OutputStream out, Stream<QueryResult<Annotation>> annotationStream) {
        AtomicInteger counter = new AtomicInteger(0);
        AtomicInteger batchCount = new AtomicInteger(0);
        try {
            annotationStream.forEach(annotationResult ->
                    annotationResult.getResults().forEach(annotation -> converter.apply(annotation)
                    .forEach(s -> {
                        try {
                            out.write((s + "\n").getBytes());

                            updateCountersAndFlushStreamWhenRequired(out, counter, batchCount);
                        } catch (IOException e) {
                            throw new StopStreamException(
                                    "Could not write OutputStream whilst writing GAF annotation: " + annotation,
                                    e);
                        }
                    })));
        } catch (StopStreamException e) {
            GAF_LOGGER.error("Client aborted streaming: closing stream.", e);
            annotationStream.close();
        }
        GAF_LOGGER.info("Written " + counter.get() + " GAF annotations");
    }

    private void updateCountersAndFlushStreamWhenRequired(OutputStream out, AtomicInteger counter,
            AtomicInteger batchCount) throws IOException {
        counter.getAndIncrement();
        batchCount.getAndIncrement();
        if (batchCount.get() >= FLUSH_INTERVAL) {
            out.flush();
            GAF_LOGGER.info("Flushed GAF http message converter output stream after: " +
                    counter.get() + " annotations.");
            batchCount.set(0);
        }
    }
}
