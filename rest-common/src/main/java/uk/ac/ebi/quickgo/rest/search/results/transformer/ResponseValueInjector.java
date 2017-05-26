package uk.ac.ebi.quickgo.rest.search.results.transformer;

import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

/**
 * This class is responsible for injecting some value from a RESTful response
 * into an instance {@link M} of the model.
 *
 * Created 07/04/17
 * @author Edd
 */
public interface ResponseValueInjector<M> {
    /**
     * An identifier for the value that is to be
     * injected into instances of {@link M}. For example, a field name.
     *
     * @return the identifier of the value to inject
     */
    String getId();

    /**
     * Injects the necessary value into an instance of {@link M}, through the use of
     * a {@link RESTFilterConverterFactory}, which can be used to fetch a RESTful response.
     * 
     * @param restFetcher used to fetch a RESTful response
     * @param model the subject of the value injection
     */
    void inject(RESTFilterConverterFactory restFetcher, M model);
}
