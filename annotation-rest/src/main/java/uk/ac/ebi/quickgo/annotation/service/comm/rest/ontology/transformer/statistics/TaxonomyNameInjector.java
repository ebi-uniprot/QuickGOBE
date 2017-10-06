package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.statistics;

import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.results.transformer.AbstractValueInjector;

/**
 * This class is responsible for supplementing an {@link StatisticsValue} instance, which contains
 * a taxonomy identifier (in the key field), with a taxonomy name, through the use of a RESTful service.
 *
 * Created 04/10/17
 * @author Tony Wardell
 */
public class TaxonomyNameInjector extends AbstractValueInjector<BasicTaxonomyNode, StatisticsValue> {

    private static final String TAXON_NAME = "taxonName";
    private static final String TAXON_ID = "taxonId";

    @Override
    public String getId() {
        return TAXON_NAME;
    }

    @Override
    public FilterRequest buildFilterRequest(StatisticsValue statisticsValue) {
        return FilterRequest.newBuilder()
                .addProperty(getId())
                .addProperty(TAXON_ID, String.valueOf(statisticsValue.getKey()))
                .build();
    }

    @Override
    public void injectValueFromResponse(ConvertedFilter<BasicTaxonomyNode> convertedRequest, StatisticsValue
            statisticsValue) {
        statisticsValue.setName(convertedRequest.getConvertedValue().getScientificName());
    }
}
