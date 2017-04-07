package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.common.transformer.ResponseValueInjector;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

/**
 * This class is responsible for supplementing an {@link Annotation} instance, which contains
 * a gene ontology identifier, with a gene ontology name, through the use of a RESTful service.
 *
 * Created 07/04/17
 * @author Edd
 */
public class OntologyNameInjector implements ResponseValueInjector {

    private static final String GO_NAME = "goName";

    @Override public String getSignature() {
        return GO_NAME;
    }

    @Override public void inject(RESTFilterConverterFactory restFetcher, Annotation annotation) {
        FilterRequest request =
                FilterRequest.newBuilder().addProperty(getSignature(), annotation.goId).build();

        ConvertedFilter<BasicOntology> convertedRequest = restFetcher.convert(request);

        BasicOntology response = convertedRequest.getConvertedValue();
        annotation.goName = response.getResults().get(0).getName();
    }
}
