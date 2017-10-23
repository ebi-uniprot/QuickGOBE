package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.completablevalue;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.model.CompletableValue;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.results.transformer.AbstractValueInjector;

import java.util.List;

/**
 * This class is responsible for supplementing an {@link CompletableValue} instance, which contains
 * a gene ontology identifier (in the key field), with a gene ontology name, through the use of a RESTful service.
 *
 * Created 04/10/17
 * @author Tony Wardell
 */
public class OntologyNameInjector extends AbstractValueInjector<BasicOntology, CompletableValue> {

    public static final String GO_ID = "goId";
    static final String GO_NAME = "goName";

    @Override
    public String getId() {
        return GO_NAME;
    }

    @Override
    public FilterRequest buildFilterRequest(CompletableValue completableValue) {
        return FilterRequest.newBuilder()
                        .addProperty(getId())
                        .addProperty(GO_ID, completableValue.getKey())
                        .build();
    }

    @Override
    public void injectValueFromResponse(ConvertedFilter<BasicOntology> convertedRequest, CompletableValue completableValue) {
        BasicOntology response = convertedRequest.getConvertedValue();

        List<BasicOntology.Result> results = response.getResults();
        if (!results.isEmpty()) {
            completableValue.setValue(results.get(0).getName());
        }
    }
}
