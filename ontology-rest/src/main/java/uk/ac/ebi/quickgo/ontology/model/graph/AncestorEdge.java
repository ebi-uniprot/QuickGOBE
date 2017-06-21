package uk.ac.ebi.quickgo.ontology.model.graph;

/**
 * Data structure for the Ancestor Graph edges.
 * @author Tony Wardell
 * Date: 20/06/2017
 * Time: 15:37
 * Created with IntelliJ IDEA.
 */
public class AncestorEdge {

    public final String subject;
    public final String predicate;
    public final String object;

    public AncestorEdge(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AncestorEdge that = (AncestorEdge) o;

        if (subject != null ? !subject.equals(that.subject) : that.subject != null) {
            return false;
        }
        if (predicate != null ? !predicate.equals(that.predicate) : that.predicate != null) {
            return false;
        }
        return object != null ? object.equals(that.object) : that.object == null;
    }

    @Override public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (predicate != null ? predicate.hashCode() : 0);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "AncestorEdge{" +
                "subject='" + subject + '\'' +
                ", predicate='" + predicate + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
