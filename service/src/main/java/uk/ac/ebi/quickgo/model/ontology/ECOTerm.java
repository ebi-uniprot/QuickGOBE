package uk.ac.ebi.quickgo.model.ontology;

/**
 * Created 13/11/15
 * @author Edd
 */
public class ECOTerm {
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
