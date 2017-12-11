package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.completablevalue;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.model.CompletableValue;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.results.transformer.AbstractValueInjector;

import java.util.List;

/**
 * This class is responsible for supplementing an {@link CompletableValue} instance, which contains
 * an ECO code (in the key field), with a ECO name, through the use of a RESTful service.
 *
 * Created 11/12/17
 * @author Tony Wardell
 */
public class EvidenceNameInjector extends AbstractValueInjector<BasicOntology, CompletableValue> {

    public static final String EVIDENCE_CODE = "evidenceCode";
    private static final String EVIDENCE_NAME = "evidenceName";

    @Override
    public String getId() {
        return EVIDENCE_NAME;
    }

    @Override
    public FilterRequest buildFilterRequest(CompletableValue completableValue) {
        return FilterRequest.newBuilder()
                .addProperty(getId())
                .addProperty(EVIDENCE_CODE, completableValue.getKey())
                .build();
    }

    @Override
    public void injectValueFromResponse(ConvertedFilter<BasicOntology> convertedRequest,
            CompletableValue completableValue) {
        BasicOntology response = convertedRequest.getConvertedValue();

        List<BasicOntology.Result> results = response.getResults();
        if (!results.isEmpty()) {
            completableValue.setValue(results.get(0).getName());
        }
    }
}
