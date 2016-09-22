package uk.ac.ebi.quickgo.client.service.loader.presets.taxon;

import uk.ac.ebi.quickgo.client.service.loader.presets.AbstractRelevancyResponseConverterTest;

import java.util.List;

/**
 * Created 06/09/16
 * @author Edd
 */
public class TaxonRelevancyResponseConverterTest
        extends AbstractRelevancyResponseConverterTest<TaxonRelevancyResponseType, TaxonRelevancyResponseConverter> {

    @Override protected TaxonRelevancyResponseType createResponseType(List<String> relevantValues) {
        TaxonRelevancyResponseType responseType = new TaxonRelevancyResponseType();
        responseType.terms = new TaxonRelevancyResponseType.Terms();
        responseType.terms.taxonIds = relevantValues;
        return responseType;
    }

    @Override protected TaxonRelevancyResponseType createResponseTypeWithNullTerms() {
        TaxonRelevancyResponseType responseType = new TaxonRelevancyResponseType();
        responseType.terms = null;
        return responseType;
    }

    @Override protected TaxonRelevancyResponseConverter createResponseConverter() {
        return new TaxonRelevancyResponseConverter();
    }

    @Override protected void insertRelevancy(TaxonRelevancyResponseType response, String term, String count) {
        response.terms.taxonIds.add(term);
        response.terms.taxonIds.add(count);
    }
}