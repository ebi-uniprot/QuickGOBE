package uk.ac.ebi.quickgo.rest.search.filter.converter;

import uk.ac.ebi.quickgo.rest.search.filter.request.RESTCommRequestFilter;
import uk.ac.ebi.quickgo.rest.search.filter.request.SimpleRequestFilter;

import java.util.Optional;

public interface FilterConverterFactory {
    Optional<FilterConverter> simpleConverter(SimpleRequestFilter requestFilter);
    Optional<RESTCommFilterConverter> restConverter(RESTCommRequestFilter requestFilter);
}