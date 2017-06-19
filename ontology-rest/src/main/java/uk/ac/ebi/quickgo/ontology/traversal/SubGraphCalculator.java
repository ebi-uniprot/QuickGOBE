package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.model.AncestorGraph;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;

import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates the calculation of a edges for a single vertex, and the creation of further instances for vertexes
 * attached to those edges.
 * @author Tony Wardell
 * Date: 19/06/2017
 * Time: 10:11
 * Created with IntelliJ IDEA.
 */
class SubGraphCalculator {

    private static Logger LOGGER = LoggerFactory.getLogger(SubGraphCalculator.class);

    static Trampoline<AncestorGraph> createTrampoline(Deque<String> targetVertices, Set<String> stopNodes,
            OntologyRelationType[] targetRelations, AncestorGraph ancestorGraph, OntologyGraph ontologyGraph) {

        String target = targetVertices.pollFirst();

        if (Objects.nonNull(target)) {

            //Process this node if it hasn't already been considered.
            if (!ancestorGraph.vertices.contains(target)) {
                ancestorGraph.vertices.add(target);

                //if target is not a stop node look for parents
                if (!stopNodes.contains(target)) {

                    try {
                        Set<OntologyRelationship> parents = ontologyGraph.parents(target, targetRelations);
                        ancestorGraph.edges.addAll(parents);
                        addParentsToWorkQueue(targetVertices, parents);

                    } catch (Exception e) {
                        LOGGER.error("SubGraphCalculator#createTrampoline looked up parents for " + target + " but " +
                                             "received exception ", e);
                    }
                }
            }

            if(!targetVertices.isEmpty()) {
                return new Trampoline<AncestorGraph>() {
                    public Optional<Trampoline<AncestorGraph>> nextTrampoline() {
                        return Optional.of(createTrampoline(targetVertices,
                                                            stopNodes,
                                                            targetRelations,
                                                            ancestorGraph,
                                                            ontologyGraph));
                    }
                };
            }

        }
        return new Trampoline<AncestorGraph>() {
            public AncestorGraph getValue() { return ancestorGraph; }
        };

    }

    private static void addParentsToWorkQueue(Deque<String> targetVertices, Set<OntologyRelationship> parents) {
        parents.stream()
               .map(p -> p.parent)
               .forEach(targetVertices::addLast);
    }
}
