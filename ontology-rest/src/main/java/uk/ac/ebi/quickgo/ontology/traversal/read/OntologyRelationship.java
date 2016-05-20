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
        return "OntologyRelationshipTuple{" +
                "child='" + child + '\'' +
                ", parent='" + parent + '\'' +
                ", relationship='" + relationship + '\'' +
                '}';
    }
}
