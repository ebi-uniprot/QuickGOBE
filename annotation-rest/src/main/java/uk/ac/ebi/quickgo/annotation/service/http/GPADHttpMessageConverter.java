package uk.ac.ebi.quickgo.annotation.service.http;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.converter.GPADAnnotationConverter;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
public class GPADHttpMessageConverter extends AbstractHttpMessageConverter<Stream<QueryResult<Annotation>>> {
    private static final MediaType MEDIA_TYPE = new MediaType("text", "gpad", Charset.forName("utf-8"));
    private static final Logger GPAD_LOGGER = getLogger(GPADHttpMessageConverter.class);
    private final GPADAnnotationConverter converter;

    public GPADHttpMessageConverter(GPADAnnotationConverter converter) {
        super(MEDIA_TYPE);
        this.converter = converter;
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    protected Stream<QueryResult<Annotation>> readInternal(
            Class<? extends Stream<QueryResult<Annotation>>> aClass,
            HttpInputMessage httpInputMessage)
            throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(
            Stream<QueryResult<Annotation>> annotationStream,
            HttpOutputMessage httpOutputMessage)
            throws IOException, HttpMessageNotWritableException {
        OutputStream out = httpOutputMessage.getBody();

        writeAnnotations(out, annotationStream);
    }

    private void writeAnnotations(OutputStream out, Stream<QueryResult<Annotation>> annotationStream) {
        AtomicInteger counter = new AtomicInteger(0);
        annotationStream.forEach(annotationResult -> {
            if (counter.get() == 0) {
                writeHeaderLines(out, converter.getHeaderLines(annotationResult));
            }
            annotationResult.getResults().forEach(annotation -> {
                try {
                    out.write((converter.convert(annotation) + "\n").getBytes());
                    counter.getAndIncrement();
                } catch (IOException e) {
                    GPAD_LOGGER.error("Could not write annotation in GPAD format: " + annotation, e);
                }
                // flush occasionally
                // todo: currently flushing every 20, to see effects. Future, flush every 10000 or so, or forget it?
                if (counter.get() % 20 == 0) {
                    try {
                        out.flush();
                    } catch (IOException e) {
                        GPAD_LOGGER.error("Could not flush stream", e);
                    }
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
