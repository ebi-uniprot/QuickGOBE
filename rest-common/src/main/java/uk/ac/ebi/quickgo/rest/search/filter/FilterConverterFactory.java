package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.search.filter.request.RESTCommRequestFilter;
import uk.ac.ebi.quickgo.rest.search.filter.request.SimpleRequestFilter;

/**
 * A factory responsible for creating {@link RequestFilterOld} instances based on the type of field and the values
 * provided.
 *
 * @author Ricardo Antunes
 */
public interface FilterConverterFactory {
    /**
     * Will create the appropriate {@link uk.ac.ebi.quickgo.common.converter.FieldConverter} for the
     * {@link RequestFilterOld}. The decision is based on what type of value is contained within the
     * {@link RequestFilterOld#getField()}.
     *
     * @param requestFilter the filter to convert
     * @return a filter capable of converting the filter into a {@link uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery}
     */
    FilterConverter createConverter(RequestFilterOld requestFilter);

    // todo: have more createConverters here, based on type -- overloading conversion?
    uk.ac.ebi.quickgo.rest.search.filter.converter.FilterConverter createConverter(SimpleRequestFilter requestFilter);
    uk.ac.ebi.quickgo.rest.search.filter.converter.FilterConverter createConverter(RESTCommRequestFilter requestFilter);
}