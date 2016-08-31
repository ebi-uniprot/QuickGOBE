package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import uk.ac.ebi.quickgo.rest.comm.ResponseType;

import java.util.List;

/**
 * Created 31/08/16
 * @author Edd
 */
public class AssignedByRelevancyResponseType implements ResponseType {
    public Terms terms;

    public static class Terms {
        public List<String> assignedBy;
    }
}
