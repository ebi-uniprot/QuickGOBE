package uk.ac.ebi.quickgo.client.service.loader.presets.assignedby;

import uk.ac.ebi.quickgo.client.service.loader.presets.AbstractRelevancyResponseConverterTest;

import java.util.List;

/**
 * Created 06/09/16
 * @author Edd
 */
public class AssignedByRelevancyResponseConverterTest
        extends
        AbstractRelevancyResponseConverterTest<AssignedByRelevancyResponseType, AssignedByRelevancyResponseConverter> {

    @Override protected AssignedByRelevancyResponseType createResponseType(List<String> relevantValues) {
        AssignedByRelevancyResponseType responseType = new AssignedByRelevancyResponseType();
        responseType.terms = new AssignedByRelevancyResponseType.Terms();
        responseType.terms.assignedBy = relevantValues;
        return responseType;
    }

    @Override protected AssignedByRelevancyResponseType createResponseTypeWithNullTerms() {
        AssignedByRelevancyResponseType responseType = new AssignedByRelevancyResponseType();
        responseType.terms = null;
        return responseType;
    }

    @Override protected AssignedByRelevancyResponseConverter createResponseConverter() {
        return new AssignedByRelevancyResponseConverter();
    }

    @Override protected void insertRelevancy(AssignedByRelevancyResponseType response, String term, String count) {
        response.terms.assignedBy.add(term);
        response.terms.assignedBy.add(count);
    }
}