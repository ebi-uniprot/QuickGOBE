package uk.ac.ebi.quickgo.annotation.download.http;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * Writes a stream of {@link QueryResult}s containing {@link Annotation} instances to a response's output stream,
 * using the provided converter to format the output.
 *
 * @author Tony Wardell
 * Date: 26/04/2017
 * Time: 14:49
 * Created with IntelliJ IDEA.
 */
public class GenericHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericHttpMessageConverter.class);
    private static final int FLUSH_INTERVAL = 1000;
    private final Function<Annotation, List<String>> converter;
    private final MediaType type;

    public GenericHttpMessageConverter(Function<Annotation, List<String>> converter, MediaType mediaType) {
        super(mediaType);
        this.converter = converter;
        this.type = mediaType;
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
        OutputStream out = outputMessage.getBody();
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
                annotationResult.getResults()
                                .forEach(annotation -> converter.apply(annotation)
                                                                             .forEach(content -> stream(out,
                                                                                                        counter,
                                                                                                        batchCount,
                                                                                                        content))));
        } catch (StopStreamException e) {
            LOGGER.error("Client aborted streaming: closing stream.", e);
            annotationStream.close();
        }
        LOGGER.info("Written " + counter.get() +  type.getType() + " annotations");
    }

    private void stream(OutputStream out, AtomicInteger counter, AtomicInteger batchCount, String content) {
        try {
            out.write((content + "\n").getBytes());
            updateCountersAndFlushStreamWhenRequired(out, counter, batchCount);
        } catch (IOException e) {
            throw new StopStreamException("Could not write OutputStream whilst writing " + type.getType() + " annotation: " +
                                                  content, e);
        }
    }

    private void updateCountersAndFlushStreamWhenRequired(OutputStream out, AtomicInteger counter,
            AtomicInteger batchCount) throws IOException {
        counter.getAndIncrement();
        batchCount.getAndIncrement();
        if (batchCount.get() >= FLUSH_INTERVAL) {
            out.flush();
            LOGGER.info("Flushed " + type.getType() + " http message converter output stream after: " +
                                counter.get() + " annotations.");
            batchCount.set(0);
        }
    }
}
