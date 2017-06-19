package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.model.AncestorGraph;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;

import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author Tony Wardell
 * Date: 19/06/2017
 * Time: 10:11
 * Created with IntelliJ IDEA.
 */
public class SubGraphCalculator {

    public static Trampoline<AncestorGraph> createTrampoline(Deque<String> targetVertices, Set<String> stopNodes,
            OntologyRelationType[] targetRelations, AncestorGraph ancestorGraph, OntologyGraph ontologyGraph) {

        String target = targetVertices.pollFirst();

        //if target is null, or is a stop node, and we don't have any more to process then don't create any more
        if (Objects.isNull(target) || stopNodes.contains(target) && targetVertices.isEmpty()) {
            return new Trampoline<AncestorGraph>(){
                public AncestorGraph getValue() { return ancestorGraph; }
            };
        }

        //If target has any parents, add to deque
        Set<OntologyRelationship> parents = null;
        try {
            parents = ontologyGraph.parents(target, targetRelations);
        } catch (Exception e) {

            //Finish up if we have an exception
            return new Trampoline<AncestorGraph>(){
                public AncestorGraph getValue() { return ancestorGraph; }
            };
        }

        //Add parents to work queue
        if (!parents.isEmpty()) {
            parents.stream()
                   .map(p -> p.parent)
                   .forEach(parent -> targetVertices.addLast(parent));
        }

        //Add this vertex, and edges from this vertex to result
        ancestorGraph.vertices.add(target);
        ancestorGraph.edges.addAll(parents);

        //Create new Trampoline with current work queue.
        return new Trampoline<AncestorGraph>() {
            public Optional<Trampoline<AncestorGraph>> nextTrampoline() {
                return Optional.of(createTrampoline(targetVertices, stopNodes, targetRelations, ancestorGraph, ontologyGraph));
            }
        };
    }
}
