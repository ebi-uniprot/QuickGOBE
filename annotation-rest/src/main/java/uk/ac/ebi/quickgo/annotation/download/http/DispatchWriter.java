package uk.ac.ebi.quickgo.annotation.download.http;

import uk.ac.ebi.quickgo.annotation.download.model.DownloadContent;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

/**
 * Writes a stream of {@link QueryResult}s containing {@link Annotation} instances to a response's output stream,
 * using the provided converter to format the output.
 *
 * @author Tony Wardell
 * Date: 26/04/2017
 * Time: 14:49
 * Created with IntelliJ IDEA.
 */
public class DispatchWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatchWriter.class);
    private static final int FLUSH_INTERVAL = 500;
    private final BiFunction<Annotation, List<String>, List<String>> converter;
    private final MediaType type;

    public DispatchWriter(BiFunction<Annotation, List<String>, List<String>> converter, MediaType mediaType) {
        this.converter = converter;
        this.type = mediaType;
    }

    @SuppressWarnings("unchecked")
    void write(Object object, OutputStream out) throws IOException {
        if (object instanceof ResponseExceptionHandler.ErrorInfo) {
            writeError(out, (ResponseExceptionHandler.ErrorInfo) object);
        } else {
            if (object instanceof DownloadContent) {
                writeAnnotations(out, (DownloadContent) object);
            } else {
                LOGGER.warn("DispatchWriter write must handle: " + object.getClass());
            }
        }
    }

    private void writeError(OutputStream out, ResponseExceptionHandler.ErrorInfo errorInfo) throws IOException {
        out.write(("URL:\n\t" + errorInfo.getUrl() + "\n").getBytes());
        out.write(("Messages:\n\t" + errorInfo.getMessages().stream().collect(Collectors.joining(",\n"))).getBytes());
    }

    private void writeAnnotations(OutputStream out, DownloadContent downloadContent) {
        AtomicInteger counter = new AtomicInteger(0);
        AtomicInteger batchCount = new AtomicInteger(0);
        try {
            downloadContent.annotationStream.forEach(annotationResult ->
                    annotationResult.getResults()
                            .forEach(annotation -> converter.apply(annotation, downloadContent.selectedFields)
                                    .forEach(content ->
                                            writeContent(content, out, counter, batchCount))));
        } catch (StopStreamException e) {
            LOGGER.error("Client aborted streaming: closing stream.", e);
            downloadContent.annotationStream.close();
        }
        LOGGER.debug("Written {} {} annotations", counter.get(), type.getType());
    }

    private void writeContent(String content, OutputStream out, AtomicInteger counter, AtomicInteger batchCount) {
        try {
            out.write((content + "\n").getBytes());
            updateCountersAndFlushStreamWhenRequired(out, counter, batchCount);
        } catch (IOException e) {
            throw new StopStreamException(
                    "Could not write OutputStream whilst writing " + type.getType() + " annotation: " +
                            content, e);
        }
    }

    private void updateCountersAndFlushStreamWhenRequired(OutputStream out, AtomicInteger counter,
            AtomicInteger batchCount) throws IOException {
        counter.getAndIncrement();
        batchCount.getAndIncrement();
        if (batchCount.get() >= FLUSH_INTERVAL) {
            out.flush();
            LOGGER.debug("Flushed " + type.getType() + " http message converter output stream after: " +
                    counter.get() + " annotations.");
            batchCount.set(0);
        }
    }
}
