package uk.ac.ebi.quickgo.rest.controller.response;

import uk.ac.ebi.quickgo.rest.search.results.DocHighlight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * <p>Used by Jackson as a proxy to the original {@link uk.ac.ebi.quickgo.rest.search.results.QueryResult} class.
 *
 * <p>This class is used to manipulate the {@link uk.ac.ebi.quickgo.rest.search.results.QueryResult} object so that the-
 * response presented to the client does not contain the {@link DocHighlight} data structure.
 *
 * <p>For more information on jackson mixins see:
 * <a href="http://wiki.fasterxml.com/JacksonMixInAnnotations">JacksonMixInAnnotations</a>
 */
public abstract class NoHighlightNoAggregateQueryResult extends NoAggregateQueryResult {
    @JsonIgnore abstract List<DocHighlight> getHighlighting();
}
