package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.common.transformer.ResponseValueInjector;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.List;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * This class is responsible for supplementing an {@link Annotation} instance, which contains
 * a gene ontology identifier, with a gene ontology name, through the use of a RESTful service.
 *
 * Created 07/04/17
 * @author Edd
 */
public class OntologyNameInjector implements ResponseValueInjector {

    private static final String GO_NAME = "goName";
    private static final String GO_ID = "goId";
    private static final Logger LOGGER = getLogger(OntologyNameInjector.class);

    @Override public String getId() {
        return GO_NAME;
    }

    @Override public void inject(RESTFilterConverterFactory restFetcher, Annotation annotation) {
        FilterRequest request =
                FilterRequest.newBuilder()
                        .addProperty(getId())
                        .addProperty(GO_ID, annotation.goId)
                        .build();

        // todo: handle 404 and 500 responses differently
        // 404 => leave empty
        // 500 => throw error back up
        try {
            ConvertedFilter<BasicOntology> convertedRequest = restFetcher.convert(request);

            BasicOntology response = convertedRequest.getConvertedValue();

            List<BasicOntology.Result> results = response.getResults();
            if (!results.isEmpty()) {
                annotation.goName = results.get(0).getName();
            }

        } catch (RetrievalException e) {
            LOGGER.error("Problem retrieving ontology service response from annotations service.", e);

            if (exceptionIsFatal(e)) {
                throw e;
            }
        }
    }

    private boolean exceptionIsFatal(RetrievalException e) {
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

    private boolean causedByHttpStatus404(Throwable parentCause) {
        return parentCause instanceof HttpClientErrorException
                && ((HttpClientErrorException) parentCause).getStatusCode() == HttpStatus.NOT_FOUND;
    }
}
