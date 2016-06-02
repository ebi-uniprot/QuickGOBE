package uk.ac.ebi.quickgo.rest.search.filter.request;

import uk.ac.ebi.quickgo.rest.search.filter.converter.FilterConverterConfigurer;
import uk.ac.ebi.quickgo.rest.search.filter.converter.RESTCommFilterConverter;

/**
 * Created 02/06/16
 * @author Edd
 */
// instances created at controller level
public class RESTCommRequestFilter implements RequestFilter, FilterConverterConfigurer<RESTCommFilterConverter> {
    // fields to populate rest comm fetcher
    @Override public String getSignature() {
        return null;
    }

    @Override public void configure(RESTCommFilterConverter filterConverter) {
        filterConverter.setRESTFetcher("pretend REST fetcher");
    }
}
