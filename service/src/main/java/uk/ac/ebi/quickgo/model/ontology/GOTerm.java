package uk.ac.ebi.quickgo.model.ontology;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GO term DTO used by the service layer.
 *
 * Created 13/11/15
 * @author Edd
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GOTerm extends OBOTerm {

    public String aspect;

    public String usage;

    // accessors ----------------------------------------------------------------

    public void setAspect(String aspect) {
        this.aspect = aspect;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }
}
