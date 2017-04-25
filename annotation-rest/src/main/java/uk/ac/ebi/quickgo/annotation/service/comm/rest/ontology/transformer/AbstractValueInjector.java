package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.common.transformer.ResponseValueInjector;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * This abstract class is responsible for providing the framework for supplementing an {@link Annotation} instance,
 * with a specialised value, through the use of a RESTful service. The value injected is specified in
 * concrete classes.
 *
 * Created 12/04/17
 * @author Edd
 */
public abstract class AbstractValueInjector<T> implements ResponseValueInjector {
    private static final Logger LOGGER = getLogger(AbstractValueInjector.class);

    /**
     * Creates a {@link FilterRequest} tailored to this specific {@link AbstractValueInjector} implementation.
     *
     * @param annotation the annotation upon with which the request can use values from
     * @return a tailored {@link FilterRequest}
     */
    abstract FilterRequest buildFilterRequest(Annotation annotation);

    /**
     * The main logic used to inject new values into a given {@link Annotation} instance based on a
     * {@link ConvertedFilter}, which contains the result of a RESTful response as its value.
     *
     * @param convertedRequest contains the RESTful response
     * @param annotation the annotation into which to inject a new value
     */
    abstract void injectValueFromResponse(ConvertedFilter<T> convertedRequest, Annotation annotation);

    @Override public void inject(RESTFilterConverterFactory restFetcher, Annotation annotation) {
        FilterRequest request = buildFilterRequest(annotation);

        try {
            ConvertedFilter<T> convertedRequest = restFetcher.convert(request);

            injectValueFromResponse(convertedRequest, annotation);
        } catch (RetrievalException e) {
            if (exceptionIsFatal(e)) {
                LOGGER.error("Problem retrieving external service response from annotations service.", e);
                throw e;
            } else {
                LOGGER.info("Recoverable exception encountered when retrieving external service response from " +
                        "annotations service.");
                LOGGER.debug("Recoverable exception info:", e);
            }
        }
    }

    /**
     * Defines whether a {@link RetrievalException} constitutes a fatal error, e.g., by
     * analysing the stack trace.
     *
     * @param e the {@link RetrievalException}
     * @return true if the supplied exception is fatal, else false
     */
    protected boolean exceptionIsFatal(RetrievalException e) {
        boolean isFatal = true;
        Throwable cause = e.getCause();
        if (cause != null) {
            Throwable parentCause = cause.getCause();
            if (parentCause != null && causedByHttpStatus404(parentCause)) {
                isFatal = false;
            }
        }
        return isFatal;
    }

    private static boolean causedByHttpStatus404(Throwable parentCause) {
        return parentCause instanceof HttpClientErrorException
                && ((HttpClientErrorException) parentCause).getStatusCode() == HttpStatus.NOT_FOUND;
    }
}
