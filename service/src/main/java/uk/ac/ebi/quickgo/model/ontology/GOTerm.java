package uk.ac.ebi.quickgo.model.ontology;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * GO term DTO used by the service layer.
 *
 * Created 13/11/15
 * @author Edd
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GOTerm extends OBOTerm {
    public List<String> aspect;
    public String usage;
}
