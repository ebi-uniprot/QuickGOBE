package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

/**
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 10:32
 * Created with IntelliJ IDEA.
 */
public interface QuickGOConverter<S> {

    QuickGOQuery convert(S source);
}
