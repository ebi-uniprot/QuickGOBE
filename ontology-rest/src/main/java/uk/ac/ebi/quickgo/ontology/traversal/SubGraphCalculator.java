package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.model.AncestorGraph;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;

import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Encapsulates the calculation of a edges for a single vertex, and the creation of further instances for vertexes
 * attached to those edges.
 * @author Tony Wardell
 * Date: 19/06/2017
 * Time: 10:11
 * Created with IntelliJ IDEA.
 */
class SubGraphCalculator {

    static Trampoline<AncestorGraph> createTrampoline(Deque<String> targetVertices, Set<String> stopNodes,
            OntologyRelationType[] targetRelations, AncestorGraph ancestorGraph, OntologyGraph ontologyGraph) {

        String target = targetVertices.pollFirst();

        if(Objects.nonNull(target)){
            ancestorGraph.vertices.add(target);
        }

        //if target is null, or is a stop node, and we don't have any more to process then don't create any more
        if (Objects.isNull(target) || stopNodes.contains(target) && targetVertices.isEmpty()){
            return new Trampoline<AncestorGraph>(){
                public AncestorGraph getValue() { return ancestorGraph; }
            };
        }

        try {
            Set<OntologyRelationship> parents = ontologyGraph.parents(target, targetRelations);
            ancestorGraph.edges.addAll(parents);
            addParentsToWorkQueue(targetVertices, parents);

        } catch (Exception e) {
            return new Trampoline<AncestorGraph>(){
                public AncestorGraph getValue() { return ancestorGraph; }
            };
        }

        return new Trampoline<AncestorGraph>() {
            public Optional<Trampoline<AncestorGraph>> nextTrampoline() {
                return Optional.of(createTrampoline(targetVertices, stopNodes, targetRelations, ancestorGraph, ontologyGraph));
            }
        };
    }

    private static void addParentsToWorkQueue(Deque<String> targetVertices, Set<OntologyRelationship> parents) {
        parents.stream()
               .map(p -> p.parent)
               .forEach(targetVertices::addLast);
    }
}
