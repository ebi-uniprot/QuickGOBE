package uk.ac.ebi.quickgo.ontology.traversal;

import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorEdge;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorGraph;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorGraphRequest;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.IS_A;

/**
 * @author Tony Wardell
 * Date: 21/06/2017
 * Time: 08:53
 * Created with IntelliJ IDEA.
 */
public class SubGraphCalculatorTest {

    private final String pyrophosphataseActivity = "GO:0016462";
    private final String cyclaseActivity = "GO:0009975";
    private final String catalyticActivity = "GO:0003824";
    private final String molecularFunction = "GO:0003674";
    private final OntologyRelationship py_IA_cy =
            new OntologyRelationship(pyrophosphataseActivity, cyclaseActivity, IS_A);
    private final OntologyRelationship cy_IA_ca = new OntologyRelationship(cyclaseActivity, catalyticActivity, IS_A);
    private final OntologyRelationship ca_IA_mf = new OntologyRelationship(catalyticActivity, molecularFunction, IS_A);
    private OntologyGraphTraversal ontologyGraphMock;

    private OntologyRelationType[] targetRelations;
    private AncestorGraphRequest request;
    private AncestorGraph<String> ancestorGraph;
    @Before
    public void setup() {
        Deque<String> targetVertices = new LinkedList<>(singletonList(pyrophosphataseActivity));
        Set<String> stopVertices = new HashSet<>(singletonList(molecularFunction));
        targetRelations = new OntologyRelationType[]{OntologyRelationType.IS_A};
        request = new AncestorGraphRequest(targetVertices, stopVertices, targetRelations);
        ancestorGraph = new AncestorGraph<>(new HashSet<>(), new HashSet<>());

        ontologyGraphMock = mock(OntologyGraphTraversal.class);
        when(ontologyGraphMock.parents(pyrophosphataseActivity, targetRelations))
                .thenReturn(new HashSet<>(singletonList(py_IA_cy)));
        when(ontologyGraphMock.parents(cyclaseActivity, targetRelations))
                .thenReturn(new HashSet<>(singletonList(cy_IA_ca)));
        when(ontologyGraphMock.parents(catalyticActivity, targetRelations))
                .thenReturn(new HashSet<>(singletonList(ca_IA_mf)));
    }

    @Test
    public void createSimpleSubGraph() {
        SubGraphCalculator.createTrampoline(request, ancestorGraph, ontologyGraphMock).compute();

        assertThat(ancestorGraph.vertices, hasSize(4));
        assertThat(ancestorGraph.vertices, containsInAnyOrder(pyrophosphataseActivity, cyclaseActivity,
                                                              catalyticActivity, molecularFunction));

        assertThat(ancestorGraph.edges, hasSize(3));
        assertThat(ancestorGraph.edges, containsInAnyOrder(toAE(py_IA_cy), toAE(cy_IA_ca), toAE(ca_IA_mf)));
    }

    @Test
    public void noParentsExistForVertex() {
        when(ontologyGraphMock.parents(catalyticActivity, targetRelations)).thenThrow(new IllegalArgumentException());

        SubGraphCalculator.createTrampoline(request, ancestorGraph, ontologyGraphMock).compute();

        assertThat(ancestorGraph.vertices, hasSize(3));
        assertThat(ancestorGraph.vertices, containsInAnyOrder(pyrophosphataseActivity, cyclaseActivity,
                                                              catalyticActivity));

        assertThat(ancestorGraph.edges, hasSize(2));
        assertThat(ancestorGraph.edges, containsInAnyOrder(toAE(py_IA_cy), toAE(cy_IA_ca)));
    }

    private AncestorEdge toAE(OntologyRelationship or){
        return new AncestorEdge(or.child, or.relationship.toString(), or.parent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfAncestorGraphRequestArgumentIsNull(){
        SubGraphCalculator.createTrampoline(null, ancestorGraph, ontologyGraphMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfAncestorGraphArgumentIsNull(){
        SubGraphCalculator.createTrampoline(request, null, ontologyGraphMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfOntologyGraphTraversalArgumentIsNull(){
        SubGraphCalculator.createTrampoline(request, ancestorGraph, null);
    }
}
