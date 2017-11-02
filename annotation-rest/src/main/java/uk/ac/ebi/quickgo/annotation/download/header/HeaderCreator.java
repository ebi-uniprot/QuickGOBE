package uk.ac.ebi.quickgo.annotation.download.header;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * A contract for writing {@link HeaderContent} to an instance of {@link ResponseBodyEmitter}
 *
 * @author Tony Wardell
 * Date: 22/05/2017
 * Time: 15:36
 * Created with IntelliJ IDEA.
 */
public interface HeaderCreator {

    /**
     * Write the contents of the header to the ResponseBodyEmitter instance.
     * @param emitter streams the header content to the client
     * @param content holds the URI and parameter list to be added to the header information.
     */
    void write(ResponseBodyEmitter emitter, HeaderContent content);
}
