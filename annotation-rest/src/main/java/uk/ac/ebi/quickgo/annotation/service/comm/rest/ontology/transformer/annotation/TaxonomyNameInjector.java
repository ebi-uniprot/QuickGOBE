package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.annotation;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.results.transformer.AbstractValueInjector;

/**
 * This class is responsible for supplementing an {@link Annotation} instance, which contains
 * a taxonomy identifier, with a taxonomy name, through the use of a RESTful service.
 *
 * Created 07/04/17
 * @author Edd
 */
public class TaxonomyNameInjector extends AbstractValueInjector<BasicTaxonomyNode, Annotation> {

    static final String TAXON_NAME = "taxonName";
    static final String TAXON_ID = "taxonId";

    @Override
    public String getId() {
        return TAXON_NAME;
    }

    @Override
    public FilterRequest buildFilterRequest(Annotation annotation) {
        return FilterRequest.newBuilder()
                .addProperty(getId())
                .addProperty(TAXON_ID, String.valueOf(annotation.taxonId))
                .build();
    }

    @Override
    public void injectValueFromResponse(ConvertedFilter<BasicTaxonomyNode> convertedRequest, Annotation annotation) {
        annotation.taxonName = convertedRequest.getConvertedValue().getScientificName();
    }
}
