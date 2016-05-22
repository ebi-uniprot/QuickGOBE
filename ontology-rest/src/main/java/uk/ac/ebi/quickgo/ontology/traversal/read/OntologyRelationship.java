package uk.ac.ebi.quickgo.ontology.traversal.read;

import uk.ac.ebi.quickgo.ontology.traversal.OntologyRelation;

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


    /**
     * Combine ontology relationships, as documented on http://geneontology.org/page/ontology-relations
     * @param child the relationship from which the combination starts
     * @param parent the relationship to which the combination ends
     * @return
     */
    public static OntologyRelationship combineRelationships(OntologyRelationship child, OntologyRelationship parent) {
        if (!child.parent.equals(parent.child)) {
            throw new RuntimeException("Incorrectly combined relationships");
        }

        String mergedType = OntologyRelation.UNDEFINED.getShortName();

        String childRelationship = child.relationship;
        String parentRelationship = parent.relationship;
        if (childRelationship.equals(OntologyRelation.IDENTITY.getShortName())) {
            mergedType = parentRelationship;
        } else if (parentRelationship.equals(OntologyRelation.IDENTITY.getShortName())) {
            mergedType = childRelationship;
        } else if (childRelationship.equals(OntologyRelation.HAS_PART.getShortName()) || parentRelationship.equals(OntologyRelation.HAS_PART.getShortName())) {
            mergedType = OntologyRelation.UNDEFINED.getShortName();
        } else if (childRelationship.equals(OntologyRelation.IS_A.getShortName())) {
            mergedType = parentRelationship;
        } else if (parentRelationship.equals((OntologyRelation.IS_A.getShortName()))) {
            mergedType = childRelationship;
        } else if (childRelationship.equals(OntologyRelation.PART_OF.getShortName()) && parentRelationship.equals((OntologyRelation.PART_OF.getShortName()))) {
            mergedType = OntologyRelation.PART_OF.getShortName();
        } else if (childRelationship.equals((OntologyRelation.OCCURS_IN.getShortName()))) {
            mergedType = OntologyRelation.OCCURS_IN.getShortName();
        } else if (childRelationship.equals(OntologyRelation.REGULATES.getShortName()) && parentRelationship.equals(OntologyRelation.PART_OF.getShortName()))
        {
            mergedType = OntologyRelation.REGULATES.getShortName();
        }


        OntologyRelationship mergedRelationship = new OntologyRelationship();
        mergedRelationship.child = child.child;
        mergedRelationship.parent = parent.parent;
        mergedRelationship.relationship = mergedType;

        return mergedRelationship;
    }
}
