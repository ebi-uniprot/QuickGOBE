package uk.ac.ebi.quickgo.common.search.solr;

import uk.ac.ebi.quickgo.common.search.results.DocHighlight;

import java.util.List;

/**
 * Contract defining the conversion of query results into a
 * domain object representation of highlighting.
 *
 * @param R type that represents the query results
 * @param H type that represents the actual highlighting information about the query results
 *
 * Created 10/02/16
 * @author Edd
 */
public interface QueryResultHighlightingConverter<R, H> {

    /**
     * Converts the query results into a {@link List} of {@link DocHighlight} instances,
     * using the specified highlighting information.
     *
     * @param results the results of a query
     * @param resultHighlights highlighting specific information from query results
     * @return
     */
    List<DocHighlight> convertResultHighlighting(
            R results,
            H resultHighlights);
}
