package uk.ac.ebi.quickgo.ontology.traversal.read;

/**
 * This class represents the components of an ontology graph: child vertices, parent vertices
 * and the edges that relate these vertices them.
 *
 * Created 18/05/16
 * @author Edd
 */
public class OntologyRelationship {
    public String child;
    public String parent;
    public String relationship;

    @Override public String toString() {
        return "OntologyRelationship{" +
                "child='" + child + '\'' +
                ", parent='" + parent + '\'' +
                ", relationship='" + relationship + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OntologyRelationship that = (OntologyRelationship) o;

        if (child != null ? !child.equals(that.child) : that.child != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        return !(relationship != null ? !relationship.equals(that.relationship) : that.relationship != null);

    }

    @Override
    public int hashCode() {
        int result = child != null ? child.hashCode() : 0;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (relationship != null ? relationship.hashCode() : 0);
        return result;
    }
}
