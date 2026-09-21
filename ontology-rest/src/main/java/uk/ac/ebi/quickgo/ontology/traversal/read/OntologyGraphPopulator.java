package uk.ac.ebi.quickgo.ontology.traversal.read;

import org.springframework.batch.infrastructure.item.Chunk;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph;

import java.util.List;
import org.slf4j.Logger;
import org.springframework.batch.infrastructure.item.ItemWriter;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * The purpose of this class is to populate an ontology graph with the base information:
 * vertices and edges.
 *
 * Created 18/05/16
 * @author Edd
 */
public class OntologyGraphPopulator implements ItemWriter<OntologyRelationship> {
    private static final Logger LOGGER = getLogger(OntologyGraphPopulator.class);
    private final OntologyGraph ontologyGraph;

    public OntologyGraphPopulator(OntologyGraph ontologyGraph) {
        this.ontologyGraph = ontologyGraph;
    }

    void write(List<? extends OntologyRelationship> list) {
        LOGGER.debug("Adding {} ontology graph tuples.", list.size());
        ontologyGraph.addRelationships(list);
    }

    @Override public void write(Chunk<? extends OntologyRelationship> chunk) {
        write(chunk.getItems());
    }
}
