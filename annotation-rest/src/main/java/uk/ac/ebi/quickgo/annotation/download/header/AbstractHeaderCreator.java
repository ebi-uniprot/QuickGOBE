package uk.ac.ebi.quickgo.annotation.download.header;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * An implementation of {@link HeaderCreator} that holds logic common to all subclasses.
 *
 * @author Tony Wardell
 * Date: 15/08/2017
 * Time: 15:59
 * Created with IntelliJ IDEA.
 */
public abstract class AbstractHeaderCreator implements HeaderCreator{
    private static final Logger LOGGER = LoggerFactory.getLogger(TSVHeaderCreator.class);

    @Override public void write(ResponseBodyEmitter emitter, HeaderContent content) {
        Preconditions.checkArgument(Objects.nonNull(emitter), "The emitter must not be null");
        Preconditions.checkArgument(Objects.nonNull(content), "The content must not be null");
        try {
            output(emitter, content);
        } catch (IOException e) {
            LOGGER.error("Failed to send download header", e);
        }
    }

    protected abstract void output(ResponseBodyEmitter emitter, HeaderContent content) throws IOException;
}
