package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.completablevalue;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;
import uk.ac.ebi.quickgo.common.model.CompletableValue;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.results.transformer.AbstractValueInjector;

/**
 * This class is responsible for supplementing an {@link CompletableValue} instance, which contains
 * a taxonomy identifier (in the key field), with a taxonomy name, through the use of a RESTful service.
 *
 * Created 04/10/17
 * @author Tony Wardell
 */
public class TaxonomyNameInjector extends AbstractValueInjector<BasicTaxonomyNode, CompletableValue> {

    public static final String TAXON_ID = "taxonId";
    static final String TAXON_NAME = "taxonName";

    @Override
    public String getId() {
        return TAXON_NAME;
    }

    @Override
    public FilterRequest buildFilterRequest(CompletableValue completableValue) {
        return FilterRequest.newBuilder()
                .addProperty(getId())
                .addProperty(TAXON_ID, String.valueOf(completableValue.getKey()))
                .build();
    }

    @Override
    public void injectValueFromResponse(ConvertedFilter<BasicTaxonomyNode> convertedRequest, CompletableValue
            completableValue) {
        completableValue.setValue(convertedRequest.getConvertedValue().getScientificName());
    }
}
