package uk.ac.ebi.quickgo.ontology.traversal.read;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Validate that the {@link OntologyGraphPopulator} populates an {@link OntologyGraph}.
 *
 * Created 20/05/16
 * @author Edd
 */
class OntologyGraphPopulatorTest {
    private OntologyGraph ontologyGraph;
    private OntologyGraphPopulator ontologyGraphPopulator;

    @BeforeEach
    void setUp() {
        ontologyGraph = new OntologyGraph();
        ontologyGraphPopulator = new OntologyGraphPopulator(ontologyGraph);
    }

    @Test
    void graphIsPopulated() throws Exception {
        int max = 10;
        List<OntologyRelationship> tuples = createOntologyTuples(max);
        ontologyGraphPopulator.write(tuples);
        assertThat(ontologyGraph.getVertices().size(), is(equalTo(max * 2)));
    }

    private List<OntologyRelationship> createOntologyTuples(int max) {
        List<OntologyRelationship> tuples = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            tuples.add(new OntologyRelationship(
                    "child" + i,
                    "parent" + i,
                    OntologyRelationType.CAPABLE_OF
            ));
        }
        return tuples;
    }
}