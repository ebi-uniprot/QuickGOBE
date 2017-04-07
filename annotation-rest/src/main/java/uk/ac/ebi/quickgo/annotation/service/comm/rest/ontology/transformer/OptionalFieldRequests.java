package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import java.util.HashSet;
import java.util.Set;

/**
 * Created 07/04/17
 * @author Edd
 */
public class OptionalFieldRequests {
    // todo: this could contain a set of RequestTransformationFields, rather than a set of strings
    private final Set<String> fieldRequests;

    public OptionalFieldRequests() {
        fieldRequests = new HashSet<>();
    }

    public Set<String> getFieldRequests() {
        return fieldRequests;
    }

    public void addFieldRequest(String field) {
        fieldRequests.add(field);
    }
}
