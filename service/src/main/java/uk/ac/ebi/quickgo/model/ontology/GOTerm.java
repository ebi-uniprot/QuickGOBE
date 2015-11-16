package uk.ac.ebi.quickgo.model.ontology;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created 13/11/15
 * @author Edd
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GOTerm {
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
