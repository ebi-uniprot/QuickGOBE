package uk.ac.ebi.quickgo.ontology.traversal.read;

import uk.ac.ebi.quickgo.ontology.traversal.OntologyRelationType;

import org.jgrapht.graph.DefaultEdge;

/**
 * This class represents the components of an ontology graph: child vertices, parent vertices
 * and the edges that relate these vertices them.
 *
 * Created 18/05/16
 * @author Edd
 */
public class OntologyRelationship extends LabelledEdge<String> {
    public OntologyRelationType relationship;

    public OntologyRelationship(String child, String parent, OntologyRelationType relationship) {
        super(child, parent, relationship);
        this.relationship = relationship;
    }

    @Override public String toString() {
        return "OntologyRelationship{" +
                "child='" + child + '\'' +
                ", parent='" + parent + '\'' +
                ", relationship=" + relationship +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OntologyRelationship that = (OntologyRelationship) o;

        if (child != null ? !child.equals(that.child) : that.child != null) {
            return false;
        }
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) {
            return false;
        }
        return relationship == that.relationship;

    }

    @Override
    public int hashCode() {
        int result = child != null ? child.hashCode() : 0;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (relationship != null ? relationship.hashCode() : 0);
        return result;
    }

    /**
     * Combine ontology relationships, as documented on http://geneontology.org/page/ontology-relations
     * @param child the relationship from which the combination starts
     * @param parent the relationship to which the combination ends
     * @return the combined relationship
     */
    public static OntologyRelationship combineRelationships(OntologyRelationship child, OntologyRelationship parent) {
        if (!child.parent.equals(parent.child)) {
            throw new IllegalArgumentException("Incorrectly combined relationships");
        }

        OntologyRelationType mergedType = OntologyRelationType.UNDEFINED;

        if (child.relationship == OntologyRelationType.IDENTITY) {
            mergedType = parent.relationship;
        } else if (parent.relationship == OntologyRelationType.IDENTITY) {
            mergedType = child.relationship;
        } else if (child.relationship == OntologyRelationType.HAS_PART ||
                parent.relationship == OntologyRelationType.HAS_PART) {
            mergedType = OntologyRelationType.UNDEFINED;
        } else if (child.relationship == OntologyRelationType.IS_A) {
            mergedType = parent.relationship;
        } else if (parent.relationship == OntologyRelationType.IS_A) {
            mergedType = child.relationship;
        } else if (child.relationship == OntologyRelationType.PART_OF &&
                parent.relationship == OntologyRelationType.PART_OF) {
            mergedType = OntologyRelationType.PART_OF;
        } else if (child.relationship == OntologyRelationType.OCCURS_IN) {
            mergedType = OntologyRelationType.OCCURS_IN;
        } else if (child.relationship == OntologyRelationType.REGULATES &&
                parent.relationship == OntologyRelationType.PART_OF) {
            mergedType = OntologyRelationType.REGULATES;
        }

        return new OntologyRelationship(child.child, parent.parent, mergedType);
    }
}

class LabelledEdge<V> extends DefaultEdge {
    public V child;
    public V parent;
    public String relation;

    LabelledEdge(V child, V v2, OntologyRelationType relation) {
        this.child = child;
        this.parent = v2;
        this.relation = relation.getShortName();
    }


}
