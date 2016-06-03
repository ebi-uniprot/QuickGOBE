package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.rest.search.filter.converter.FilterConverter;
import uk.ac.ebi.quickgo.rest.search.filter.converter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.filter.request.RESTCommRequestFilter;
import uk.ac.ebi.quickgo.rest.search.filter.request.SimpleRequestFilter;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created 03/06/16
 * @author Edd
 */
public class AnnotationRequestHelper {
    public static Stream<QuickGOQuery> createRESTQueries(Set<RESTCommRequestFilter> filters, FilterConverterFactory
            filterConverterFactory) {
        return filters.stream()
                .map(filterConverterFactory::restConverter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(FilterConverter::transform);
    }

    public static Stream<QuickGOQuery> createSimpleQueries(Set<SimpleRequestFilter> filters, FilterConverterFactory
            filterConverterFactory) {
        return filters.stream()
                .map(filterConverterFactory::simpleConverter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(FilterConverter::transform);
    }
}
