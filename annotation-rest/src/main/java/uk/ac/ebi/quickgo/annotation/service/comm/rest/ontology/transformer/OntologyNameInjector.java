package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.common.transformer.ResponseValueInjector;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.RESTFilterConverterFactory;

import java.util.List;

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
        ConvertedFilter<BasicOntology> convertedRequest = restFetcher.convert(request);

        BasicOntology response = convertedRequest.getConvertedValue();
        
        List<BasicOntology.Result> results = response.getResults();
        if (!results.isEmpty()) {
            annotation.goName = results.get(0).getName();
        }
    }
}
