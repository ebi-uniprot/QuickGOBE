package uk.ac.ebi.quickgo.rest.search.filter;

import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link FilterFactory} interface.
 *
 * This class knows how the {@link RequestFilter} instances should be created, based on the provided field name.
 * Depending on the implementation of the {@link RequestFilter} being instantiated it may be necessary to inject
 * extra processing logic into the filter instance. It is thus the responsibility of this factory to know which
 * processing logic to inject into the filter for it to be able to transform itself successfully into a
 * {@link uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery}.
 *
 * @author Ricardo Antunes
 */
@Component
public class FilterFactoryImpl implements FilterFactory {
    @Override public RequestFilter createFilter(String field, String... values) {
        return new SimpleFilter(field, values);
    }
}
