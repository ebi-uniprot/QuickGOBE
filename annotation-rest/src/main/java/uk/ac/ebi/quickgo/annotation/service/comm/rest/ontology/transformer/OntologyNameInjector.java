package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.List;

/**
 * This class is responsible for supplementing an {@link Annotation} instance, which contains
 * a gene ontology identifier, with a gene ontology name, through the use of a RESTful service.
 *
 * Created 07/04/17
 * @author Edd
 */
public class OntologyNameInjector extends AbstractValueInjector<BasicOntology> {

    static final String GO_ID = "goId";
    static final String GO_NAME = "goName";

    @Override
    public String getId() {
        return GO_NAME;
    }

    @Override FilterRequest buildFilterRequest(Annotation annotation) {
        return FilterRequest.newBuilder()
                        .addProperty(getId())
                        .addProperty(GO_ID, annotation.goId)
                        .build();
    }

    @Override void injectValueFromResponse(ConvertedFilter<BasicOntology> convertedRequest, Annotation annotation) {
        BasicOntology response = convertedRequest.getConvertedValue();

        List<BasicOntology.Result> results = response.getResults();
        if (!results.isEmpty()) {
            annotation.goName = results.get(0).getName();
        }
    }
}
