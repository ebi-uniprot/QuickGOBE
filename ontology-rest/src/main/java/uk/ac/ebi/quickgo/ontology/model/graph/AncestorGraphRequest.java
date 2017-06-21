package uk.ac.ebi.quickgo.ontology.model.graph;

import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;

import java.util.Deque;
import java.util.Set;

/**
 * Data structure that describes the requirements for a sub-graph from an ontology.
 * @author Tony Wardell
 * Date: 21/06/2017
 * Time: 09:03
 * Created with IntelliJ IDEA.
 */
public class AncestorGraphRequest {

    public final Deque<String> targetVertices;
    public final Set<String> stopVertices;
    public final OntologyRelationType[] targetRelations;

    public AncestorGraphRequest(Deque<String> targetVertices, Set<String> stopVertices,
            OntologyRelationType[] targetRelations) {
        this.targetVertices = targetVertices;
        this.stopVertices = stopVertices;
        this.targetRelations = targetRelations;
    }
}
