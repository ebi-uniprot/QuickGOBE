package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.rest.search.results.AggregateResponse;
import uk.ac.ebi.quickgo.rest.search.results.DocHighlight;
import uk.ac.ebi.quickgo.rest.search.results.Facet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * <p>This class is used to manipulate the {@link uk.ac.ebi.quickgo.rest.search.results.QueryResult} object so that the
 * response presented to the client is adjusted to what a client expects when receiving a response.
 *
 * <p>Examples of manipulation:
 * <ul>
 *     <li>filtering of unnecessary fields</li>
 * </ul>
 *
 * @author Ricardo Antunes
 */
abstract class QueryResultMixin {
    @JsonIgnore abstract Facet getFacet();

    @JsonIgnore abstract List<DocHighlight> getHighlighting();

    @JsonIgnore abstract AggregateResponse getAggregation();
}
