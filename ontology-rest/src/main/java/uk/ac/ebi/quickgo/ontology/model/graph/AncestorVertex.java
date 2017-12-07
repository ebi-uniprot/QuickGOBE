package uk.ac.ebi.quickgo.ontology.model.graph;

/**
 * Data structure for the vertex within an Ontology.
 * @author Tony Wardell
 * Date: 20/06/2017
 * Time: 10:40
 * Created with IntelliJ IDEA.
 */
public class AncestorVertex {
    public final String id;
    public final String label;

    public AncestorVertex(String id, String label) {
        this.id = id;
        this.label = label;
    }

    @Override public String toString() {
        return "AncestorVertex{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AncestorVertex that = (AncestorVertex) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        return label != null ? label.equals(that.label) : that.label == null;
    }

    @Override public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }
}
