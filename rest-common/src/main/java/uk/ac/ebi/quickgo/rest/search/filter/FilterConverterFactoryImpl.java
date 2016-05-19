package uk.ac.ebi.quickgo.rest.search.filter;

import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link FilterConverterFactory} interface.
 *
 * This class knows which implementation of the {@link FilterConverter} is needed to convert a {@link RequestFilter}
 * into a {@link uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery}.
 *
 * @author Ricardo Antunes
 */
@Component
public class FilterConverterFactoryImpl implements FilterConverterFactory {
    @Override public FilterConverter createConverter(RequestFilter requestFilter) {
        return new SimpleFilterConverter(requestFilter);
    }
}
