package uk.ac.ebi.quickgo.ontology.traversal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.ontology.common.OntologyType.ECO;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.*;
import static uk.ac.ebi.quickgo.ontology.traversal.TermSlimmer.DEFAULT_RELATION_TYPES;

/**
 * Test the {@link TermSlimmer} functionality according to the requirements specified in:
 * https://www.ebi.ac.uk/seqdb/confluence/display/GOA/GO+Slimming+-+an+executive+summary
 *
 * Created 09/10/17
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class TermSlimmerTest {
    public static final String GO = "GO";
    private static final String CELLULAR_COMPONENT = "GO:0005575";
    private static final String CELL = "GO:0005623";
    private static final String MEMBRANE = "GO:0016020";
    private static final String CELL_PART = "GO:0044464";
    private static final String CELL_PERIPHERY = "GO:0071944";
    private static final String MEMBRANE_PART = "GO:0044425";
    private static final String PLASMA_MEMBRANE = "GO:0005886";
    private static final String PLASMA_MEMBRANE_PART = "GO:0044459";
    private static final String LATERAL_PLASMA_MEMBRANE = "GO:0016328";
    
    @Mock
    private OntologyGraphTraversal mockOntologyGraph;
    @Mock
    private List<String> mockSlimSet;
    private OntologyGraphTraversal ontology;

    @BeforeEach
    void setUp() {
        OntologyGraph ontology = new OntologyGraph();
        ontology.addRelationships(createRelationships());
        this.ontology = ontology;
    }

    @Test
    void nullOntologyTypeCausesCreationException() {
        assertThrows(IllegalArgumentException.class, () -> TermSlimmer.createSlims(null, mockOntologyGraph, mockSlimSet));
    }

    @Test
    void nullOntologyCausesCreationException() {
        assertThrows(IllegalArgumentException.class, () -> TermSlimmer.createSlims(OntologyType.GO, null, mockSlimSet));
    }

    @Test
    void nullSlimsCausesCreationException() {
        assertThrows(IllegalArgumentException.class, () -> TermSlimmer.createSlims(OntologyType.GO, mockOntologyGraph, null));
    }

    @Test
    void emptySlimsCausesCreationException() {
        assertThrows(IllegalArgumentException.class, () -> TermSlimmer.createSlims(OntologyType.GO, mockOntologyGraph, emptyList()));
    }

    @Test
    void unsupportedOntologyTypeCausesCreationException() {
        assertThrows(IllegalArgumentException.class, () -> TermSlimmer.createSlims(ECO, mockOntologyGraph, mockSlimSet));
    }

    @Test
    void nullRelationshipsCauseCreationException() {
        assertThrows(IllegalArgumentException.class, () -> TermSlimmer.createSlims(OntologyType.GO, mockOntologyGraph, mockSlimSet, null));
    }

    @Test
    void noRelationshipsResultsInDefaultsUsed() {
        TermSlimmer termSlimmer = TermSlimmer.createSlims(OntologyType.GO, ontology, singletonList(CELLULAR_COMPONENT));
        assertThat(termSlimmer.getRelationTypes(), is(DEFAULT_RELATION_TYPES));
    }

    @Test
    void nonEmptyRelationshipsAreUsed() {
        OntologyRelationType[] requestedRelationships = new OntologyRelationType[]{HAS_PART, CAPABLE_OF_PART_OF};
        TermSlimmer termSlimmer = TermSlimmer.createSlims(OntologyType.GO, ontology, singletonList(CELLULAR_COMPONENT),
                requestedRelationships);
        assertThat(termSlimmer.getRelationTypes(), is(requestedRelationships));
    }

    @Test
    void termsSlimToSingleTerm() {
        List<String> slimSetVertices = singletonList(MEMBRANE);
        TermSlimmer termSlimmer = TermSlimmer.createSlims(OntologyType.GO, ontology, slimSetVertices);
        assertThat(termSlimmer.getSlimmedTermsMap().size(), is(5));
        assertThat(termSlimmer.findSlimmedToTerms(MEMBRANE), contains(MEMBRANE));
        assertThat(termSlimmer.findSlimmedToTerms(MEMBRANE_PART), contains(MEMBRANE));
        assertThat(termSlimmer.findSlimmedToTerms(PLASMA_MEMBRANE), contains(MEMBRANE));
        assertThat(termSlimmer.findSlimmedToTerms(PLASMA_MEMBRANE_PART), contains(MEMBRANE));
        assertThat(termSlimmer.findSlimmedToTerms(LATERAL_PLASMA_MEMBRANE), contains(MEMBRANE));
    }

    @Test
    void termsSlimToMultipleTerms() {
        List<String> slimSetVertices = asList(CELL_PART, MEMBRANE_PART);
        TermSlimmer termSlimmer = TermSlimmer.createSlims(OntologyType.GO, ontology, slimSetVertices);
        assertThat(termSlimmer.getSlimmedTermsMap().size(), is(6));
        assertThat(termSlimmer.findSlimmedToTerms(CELL_PART), contains(CELL_PART));
        assertThat(termSlimmer.findSlimmedToTerms(MEMBRANE_PART), contains(MEMBRANE_PART));
        assertThat(termSlimmer.findSlimmedToTerms(CELL_PERIPHERY), contains(CELL_PART));
        assertThat(termSlimmer.findSlimmedToTerms(PLASMA_MEMBRANE), contains(CELL_PART));
        assertThat(termSlimmer.findSlimmedToTerms(PLASMA_MEMBRANE_PART), contains(CELL_PART, MEMBRANE_PART));
        assertThat(termSlimmer.findSlimmedToTerms(LATERAL_PLASMA_MEMBRANE), contains(CELL_PART, MEMBRANE_PART));
    }

    @Test
    void slimmedTermsAreHidden() {
        List<String> slimSetVertices = asList(CELL_PART, MEMBRANE_PART, PLASMA_MEMBRANE_PART);
        TermSlimmer termSlimmer = TermSlimmer.createSlims(OntologyType.GO, ontology, slimSetVertices);
        assertThat(termSlimmer.getSlimmedTermsMap().size(), is(6));
        assertThat(termSlimmer.findSlimmedToTerms(CELL_PART), contains(CELL_PART));
        assertThat(termSlimmer.findSlimmedToTerms(MEMBRANE_PART), contains(MEMBRANE_PART));
        assertThat(termSlimmer.findSlimmedToTerms(CELL_PERIPHERY), contains(CELL_PART));
        assertThat(termSlimmer.findSlimmedToTerms(PLASMA_MEMBRANE), contains(CELL_PART));
        assertThat(termSlimmer.findSlimmedToTerms(PLASMA_MEMBRANE_PART), contains(PLASMA_MEMBRANE_PART));
        assertThat(termSlimmer.findSlimmedToTerms(LATERAL_PLASMA_MEMBRANE), contains(PLASMA_MEMBRANE_PART));
    }

    @Test
    void termInOntologySlimsToNothing() {
        TermSlimmer termSlimmer = TermSlimmer.createSlims(OntologyType.GO, ontology, singletonList(CELL));
        assertThat(termSlimmer.findSlimmedToTerms(CELLULAR_COMPONENT), is(empty()));
    }

    @Test
    void termNotInOntologySlimsToNothing() {
        TermSlimmer termSlimmer = TermSlimmer.createSlims(OntologyType.GO, ontology, singletonList(CELLULAR_COMPONENT));
        assertThat(termSlimmer.findSlimmedToTerms("XXXXXX"), is(empty()));
    }

    /**
     * Graph structure taken from https://www.ebi.ac.uk/seqdb/confluence/display/GOA/GO+Slimming+-+an+executive+summary,
     * which is our confluence description of slimming.
     * @return an ontology graph that can be used to demonstrate slimming
     */
    private List<OntologyRelationship> createRelationships() {
        List<OntologyRelationship> relationships = new ArrayList<>();

        relationships.add(new OntologyRelationship(CELL, CELLULAR_COMPONENT, IS_A));
        relationships.add(new OntologyRelationship(MEMBRANE, CELLULAR_COMPONENT, IS_A));
        relationships.add(new OntologyRelationship(CELL_PART, CELL, PART_OF));
        relationships.add(new OntologyRelationship(CELL_PART, CELLULAR_COMPONENT, IS_A));
        relationships.add(new OntologyRelationship(CELL_PERIPHERY, CELL_PART, IS_A));
        relationships.add(new OntologyRelationship(MEMBRANE_PART, MEMBRANE, PART_OF));
        relationships.add(new OntologyRelationship(MEMBRANE_PART, CELLULAR_COMPONENT, IS_A));
        relationships.add(new OntologyRelationship(PLASMA_MEMBRANE, CELL_PERIPHERY, PART_OF));
        relationships.add(new OntologyRelationship(PLASMA_MEMBRANE, CELL_PART, IS_A));
        relationships.add(new OntologyRelationship(PLASMA_MEMBRANE, MEMBRANE, IS_A));
        relationships.add(new OntologyRelationship(PLASMA_MEMBRANE_PART, PLASMA_MEMBRANE, PART_OF));
        relationships.add(new OntologyRelationship(PLASMA_MEMBRANE_PART, CELL_PART, IS_A));
        relationships.add(new OntologyRelationship(PLASMA_MEMBRANE_PART, MEMBRANE_PART, IS_A));
        relationships.add(new OntologyRelationship(LATERAL_PLASMA_MEMBRANE, PLASMA_MEMBRANE_PART, IS_A));

        return relationships;
    }
}