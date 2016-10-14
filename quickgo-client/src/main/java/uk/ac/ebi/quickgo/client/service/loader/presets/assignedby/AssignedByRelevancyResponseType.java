package uk.ac.ebi.quickgo.client.service.loader.presets.assignedby;

import uk.ac.ebi.quickgo.rest.comm.ResponseType;

import java.util.List;

/**
 * <p>Provides the data-type object that captures assigned by relevancy information.
 * The {@code terms.assignedBy} value is a list of {@link String}s (returned from an
 * external REST endpoint) which capture relevancy information in the form of:
 * {@code [ "term1", "term1Frequency", "term2", "term2Frequency" ... ]}.
 * <p>
 * Created 31/08/16
 * @author Edd
 */
public class AssignedByRelevancyResponseType implements ResponseType {
    public Terms terms;

    public static class Terms {
        public List<String> assignedBy;
    }
}
