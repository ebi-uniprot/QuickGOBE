package uk.ac.ebi.quickgo.rest.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <p>Used by Jackson as a proxy to the original {@link uk.ac.ebi.quickgo.rest.search.results.PageInfo} class.
 *
 * <p>This class is used to manipulate the {@link uk.ac.ebi.quickgo.rest.search.results.PageInfo} object so that
 * the response presented to the client does not contain the {@code nextCursor} field.
 *
 * <p>For more information on jackson mixins see:
 * <a href="http://wiki.fasterxml.com/JacksonMixInAnnotations">JacksonMixInAnnotations</a>
 *
 * Created 16/01/17
 * @author Edd
 */
public abstract class NoNextCursorPageInfo {
    @JsonIgnore abstract String getNextCursor();
}
