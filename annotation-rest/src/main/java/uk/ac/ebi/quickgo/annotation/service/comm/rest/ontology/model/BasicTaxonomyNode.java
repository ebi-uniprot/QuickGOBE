package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model;

import uk.ac.ebi.quickgo.rest.comm.ResponseType;

/**
 * Created 12/04/17
 * @author Edd
 */
public class BasicTaxonomyNode implements ResponseType {
    private String scientificName;

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getScientificName() {
        return scientificName;
    }
}
