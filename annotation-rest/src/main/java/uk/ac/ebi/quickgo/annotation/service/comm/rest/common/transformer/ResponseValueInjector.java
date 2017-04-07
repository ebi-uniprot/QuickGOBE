package uk.ac.ebi.quickgo.annotation.service.comm.rest.common.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

/**
 * This class is responsible for injecting some value from a RESTful response
 * into an {@link Annotation} instance.
 *
 * Created 07/04/17
 * @author Edd
 */
public interface ResponseValueInjector {
    /**
     * A signature that identifies the value that is to be
     * injected into the {@link Annotation}. For example, a field name.
     *
     * @return the signature of the value to inject
     */
    String getSignature();

    /**
     * Injects the necessary value into the {@link Annotation}, through the use of
     * a {@link RESTFilterConverterFactory}, which can be used to fetch a RESTful response.
     * 
     * @param restFetcher used to fetch a RESTful response
     * @param annotation the subject of the value injection
     */
    void inject(RESTFilterConverterFactory restFetcher, Annotation annotation);
}
